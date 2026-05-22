package objetosNegocio;

import Entitys.AsientoEvento;
import Entitys.Asistencia;
import Entitys.Boleto;
import adapters.AsientoEventoAdapter;
import adapters.AsistenciaAdapter;
import adapters.BoletoAdapter;
import adapters.DevolucionAdapter;
import adapters.ReservacionAdapter;
import daos.ReservacionDAO;
import dtos.AsientoEventoDTO;
import dtos.AsistenciaDTO;
import dtos.BoletoDTO;
import dtos.DevolucionDTO;
import dtos.ReporteAsistenciaDTO;
import dtos.ReservacionDTO;
import entidadesmongo.ReporteAsistencia;
import excepciones.NegocioException;
import excepciones.PersistenciaException;
import interfaces.IReservacionBO;
import interfaces.IReservacionDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maria
 */
public class ReservacionBO implements IReservacionBO {

    private static ReservacionBO instance;
    private final IReservacionDAO reservacionDAO = ReservacionDAO.getInstance();

    private ReservacionBO() {
    }

    public static ReservacionBO getInstance() {
        if (instance == null) {
            instance = new ReservacionBO();
        }
        return instance;
    }

    @Override
    public boolean agregarReservacion(ReservacionDTO reservacion) throws NegocioException {
        try {
            if (!validarDatos(reservacion)) {
                throw new NegocioException("Reservación inválida.");
            }
            return reservacionDAO.guardarReservacion(ReservacionAdapter.dtoAEntidad(reservacion));
        } catch (PersistenciaException ex) {
            throw new NegocioException(ex.getMessage());
        }
    }

    @Override
    public List<ReservacionDTO> obtenerReservacionesUsuario(String idUsuario) throws NegocioException {
        try {
            if (idUsuario == null) {
                throw new NegocioException("ID usuario inválido.");
            }
            return ReservacionAdapter.listaDTOs(reservacionDAO.obtenerReservacionesUsuario(idUsuario));
        } catch (PersistenciaException ex) {
            throw new NegocioException(ex.getMessage());
        }
    }

    private boolean validarDatos(ReservacionDTO r) {

        if (r == null) {
            return false;
        }

        if (r.getBoleto() == null) {
            return false;
        }

        if (r.getEstado() == null) {
            return false;
        }

        if (r.getFechaHora() == null) {
            return false;
        }

        if (r.getTotal() == null) {
            return false;
        }

        if (r.getUsuario() == null) {
            return false;
        }

        return true;
    }

    private boolean validarDevolucion(DevolucionDTO devolucion) {

        if (devolucion == null) {
            return false;
        }

        if (devolucion.getMotivo() == null) {
            return false;
        }

        if (devolucion.getFechaHoraDevolucion() == null) {
            devolucion.setFechaHoraDevolucion(LocalDateTime.now());
        }

        return true;
    }

    @Override
    public boolean cancelarReservacion(DevolucionDTO devolucion, String idReservacion) throws NegocioException {
        try {
            if (!validarDevolucion(devolucion)) {
                throw new NegocioException("La devolución no es válida por falta de información.");
            }
            return reservacionDAO.cancelarReservacion(DevolucionAdapter.convertirAEntidad(devolucion), idReservacion);
        } catch (PersistenciaException pe) {
            throw new NegocioException(pe.getMessage());
        }
    }

    /**
     * Busca un boleto mediante su token.
     *
     * @param token Token del boleto.
     *
     * @return Boleto encontrado.
     *
     * @throws NegocioException Se lanza cuando ocurre un error.
     */
    @Override
    public BoletoDTO buscarPorToken(String token) throws NegocioException {

        if (token == null) {
            throw new NegocioException("El token no puede ser nulo.");
        }

        try {

            Boleto boleto = reservacionDAO.buscarPorToken(token);

            return BoletoAdapter.entidadADTO(boleto);

        } catch (PersistenciaException e) {

            throw new NegocioException(e.getMessage());
        }
    }

