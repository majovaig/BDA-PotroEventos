package daos;

import Entitys.AsientoEvento;
import Entitys.Asistencia;
import Entitys.Boleto;
import Entitys.Devolucion;
import Entitys.ENUMS.EstadoBoleto;
import Entitys.Reservacion;
import adaptadores.BoletoPersistenciaAdapter;
import adaptadores.ReservacionPersistenciaAdapter;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import conexion.ConexionMongo;
import entidadesmongo.AsistenciaMongoEntidad;
import entidadesmongo.ReporteAsistencia;
import entidadesmongo.ReservacionMongoEntidad;
import entidadesresumenmongo.EmpleadoResumenMongo;
import excepciones.PersistenciaException;
import interfaces.IReservacionDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * DAO para la gestión de Reservaciones en MongoDB. Combina soporte para eventos
 * regulares, eventos gratuitos sin asientos y cancelaciones.
 */
public class ReservacionDAO implements IReservacionDAO {

    private MongoCollection<ReservacionMongoEntidad> coleccionReservaciones = ConexionMongo.obtenerColeccionReservaciones();
    private static ReservacionDAO instance;
    private static final Logger LOG = Logger.getLogger(ReservacionDAO.class.getName());

    private ReservacionDAO() {
        coleccionReservaciones.createIndex(
                Indexes.ascending("boleto.codigoQR"),
                new IndexOptions().unique(true)
        );
        LOG.info("ReservacionDAO inicializado e índices verificados.");
    }

    public static ReservacionDAO getInstance() {
        if (instance == null) {
            instance = new ReservacionDAO();
        }
        return instance;
    }

    @Override
    public boolean guardarReservacion(Reservacion reservacion) throws PersistenciaException {
        LOG.info("Iniciando proceso: guardarReservacion.");
        if (reservacion == null) {
            LOG.warning("Se intentó guardar una reservación nula.");
            throw new PersistenciaException("La reservacion no puede ser nula.");
        }

        try {
            ReservacionMongoEntidad r = ReservacionPersistenciaAdapter.convertirAMongo(reservacion);
            InsertOneResult resultado = this.coleccionReservaciones.insertOne(r);

            if (resultado.getInsertedId() == null) {
                LOG.severe("Fallo en MongoDB: No se generó un InsertedId para la reservación.");
                throw new PersistenciaException("Error al guardar.");
            }

            r.setId(resultado.getInsertedId().asObjectId().getValue());
            LOG.info("Reservación guardada exitosamente. Nuevo ID generado: " + r.getId().toHexString());

            return resultado.wasAcknowledged();
        } catch (MongoException e) {
            LOG.log(Level.SEVERE, "Excepción en MongoDB al guardar reservación: {0}", e.getMessage());
            throw new PersistenciaException("No fue posible guardar la reservación: " + e.getMessage());
        }

    }

    @Override
    public List<Reservacion> obtenerReservacionesUsuario(String idUsuario) throws PersistenciaException {
        LOG.info("Iniciando consulta: obtenerReservacionesUsuario para el Usuario ID: " + idUsuario);
        try {
            List<Document> reservaciones = coleccionReservaciones
                .withDocumentClass(Document.class)
                .aggregate(Arrays.asList(
                        Aggregates.match(Filters.eq("usuario._id", new ObjectId(idUsuario))),
                        Aggregates.lookup("usuarios", "usuario._id", "_id", "usuario"),
                        Aggregates.unwind("$usuario", new UnwindOptions().preserveNullAndEmptyArrays(true)),
                        Aggregates.lookup("eventos", "boleto.evento._id", "_id", "evento_temp"),
                        Aggregates.unwind("$evento_temp", new UnwindOptions().preserveNullAndEmptyArrays(true)),
                        Aggregates.addFields(new Field<>("boleto.evento", "$evento_temp")),
                        Aggregates.project(Projections.exclude("evento_temp")),
                        Aggregates.lookup("ubicaciones", "boleto.evento.ubicacion._id", "_id", "ubicacion_temp"),
                        Aggregates.unwind("$ubicacion_temp", new UnwindOptions().preserveNullAndEmptyArrays(true)),
                        Aggregates.addFields(new Field<>("boleto.evento.ubicacion", "$ubicacion_temp")),
                        Aggregates.project(Projections.exclude("ubicacion_temp"))
                )).into(new ArrayList<>());

            LOG.info("Consulta éxitosa. Reservaciones encontradas: " + reservaciones.size());
            return ReservacionPersistenciaAdapter.convertirListaADominio(reservaciones);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error durante la agregación de reservaciones para el usuario {0}: {1}", new Object[]{idUsuario, e.getMessage()});
            throw new PersistenciaException("No fue posible obtener las reservaciones");
        }
    }

