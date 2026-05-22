package compraBoleto;

import dtos.*;
import objetosNegocio.*;
import excepciones.CompraBoletoException;
import excepciones.NegocioException;
import excepciones.PagoException;
import itson.FachadaITSON;
import interfaces.IAsientoBO;
import interfaces.IAsientoEventoBO;
import interfaces.IReservacionBO;
import interfaces.ISeccionBO;
import interfaces.IUsuarioBO;
import itson.IITSON;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import pago.IPago;
import pago.PagoFachada;

/**
 * Controlador de caso de uso para la compra de boletos. Orquesta la
 * comunicación con los BOs y transforma Entidades a DTOs.
 *
 * @author Kaleb
 */
public class ControlCompraBoleto{

    private final ISeccionBO seccionBO;
    private final IAsientoBO asientoBO;
    private final IAsientoEventoBO asientoEventoBO;
    private final IReservacionBO reservacionBO;
    private final IUsuarioBO usuarioBO;
    private final IPago controlPago;
    private final IITSON controlItson;

    /**
     * Lista de asientos pendientes de pago
     */
    private List<AsientoEventoDTO> asientosPendientesCompra;

    /**
     * Total pendiente en centavos
     */
    private Long totalPendienteCompra;
    private static ControlCompraBoleto instance;
    /**
     * Constructor que inicializa dependencias y estado interno.
     */
    public ControlCompraBoleto() {
        this.seccionBO = SeccionBO.getInstance();
        this.asientoBO = AsientoBO.getInstance();
        this.asientoEventoBO = AsientoEventoBO.getInstance();
        this.reservacionBO = ReservacionBO.getInstance();
        this.usuarioBO = UsuarioBO.getInstance();
        this.controlPago = new PagoFachada();
        this.controlItson = new FachadaITSON();

        this.asientosPendientesCompra = new ArrayList<>();
        this.totalPendienteCompra = 0L;
    }
    
    public static ControlCompraBoleto getInstance(){
        if(instance == null){
            instance = new ControlCompraBoleto();
        }
        return instance;
    }
    /** 
     * Obtiene las secciones de un evento.
     *
     * @param idEvento identificador del evento
     * @return lista de secciones
     * @throws CompraBoletoException si ocurre un error
     */
    protected List<SeccionDTO> obtenerSeccionesEvento(String idEvento) throws CompraBoletoException {
        try {
            return seccionBO.consultarSeccionesPorEvento(idEvento);
        } catch (Exception ex) {
            throw new CompraBoletoException("Error al cargar las secciones: " + ex.getMessage());
        }
    }

    /**
     * Obtiene la ocupación de los asientos de un evento.
     *
     * @param idEvento identificador del evento
     * @return lista de estados de asientos
     * @throws CompraBoletoException si ocurre un error
     */
    protected List<AsientoEventoDTO> obtenerOcupacionEvento(String idEvento) throws CompraBoletoException {
        try {
            return asientoEventoBO.consultarEstadosPorEvento(idEvento);
        } catch (Exception ex) {
            throw new CompraBoletoException("Error al cargar la ocupación del evento: " + ex.getMessage());
        }
    }

    /**
     * Obtiene el catálogo de asientos físicos.
     *
     * @return lista de asientos
     * @throws CompraBoletoException si ocurre un error
     */
    protected List<AsientoDTO> obtenerCatalogoAsientos() throws CompraBoletoException {
        try {
            List<AsientoDTO> asientos = asientoBO.consultarAsientos();

            return asientos.stream().map(a -> new AsientoDTO(
                    a.getIdAsiento(),
                    a.getFila(),
                    a.getNumero(),
                    a.getUbicacion(),
                    a.getSeccion()
            )).collect(Collectors.toList());

        } catch (Exception ex) {
            throw new CompraBoletoException("Error al cargar el catálogo de asientos: " + ex.getMessage());
        }
    }

    /**
     * Construye un mapa de ocupación agrupado por sección.
     *
     * @param idEvento identificador del evento
     * @return mapa de secciones con sus asientos
     * @throws CompraBoletoException si ocurre un error
     */
    protected Map<SeccionDTO, List<AsientoEventoDTO>> obtenerMapaOcupacion(EventoDTO evento) throws CompraBoletoException {
        try {
            if (evento == null || evento.getUbicacion() == null) {
                throw new CompraBoletoException("El evento y/o su ubicación no puede ser nula.");
            }
            List<SeccionDTO> secciones = evento.getUbicacion().getSecciones();
            List<AsientoEventoDTO> ocupacion = asientoEventoBO.consultarEstadosPorEvento(evento.getIdEvento());

            Map<SeccionDTO, List<AsientoEventoDTO>> mapa = new HashMap<>();
            for (SeccionDTO seccion : secciones) {
                List<AsientoEventoDTO> asientosDeSeccion = ocupacion.stream()
                        .filter(ae -> ae.getAsiento() != null
                        && ae.getAsiento().getSeccion() != null
                        && String.valueOf(ae.getAsiento().getSeccion().getIdSeccion()).equals(seccion.getIdSeccion()))
                        .collect(Collectors.toList());
                mapa.put(seccion, asientosDeSeccion);
            }
            return mapa;

        } catch (Exception e) {
            throw new CompraBoletoException("Error al construir mapa de ocupación: " + e.getMessage());
        }
    }