    /**
     * Actualiza el estado de un boleto.
     *
     * @param boletoDTO Boleto a actualizar.
     *
     * @return true si se actualizó correctamente.
     *
     * @throws NegocioException Se lanza cuando ocurre un error.
     */
    @Override
    public boolean actualizarEstado(BoletoDTO boletoDTO) throws NegocioException {

        if (boletoDTO == null) {
            throw new NegocioException("El boleto no puede ser nulo.");
        }

        try {

            Boleto boleto = BoletoAdapter.dtoAEntidad(boletoDTO);

            return reservacionDAO.actualizarEstado(boleto);

        } catch (PersistenciaException e) {

            throw new NegocioException(e.getMessage());
        }
    }

    /**
     * Registra la asistencia de un boleto.
     *
     * @param boletoDTO Boleto al que se registrará asistencia.
     *
     * @param asistenciaDTO Información de asistencia.
     *
     * @return Asistencia registrada.
     *
     * @throws NegocioException Se lanza cuando ocurre un error.
     */
    @Override
    public AsistenciaDTO registrarAsistencia(BoletoDTO boletoDTO, AsistenciaDTO asistenciaDTO) throws NegocioException {

        if (boletoDTO == null || asistenciaDTO == null) {
            throw new NegocioException("Los parámetros no pueden ser nulos.");
        }

        try {

            Boleto boleto = BoletoAdapter.dtoAEntidad(boletoDTO);

            Asistencia asistencia = AsistenciaAdapter.convertirAsistenciaEntidad(asistenciaDTO);

            Asistencia asistenciaRegistrada = reservacionDAO.registrarAsistencia(boleto, asistencia);

            return AsistenciaAdapter.convertirAsistenciaDTO(asistenciaRegistrada);

        } catch (PersistenciaException e) {

            throw new NegocioException(e.getMessage());
        }
    }

    /**
     * Obtiene el resumen de boletos de un evento de manera estructurada.
     *
     * @param idEvento ID del evento.
     *
     * @return Objeto ReporteAsistencia con los contadores calculados.
     *
     * @throws NegocioException Se lanza cuando ocurre un error en la capa
     * inferior.
     */
    @Override
    public ReporteAsistenciaDTO obtenerResumenBoletosEvento(String idEvento) throws NegocioException {

        if (idEvento == null || idEvento.isBlank()) {
            throw new NegocioException("El id del evento no puede ser nulo o vacío.");
        }

        ReporteAsistencia dominio = reservacionDAO.obtenerReporteAsistencia(idEvento);
        return new ReporteAsistenciaDTO(dominio.getAsistidos(), dominio.getPendientes());
    }

    /**
     * Obtiene los asientos con asistencia registrada.
     *
     * @param idEvento ID del evento.
     *
     * @return Lista de asientos.
     *
     * @throws NegocioException Se lanza cuando ocurre un error.
     */
    @Override
    public List<AsientoEventoDTO> obtenerAsientosConAsistencia(String idEvento) throws NegocioException {

        if (idEvento == null) {
            throw new NegocioException("El id del evento no puede ser nulo.");
        }

        try {

            List<AsientoEvento> asientos = reservacionDAO.obtenerAsientosConAsistencia(idEvento);

            List<AsientoEventoDTO> asientosDTO = new ArrayList<>();

            for (AsientoEvento asiento : asientos) {
                asientosDTO.add(AsientoEventoAdapter.entidadADTO(asiento));
            }

            return asientosDTO;

        } catch (PersistenciaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    
    @Override
    public boolean obtenerReservacion(String idReservacion) throws NegocioException {
        try{
            if(idReservacion == null){
                throw new NegocioException("Id reserva inválido");
            }
            return reservacionDAO.tieneFactura(idReservacion);
        }catch(PersistenciaException ex){
            throw new NegocioException(ex.getMessage());
        }
    }
}
