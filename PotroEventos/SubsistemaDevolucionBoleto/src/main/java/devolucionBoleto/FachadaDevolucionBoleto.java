/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package devolucionBoleto;

import dtos.DevolucionDTO;
import dtos.ENUMS.TipoDevolucionN;
import dtos.ReembolsoDTO;
import dtos.ReservacionDTO;
import excepciones.DevolucionBoletoException;
import java.time.LocalDateTime;

/**
 *
 * @author maria
 */
public class FachadaDevolucionBoleto implements IDevolucionBoleto {

    private final ControlDevolucion controlDevolucion = ControlDevolucion.getInstance();

    @Override
    public boolean realizarDevolucionGratuita(ReservacionDTO reservacion, DevolucionDTO devolucion) throws DevolucionBoletoException {
        if (!controlDevolucion.validarReservacion(reservacion)) {
            throw new DevolucionBoletoException("Reservación inválida a cancelar.");
        }

        if (!controlDevolucion.validarDevolucion(devolucion, true)) {
            throw new DevolucionBoletoException("Devolución inválida de la reservación.");
        }

        System.out.println("Llegué hasta la fachada.");
        return controlDevolucion.cancelarReservacion(reservacion.getIdReservacion(), devolucion);
    }

    @Override
    public boolean realizarDevolucionPago(ReservacionDTO reservacion, TipoDevolucionN tipo, DevolucionDTO devolucion) throws DevolucionBoletoException {
        if (!controlDevolucion.validarReservacion(reservacion)) {
            throw new DevolucionBoletoException("Reservación inválida a cancelar.");
        }

        switch (tipo) {
            case CREDITO:
                devolucion.setReembolso(new ReembolsoDTO(null, LocalDateTime.now(), reservacion.getTotal() * 2, "Créditos"));
                if(!controlDevolucion.depositarCreditos(reservacion.getUsuario().getIdUsuario(), reservacion.getTotal().intValue()*2)){
                    throw new DevolucionBoletoException("No se pudo realizar la devolución de los créditos.");
                }
                break;
            case DINERO:
                ReembolsoDTO reembolso = controlDevolucion.regresarDinero(reservacion.getPago().getIdTransaccion());
                if (reembolso == null) {
                    throw new DevolucionBoletoException("No se pudo realizar la devolución del dinero.");
                }
                reembolso.setMetodoPago("Tarjeta");
                devolucion.setReembolso(reembolso);
                break;
            default:
                throw new DevolucionBoletoException("Tipo de devolución inválida, solo puede ser a través de pago a tarjeta o con créditos de la aplicación.");
        }

        if (!controlDevolucion.validarDevolucion(devolucion, false)) {
            throw new DevolucionBoletoException("Devolución inválida de la reservación.");
        }

        if (controlDevolucion.cancelarReservacion(reservacion.getIdReservacion(), devolucion)) {
            if (controlDevolucion.liberarAsiento(reservacion.getBoleto().getAsiento().getIdAsientoEvento())) {
                return true;
            }
        }
        System.out.println("Llegué hasta la fachada.");
        return false;
    }

    @Override
    public boolean validarTiempoDevolucion(LocalDateTime fechaEvento) {
        return controlDevolucion.tiempoValidoDevolucion(fechaEvento);
    }
}
