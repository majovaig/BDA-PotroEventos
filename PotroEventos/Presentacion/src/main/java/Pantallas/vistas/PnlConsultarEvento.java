/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Pantallas.vistas;

import Controlador.interfaz.ICoordinadorAplicacion;
import dtos.AsientoDTO;
import dtos.AsientoEventoDTO;
import dtos.BoletoDTO;
import dtos.ENUMS.EstadoBoletoDTO;
import dtos.ENUMS.ReservacionEstadoDTO;
import dtos.ENUMS.TipoEventoN;
import dtos.EventoDTO;
import dtos.PagoDTO;
import dtos.ReservacionDTO;
import dtos.SeccionDTO;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import utilerias.BotonUtileria;

/**
 * Panel para consultar la información de un evento específico y seleccionar
 * asientos gráficamente a través de PnlEstadio.
 *
 * @author Aaron Burciaga - 262788
 * @author Brian Sandoval - 262741
 * @author Dayanara Peralta - 262695
 * @author María Valdez - 262775
 */
public class PnlConsultarEvento extends javax.swing.JPanel {

    private ICoordinadorAplicacion coordinador;
    private EventoDTO evento;
    private PnlEstadio estadioVisual;
    private ReservacionDTO reservacionParcial;
    private List<AsientoEventoDTO> asientosSeleccionados = new ArrayList<>();

    //Variables para el Temporizador
    private Timer temporizador;
    private int tiempoRestante = 600; // 600 segundos

    /**
     * Guarda el total actual de la compra.
     */
    private Long totalCompra = 0L;

