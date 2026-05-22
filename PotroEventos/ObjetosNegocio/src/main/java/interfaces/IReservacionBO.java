/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import dtos.AsientoEventoDTO;
import dtos.AsistenciaDTO;
import dtos.BoletoDTO;
import dtos.DevolucionDTO;
import dtos.ReporteAsistenciaDTO;
import dtos.ReservacionDTO;
import excepciones.NegocioException;
import java.util.List;

/**
 *
 * @author maria
 */
public interface IReservacionBO {

    boolean agregarReservacion(ReservacionDTO reservacion) throws NegocioException;

    List<ReservacionDTO> obtenerReservacionesUsuario(String idUsuario) throws NegocioException;

    boolean cancelarReservacion(DevolucionDTO devolucion, String idReservacion) throws NegocioException;

    /**
     * Busca un boleto mediante su token único.
     *
     * @param token Token del boleto.
     *
     * @return Boleto encontrado.
     *
     * @throws NegocioException Se lanza cuando ocurre un error en la búsqueda.
     */
    BoletoDTO buscarPorToken(String token) throws NegocioException;

    /**
     * Actualiza el estado de un boleto.
     *
     * @param boletoDTO Boleto a actualizar.
     *
     * @return true si la actualización fue exitosa.
     *
     * @throws NegocioException Se lanza cuando ocurre un error al actualizar.
     */
    boolean actualizarEstado(BoletoDTO boletoDTO) throws NegocioException;

    /**
     * Registra la asistencia de un boleto.
     *
     * @param boletoDTO Boleto al que se registrará asistencia.
     *
     * @param asistenciaDTO Información de asistencia.
     *
     * @return Asistencia registrada.
     *
     * @throws NegocioException Se lanza cuando ocurre un error al registrar.
     */
    AsistenciaDTO registrarAsistencia(BoletoDTO boletoDTO, AsistenciaDTO asistenciaDTO) throws NegocioException;

    /**
     * Obtiene el resumen de boletos de un evento.
     *
     * @param idEvento ID del evento.
     *
     * @return Resumen de boletos.
     *
     * @throws NegocioException Se lanza cuando ocurre un error.
     */
    public ReporteAsistenciaDTO obtenerResumenBoletosEvento(String idEvento) throws NegocioException;

    /**
     * Obtiene los asientos que cuentan con asistencia registrada.
     *
     * @param idEvento ID del evento.
     *
     * @return Lista de asientos con asistencia.
     *
     * @throws NegocioException Se lanza cuando ocurre un error.
     */
    List<AsientoEventoDTO> obtenerAsientosConAsistencia(String idEvento) throws NegocioException;

    boolean obtenerReservacion(String idReservacion) throws NegocioException;
}

