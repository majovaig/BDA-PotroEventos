/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package PantallasDevolucion;

import Controlador.interfaz.ICoordinadorDevolucion;
import dtos.DevolucionDTO;
import dtos.ENUMS.MotivoDevolucionN;
import dtos.ENUMS.TipoDevolucionN;
import dtos.ReembolsoDTO;
import dtos.ReservacionDTO;
import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

/**
 *
 * @author maria
 */
public class PantallaDevolucion extends javax.swing.JFrame {
    
    private ICoordinadorDevolucion coordinadorDevolucion;
    private DevolucionDTO devolucion;
    private ReservacionDTO reservacion;
    private ButtonGroup grupoMotivos;

    /**
     * Creates new form PantallaDevolucion
     */
    public PantallaDevolucion(ICoordinadorDevolucion coordinadorDev, ReservacionDTO reservacion) {
        this.reservacion = reservacion;
        this.coordinadorDevolucion = coordinadorDev;
        initComponents();
        txtComentarios.setLineWrap(true);
        txtComentarios.setWrapStyleWord(true);
        cargarDatos();
        setLocationRelativeTo(null);
    }
    
    public void setReservacion(ReservacionDTO reservacion){
        limpiar();
        this.reservacion = reservacion;
        cargarDatos();
    }
    
    private void cargarDatos(){
        pnlMotivos.removeAll();
        lblNombreEvento.setText(reservacion.getBoleto().getEvento().getNombreEvento());
        lblUbicacion.setText(reservacion.getBoleto().getEvento().getUbicacion().getNombre());
        DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formateadorHora = DateTimeFormatter.ofPattern("HH:mm");
        txtFechaHora.setText(String.valueOf(reservacion.getBoleto().getEvento().getFechaHora().format(formateadorFecha)) + " - " + String.valueOf(reservacion.getBoleto().getEvento().getFechaHora().format(formateadorHora)));
        // cargar imagen
        String rutaLimpia = reservacion.getBoleto().getEvento().getUrlImagen().replace("/src/main/resources", "");
        String rutaAlternativa = reservacion.getBoleto().getEvento().getUrlImagen().replace("src/main/resources", "");

        URL imgUrl = getClass().getResource(rutaLimpia);
        if (imgUrl == null) {
            imgUrl = getClass().getResource(rutaAlternativa);
        }

        if (imgUrl != null) {
            ImageIcon icono = new ImageIcon(imgUrl);

            int ancho = 123;
            int alto = 115;

            Image imagenEscalada = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            iconEvento.setIcon(new ImageIcon(imagenEscalada));
        }
        iconEvento.setText("");
        
        Dimension tamanioPanel = pnlMotivos.getPreferredSize();
        pnlMotivos.setLayout(new BoxLayout(pnlMotivos, BoxLayout.Y_AXIS));
        
        grupoMotivos = new ButtonGroup();
        
        for(MotivoDevolucionN tipo : MotivoDevolucionN.values()){
            JRadioButton opcion = new JRadioButton(tipo.name());
            opcion.setOpaque(false);
            opcion.setActionCommand(tipo.name());
            grupoMotivos.add(opcion);
            pnlMotivos.add(opcion);
        }
        pnlMotivos.setSize(tamanioPanel);
        pnlMotivos.revalidate();
        pnlMotivos.repaint();
    }
    
    private void limpiar(){
        lblNombreEvento.setText("");
        lblUbicacion.setText("");
        txtFechaHora.setText("");
        iconEvento.setIcon(null);
        iconEvento.setText("");
        pnlMotivos.removeAll();
        txtComentarios.setText("");
        devolucion = new DevolucionDTO();
        reservacion = new ReservacionDTO();
    }
    
