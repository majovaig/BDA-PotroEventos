/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Pantallas.vistas;

import Controlador.interfaz.ICoordinadorAplicacion;
import dtos.EventoDTO;
import dtos.FacturaDTO;
import dtos.PerfilFiscalDTO;
import dtos.ReservacionDTO;
import excepciones.CoordinadorException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Panel;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Aaron Burciaga - 262788
 * @author Brian Sandoval - 262741
 * @author Dayanara Peralta - 262695
 * @author María Valdez - 262775
 */
public class PnlEvento extends Panel {

    private boolean modoConsulta;
    private EventoDTO evento;
    private ReservacionDTO reservacion;
    private ICoordinadorAplicacion coordinador;

    /**
     * Constructor PRIVADO. Solo puede ser llamado por los métodos estáticos de
     * abajo.
     */
    private PnlEvento(EventoDTO evento, ReservacionDTO reservacion, Component padre, boolean modoConsulta, ICoordinadorAplicacion coordinador) {
        this.evento = evento;
        this.reservacion = reservacion;
        this.modoConsulta = modoConsulta;
        this.coordinador = coordinador;

        initComponents();
        
        this.setMinimumSize(new Dimension(100, 188));
        this.setPreferredSize(null);
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 188));
        
        cargarEvento();
        configurarModo();
        utilerias.BotonUtileria.estilizarBoton(btnMostrar);
    }

    public static PnlEvento crearParaVista(EventoDTO evento, Component padre, ICoordinadorAplicacion coordinador) {
        // Le pasamos null a la reservación y false al modoConsulta
        return new PnlEvento(evento, null, padre, false, coordinador);
    }

    public static PnlEvento crearParaConsulta(ReservacionDTO reservacion, Component padre, ICoordinadorAplicacion coordinador) {
        // Le pasamos la reservación y true al modoConsulta
        return new PnlEvento(reservacion.getBoleto().getEvento(), reservacion, padre, true, coordinador);
    }
    
    public void mostrarBotonCancelar(){
        btnCancelar.setVisible(true);
    }

    private void cargarEvento() {
        btnCancelar.setVisible(false);
        if (modoConsulta) {
            if (reservacion.getBoleto().getEvento().getUrlImagen() != null && !reservacion.getBoleto().getEvento().getUrlImagen().isEmpty()) {

                String rutaLimpia = reservacion.getBoleto().getEvento().getUrlImagen().replace("/src/main/resources", "");
                String rutaAlternativa = reservacion.getBoleto().getEvento().getUrlImagen().replace("src/main/resources", "");

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
        } else {
            if (evento.getUrlImagen() != null && !evento.getUrlImagen().isEmpty() && !evento.getUrlImagen().isBlank()) {
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
        }
        this.lblNombre.setText(evento.getNombreEvento());
        DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formateadorHora = DateTimeFormatter.ofPattern("HH:mm");
        this.lblFechaHora.setText(String.valueOf(evento.getFechaHora().format(formateadorFecha)) + " - " + String.valueOf(evento.getFechaHora().format(formateadorHora)));
        this.lblUbicacion.setText(evento.getUbicacion().getNombre());
    }

    private void configurarModo() {
        if (modoConsulta) {
            btnFacturar.setVisible(true);
            btnFacturar.setText("Facturar");
            btnMostrar.setText("Ver mis boletos");
            if (evento.isGratuito()) {
                btnFacturar.setVisible(false);
            }
            btnMostrar.setText("Ver mis boletos");
        } else {
            btnFacturar.setVisible(false);
            btnMostrar.setText("Mostrar Información");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNombre = new javax.swing.JLabel();
        lblFechaHora = new javax.swing.JLabel();
        lblUbicacion = new javax.swing.JLabel();
        iconEvento = new javax.swing.JLabel();
        btnMostrar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnFacturar = new javax.swing.JButton();

        setBackground(new java.awt.Color(221, 212, 212));
        setMaximumSize(new java.awt.Dimension(270, 188));
        setMinimumSize(new java.awt.Dimension(270, 188));
        setPreferredSize(new java.awt.Dimension(270, 188));

        lblNombre.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lblNombre.setText("Nombre");

        lblFechaHora.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lblFechaHora.setText("Fecha y Hora");

        lblUbicacion.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lblUbicacion.setText("Ubicación");

        iconEvento.setText("iconEvento");

        btnMostrar.setBackground(new java.awt.Color(31, 92, 204));
        btnMostrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMostrar.setForeground(new java.awt.Color(255, 255, 255));
        btnMostrar.setText("Mostrar Información");
        btnMostrar.addActionListener(this::btnMostrarActionPerformed);

        btnCancelar.setBackground(new java.awt.Color(255, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("Cancelar");
        btnCancelar.setBorderPainted(false);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        btnFacturar.setBackground(new java.awt.Color(69, 105, 82));
        btnFacturar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnFacturar.setForeground(new java.awt.Color(255, 255, 255));
        btnFacturar.setText("Facturar");
        btnFacturar.addActionListener(this::btnFacturarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iconEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblFechaHora, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                            .addComponent(lblNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnMostrar)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnFacturar)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iconEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNombre)
                        .addGap(18, 18, 18)
                        .addComponent(lblFechaHora)
                        .addGap(18, 18, 18)
                        .addComponent(lblUbicacion)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnMostrar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancelar)
                        .addComponent(btnFacturar)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarActionPerformed
        if ("Ver mis boletos".equals(btnMostrar.getText())) {
            coordinador.mostrarDetalles(reservacion);
        } else {
            coordinador.mostrarInfoEvento(evento);
        }
    }//GEN-LAST:event_btnMostrarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        coordinador.cancelarReservacion(reservacion);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturarActionPerformed
        try {
            if(coordinador.facturar(reservacion.getIdReservacion())){
                JOptionPane.showMessageDialog(this, "La reserva ya ha sido facturada");
                return;
            }
            PerfilFiscalDTO perfilObtenido = coordinador.recuperarPerfilFiscal(reservacion.getUsuario().getIdUsuario());

            if(perfilObtenido == null){
                return;
            }
            FacturaDTO factura = coordinador.crearFactura(perfilObtenido, reservacion);
            coordinador.mostrarDatosFactura(factura);
        } catch (CoordinadorException ex) {
            JOptionPane.showMessageDialog(this, "Surgió algo inesperado: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnFacturarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnFacturar;
    private javax.swing.JButton btnMostrar;
    private javax.swing.JLabel iconEvento;
    private javax.swing.JLabel lblFechaHora;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblUbicacion;
    // End of variables declaration//GEN-END:variables
}
