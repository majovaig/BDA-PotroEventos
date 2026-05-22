/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Pantallas.vistas;

import Controlador.interfaz.ICoordinadorAplicacion;
import dtos.ENUMS.ReservacionEstadoDTO;
import dtos.ReservacionDTO;
import dtos.UsuarioDTO;
import java.awt.Dimension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 *
 * @author Aaron Burciaga - 262788
 * @author Brian Sandoval - 262741
 * @author Dayanara Peralta - 262695
 * @author María Valdez - 262775
 */
public class PnlConsultar extends javax.swing.JPanel {

    private ICoordinadorAplicacion coordinador;

    private UsuarioDTO usuario;

    /**
     * Creates new form PnlConsultar
     *
     * @param coordinador
     * @param tipoEvento
     */
    public PnlConsultar(ICoordinadorAplicacion coordinador, String tipoEvento) {
        this.coordinador = coordinador;
        this.usuario = coordinador.getUsuarioSesion();
        initComponents();
        utilerias.BotonUtileria.estilizarBoton(btnVolver);
        pnlEventos.setLayout(new BoxLayout(pnlEventos, BoxLayout.Y_AXIS));
        
        jScrollPane2.setOpaque(true);
        jScrollPane2.getViewport().setOpaque(true);
        jScrollPane2.setBackground(java.awt.Color.WHITE);
        jScrollPane2.getViewport().setBackground(java.awt.Color.WHITE);
        
        jScrollPane2.getViewport().setScrollMode(javax.swing.JViewport.SIMPLE_SCROLL_MODE);
        
        pnlEventos.setPreferredSize(null); 
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(16); 
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        switch (tipoEvento) {
            case "Proximos" ->
                mostrarEventosProximos();
            case "Pasados" ->
                mostrarEventosPasados();
            case "Cancelados" ->
                mostrarEventosCancelados();
            default ->
                throw new AssertionError();
        }

    }

    public void mostrarDetalles(ReservacionDTO reservacion) {
        coordinador.mostrarDetalles(reservacion);
    }

    private void mostrarEventosCancelados() {

        lblNombreEventos.setText("Reservaciones Canceladas");

        List<ReservacionDTO> cancelados = new ArrayList<>();

        for (ReservacionDTO r : coordinador.consultarReservaciones(usuario.getIdUsuario())) {
            if (r.getEstado() == ReservacionEstadoDTO.CANCELADA) {
                cancelados.add(r);
            }
        }

        for (ReservacionDTO cancelado : cancelados) {
            PnlEvento panel = PnlEvento.crearParaConsulta(cancelado, this, coordinador);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
            pnlEventos.add(panel);
            pnlEventos.add(Box.createRigidArea(new Dimension(0, 15)));
            JSeparator separador = new JSeparator(SwingConstants.HORIZONTAL);
            separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            pnlEventos.add(separador);
            pnlEventos.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        pnlEventos.revalidate();
        pnlEventos.repaint();

    }

    private void mostrarEventosPasados() {
        lblNombreEventos.setText("Eventos Pasados");

        List<ReservacionDTO> pasados = new ArrayList<>();

        for (ReservacionDTO r : coordinador.consultarReservaciones(usuario.getIdUsuario())) {
            if (r.getEstado() == ReservacionEstadoDTO.ACTIVA && r.getBoleto().getEvento().getFechaHora().isBefore(LocalDateTime.now())) {
                pasados.add(r);
            }
        }

        for (ReservacionDTO pasado : pasados) {
            PnlEvento panel = PnlEvento.crearParaConsulta(pasado, this, coordinador);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
            pnlEventos.add(panel);
            pnlEventos.add(Box.createRigidArea(new Dimension(0, 15)));
            JSeparator separador = new JSeparator(SwingConstants.HORIZONTAL);
            separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            pnlEventos.add(separador);
            pnlEventos.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        pnlEventos.revalidate();
        pnlEventos.repaint();

    }

    private void mostrarEventosProximos() {

        lblNombreEventos.setText("Eventos Próximos");

        List<ReservacionDTO> proximos = new ArrayList<>();

        for (ReservacionDTO r : coordinador.consultarReservaciones(usuario.getIdUsuario())) {
            if (r.getEstado() == ReservacionEstadoDTO.ACTIVA && r.getBoleto().getEvento().getFechaHora().isAfter(LocalDateTime.now())) {
                proximos.add(r);
            }

        }
        for (ReservacionDTO proximo : proximos) {
            PnlEvento panel = PnlEvento.crearParaConsulta(proximo, this, coordinador);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
            panel.mostrarBotonCancelar();
            pnlEventos.add(panel);
            pnlEventos.add(Box.createRigidArea(new Dimension(0, 15)));
            JSeparator separador = new JSeparator(SwingConstants.HORIZONTAL);
            separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            pnlEventos.add(separador);
            pnlEventos.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        pnlEventos.revalidate();
        pnlEventos.repaint();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel7 = new javax.swing.JPanel();
        lblNombreEventos = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlEventos = new javax.swing.JPanel();
        btnVolver = new javax.swing.JButton();

        jScrollPane1.setViewportView(jEditorPane1);

        setOpaque(false);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        lblNombreEventos.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblNombreEventos.setText("Eventos Próximos");

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlEventos.setPreferredSize(new java.awt.Dimension(1113, 429));
        pnlEventos.setLayout(new javax.swing.BoxLayout(pnlEventos, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane2.setViewportView(pnlEventos);

        btnVolver.setBackground(new java.awt.Color(31, 92, 204));
        btnVolver.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btnVolver.setForeground(new java.awt.Color(255, 255, 255));
        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(478, 478, 478)
                        .addComponent(lblNombreEventos))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lblNombreEventos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnVolver)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        coordinador.mostrarConsultarMenu();
    }//GEN-LAST:event_btnVolverActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVolver;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblNombreEventos;
    private javax.swing.JPanel pnlEventos;
    // End of variables declaration//GEN-END:variables
}
