package Pantallas;

import Controlador.coordinador.CoordinadorAplicacion;
import Pantallas.vistas.dialogos.DlgMensaje;
import dtos.AsistenciaDTO;
import dtos.BoletoDTO;
import dtos.EventoDTO;
import excepciones.CoordinadorException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;

/**
 * Pantalla encargada de renderizar los frames individuales de imágenes y
 * enviarlos al lector de QR de manera controlada con rotación vertical.
 *
 * @author Brian Sandoval - 262741
 */
public class FrmCamara extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmCamara.class.getName());

    private final CoordinadorAplicacion coordinador;
    private Thread hiloCamara;
    private EventoDTO evento;

    // Volatile asegura que el cambio de la variable sea visto instantáneamente por el hilo
    private volatile boolean ejecutando;

    // Almacena el frame descargado de forma segura para el repintado
    private BufferedImage frameActual;
    private int contadorFrames = 0;

    public FrmCamara(CoordinadorAplicacion coordinador, EventoDTO evento) throws CoordinadorException {
        // Inicialización de componentes de NetBeans
        initComponents();
        this.coordinador = coordinador;
        this.evento = evento;

        // Sobreescribimos el método de dibujo del panel para aceleración gráfica nativa con rotación
        PnlCamara = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Si la pantalla se está cerrando, evitamos pintar basura en memoria
                if (!ejecutando) {
                    return;
                }

                if (frameActual != null) {
                    // Casteamos a Graphics2D para usar herramientas avanzadas de rotación
                    Graphics2D g2d = (Graphics2D) g.create();

                    // 1. Trasladamos el origen al ancho del panel
                    g2d.translate(getWidth(), 0);

                    // 2. Rotamos 90 grados en radianes
                    g2d.rotate(Math.PI / 2);

                    // 3. Dibujamos la imagen invirtiendo alto y ancho por el lienzo rotado
                    g2d.drawImage(frameActual, 0, 0, getHeight(), getWidth(), null);

                    // Liberamos los recursos del clon gráfico
                    g2d.dispose();
                }
            }
        };

        // Re-vinculamos los componentes gráficos
        PnlContenedor.add(PnlCamara);
        PnlCamara.setBounds(412, 90, 400, 400);

        iniciarCamara();
    }

    /**
     * Inicia el ciclo repetitivo de descarga de imágenes en un hilo secundario.
     */
    private void iniciarCamara() throws CoordinadorException {
        boolean iniciada = coordinador.iniciarCamara();

        if (!iniciada) {
            JOptionPane.showMessageDialog(this, "No fue posible establecer conexión con el servidor de la cámara IP.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            throw new CoordinadorException("No fue posible establecer conexión con el servidor de la cámara IP.");
        }

        ejecutando = true;

        hiloCamara = new Thread(() -> {
            while (ejecutando && !Thread.currentThread().isInterrupted()) {
                try {
                    // Pide y descarga una nueva imagen limpia al controlador
                    BufferedImage frame = coordinador.obtenerFrameActual();

                    // Validación de salida doble por si se cambió la bandera durante la descarga de red
                    if (!ejecutando) {
                        break;
                    }

                    if (frame != null) {
                        this.frameActual = frame;

                        // Solicita a Swing que repinte el panel de forma asíncrona y eficiente
                        PnlCamara.repaint();

                        contadorFrames++;

                        // Analiza el QR cada 8 imágenes descargadas para balancear velocidad de video y CPU
                        if (contadorFrames >= 8) {
                            contadorFrames = 0;

                            String qr = coordinador.leerQR(frame);

                            if (qr != null && ejecutando) {

                                try {

                                    AsistenciaDTO asistenciaDTO = new AsistenciaDTO();
                                    asistenciaDTO.setEmpleadoDTO(coordinador.obtenerSesionEmpleado());

                                    AsistenciaDTO asistencia = coordinador.registrarAsistencia(
                                            qr,
                                            asistenciaDTO,
                                            evento.getIdEvento()
                                    );

                                    if (asistencia != null) {
                                        DlgMensaje dialogo = new DlgMensaje(this, true);

                                        dialogo.mostrarExito(
                                                "ACCESO PERMITIDO",
                                                "La asistencia fue registrada correctamente."
                                        );

                                        dialogo.setLocationRelativeTo(this);
                                        dialogo.setVisible(true);
                                        FrmAsistencias frm = coordinador.getFrmAsistencias();
                                        if (frm != null) {
                                            frm.refrescarAsistencias();
                                        }

                                    }

                                } catch (Exception ex) {
                                    DlgMensaje dialogo = new DlgMensaje(this, true);

                                    // Si el error dice que ya fue utilizado, extraemos el boleto
                                    if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("ya fue utilizado")) {

                                        try {
                                            // Buscamos el boleto por su token/QR
                                            BoletoDTO boletoUsado = coordinador.obtenerBoletoPorToken(qr);

                                            if (boletoUsado != null && boletoUsado.getAsistenciaDTO() != null) {

                                                dialogo.mostrarErrorBoletoUsado(
                                                        "BOLETO YA UTILIZADO",
                                                        ex.getMessage(),
                                                        boletoUsado.getAsistenciaDTO()
                                                );

                                            } else {
                                                dialogo.mostrarError("ACCESO DENEGADO", ex.getMessage());
                                            }
                                        } catch (Exception e) {
                                            // Si falla la consulta al coordinador, mostramos el error original
                                            dialogo.mostrarError("ACCESO DENEGADO", ex.getMessage());
                                        }

                                    } else {
                                        // Para cualquier otro error (no existe, expirado, null pointers del servidor, etc.)
                                        dialogo.mostrarError("ACCESO DENEGADO", ex.getMessage());
                                    }

                                    dialogo.setLocationRelativeTo(this);
                                    dialogo.setVisible(true);
                                }

                                Thread.sleep(1500);
                            }
                        }
                    }

                    // Pausa de sincronización corta (Aprox 30-33 FPS teóricos)
                    Thread.sleep(16);

                } catch (InterruptedException e) {
                    // Captura limpia de la interrupción del hilo para salir de inmediato sin errores
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    if (ejecutando) {
                        e.printStackTrace();
                    }
                }
            }
        }, "Hilo-CamaraIP");

        hiloCamara.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        PnlContenedor = new javax.swing.JPanel();
        PnlCamara = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);

        jPanel2.setBackground(new java.awt.Color(31, 92, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("PotroEventos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(0, 49, 141));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1220, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 54, Short.MAX_VALUE)
        );

        PnlContenedor.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout PnlCamaraLayout = new javax.swing.GroupLayout(PnlCamara);
        PnlCamara.setLayout(PnlCamaraLayout);
        PnlCamaraLayout.setHorizontalGroup(
            PnlCamaraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        PnlCamaraLayout.setVerticalGroup(
            PnlCamaraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("evento");

        btnVolver.setBackground(new java.awt.Color(31, 92, 204));
        btnVolver.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnVolver.setForeground(new java.awt.Color(255, 255, 255));
        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PnlContenedorLayout = new javax.swing.GroupLayout(PnlContenedor);
        PnlContenedor.setLayout(PnlContenedorLayout);
        PnlContenedorLayout.setHorizontalGroup(
            PnlContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PnlContenedorLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(PnlCamara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(412, 412, 412))
            .addGroup(PnlContenedorLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PnlContenedorLayout.setVerticalGroup(
            PnlContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PnlContenedorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(41, 41, 41)
                .addComponent(PnlCamara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PnlContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(PnlContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        coordinador.mostrarConsultarEventos();
    }//GEN-LAST:event_btnVolverActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PnlCamara;
    private javax.swing.JPanel PnlContenedor;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