    /**
     * Agrega una reservación al sistema.
     *
     * @param reservacion reservación a guardar.
     * @return sí se realizó la operación o no.
     * @throws CompraBoletoException si ocurre un error
     */
    protected boolean agregarReservacion(ReservacionDTO reservacion) throws CompraBoletoException {
        try {
            return reservacionBO.agregarReservacion(reservacion);
        } catch (NegocioException ex) {
            throw new CompraBoletoException("Error al agregar la reservación: " + ex.getMessage());
        }
    }

    /**
     * Genera un código QR para un boleto.
     *
     * @param evento evento asociado
     * @param asiento asiento del boleto
     * @param token token del boleto
     * @return ruta del archivo QR generado
     * @throws CompraBoletoException si ocurre un error
     */
    protected String generarCodigoQR(EventoDTO evento, AsientoEventoDTO asiento, String token) throws CompraBoletoException {
        try {
            String asientoID = null;
            String identificador;

            if (asiento == null || asiento.getIdAsientoEvento() == null) {

                identificador = UUID.randomUUID().toString();

            } else {

                asientoID = asiento.getAsiento().getIdAsiento();
                identificador = asiento.getAsiento().getIdAsiento();
            }

            String datos = token;

            String ruta = "/src/main/resources/qrs-boletos";
            Path directorioDestino = Paths.get("src", "main", "resources", "qrs-boletos");
            if (!Files.exists(directorioDestino)) {
                Files.createDirectories(directorioDestino);
            }

            File temp = QRCode.from(datos).to(ImageType.PNG).withSize(300, 300).file();
            String nombreArchivo = "Boleto_" + identificador + "_" + evento.getIdEvento() + ".png";
            Path rutaDestino = directorioDestino.resolve(nombreArchivo);
            Files.copy(temp.toPath(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
            return "/" + rutaDestino.toString().replace("\\", "/");
        } catch (Exception e) {
            throw new CompraBoletoException("Error al generar QR: " + e.getMessage());
        }
    }

    /**
     * Reserva un asiento.
     *
     * @param idAsientoEvento identificador del asiento
     * @return true si se reservó
     * @throws CompraBoletoException si ocurre un error
     */
    protected boolean reservarAsiento(String idAsientoEvento) throws CompraBoletoException {
        try {
            return asientoEventoBO.reservarAsiento(idAsientoEvento);
        } catch (NegocioException e) {
            throw new CompraBoletoException(e.getMessage());
        }
    }

    /**
     * Libera un asiento reservado.
     *
     * @param idAsientoEvento identificador del asiento
     * @return true si se liberó
     * @throws CompraBoletoException si ocurre un error
     */
    protected boolean liberarAsiento(String idAsientoEvento) throws CompraBoletoException {
        try {
            return asientoEventoBO.liberarAsiento(idAsientoEvento);
        } catch (NegocioException e) {
            throw new CompraBoletoException(e.getMessage());
        }
    }

    /**
     * Maneja la lógica de venta de asientos.
     */
    protected boolean venderAsientos(List<AsientoEventoDTO> asientosSeleccionados, Long totalCompra, boolean gratuito, ReservacionDTO reservacion) throws CompraBoletoException {
        try {
            if (reservacion == null) {
                return false;
            }

            if (gratuito) {
                reservacionBO.agregarReservacion(reservacion);
                return true;
            }
            
            if (reservacion.getPago() != null) {
                System.out.println((int) (totalCompra / 100.0 * 2));
                if (usuarioBO.restarCreditos((int) (totalCompra / 100.0 * 2), reservacion.getUsuario().getIdUsuario())) {
                    asientoEventoBO.venderAsiento(reservacion.getBoleto().getAsiento().getIdAsientoEvento());
                    reservacionBO.agregarReservacion(reservacion);
                    return true;
                }
                return false;
            }
            
            this.asientosPendientesCompra = new ArrayList<>(asientosSeleccionados);
            this.totalPendienteCompra = totalCompra;

            return true;

        } catch (NegocioException e) {
            throw new CompraBoletoException(e.getMessage());
        }
    }

    /**
     * Realiza el pago de la compra pendiente.
     */
    protected PagoDTO realizarCompra(TarjetaDTO tarjeta, CobroDTO cobro) throws CompraBoletoException {
        try {
            PagoDTO pago = controlPago.procesarPago(tarjeta, cobro);

            if (pago != null) {
                for (AsientoEventoDTO asiento : asientosPendientesCompra) {
                    asientoEventoBO.venderAsiento(asiento.getIdAsientoEvento());
                }

                asientosPendientesCompra.clear();
                totalPendienteCompra = 0L;

                return pago;
            }

        } catch (NegocioException | PagoException ex) {
            throw new CompraBoletoException(ex.getMessage());
        }

        return null;
    }

    /**
     * Obtiene el total pendiente de pago.
     *
     * @return total en centavos
     */
    protected Long getTotalPendiente() {
        return totalPendienteCompra;
    }

    /**
     * Valida que un usuario si sea perteneciente a ITSON
     *
     * @param usuario El DTO con la información para validar al usuario en el
     * subsistema ITSON.
     * @return true si sí es usuario ITSON, false de lo contrario.
     */
    protected boolean validarCredencialesITSON(UsuarioInstitucionalDTO usuario) {
        return controlItson.validarUsuarioITSON(usuario);
    }
}