    public void llenarFormularioDevolucion(){
        devolucion = new DevolucionDTO();
        devolucion.setMotivo(MotivoDevolucionN.valueOf(grupoMotivos.getSelection().getActionCommand()));
        if(txtComentarios.getText() == null || txtComentarios.getText().isEmpty() || txtComentarios.getText().isBlank()){
            devolucion.setDescripcion(null);
        } else {
            devolucion.setDescripcion(txtComentarios.getText());
        }
        devolucion.setFechaHoraDevolucion(LocalDateTime.now());
    }
    
    public TipoDevolucionN mostrarTipoDevolucion(){
        Object[] opciones = {"Crédito de la Aplicación", "Pago a tarjeta"};
        int opcion = JOptionPane.showOptionDialog(
                this, 
                "El evento es de paga, donde tuvo un costo de $" + reservacion.getTotal() + ". ¿Cómo prefiere la devolución de su costo?", 
                "Tipo Devolución",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                null);
        if(opcion == 0){
            return TipoDevolucionN.CREDITO;
        } else if (opcion == 1){
            return TipoDevolucionN.DINERO;
        } 
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBlanco = new javax.swing.JPanel();
        pnlAzulFuerte = new javax.swing.JPanel();
        lblForm = new javax.swing.JLabel();
        lblMotivo = new javax.swing.JLabel();
        btnRegresar = new javax.swing.JButton();
        pnlMotivos = new javax.swing.JPanel();
        btnCancelar = new javax.swing.JButton();
        lblComen = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtComentarios = new javax.swing.JTextArea();
        lblEvento = new javax.swing.JLabel();
        pnlEvento = new javax.swing.JPanel();
        iconEvento = new javax.swing.JLabel();
        lblNombreEvento = new javax.swing.JLabel();
        lblUbicacion = new javax.swing.JLabel();
        txtFechaHora = new javax.swing.JLabel();
        pnlAzul = new javax.swing.JPanel();
        lblPotroEventos = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlBlanco.setBackground(new java.awt.Color(255, 255, 255));

        pnlAzulFuerte.setBackground(new java.awt.Color(0, 49, 141));

        javax.swing.GroupLayout pnlAzulFuerteLayout = new javax.swing.GroupLayout(pnlAzulFuerte);
        pnlAzulFuerte.setLayout(pnlAzulFuerteLayout);
        pnlAzulFuerteLayout.setHorizontalGroup(
            pnlAzulFuerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlAzulFuerteLayout.setVerticalGroup(
            pnlAzulFuerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 44, Short.MAX_VALUE)
        );

        lblForm.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lblForm.setText("Formulario Cancelación");

        lblMotivo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblMotivo.setText("Motivo:");

        btnRegresar.setBackground(new java.awt.Color(47, 47, 47));
        btnRegresar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnRegresar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegresar.setText("Regresar");
        btnRegresar.setBorder(null);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.addActionListener(this::btnRegresarActionPerformed);

        pnlMotivos.setBackground(new java.awt.Color(243, 237, 247));

        javax.swing.GroupLayout pnlMotivosLayout = new javax.swing.GroupLayout(pnlMotivos);
        pnlMotivos.setLayout(pnlMotivosLayout);
        pnlMotivosLayout.setHorizontalGroup(
            pnlMotivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 264, Short.MAX_VALUE)
        );
        pnlMotivosLayout.setVerticalGroup(
            pnlMotivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );

        btnCancelar.setBackground(new java.awt.Color(255, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("Cancelar");
        btnCancelar.setBorder(null);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        lblComen.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblComen.setText("Comentarios (Opcional):");

        txtComentarios.setColumns(20);
        txtComentarios.setRows(5);
        jScrollPane1.setViewportView(txtComentarios);

        lblEvento.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblEvento.setText("Evento");

        iconEvento.setText("icon");

        lblNombreEvento.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lblNombreEvento.setText("NombreEvento");

        lblUbicacion.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lblUbicacion.setText("Ubicación");

        txtFechaHora.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txtFechaHora.setText("FechaHora");

        javax.swing.GroupLayout pnlEventoLayout = new javax.swing.GroupLayout(pnlEvento);
        pnlEvento.setLayout(pnlEventoLayout);
        pnlEventoLayout.setHorizontalGroup(
            pnlEventoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEventoLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(iconEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlEventoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreEvento, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(lblUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFechaHora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        pnlEventoLayout.setVerticalGroup(
            pnlEventoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEventoLayout.createSequentialGroup()
                .addGroup(pnlEventoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlEventoLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(iconEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlEventoLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(lblNombreEvento)
                        .addGap(12, 12, 12)
                        .addComponent(lblUbicacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFechaHora)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pnlAzul.setBackground(new java.awt.Color(31, 92, 204));

        lblPotroEventos.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        lblPotroEventos.setForeground(new java.awt.Color(255, 255, 255));
        lblPotroEventos.setText("PotroEventos");

        javax.swing.GroupLayout pnlAzulLayout = new javax.swing.GroupLayout(pnlAzul);
        pnlAzul.setLayout(pnlAzulLayout);
        pnlAzulLayout.setHorizontalGroup(
            pnlAzulLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAzulLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblPotroEventos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAzulLayout.setVerticalGroup(
            pnlAzulLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAzulLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(lblPotroEventos)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlBlancoLayout = new javax.swing.GroupLayout(pnlBlanco);
        pnlBlanco.setLayout(pnlBlancoLayout);
        pnlBlancoLayout.setHorizontalGroup(
            pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlAzul, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlAzulFuerte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlBlancoLayout.createSequentialGroup()
                .addGroup(pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBlancoLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(lblForm))
                    .addGroup(pnlBlancoLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(btnRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addGroup(pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlBlancoLayout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(117, 117, 117)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlBlancoLayout.createSequentialGroup()
                                .addGroup(pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblMotivo)
                                    .addComponent(pnlMotivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(94, 94, 94)
                                .addGroup(pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblEvento)
                                    .addComponent(pnlEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblComen))))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        pnlBlancoLayout.setVerticalGroup(
            pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBlancoLayout.createSequentialGroup()
                .addComponent(pnlAzul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlAzulFuerte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(lblForm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMotivo)
                    .addComponent(lblEvento))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMotivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblComen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBlancoLayout.createSequentialGroup()
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBlancoLayout.createSequentialGroup()
                        .addGroup(pnlBlancoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRegresar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(76, 76, 76))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBlanco, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBlanco, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarActionPerformed
        // TODO add your handling code here:
        coordinadorDevolucion.abrirMostrarEventoCancelar(reservacion);
    }//GEN-LAST:event_btnRegresarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        if(grupoMotivos.getSelection() == null){
            JOptionPane.showMessageDialog(this, "Por lo menos seleccione el motivo de cancelación.");
            return;
        }
        llenarFormularioDevolucion();
        if(reservacion.getBoleto().getEvento().isGratuito()){
            devolucion.setTipo(null);
            devolucion.setReembolso(null);
            boolean registro = coordinadorDevolucion.cancelarReservacionGratuita(reservacion, devolucion);
            if(!registro){
                JOptionPane.showMessageDialog(this, "No se pudo cancelar su reservación. Por favor intente otra vez o más tarde.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Su reservación para el evento " + reservacion.getBoleto().getEvento().getNombreEvento().toUpperCase() + " ha sido cancelada correctamente!");
            coordinadorDevolucion.aumentarCapacidadEvento(reservacion.getBoleto().getEvento().getIdEvento());
            coordinadorDevolucion.abrirConsultar("Cancelados");
            limpiar();
        } else {
            /*
            esta condición cambiará ya que en el programa en su última versión
            sí existe el dto pago y será más fácil detectar si una reservación fue
            pagada con créditos de la app, ahorita mismo no funciona mucho como tal
            */
            if(reservacion.getPago().getMetodoPago().equals("Créditos")){
                int opcion = JOptionPane.showConfirmDialog(this, "La reservación fue pagada con créditos de la aplicación, por lo que solo se le pueden reembolsar con créditos de la aplicación");
                if(opcion == JOptionPane.CANCEL_OPTION || opcion == JOptionPane.NO_OPTION){
                    JOptionPane.showMessageDialog(this, "Operación cancelada.");
                    coordinadorDevolucion.abrirConsultar("Próximos");
                    limpiar();
                    return;
                }
                boolean exitoCreditos = coordinadorDevolucion.cancelarReservacionPaga(reservacion, TipoDevolucionN.CREDITO, devolucion);
                if(!exitoCreditos){
                    JOptionPane.showMessageDialog(this, "No se pudo cancelar su reservación. Por favor intente otra vez o más tarde.");
                    return;
                }
                JOptionPane.showMessageDialog(
                    this,
                    "Se le cargarán " + reservacion.getTotal().intValue()*2 + " créditos a su cuenta para usar en futuras compras en la aplicación",
                    "Operación Éxitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                devolucion.setTipo(TipoDevolucionN.CREDITO);
                devolucion.setReembolso(new ReembolsoDTO(null, LocalDateTime.now(), reservacion.getTotal()*2, "Créditos"));
                coordinadorDevolucion.aumentarCapacidadEvento(reservacion.getBoleto().getEvento().getIdEvento());
                coordinadorDevolucion.abrirConsultar("Cancelados");
                limpiar();
                return;
            }
            TipoDevolucionN tipo = mostrarTipoDevolucion();
            if(tipo == TipoDevolucionN.CREDITO){
                devolucion.setTipo(tipo);
                boolean exitoCreditos = coordinadorDevolucion.cancelarReservacionPaga(reservacion, tipo, devolucion);
                if(!exitoCreditos){
                    JOptionPane.showMessageDialog(this, "No se pudo cancelar su reservación. Por favor intente otra vez o más tarde.");
                    return;
                }
                JOptionPane.showMessageDialog(
                    this,
                    "Se le cargarán " + reservacion.getTotal().intValue()*2 + " créditos a su cuenta para usar en futuras compras en la aplicación",
                    "Operación Éxitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                devolucion.setReembolso(new ReembolsoDTO(null, LocalDateTime.now(), reservacion.getTotal()*2, "Créditos"));
                coordinadorDevolucion.aumentarCapacidadEvento(reservacion.getBoleto().getEvento().getIdEvento());
                coordinadorDevolucion.abrirConsultar("Cancelados");
                limpiar();
            } else if (tipo == TipoDevolucionN.DINERO){
                devolucion.setTipo(tipo);
                boolean exitoDinero = coordinadorDevolucion.cancelarReservacionPaga(reservacion, tipo, devolucion);
                if(!exitoDinero){
                    JOptionPane.showMessageDialog(this, "No se pudo cancelar su reservación. Por favor intente otra vez o más tarde.");
                    return;
                }
                JOptionPane.showMessageDialog(
                    this,
                    "Su devolución, con un monto de $" + reservacion.getTotal() + ", se verá reflejada en las próximas 72 horas.",
                    "Operación Éxitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                devolucion.setReembolso(new ReembolsoDTO(null, LocalDateTime.now(), reservacion.getTotal(), "Tarjeta"));
                coordinadorDevolucion.aumentarCapacidadEvento(reservacion.getBoleto().getEvento().getIdEvento());
                coordinadorDevolucion.abrirConsultar("Cancelados");
                limpiar();
            }
        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnRegresar;
    private javax.swing.JLabel iconEvento;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblComen;
    private javax.swing.JLabel lblEvento;
    private javax.swing.JLabel lblForm;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblNombreEvento;
    private javax.swing.JLabel lblPotroEventos;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JPanel pnlAzul;
    private javax.swing.JPanel pnlAzulFuerte;
    private javax.swing.JPanel pnlBlanco;
    private javax.swing.JPanel pnlEvento;
    private javax.swing.JPanel pnlMotivos;
    private javax.swing.JTextArea txtComentarios;
    private javax.swing.JLabel txtFechaHora;
    // End of variables declaration//GEN-END:variables
}
