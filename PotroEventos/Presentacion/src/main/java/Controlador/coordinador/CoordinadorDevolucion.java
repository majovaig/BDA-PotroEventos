/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.coordinador;

import Controlador.interfaz.ICoordinadorAplicacion;
import Controlador.interfaz.ICoordinadorDevolucion;
import PantallasDevolucion.PantallaDevolucion;
import PantallasDevolucion.PantallaEventoCancelar;
import devolucionBoleto.FachadaDevolucionBoleto;
import devolucionBoleto.IDevolucionBoleto;
import dtos.DevolucionDTO;
import dtos.ENUMS.TipoDevolucionN;
import dtos.EventoDTO;
import dtos.ReservacionDTO;
import excepciones.DevolucionBoletoException;
import excepciones.GestionEventoException;
import gestionEvento.GestionEventoFachada;
import gestionEvento.IFachadaGestionEvento;

/**
 *
 * @author maria
 */
public class CoordinadorDevolucion implements ICoordinadorDevolucion {

    private PantallaDevolucion frmDevolucion;
    private PantallaEventoCancelar frmEventoCancelar;
    private final ICoordinadorAplicacion coordinadorApp;
    private final IFachadaGestionEvento controlEvento = new GestionEventoFachada();
    private final IDevolucionBoleto controlDevolucion = new FachadaDevolucionBoleto();

    public CoordinadorDevolucion(ICoordinadorAplicacion coordinador) {
        this.coordinadorApp = coordinador;
    }

    private void ocultarTodo() {
        if (frmDevolucion != null) {
            frmDevolucion.setVisible(false);
        }
        if (frmEventoCancelar != null) {
            frmEventoCancelar.setVisible(false);
        }
    }

    @Override
    public void abrirMostrarEventoCancelar(ReservacionDTO reservacion) {
        ocultarTodo();
        if (frmEventoCancelar == null) {
            frmEventoCancelar = new PantallaEventoCancelar(reservacion, this);
        } else {
            frmEventoCancelar.setReservacion(reservacion);
        }
        frmEventoCancelar.setVisible(true);
    }

    @Override
    public void mostrarDevolucion(ReservacionDTO reservacion) {
        ocultarTodo();
        if (frmDevolucion == null) {
            frmDevolucion = new PantallaDevolucion(this, reservacion);
        } else {
            frmDevolucion.setReservacion(reservacion);
        }
        frmDevolucion.setVisible(true);
    }

    @Override
    public boolean cancelarReservacionGratuita(ReservacionDTO reservacion, DevolucionDTO devolucion) {
        try {
            return controlDevolucion.realizarDevolucionGratuita(reservacion, devolucion);
        } catch (DevolucionBoletoException dbe) {
            return false;
        }
    }

    @Override
    public boolean cancelarReservacionPaga(ReservacionDTO reservacion, TipoDevolucionN tipo, DevolucionDTO devolucion) {
        try {
            return controlDevolucion.realizarDevolucionPago(reservacion, tipo, devolucion);
        } catch (DevolucionBoletoException dbe) {
            return false;
        }
    }

    @Override
    public void abrirConsultar(String tipoEvento) {
        ocultarTodo();
        coordinadorApp.mostrarConsultar(tipoEvento);
    }

    /*
    sabes k we yo creo q esto lo puede hacer el subsistema as well
     */
    @Override
    public boolean validarTiempo(EventoDTO evento) {
        return controlDevolucion.validarTiempoDevolucion(evento.getFechaHora());
    }

    @Override
    public boolean aumentarCapacidadEvento(String idEvento) {
        try {
            return controlEvento.aumentarCapacidad(idEvento);
        } catch (GestionEventoException gee) {
            return false;
        }
    }

    @Override
    public void inicioAplicacion() {
        ocultarTodo();
        coordinadorApp.mostrarInicio();
    }

}