    @Override
    public Boleto obtenerBoleto(String idReservacion) throws PersistenciaException {
        LOG.info("Buscando boleto perteneciente a la Reservación ID: " + idReservacion);
        try {
            Document reservacion = coleccionReservaciones
                    .withDocumentClass(Document.class)
                    .aggregate(Arrays.asList(
                            Aggregates.match(Filters.eq("_id", new ObjectId(idReservacion))),
                            Aggregates.addFields(new Field<>("id_evento_busqueda",
                                    new Document("$ifNull", Arrays.asList("$boleto.asiento.idAsientoEvento", "$evento"))
                            )),
                            Aggregates.lookup("eventos", "id_evento_busqueda", "_id", "boleto.evento"),
                            Aggregates.unwind("$boleto.evento", new UnwindOptions().preserveNullAndEmptyArrays(true)),
                            Aggregates.lookup("asientos", "boleto.asiento.idAsientoEvento", "_id", "boleto.asiento_lookup"),
                            Aggregates.unwind("$boleto.asiento_lookup", new UnwindOptions().preserveNullAndEmptyArrays(true)),
                            Aggregates.project(Projections.exclude("id_evento_busqueda"))
                    )).first();

            if (reservacion == null) {
                LOG.warning("No se encontró ninguna reservación con el ID: " + idReservacion);
                return null;
            }

            LOG.info("Boleto extraído correctamente de la reservación ID: " + idReservacion);
            return BoletoPersistenciaAdapter.convertirADominio((Document) reservacion.get("boleto"));
        } catch (MongoException e) {
            LOG.log(Level.SEVERE, "Error al extraer boleto de reservación {0}: {1}", new Object[]{idReservacion, e.getMessage()});
            throw new PersistenciaException("No fue posible obtener el boleto.");
        }
    }

    @Override
    public Boleto buscarPorToken(String token) throws PersistenciaException {
        LOG.info("Buscando boleto mediante Token: " + token);
        if (token == null || token.isBlank()) {
            LOG.warning("Intento de búsqueda con un token nulo o vacío.");
            throw new PersistenciaException("El token es requerido.");
        }
        try {
            Document reservacion = coleccionReservaciones
                    .withDocumentClass(Document.class)
                    .find(eq("boleto.token", token)).first();

            if (reservacion == null) {
                LOG.info("Búsqueda finalizada. No se encontró boleto asociado al token: " + token);
                return null;
            }

            LOG.info("Boleto encontrado éxitosamente para el token especificado.");
            return BoletoPersistenciaAdapter.convertirADominio(reservacion.get("boleto", Document.class));
        } catch (MongoException e) {
            LOG.log(Level.SEVERE, "Excepción en MongoDB al buscar por token {0}: {1}", new Object[]{token, e.getMessage()});
            throw new PersistenciaException("No fue posible buscar el boleto.");
        }
    }

    @Override
    public boolean actualizarEstado(Boleto boleto) throws PersistenciaException {
        LOG.info("Intentando actualizar estado del boleto.");
        if (boleto == null || boleto.getToken() == null) {
            LOG.warning("Se recibieron datos inválidos o boleto nulo para la actualización de estado.");
            throw new PersistenciaException("Datos inválidos.");
        }

        LOG.info(String.format("Token objetivo: %s | Nuevo Estado: %s", boleto.getToken(), boleto.getEstadoBoleto().name()));

        try {
            UpdateResult resultado = coleccionReservaciones.updateOne(
                    eq("boleto.token", boleto.getToken()),
                    Updates.set("boleto.estado", boleto.getEstadoBoleto().name())
            );

            boolean modificado = resultado.getModifiedCount() > 0;
            if (modificado) {
                LOG.info("Estado del boleto actualizado exitosamente.");
            } else {
                LOG.warning("No se actualizó ningún documento (Token posiblemente no encontrado o estado idéntico).");
            }
            return modificado;
        } catch (MongoException e) {
            LOG.log(Level.SEVERE, "Error al actualizar estado del boleto token {0}: {1}", new Object[]{boleto.getToken(), e.getMessage()});
            throw new PersistenciaException("No fue posible actualizar el boleto.");
        }
    }

