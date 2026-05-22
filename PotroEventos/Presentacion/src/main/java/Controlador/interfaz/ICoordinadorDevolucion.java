/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Controlador.interfaz;

import dtos.DevolucionDTO;
import dtos.ENUMS.TipoDevolucionN;
import dtos.EventoDTO;
import dtos.ReservacionDTO;

/**
 *
 * @author maria
 */
public interface ICoordinadorDevolucion {

    /*
    esto es para ver la info del evento q se quiere cancelar
     */
    void abrirMostrarEventoCancelar(ReservacionDTO reservacion);

    /*
    esto es para abrir el panel de formulario de cancelacion
     */
    void mostrarDevolucion(ReservacionDTO reservacion);

    /*
    esto ya son operaciones de negocio, es ir al subsistema y cancelar reservacion gratis
     */
    boolean cancelarReservacionGratuita(ReservacionDTO reservacion, DevolucionDTO devolucion);

    /*
    esto ya son operaciones de negocio, es ir al subsistema y cancelar reservación de paga
     */
    boolean cancelarReservacionPaga(ReservacionDTO reservacion, TipoDevolucionN tipo, DevolucionDTO devolucion);

    /*
    abrir el panel de consultar
     */
    void abrirConsultar(String tipoEvento);


    /*
    chamba interna donde cuida que el evento que se quiere cancelar no esté a 
    menos de 48 horas de ocurrir
     */
    boolean validarTiempo(EventoDTO evento);

    /*
    solo es ir a la gestión evento y aumentar la capacidad
     */
    boolean aumentarCapacidadEvento(String idEvento);

    void inicioAplicacion();

}