    /**
     * Constructor del panel de consulta de evento.
     *
     * @param coordinador Interfaz para la comunicación y navegación.
     * @param evento El evento que se va a consultar.
     */
    public PnlConsultarEvento(ICoordinadorAplicacion coordinador, EventoDTO evento) {
        this.coordinador = coordinador;
        this.evento = evento;

        java.awt.EventQueue.invokeLater(() -> {
            validarUsuarioITSON();
        });

        initComponents();

        txtInfo.setContentType("text/html");
        txtInfo.setEditable(false);
        txtInfo.setOpaque(false);
        BotonUtileria.estilizarBoton(btnVolver);
        BotonUtileria.estilizarBoton(btnComprar);
        lblTemporizador.setText(String.format(formatoTemporizador(tiempoRestante)));

        modoPantalla();
        cargarDatos();
        if (!evento.isGratuito()) {
            cargarEstadio();
            actualizarEtiquetasAsientos(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        iniciarTemporizador();
    }

    private void validarUsuarioITSON() {
        if (evento.getTipoEvento() == TipoEventoN.ITSON) {
            if (!coordinador.isUsuarioITSONRegistrado()) {
                coordinador.mostarRegistroITSON();
            }
        }
    }

    public void modoPantalla() {
        if (evento.isGratuito()) {
            lblTuSeccion.setText("Tu Boleto");
            lblPrecio.setText("");
            txtTotal.setText("Total: GRATIS");
            lblSecc.setText("");
            lblSeccion.setText("");
            jLabel10.setText("");
            lblFila.setText("");
            jLabel12.setText("");
            lblAsiento.setText("");
            jLabel14.setText("");
            btnComprar.setText("Adquirir Boleto");
            PnlEstadio.setVisible(true);
            jSeparator1.setVisible(false);
            jSeparator2.setVisible(false);
            jSeparator3.setVisible(false);
            jSeparator4.setVisible(false);
        } else {
            lblTuSeccion.setText("Tu Sección");
            lblSecc.setText("Sección");
            jLabel10.setText("Fila");
            jLabel12.setText("Numero Asiento");
            jLabel14.setText("Precio Unitario");
            btnComprar.setText("Comprar Boleto");
            PnlEstadio.setVisible(true);
            jSeparator1.setVisible(true);
            jSeparator2.setVisible(true);
            jSeparator3.setVisible(true);
            jSeparator4.setVisible(true);
        }
    }

    private String formatoTemporizador(int tiempoRestante) {
        int minutos = tiempoRestante / 60;
        int segundos = tiempoRestante % 60;
        return String.format("Tiempo : %02d:%02d", minutos, segundos);
    }

    /**
     * Inicia el temporizador de 10 minutos.
     */
    private void iniciarTemporizador() {
        if (temporizador == null) {
            temporizador = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tiempoRestante--;
                    lblTemporizador.setText(formatoTemporizador(tiempoRestante));

                    if (tiempoRestante <= 0) {
                        tiempoAgotado();
                    }
                }
            });
        }
        if (!temporizador.isRunning()) {
            temporizador.start();
        }
    }

    /**
     * Detiene el timer y lo devuelve a 10 minutos.
     */
    private void detenerYReiniciarTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }
        tiempoRestante = 600;
        lblTemporizador.setText("Tiempo : 10:00");
    }

    /**
     * Lógica a ejecutar cuando los 10 minutos se terminan.
     */
    private void tiempoAgotado() {
        detenerYReiniciarTemporizador();
        JOptionPane.showMessageDialog(this, "El tiempo de tu sesión ha expirado. Los asientos reservados se han liberado.", "Tiempo Agotado", JOptionPane.WARNING_MESSAGE);

        //AQUÍ ENTRA LA DAO (Vía Coordinador) - LIBERACIÓN MASIVA
        // Si el tiempo se acaba, hay que enviar la orden a la base de datos para que
        // todos los asientos que estaban en estado "RESERVADO" bajo esta sesión
        // vuelvan a estar en "DISPONIBLE".
        if (estadioVisual != null) {
            estadioVisual.limpiarSeleccion();
        }
        coordinador.mostrarInicio();
    }

    private void cargarEstadio() {
        try {
            Map<SeccionDTO, List<AsientoEventoDTO>> mapa = coordinador.obtenerMapaOcupacion(evento);
            List<AsientoDTO> catalogo = coordinador.obtenerCatalogoAsientos();

            if (mapa == null || catalogo == null) {
                System.err.println("Datos del estadio nulos");
                return;
            }

            estadioVisual = new PnlEstadio(mapa, catalogo, (List<SeccionDTO> secciones, List<AsientoDTO> asientosInfo, List<AsientoEventoDTO> asientosEventos) -> {
                actualizarEtiquetasAsientos(secciones, asientosInfo, asientosEventos);
            }, coordinador);

            estadioVisual.setPreferredSize(new java.awt.Dimension(400, 400));

            PnlEstadio.removeAll();
            PnlEstadio.setLayout(new java.awt.BorderLayout());

            PnlEstadio.add(estadioVisual, java.awt.BorderLayout.CENTER);

            PnlEstadio.revalidate();
            PnlEstadio.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza las etiquetas de UI dependiendo de si hay 0, 1 o múltiples
     * asientos seleccionados y calcula el total general.
     *
     * @param secciones Lista de secciones correspondientes a los asientos.
     * @param asientosInfo Lista con la información técnica de los asientos.
     * @param asientosEventos Lista de asientos seleccionados del evento.
     */
    private void actualizarEtiquetasAsientos(
            List<SeccionDTO> secciones,
            List<AsientoDTO> asientosInfo,
            List<AsientoEventoDTO> asientosEventos
    ) {

        // Guardar los asientos seleccionados
        this.asientosSeleccionados = asientosEventos;

        if (evento == null || evento.isGratuito()) {
            totalCompra = 0L;
            return;
        }

        // Caso: ningún asiento seleccionado
        if (asientosEventos == null || asientosEventos.isEmpty()) {
            lblSeccion.setText("-");
            lblFila.setText("-");
            lblAsiento.setText("-");
            lblPrecio.setText("$0.00");
            txtTotal.setText("Total: $0.00");
            totalCompra = 0L;
            return;
        }

        // Calcular total general
        totalCompra = calcularTotalCompra(secciones);

        // Caso: un solo asiento
        if (asientosEventos.size() == 1) {
            SeccionDTO seccion = secciones.get(0);
            AsientoDTO asiento = asientosInfo.get(0);

            lblSeccion.setText(seccion.getNombre());
            lblFila.setText(asiento.getFila());
            lblAsiento.setText(String.valueOf(asiento.getNumero()));
            lblPrecio.setText(String.format("$%.2f", (double) seccion.getPrecioBase()));
        } else {

            // Caso: múltiples asientos
            StringBuilder textoSecciones = new StringBuilder("<html>");
            StringBuilder textoFilas = new StringBuilder("<html>");
            StringBuilder textoAsientos = new StringBuilder("<html>");
            StringBuilder textoPrecios = new StringBuilder("<html>");

            for (int i = 0; i < asientosEventos.size(); i++) {
                SeccionDTO seccion = secciones.get(i);
                AsientoDTO asiento = asientosInfo.get(i);

                if (seccion != null && asiento != null) {
                    textoSecciones.append(seccion.getNombre()).append("<br>");
                    textoFilas.append(asiento.getFila()).append("<br>");
                    textoAsientos.append(asiento.getNumero()).append("<br>");
                    textoPrecios.append(
                            String.format("$%.2f", (double) seccion.getPrecioBase())
                    ).append("<br>");
                }
            }

            textoSecciones.append("</html>");
            textoFilas.append("</html>");
            textoAsientos.append("</html>");
            textoPrecios.append("</html>");

            lblSeccion.setText(textoSecciones.toString());
            lblFila.setText(textoFilas.toString());
            lblAsiento.setText(textoAsientos.toString());
            lblPrecio.setText(textoPrecios.toString());
        }

        txtTotal.setText(String.format("Total: $%.2f", (double) totalCompra));
    }

    /**
     * Calcula el total de la compra sumando el precio base de cada sección
     * seleccionada.
     *
     * @param secciones Lista de secciones seleccionadas.
     * @return Total acumulado de la compra.
     */
    private Long calcularTotalCompra(List<SeccionDTO> secciones) {
        Long total = 0L;

        for (SeccionDTO seccion : secciones) {
            if (seccion != null) {
                total += seccion.getPrecioBase();
            }
        }

        return total;
    }

    public void cargarDatos() {
        if (evento == null) {
            return;
        }

        reservacionParcial = new ReservacionDTO();

        if (evento.getUrlImagen() != null && !evento.getUrlImagen().isEmpty()) {

            String rutaLimpia = evento.getUrlImagen().replace("/src/main/resources", "");
            String rutaAlternativa = evento.getUrlImagen().replace("src/main/resources", "");

            java.net.URL imgUrl = getClass().getResource(rutaLimpia);
            if (imgUrl == null) {
                imgUrl = getClass().getResource(rutaAlternativa);
            }

            if (imgUrl != null) {
                ImageIcon icono = new ImageIcon(imgUrl);

                int ancho = 306;
                int alto = 202;

                Image imagenEscalada = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                iconEvento.setIcon(new ImageIcon(imagenEscalada));
            }
            iconEvento.setText("");
        }

        this.lblNombre.setText(evento.getNombreEvento());
        txtInfo.setContentType("text/html");
        txtInfo.setText("<html><div style='text-align: justify; font-family: sans-serif; font-size: 11px;'>"
                + evento.getInformacionEvento() + "</div></html>");
        txtInfo.setEditable(false);
        txtInfo.setOpaque(false);
        txtInfo.setBackground(new java.awt.Color(0, 0, 0, 0));
        jScrollPane2.setOpaque(false);
        jScrollPane2.getViewport().setOpaque(false);
        jScrollPane2.setBorder(null);
        DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formateadorHora = DateTimeFormatter.ofPattern("HH:mm");
        this.lblFechaHora.setText(String.valueOf(evento.getFechaHora().format(formateadorFecha)) + " - " + String.valueOf(evento.getFechaHora().format(formateadorHora)));
        this.lblUbicacion.setText(evento.getUbicacion().getNombre());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        iconEvento = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        lblFechaHora = new javax.swing.JLabel();
        lblUbicacion = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();
        lblTemporizador = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblTuSeccion = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblSecc = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lblSeccion = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        lblFila = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblAsiento = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        lblPrecio = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        btnComprar = new javax.swing.JButton();
        PnlEstadio = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextPane();

        setOpaque(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        iconEvento.setText("iconEvento");

        lblNombre.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblNombre.setText("Título Evento");

        lblFechaHora.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblFechaHora.setText("Fecha y Hora");

        lblUbicacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblUbicacion.setText("Ubicación Evento");

        btnVolver.setBackground(new java.awt.Color(31, 92, 204));
        btnVolver.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnVolver.setForeground(new java.awt.Color(255, 255, 255));
        btnVolver.setText("Volver");
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setOpaque(true);
        btnVolver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnVolverMouseClicked(evt);
            }
        });
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        lblTemporizador.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTemporizador.setText("Tiempo : Ejemplo");

        lblTuSeccion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTuSeccion.setText("Tu Sección");

        lblSecc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSecc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSecc.setText("Sección");

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lblSeccion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSeccion.setText("seccion");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Fila");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lblFila.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFila.setText("fila");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Numero Asiento");

        lblAsiento.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAsiento.setText("asientos");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Precio Unitario");

        lblPrecio.setText("precio");

        txtTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtTotal.setText("Total: $");
        txtTotal.setEnabled(false);
        txtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalActionPerformed(evt);
            }
        });

        btnComprar.setBackground(new java.awt.Color(31, 92, 204));
        btnComprar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnComprar.setForeground(new java.awt.Color(255, 255, 255));
        btnComprar.setText("Comprar Boleto");
        btnComprar.setBorderPainted(false);
        btnComprar.setFocusPainted(false);
        btnComprar.setOpaque(true);
        btnComprar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnComprarMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator4)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblSeccion, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblSecc, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblFila, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel12)
                                            .addComponent(lblAsiento, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnComprar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTotal))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(lblPrecio))
                        .addContainerGap(182, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTuSeccion)
                .addGap(104, 104, 104))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTuSeccion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSecc)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblSeccion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblFila)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jSeparator2)
                    .addComponent(jSeparator3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblAsiento)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(lblPrecio)
                .addGap(43, 43, 43)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnComprar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PnlEstadio.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout PnlEstadioLayout = new javax.swing.GroupLayout(PnlEstadio);
        PnlEstadio.setLayout(PnlEstadioLayout);
        PnlEstadioLayout.setHorizontalGroup(
            PnlEstadioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 287, Short.MAX_VALUE)
        );
        PnlEstadioLayout.setVerticalGroup(
            PnlEstadioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 288, Short.MAX_VALUE)
        );

        txtInfo.setEditable(false);
        txtInfo.setBorder(null);
        txtInfo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtInfo.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(txtInfo);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iconEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                                .addComponent(PnlEstadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(69, 69, 69))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblUbicacion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(55, 55, 55)
                                .addComponent(lblTemporizador))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblFechaHora, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(iconEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTemporizador, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNombre))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(138, 138, 138)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(lblFechaHora)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblUbicacion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PnlEstadio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(12, 12, 12))))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalActionPerformed

    private void btnVolverMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseClicked
        // TODO add your handling code here:
        coordinador.mostrarInicio();
    }//GEN-LAST:event_btnVolverMouseClicked

    private void btnComprarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnComprarMouseClicked

        if (evento.isGratuito()) {

            String tokenNuevo = UUID.randomUUID().toString();

            // 2. Generar la ruta del QR usando ese token
            String rutaQR = coordinador.generarQR(evento, null, tokenNuevo);

            BoletoDTO boletoGratis = new BoletoDTO(rutaQR, 0.0, EstadoBoletoDTO.ACTIVO, evento, null, tokenNuevo);

            reservacionParcial.setBoleto(boletoGratis);
            reservacionParcial.setPago(null);
            reservacionParcial.setTotal(0.0);
            reservacionParcial.setEstado(ReservacionEstadoDTO.ACTIVA);
            reservacionParcial.setUsuario(coordinador.getUsuarioSesion());
            reservacionParcial.setFechaHora(LocalDateTime.now());

            coordinador.venderAsientos(asientosSeleccionados, 0L, true, reservacionParcial);

            JOptionPane.showMessageDialog(this, "Boleto adquirido correctamente.");
            coordinador.mostrarDetalles(reservacionParcial);
            return;
        }

        // ================= EVENTO DE PAGO =================
        if (asientosSeleccionados == null || asientosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un asiento en el mapa.");
            return;
        }

        if (asientosSeleccionados.size() != 1) {
            JOptionPane.showMessageDialog(this, "Solo puede adquirir un boleto por compra.");
            return;
        }

        AsientoEventoDTO asientoDTO = new AsientoEventoDTO(
                asientosSeleccionados.get(0).getIdAsientoEvento(),
                asientosSeleccionados.get(0).getPrecio(),
                asientosSeleccionados.get(0).getEstadoAsiento(),
                asientosSeleccionados.get(0).getAsiento(),
                asientosSeleccionados.get(0).getEvento()
        );

        // en el peor d los casos esto puede ser comentareado!
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea pagar con créditos de la aplicación?");

        // ================= PAGO CON CRÉDITOS =================
        if (opcion == JOptionPane.OK_OPTION) {

            Long total = totalCompra;

            reservacionParcial.setPago(
                    new PagoDTO(null, LocalDateTime.now(), total.doubleValue() * 2, "CREDITO")
            );

            reservacionParcial.setUsuario(coordinador.getUsuarioSesion());
            String tokenPago = UUID.randomUUID().toString();
            String rutaQR = coordinador.generarQR(evento, asientoDTO, tokenPago);
            BoletoDTO boleto = new BoletoDTO(rutaQR, totalCompra.doubleValue() * 2, EstadoBoletoDTO.ACTIVO, evento, asientoDTO, tokenPago);
            reservacionParcial.setFechaHora(LocalDateTime.now());
            reservacionParcial.setEstado(ReservacionEstadoDTO.ACTIVA);
            reservacionParcial.setBoleto(boleto);

            boolean exito = coordinador.venderAsientos(asientosSeleccionados, total, false, reservacionParcial);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Compra realizada con créditos.");
                coordinador.mostrarDetalles(reservacionParcial);
                return;
            } else {
                JOptionPane.showMessageDialog(this, "No tienes créditos suficientes.");
                return;
            }

        }

        // 1. Generar token
        String tokenPago = UUID.randomUUID().toString();

        // 2. Generar QR
        String rutaQR = coordinador.generarQR(evento, asientoDTO, tokenPago);

        // 3. Crear DTO
        BoletoDTO boleto = new BoletoDTO(rutaQR, totalCompra.doubleValue(), EstadoBoletoDTO.ACTIVO, evento, asientoDTO, tokenPago);

        reservacionParcial.setBoleto(boleto);

        reservacionParcial.setBoleto(boleto);
        reservacionParcial.setFechaHora(LocalDateTime.now());
        reservacionParcial.setTotal(totalCompra.doubleValue());
        reservacionParcial.setEstado(ReservacionEstadoDTO.ACTIVA);
        reservacionParcial.setUsuario(coordinador.getUsuarioSesion());

        // Guardas como pendiente en backend
        coordinador.venderAsientos(asientosSeleccionados, totalCompra, false, reservacionParcial);

        // Ahora sí vas a la pantalla de pago
        coordinador.mostrarPago(reservacionParcial);
    }//GEN-LAST:event_btnComprarMouseClicked

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        // TODO add your handling code here:
        int opcion = JOptionPane.showConfirmDialog(this, "¿Seguro de volver? Esto eliminará el proceso de compra actual.");
        if (opcion == JOptionPane.OK_OPTION) {
            coordinador.mostrarInicio();
        }
    }//GEN-LAST:event_btnVolverActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PnlEstadio;
    private javax.swing.JButton btnComprar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel iconEvento;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblAsiento;
    private javax.swing.JLabel lblFechaHora;
    private javax.swing.JLabel lblFila;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblSecc;
    private javax.swing.JLabel lblSeccion;
    private javax.swing.JLabel lblTemporizador;
    private javax.swing.JLabel lblTuSeccion;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JTextPane txtInfo;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