    @Override
    public Asistencia registrarAsistencia(Boleto boleto, Asistencia asistencia) throws PersistenciaException {
        LOG.info("Proceso de registro de asistencia iniciado.");
        if (boleto == null || asistencia == null) {
            LOG.warning("Intento fallido: Boleto o Asistencia nulos.");
            throw new PersistenciaException("Argumentos requeridos nulos.");
        }

        LOG.info(String.format("Procesando acceso para Boleto Token: %s | Empleado Autorizador ID: %s",
                boleto.getToken(), asistencia.getEmpleado().getIdEmpleado()));

        try {
            if (EstadoBoleto.USADO == boleto.getEstadoBoleto() || boleto.getAsistencia() != null) {
                LOG.warning("Rechazado: El boleto Token " + boleto.getToken() + " ya registra un acceso previo (Estado USADO).");
                throw new PersistenciaException("El boleto ya fue procesado con anterioridad.");
            }

            AsistenciaMongoEntidad asistenciaMongo = new AsistenciaMongoEntidad();
            asistenciaMongo.setEmpleado(new EmpleadoResumenMongo(new ObjectId(asistencia.getEmpleado().getIdEmpleado())));
            asistenciaMongo.setFechaHoraRegistro(LocalDateTime.now());

            Bson actualizacion = Updates.combine(
                    Updates.set("boleto.estado", EstadoBoleto.USADO.name()),
                    Updates.set("boleto.asistencia", asistenciaMongo)
            );

            UpdateResult resultado = coleccionReservaciones.updateOne(
                    eq("boleto.token", boleto.getToken()),
                    actualizacion
            );

            if (resultado.getModifiedCount() == 0) {
                LOG.severe("La actualización en MongoDB reportó 0 documentos modificados para el Token: " + boleto.getToken());
                throw new PersistenciaException("No se pudo registrar la asistencia.");
            }

            LOG.info("Asistencia registrada y boleto quemado exitosamente. Accesso Concedido.");
            return asistencia;
        } catch (MongoException e) {
            LOG.log(Level.SEVERE, "Error en Base de Datos al registrar asistencia para token {0}: {1}", new Object[]{boleto.getToken(), e.getMessage()});
            throw new PersistenciaException("Error en base de datos al registrar asistencia.");
        }
    }

    @Override
    public ReporteAsistencia obtenerReporteAsistencia(String idEvento) {
        LOG.info("Generando reporte de asistencia para el Evento ID: " + idEvento);
        try {
            List<Bson> pipeline = Arrays.asList(
                    Aggregates.match(Filters.and(
                            Filters.eq("estado", "ACTIVA"),
                            Filters.eq("boleto.evento._id", new ObjectId(idEvento))
                    )),
                    Aggregates.facet(
                            new Facet("asistidos",
                                    Aggregates.match(Filters.eq("boleto.estado", "USADO")),
                                    Aggregates.count("total")
                            ),
                            new Facet("pendientes",
                                    Aggregates.match(Filters.eq("boleto.estado", "ACTIVO")),
                                    Aggregates.count("total")
                            )
                    )
            );

            Document resultado = coleccionReservaciones
                    .withDocumentClass(Document.class)
                    .aggregate(pipeline)
                    .first();

            long asistidos = 0;
            long pendientes = 0;

            if (resultado != null) {
                List<Document> listaAsistidos = resultado.getList("asistidos", Document.class);
                List<Document> listaPendientes = resultado.getList("pendientes", Document.class);

                if (listaAsistidos != null && !listaAsistidos.isEmpty()) {
                    asistidos = listaAsistidos.get(0).getInteger("total", 0);
                }
                if (listaPendientes != null && !listaPendientes.isEmpty()) {
                    pendientes = listaPendientes.get(0).getInteger("total", 0);
                }
            }

            LOG.info(String.format("Reporte generado — Asistidos: %d | Pendientes: %d", asistidos, pendientes));
            return new ReporteAsistencia(asistidos, pendientes);

        } catch (MongoException e) {
            LOG.log(Level.SEVERE, "Error al generar reporte de asistencia para evento {0}: {1}",
                    new Object[]{idEvento, e.getMessage()});
            return new ReporteAsistencia(0, 0);
        }
    }

