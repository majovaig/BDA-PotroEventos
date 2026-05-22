/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package devolucionBoleto;

import dtos.DevolucionDTO;
import dtos.ENUMS.ReservacionEstadoDTO;
import dtos.ENUMS.TipoDevolucionN;
import dtos.ReembolsoDTO;
import dtos.ReservacionDTO;
import excepciones.DevolucionBoletoException;
import excepciones.NegocioException;
import excepciones.PagoException;
import interfaces.IAsientoEventoBO;
import interfaces.IReservacionBO;
import interfaces.IUsuarioBO;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import objetosNegocio.AsientoEventoBO;
import objetosNegocio.ReservacionBO;
import objetosNegocio.UsuarioBO;
import pago.IPago;
import pago.PagoFachada;

/**
 *
 * @author maria
 */
public class ControlDevolucion {

    private static ControlDevolucion instancia;
    private final IReservacionBO reservacionBO = ReservacionBO.getInstance();
    private final IAsientoEventoBO asientoEventoBO = AsientoEventoBO.getInstance();
    private final IUsuarioBO usuarioBO = UsuarioBO.getInstance();
    private final IPago controlPago = new PagoFachada();

    private ControlDevolucion() {
    }

    protected static ControlDevolucion getInstance() {
        if (instancia == null) {
            instancia = new ControlDevolucion();
        }
        return instancia;
    }

    // este lo acabo de agregar pq debato entre si lo dejo aquí o en la presentación
    protected boolean tiempoValidoDevolucion(LocalDateTime fechaEvento) {
        if (fechaEvento == null) {
            return false;
        }
        LocalDateTime limite48Horas = LocalDateTime.now().minus(48, ChronoUnit.HOURS);
        return fechaEvento.isBefore(limite48Horas);
    }

    protected boolean cancelarReservacion(String idReservacion, DevolucionDTO devolucion) throws DevolucionBoletoException {
        try {
            return reservacionBO.cancelarReservacion(devolucion, idReservacion);
        } catch (NegocioException ne) {
            throw new DevolucionBoletoException(ne.getMessage());
        }
    }

    protected boolean liberarAsiento(String idAsiento) throws DevolucionBoletoException {
        try {
            return asientoEventoBO.liberarAsiento(idAsiento);
        } catch (NegocioException ne) {
            throw new DevolucionBoletoException(ne.getMessage());
        }
    }

    protected boolean depositarCreditos(String idUsuario, Integer cantidad) throws DevolucionBoletoException {
        try {
            return usuarioBO.aumentarCreditos(cantidad, idUsuario);
        } catch (NegocioException ne) {
            throw new DevolucionBoletoException(ne.getMessage());
        }
    }

    protected ReembolsoDTO regresarDinero(String idOperacion) throws DevolucionBoletoException {
        try {
            return controlPago.regresarDinero(idOperacion);
        } catch (PagoException pe) {
            throw new DevolucionBoletoException(pe.getMessage());
        }
    }

    protected boolean validarDevolucion(DevolucionDTO devolucion, boolean gratuita) throws DevolucionBoletoException {
        if (devolucion == null) {
            return false;
        }

        if (devolucion.getMotivo() == null) {
            return false;
        }

        if (devolucion.getFechaHoraDevolucion() == null) {
            return false;
        }

        if (!gratuita) {
            if (devolucion.getTipo() == null) {
                throw new DevolucionBoletoException("Me falló porque no tenía tipo.");
            }

            if (devolucion.getReembolso() == null) {
                throw new DevolucionBoletoException("Me falló porque no tenía reembolso.");
            }

            if (devolucion.getReembolso().getIdOperacion() == null && devolucion.getTipo() == TipoDevolucionN.DINERO) {
                throw new DevolucionBoletoException("Me falló porque no tenía ID el reembolso y es de dinero.");
            }

            if (devolucion.getReembolso().getFechaOperacion() == null) {
                throw new DevolucionBoletoException("Me falló porque no tenía fecha la devolución.");
            }

            // pongo esto pq hay un mínimo que stripe deja reembolsar y es de 0.50 dls (como 8-9 pesos).
            if (devolucion.getReembolso().getImporte() == null || devolucion.getReembolso().getImporte() < 10.0) {
                throw new DevolucionBoletoException("Me falló porque no tenía importe o el importe era menor a 10 pesos.");
            }

            /*
            if(devolucion.getReembolso().getMetodoPago() == null || !devolucion.getReembolso().getMetodoPago().equals("Tarjeta") || !devolucion.getReembolso().getMetodoPago().equals("Créditos")){
                throw new DevolucionBoletoException("Me falló porque no tenía tipo de método de pago válido.");
            }*/
        }
        return true;
    }

    protected boolean validarReservacion(ReservacionDTO reservacion) {
        if (reservacion == null) {
            return false;
        }

        if (reservacion.getEstado() == ReservacionEstadoDTO.CANCELADA || reservacion.getDevolucion() != null) {
            return false;
        }

        if (reservacion.getBoleto() == null) {
            return false;
        }

        if (reservacion.getIdReservacion() == null) {
            return false;
        }

        if (reservacion.getUsuario() == null) {
            return false;
        }
        return true;
    }
}