    @Override
    public List<AsientoEvento> obtenerAsientosConAsistencia(String idEvento) throws PersistenciaException {
        if (idEvento == null || idEvento.isBlank()) {
            throw new PersistenciaException("El id del evento es requerido.");
        }
        if (!ObjectId.isValid(idEvento)) {
            throw new PersistenciaException("El id del evento no tiene formato válido.");
        }
        LOG.info("Recuperando listado de asientos con asistencia confirmada (USADO) para el Evento ID: " + idEvento);
        try {
            Bson filtro = Filters.and(
                    Filters.eq("boleto.evento._id", new ObjectId(idEvento)),
                    Filters.eq("boleto.estado", "USADO")
            );

            List<ReservacionMongoEntidad> reservaciones = coleccionReservaciones.find(filtro).into(new ArrayList<>());
            List<AsientoEvento> asientos = new ArrayList<>();

            for (ReservacionMongoEntidad res : reservaciones) {
                if (res == null || res.getBoleto() == null) {
                    continue;
                }

                AsientoEvento asiento = new AsientoEvento();

                ObjectId idAsientoEvento = res.getBoleto().getAsiento() != null
                        ? res.getBoleto().getAsiento().getIdAsientoEvento()
                        : null;

                if (idAsientoEvento != null) {
                    asiento.setIdAsientoEvento(idAsientoEvento.toHexString());
                } else {
                    // Eventos gratuitos sin asiento asignado
                    asiento.setIdAsientoEvento("GEN-GRATIS-" + res.getIdComoTexto());
                }

                asientos.add(asiento);
            }

            LOG.info(String.format("Proceso completado. Se extrajeron %d asientos asistidos para el evento.", asientos.size()));
            return asientos;
        } catch (MongoException e) {
            LOG.log(Level.SEVERE, "Error al extraer asientos asignados para el evento {0}: {1}", new Object[]{idEvento, e.getMessage()});
            throw new PersistenciaException("Error al extraer asientos asignados.");
        }
    }

    @Override
    public boolean cancelarReservacion(Devolucion devolucion, String idReservacion) throws PersistenciaException {
        LOG.info("Iniciando proceso: cancelarReservacion para la Reservación ID: " + idReservacion);
        try {
            Bson filtro = Filters.eq("_id", new ObjectId(idReservacion));
            Bson actualizacion = Updates.combine(
                    Updates.set("estado", "CANCELADA"),
                    Updates.set("boleto.estado", "CANCELADO"),
                    Updates.set("devolucion", devolucion)
            );

            UpdateResult resultado = coleccionReservaciones.updateOne(filtro, actualizacion);
            boolean modificado = resultado.getModifiedCount() > 0;

            if (modificado) {
                LOG.info("Reservación cancelada exitosamente.");
            } else {
                LOG.warning("No se modificó ningún documento. Es posible que el ID no exista o ya se encuentre cancelada.");
            }

            return modificado;
        } catch (MongoException me) {
            LOG.log(Level.SEVERE, "Excepción en MongoDB al intentar cancelar la reservación {0}: {1}", new Object[]{idReservacion, me.getMessage()});
            throw new PersistenciaException("No fue posible cancelar la reservación.");
        }
    }
    // --- caso factura ---

    /**
     * Cuenta si la reserva cumple con el requisito de:
     * - ser igual al id que se proporciona
     * - tenga una columna de idFactura existiendo
     * - que el campo no tenga valor nulo
     * @param idReservacion
     * @return verdadero si tiene factura, falso si no ha sido facturada
     * @throws PersistenciaException 
     */
    @Override
    public boolean tieneFactura(String idReservacion) throws PersistenciaException {
        try{
            
            if(idReservacion == null){
                throw new MongoException("Reserva inválida");
            }
            
            // cuenta si esa reserva cumple con el filtro
            Long count = coleccionReservaciones
            .countDocuments(Filters.and(
                    Filters.eq("_id", new ObjectId(idReservacion)),
                    Filters.exists("idFactura", true),
                    Filters.ne("idFactura", null)
            ));

            return count > 0; 
        }catch(MongoException me){
            throw new MongoException("Hubo un error al comprobar la reserva.");
        }
        
                
    }
    @Override
    public boolean asociarFactura(String idReservacion, String idFactura) throws PersistenciaException {
        try {
            UpdateResult resultado = coleccionReservaciones.updateOne(
                Filters.eq("_id", new ObjectId(idReservacion)),
                Updates.set("idFactura", new ObjectId(idFactura))
            );

            if (resultado.getMatchedCount() == 0) {
                throw new PersistenciaException("No se encontró la reservación con ID: " + idReservacion);
            }
            return true;
        } catch (MongoException e) {
            throw new PersistenciaException("Error al asociar factura a reservación: " + e.getMessage());
        }
    }
}
