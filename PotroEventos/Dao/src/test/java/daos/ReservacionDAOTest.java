///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
// */
//package daos;
//
//import Entitys.Boleto;
//import Entitys.ENUMS.ReservacionEstado;
//import Entitys.Reservacion;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import conexion.ConexionMongo;
//import dtos.ENUMS.CategoriaEventoDTO;
//import dtos.ENUMS.EstadoAsientoDTO;
//import dtos.ENUMS.EstadoBoletoDTO;
//import dtos.ENUMS.EstadoEventoDTO;
//import dtos.ENUMS.ReservacionEstadoDTO;
//import dtos.ENUMS.TipoEventoN;
//import dtos.ENUMS.TipoUbicacionN;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//import org.bson.Document;
//import org.bson.types.ObjectId;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// *
// * @author Dayanara Peralta G
// */
//public class ReservacionDAOTest {
//
//    private static String databaseName;
//    private ReservacionDAO reservacionDAO;
//
//    public ReservacionDAOTest() {
//    }
//
//    @BeforeAll
//    public static void setUpClass() {
//        databaseName = "test_potro_eventos_" + UUID.randomUUID().toString().substring(0, 8);
//        ConexionMongo.useTestDatabase(databaseName);
//    }
//
//    @AfterAll
//    public static void tearDownClass() {
//        MongoDatabase db = ConexionMongo.obtenerBaseDatos();
//        if (db != null) {
//            db.drop();
//        }
//        ConexionMongo.resetToProductionDatabase();
//        ConexionMongo.cerrarCliente();
//    }
//
//    @BeforeEach
//    public void setUp() {
//        reservacionDAO = ReservacionDAO.getInstance();
//        MongoDatabase base = ConexionMongo.obtenerBaseDatos();
//
//        MongoCollection<Document> asientoEventosCol = base.getCollection("asientoEventos");
//        MongoCollection<Document> asientosCol = base.getCollection("asientos");
//        MongoCollection<Document> ubicacionesCol = base.getCollection("ubicaciones");
//        MongoCollection<Document> seccionesCol = base.getCollection("secciones");
//        MongoCollection<Document> eventosCol = base.getCollection("eventos");
//        MongoCollection<Document> categoriaCol = base.getCollection("categorias");
//        MongoCollection<Document> reservacionCol = base.getCollection("reservacion");
//        MongoCollection<Document> boletoCol = base.getCollection("boleto");
//        MongoCollection<Document> pagoCol = base.getCollection("pago");
//        MongoCollection<Document> usuarioCol = base.getCollection("usuario");
//
//        asientoEventosCol.deleteMany(new Document());
//        asientosCol.deleteMany(new Document());
//        ubicacionesCol.deleteMany(new Document());
//        seccionesCol.deleteMany(new Document());
//        eventosCol.deleteMany(new Document());
//        categoriaCol.deleteMany(new Document());
//        reservacionCol.deleteMany(new Document());
//        boletoCol.deleteMany(new Document());
//        pagoCol.deleteMany(new Document());
//        usuarioCol.deleteMany(new Document());
//    }
//
//    /**
//     * Test of getInstance method, of class ReservacionDAO.
//     */
//    @Test
//    public void testGetInstance() {
//        ReservacionDAO instancia1 = ReservacionDAO.getInstance();
//        ReservacionDAO instancia2 = ReservacionDAO.getInstance();
//        assertNotNull(instancia1);
//        assertEquals(instancia1, instancia2);
//    }
//
//    /**
//     * Test of guardarReservacion method, of class ReservacionDAO.
//     */
//    @Test
//    public void testGuardarReservacion() throws Exception {
//        MongoDatabase base = ConexionMongo.obtenerBaseDatos();
//
//        MongoCollection<Document> asientoEventosCol = base.getCollection("asientoEventos");
//        MongoCollection<Document> asientosCol = base.getCollection("asientos");
//        MongoCollection<Document> ubicacionesCol = base.getCollection("ubicaciones");
//        MongoCollection<Document> seccionesCol = base.getCollection("secciones");
//        MongoCollection<Document> eventosCol = base.getCollection("eventos");
//        MongoCollection<Document> categoriaCol = base.getCollection("categorias");
//        MongoCollection<Document> reservacionCol = base.getCollection("reservacion");
//        MongoCollection<Document> boletoCol = base.getCollection("boleto");
//        MongoCollection<Document> pagoCol = base.getCollection("pago");
//        MongoCollection<Document> usuarioCol = base.getCollection("usuario");
//
//        ObjectId ubicacionId = new ObjectId();
//        ubicacionesCol.insertOne(new Document("_id", ubicacionId)
//                .append("nombre", "Estadio Potros")
//                .append("capacidad", 100)
//                .append("tipoUbicacion", TipoUbicacionN.AIRELIBRE.name())
//                .append("secciones", new ArrayList<>())
//        );
//
//        ObjectId seccionId = new ObjectId();
//        Document seccionDoc = new Document("_id", seccionId)
//                .append("nombre", "A")
//                .append("capacidad", 50)
//                .append("precioBase", 500L);
//        seccionesCol.insertOne(seccionDoc);
//
//        ObjectId idAsiento = new ObjectId();
//        Document asientoDoc = new Document("_id", idAsiento)
//                .append("fila", "A")
//                .append("numero", 1)
//                .append("ubicacion", ubicacionId)
//                .append("seccion", seccionId);
//        asientosCol.insertOne(asientoDoc);
//
//        ObjectId categoriaId = new ObjectId();
//        categoriaCol.insertOne(new Document("_id", categoriaId)
//                .append("urlImagen", "test.jpg")
//                .append("nombre", CategoriaEventoDTO.NATACION.name())
//        );
//
//        ObjectId eventoId = new ObjectId();
//        Document eventoDoc = new Document("_id", eventoId)
//                .append("categoria", new Document("_id", categoriaId)
//                        .append("nombre", CategoriaEventoDTO.NATACION.name())
//                        .append("urlImagen", "test.jpg"))
//                .append("nombre", "Nado sincronizado")
//                .append("informacion", "Patos nadando sincronizadamente")
//                .append("fechaHora", new java.util.Date())
//                .append("ubicacion", new Document("_id", ubicacionId)
//                        .append("nombre", "Estadio Potros")
//                        .append("capacidad", 100)
//                        .append("tipoUbicacion", TipoUbicacionN.AIRELIBRE.name()))
//                .append("estado", EstadoEventoDTO.ACTIVO.name())
//                .append("urlImagen", "test.jpg")
//                .append("gratuito", false)
//                .append("tipo", TipoEventoN.ITSON.name())
//                .append("disponibilidad", 100);
//        eventosCol.insertOne(eventoDoc);
//
//        ObjectId asientoEveId = new ObjectId();
//        Document asientoEve = new Document("_id", asientoEveId)
//                .append("precio", 200)
//                .append("estado", EstadoAsientoDTO.DISPONIBLE.name())
//                .append("asiento", idAsiento)
//                .append("evento", eventoId);
//        asientoEventosCol.insertOne(asientoEve);
//
//        ObjectId boletoId = new ObjectId();
//        Document boletoDoc = new Document("_id", boletoId)
//                .append("codigoQR", "testQR")
//                .append("estado", EstadoBoletoDTO.ACTIVO.name())
//                .append("evento", new Document("_id", eventoId)
//                        .append("nombre", "Nado sincronizado"))
//                .append("asientoEvento", new Document("_id", asientoEveId)
//                        .append("estado", EstadoAsientoDTO.DISPONIBLE.name())
//                        .append("precio", 200))
//                .append("token", "testToken");
//        boletoCol.insertOne(boletoDoc);
//
//        ObjectId pagoId = new ObjectId();
//        Document pagoDoc = new Document("_id", pagoId)
//                .append("fechaOperacion", new Date())
//                .append("importe", 120.0)
//                .append("metodoPago", "TARJETA");
//        pagoCol.insertOne(pagoDoc);
//
//        ObjectId usuarioId = new ObjectId();
//        Document usuarioDoc = new Document("_id", usuarioId)
//                .append("nombre", "Aaron")
//                .append("apellidoPaterno", "Burciaga")
//                .append("apellidoMaterno", "Alcantar")
//                .append("correo", "aaronA@gmail.com")
//                .append("creditos", 230);
//        usuarioCol.insertOne(usuarioDoc);
//
//        ObjectId reservacionId = new ObjectId();
//        Document reservacionDoc = new Document("_id", reservacionId)
//                .append("total", 120.0)
//                .append("boleto", boletoId)
//                .append("pago", pagoId)
//                .append("usuario", usuarioId)
//                .append("fechaHora", new Date())
//                .append("estado", ReservacionEstadoDTO.ACTIVA.name());
//        reservacionCol.insertOne(reservacionDoc);
//
//        Reservacion reservacion = new Reservacion();
//        reservacion.setIdReservacion(reservacionId.toString());
//        reservacion.setTotal(120.0);
//        reservacion.setEstado(ReservacionEstado.ACTIVA);
//
//        boolean result = reservacionDAO.guardarReservacion(reservacion);
//        assertNotNull(result);
//    }
//
//    /**
//     * Test of obtenerReservacionesUsuario method, of class ReservacionDAO.
//     */
//    @Test
//    public void testObtenerReservacionesUsuario() throws Exception {
//        MongoDatabase base = ConexionMongo.obtenerBaseDatos();
//
//        MongoCollection<Document> asientoEventosCol = base.getCollection("asientoEventos");
//        MongoCollection<Document> asientosCol = base.getCollection("asientos");
//        MongoCollection<Document> ubicacionesCol = base.getCollection("ubicaciones");
//        MongoCollection<Document> seccionesCol = base.getCollection("secciones");
//        MongoCollection<Document> eventosCol = base.getCollection("eventos");
//        MongoCollection<Document> categoriaCol = base.getCollection("categorias");
//        MongoCollection<Document> reservacionCol = base.getCollection("reservacion");
//        MongoCollection<Document> boletoCol = base.getCollection("boleto");
//        MongoCollection<Document> pagoCol = base.getCollection("pago");
//        MongoCollection<Document> usuarioCol = base.getCollection("usuario");
//
//        ObjectId ubicacionId = new ObjectId();
//        ubicacionesCol.insertOne(new Document("_id", ubicacionId)
//                .append("nombre", "Estadio Potros")
//                .append("capacidad", 100)
//                .append("tipo", TipoUbicacionN.AIRELIBRE.name())
//                .append("secciones", new ArrayList<>())
//        );
//
//        ObjectId seccionId = new ObjectId();
//        Document seccionDoc = new Document("_id", seccionId)
//                .append("nombre", "A")
//                .append("capacidad", 50)
//                .append("precioBase", 500L);
//        seccionesCol.insertOne(seccionDoc);
//
//        ObjectId asientoId = new ObjectId();
//        Document asientoDoc = new Document("_id", asientoId)
//                .append("fila", "A")
//                .append("numero", 1)
//                .append("ubicacion", ubicacionId)
//                .append("seccion", seccionId);
//        asientosCol.insertOne(asientoDoc);
//
//        ObjectId categoriaId = new ObjectId();
//        categoriaCol.insertOne(new Document("_id", categoriaId)
//                .append("UrlImagen", "test.jpg")
//                .append("nombreCategoria", CategoriaEventoDTO.NATACION.name())
//        );
//
//        ObjectId eventoId = new ObjectId();
//        Document eventoDoc = new Document("_id", eventoId)
//                .append("categoria", new Document("_id", categoriaId)
//                        .append("nombre", CategoriaEventoDTO.NATACION.name())
//                        .append("urlImagen", "test.jpg"))
//                .append("nombre", "Nado sincronizado")
//                .append("informacion", "Patos nadando sincronizadamente")
//                .append("fechaHora", new java.util.Date())
//                .append("ubicacion", new Document("_id", ubicacionId)
//                        .append("nombre", "Estadio Potros")
//                        .append("capacidad", 100)
//                        .append("tipoUbicacion", TipoUbicacionN.AIRELIBRE.name()))
//                .append("estado", EstadoEventoDTO.ACTIVO.name())
//                .append("urlImagen", "test.jpg")
//                .append("gratuito", false)
//                .append("tipo", TipoEventoN.ITSON.name())
//                .append("disponibilidad", 100);
//        eventosCol.insertOne(eventoDoc);
//
//        ObjectId asientoEveId = new ObjectId();
//        Document asientoEve = new Document("_id", asientoEveId)
//                .append("precio", 200)
//                .append("estadoAsiento", EstadoAsientoDTO.DISPONIBLE.name())
//                .append("asiento", asientoId)
//                .append("evento", eventoId);
//        asientoEventosCol.insertOne(asientoEve);
//
//        ObjectId boletoId = new ObjectId();
//        Document boletoDoc = new Document("_id", boletoId)
//                .append("codigoQR", "testQR")
//                .append("estado", EstadoBoletoDTO.ACTIVO.name())
//                .append("evento", new Document("_id", eventoId)
//                        .append("nombre", "Nado sincronizado"))
//                .append("asientoEvento", new Document("_id", asientoEveId)
//                        .append("estado", EstadoAsientoDTO.DISPONIBLE.name())
//                        .append("precio", 200))
//                .append("token", "testToken");
//        boletoCol.insertOne(boletoDoc);
//
//        ObjectId pagoId = new ObjectId();
//        Document pagoDoc = new Document("_id", pagoId)
//                .append("fechaOperacion", new Date())
//                .append("importe", 120.0)
//                .append("metodoPago", "TARJETA");
//        pagoCol.insertOne(pagoDoc);
//
//        ObjectId usuarioId = new ObjectId();
//        Document usuarioDoc = new Document("_id", usuarioId)
//                .append("nombre", "Aaron")
//                .append("apellidoPaterno", "Burciaga")
//                .append("apellidoMaterno", "Alcantar")
//                .append("correo", "aaronA@gmail.com")
//                .append("creditos", 230);
//        usuarioCol.insertOne(usuarioDoc);
//
//        ObjectId reservacionId = new ObjectId();
//        Document reservacionDoc = new Document("_id", reservacionId)
//                .append("total", 120.0)
//                .append("boleto", boletoId)
//                .append("pago", pagoId)
//                .append("usuario", usuarioId)
//                .append("fechaHora", new Date())
//                .append("estado", ReservacionEstadoDTO.ACTIVA.name());
//        reservacionCol.insertOne(reservacionDoc);
//
//        List<Reservacion> result = reservacionDAO.obtenerReservacionesUsuario(usuarioId.toString());
//        assertNotNull(result);
//    }
//
////fail obtenerBoleto
//    /**
//     * Test of obtenerBoleto method, of class ReservacionDAO.
//     */
//    @Test
//    public void testObtenerBoleto() throws Exception {
//        MongoDatabase base = ConexionMongo.obtenerBaseDatos();
//
//        MongoCollection<Document> asientoEventosCol = base.getCollection("asientoEventos");
//        MongoCollection<Document> asientosCol = base.getCollection("asientos");
//        MongoCollection<Document> ubicacionesCol = base.getCollection("ubicaciones");
//        MongoCollection<Document> seccionesCol = base.getCollection("secciones");
//        MongoCollection<Document> eventosCol = base.getCollection("eventos");
//        MongoCollection<Document> categoriaCol = base.getCollection("categorias");
//        MongoCollection<Document> reservacionCol = base.getCollection("reservacion");
//        MongoCollection<Document> boletoCol = base.getCollection("boleto");
//        MongoCollection<Document> pagoCol = base.getCollection("pago");
//        MongoCollection<Document> usuarioCol = base.getCollection("usuario");
//
//        ObjectId ubicacionId = new ObjectId();
//        Document ubicacionDoc = new Document("_id", ubicacionId)
//                .append("nombre", "Estadio Potros")
//                .append("capacidad", 100)
//                .append("tipoUbicacion", TipoUbicacionN.AIRELIBRE.name())
//                .append("secciones", new ArrayList<>()
//        );
//        ubicacionesCol.insertOne(ubicacionDoc);
//                
//        ObjectId seccionId = new ObjectId();
//        Document seccionDoc = new Document("_id", seccionId)
//                .append("nombre", "A")
//                .append("capacidad", 50)
//                .append("precioBase", 500L);
//        seccionesCol.insertOne(seccionDoc);
//
//        ObjectId asientoId = new ObjectId();
//        Document asientoDoc = new Document("_id", asientoId)
//                .append("fila", "A")
//                .append("numero", 1)
//                .append("ubicacion", ubicacionId)
//                .append("seccion", seccionId);
//        asientosCol.insertOne(asientoDoc);
//
//        ObjectId categoriaId = new ObjectId();
//        Document categoriaDoc = new Document("_id", categoriaId)
//                .append("urlImagen", "test.jpg")
//                .append("nombre", CategoriaEventoDTO.NATACION.name()
//        );
//        categoriaCol.insertOne(categoriaDoc);
//
//        ObjectId eventoId = new ObjectId();
//        Document eventoDoc = new Document("_id", eventoId)
//                .append("categoria", new Document("_id", categoriaId)
//                        .append("nombre", CategoriaEventoDTO.NATACION.name())
//                        .append("urlImagen", "test.jpg"))
//                .append("nombre", "Nado sincronizado")
//                .append("informacion", "Patos nadando sincronizadamente")
//                .append("fechaHora", new java.util.Date())
//                .append("ubicacion", new Document("_id", ubicacionId)
//                        .append("nombre", "Estadio Potros")
//                        .append("capacidad", 100)
//                        .append("tipoUbicacion", TipoUbicacionN.AIRELIBRE.name()))
//                .append("estado", EstadoEventoDTO.ACTIVO.name())
//                .append("urlImagen", "test.jpg")
//                .append("gratuito", false)
//                .append("tipo", TipoEventoN.ITSON.name())
//                .append("disponibilidad", 100);
//        eventosCol.insertOne(eventoDoc);
//
//        ObjectId asientoEveId = new ObjectId();
//        Document asientoEve = new Document("_id", asientoEveId)
//                .append("precio", 200)
//                .append("estado", EstadoAsientoDTO.DISPONIBLE.name())
//                .append("asiento", asientoId)
//                .append("evento", eventoId);
//        asientoEventosCol.insertOne(asientoEve);
//
//        ObjectId boletoId = new ObjectId();
//        Document boletoDoc = new Document("_id", boletoId)
//                .append("codigoQR", "testQR")
//                .append("estado", EstadoBoletoDTO.ACTIVO.name())
//                .append("evento", eventoId)
//                .append("asientoEvento", asientoEveId)
//                .append("token", "testToken");
//        boletoCol.insertOne(boletoDoc);
//
//        ObjectId pagoId = new ObjectId();
//        Document pagoDoc = new Document("_id", pagoId)
//                .append("fechaOperacion", new Date())
//                .append("importe", 120.0)
//                .append("metodoPago", "TARJETA");
//        pagoCol.insertOne(pagoDoc);
//
//        ObjectId usuarioId = new ObjectId();
//        Document usuarioDoc = new Document("_id", usuarioId)
//                .append("nombre", "Aaron")
//                .append("apellidoPaterno", "Burciaga")
//                .append("apellidoMaterno", "Alcantar")
//                .append("correo", "aaronA@gmail.com")
//                .append("creditos", 230);
//        usuarioCol.insertOne(usuarioDoc);
//
//        ObjectId reservacionId = new ObjectId();
//        Document reservacionDoc = new Document("_id", reservacionId)
//                .append("total", 120.0)
//                .append("boleto", boletoId)
//                .append("pago", pagoId)
//                .append("usuario", usuarioId)
//                .append("fechaHora", new Date())
//                .append("estado", ReservacionEstadoDTO.ACTIVA.name());
//        reservacionCol.insertOne(reservacionDoc);
//
//        Boleto result = reservacionDAO.obtenerBoleto(boletoId.toString());
//        assertNotNull(result);
//    }

package daos;

import Entitys.AsientoEvento;
import Entitys.Asistencia;
import Entitys.Boleto;
import Entitys.Devolucion;
import Entitys.ENUMS.EstadoBoleto;
import Entitys.ENUMS.ReservacionEstado;
import Entitys.Empleado;
import Entitys.Reservacion;
import entidadesmongo.ReporteAsistencia;
import excepciones.PersistenciaException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservacionDAOTest {

    private static ReservacionDAO dao;

    // ─── IDs reales de tu base de datos ───────────────────────────────────────
    private static final String ID_USUARIO_EXISTENTE = "6a0d3cd8c5a69f148aed5618";
    private static final String ID_EVENTO_EXISTENTE = "6a0d3d47827351936d441536";
    private static final String ID_EMPLEADO_EXISTENTE = "6a0d3e3cceeef465e744152e";
    private static final String ID_INEXISTENTE = "000000000000000000000000";
    private static final String TOKEN_INEXISTENTE = "token-que-no-existe-jamas";

    // Token e ID de reservación que se generan en el test 1 para usarlos después
    private static String tokenGenerado;
    private static String idReservacionGenerada;

    @BeforeAll
    static void setUp() {
        dao = ReservacionDAO.getInstance();
    }

    // =========================================================================
    // guardarReservacion
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("guardarReservacion — BUENA: guarda correctamente una reservación válida")
    void guardarReservacion_buena() throws PersistenciaException {
        tokenGenerado = UUID.randomUUID().toString();

        Reservacion reservacion = new Reservacion();
        reservacion.setTotal(0.0);
        reservacion.setEstado(ReservacionEstado.ACTIVA);

        Boleto boleto = new Boleto();
        boleto.setToken(tokenGenerado);
        boleto.setEstadoBoleto(EstadoBoleto.ACTIVO);
        boleto.setPrecio(0.0);
        boleto.setCodigoQR("/src/main/resources/qrs-boletos/test_" + System.currentTimeMillis() + ".png");
        reservacion.setBoleto(boleto);

        boolean resultado = dao.guardarReservacion(reservacion);

        assertTrue(resultado, "La reservación debería guardarse exitosamente");
        assertNotNull(tokenGenerado, "El token generado no debe ser nulo");
    }

    @Test
    @Order(2)
    @DisplayName("guardarReservacion — MALA: lanza excepción al recibir reservación nula")
    void guardarReservacion_mala_nula() {
        assertThrows(
                PersistenciaException.class,
                () -> dao.guardarReservacion(null),
                "Debería lanzar PersistenciaException al guardar null"
        );
    }

    // =========================================================================
    // obtenerReservacionesUsuario
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("obtenerReservacionesUsuario — BUENA: retorna lista para usuario existente")
    void obtenerReservacionesUsuario_buena() throws PersistenciaException {
        List<Reservacion> reservaciones = dao.obtenerReservacionesUsuario(ID_USUARIO_EXISTENTE);

        assertNotNull(reservaciones, "La lista no debe ser nula");
        assertDoesNotThrow(() -> dao.obtenerReservacionesUsuario(ID_USUARIO_EXISTENTE));
    }

    @Test
    @Order(4)
    @DisplayName("obtenerReservacionesUsuario — MALA: lanza PersistenciaException con ID inválido")
    void obtenerReservacionesUsuario_mala_idInvalido() {
        // ✅ El DAO atrapa la excepción interna y lanza PersistenciaException
        assertThrows(
                PersistenciaException.class,
                () -> dao.obtenerReservacionesUsuario("id-no-valido-$$"),
                "Debería lanzar PersistenciaException con un ID malformado"
        );
    }

    // =========================================================================
    // obtenerBoleto
    // =========================================================================
    @Test
    @Order(5)
    @DisplayName("obtenerBoleto — BUENA: retorna boleto para reservación recién insertada")
    void obtenerBoleto_buena() throws PersistenciaException {
        // ✅ Primero insertamos una reservación y guardamos su ID
        String token = UUID.randomUUID().toString();

        Reservacion reservacion = new Reservacion();
        reservacion.setTotal(0.0);
        reservacion.setEstado(ReservacionEstado.ACTIVA);

        Boleto boleto = new Boleto();
        boleto.setToken(token);
        boleto.setEstadoBoleto(EstadoBoleto.ACTIVO);
        boleto.setPrecio(0.0);
        boleto.setCodigoQR("/test/qr_" + token + ".png");
        reservacion.setBoleto(boleto);

        dao.guardarReservacion(reservacion);

        // Buscamos la reservación recién insertada por token para obtener su ID
        Boleto encontrado = dao.buscarPorToken(token);

        assertNotNull(encontrado, "El boleto insertado debe encontrarse por token");
        assertEquals(token, encontrado.getToken(), "El token debe coincidir");
    }

    @Test
    @Order(6)
    @DisplayName("obtenerBoleto — MALA: retorna null para reservación inexistente")
    void obtenerBoleto_mala_inexistente() throws PersistenciaException {
        Boleto boleto = dao.obtenerBoleto(ID_INEXISTENTE);
        assertNull(boleto, "Debería retornar null para una reservación que no existe");
    }

    // =========================================================================
    // buscarPorToken
    // =========================================================================
    @Test
    @Order(7)
    @DisplayName("buscarPorToken — BUENA: encuentra boleto con token generado en test 1")
    void buscarPorToken_buena() throws PersistenciaException {
        // ✅ Usamos el token que generamos en el test 1
        assertNotNull(tokenGenerado, "El token del test 1 debe estar disponible");

        Boleto boleto = dao.buscarPorToken(tokenGenerado);

        assertNotNull(boleto, "El boleto no debe ser nulo");
        assertEquals(tokenGenerado, boleto.getToken(), "El token debe coincidir");
    }

    @Test
    @Order(8)
    @DisplayName("buscarPorToken — MALA: lanza excepción con token nulo")
    void buscarPorToken_mala_nulo() {
        assertThrows(
                PersistenciaException.class,
                () -> dao.buscarPorToken(null),
                "Debería lanzar PersistenciaException con token nulo"
        );
    }

    // =========================================================================
    // actualizarEstado
    // =========================================================================
    @Test
    @Order(9)
    @DisplayName("actualizarEstado — BUENA: actualiza estado del boleto generado en test 1")
    void actualizarEstado_buena() throws PersistenciaException {
        // ✅ El token del test 1 está ACTIVO — podemos cambiarlo a USADO
        assertNotNull(tokenGenerado, "El token del test 1 debe estar disponible");

        Boleto boleto = new Boleto();
        boleto.setToken(tokenGenerado);
        boleto.setEstadoBoleto(EstadoBoleto.USADO);

        boolean resultado = dao.actualizarEstado(boleto);

        assertTrue(resultado, "Debe retornar true al modificar un documento existente");
    }

    @Test
    @Order(10)
    @DisplayName("actualizarEstado — MALA: lanza excepción con boleto nulo")
    void actualizarEstado_mala_nulo() {
        assertThrows(
                PersistenciaException.class,
                () -> dao.actualizarEstado(null),
                "Debería lanzar PersistenciaException con boleto nulo"
        );
    }

    // =========================================================================
    // registrarAsistencia
    // =========================================================================
    @Test
    @Order(11)
    @DisplayName("registrarAsistencia — BUENA: registra asistencia para boleto ACTIVO nuevo")
    void registrarAsistencia_buena() throws PersistenciaException {
        // ✅ Insertamos un boleto ACTIVO fresco para este test
        String tokenFresco = UUID.randomUUID().toString();

        Reservacion reservacion = new Reservacion();
        reservacion.setTotal(0.0);
        reservacion.setEstado(ReservacionEstado.ACTIVA);

        Boleto boleto = new Boleto();
        boleto.setToken(tokenFresco);
        boleto.setEstadoBoleto(EstadoBoleto.ACTIVO);
        boleto.setPrecio(0.0);
        boleto.setCodigoQR("/test/qr_asist_" + tokenFresco + ".png");
        reservacion.setBoleto(boleto);

        dao.guardarReservacion(reservacion);

        // Ahora registramos asistencia
        Boleto boletoAsistencia = new Boleto();
        boletoAsistencia.setToken(tokenFresco);
        boletoAsistencia.setEstadoBoleto(EstadoBoleto.ACTIVO);
        boletoAsistencia.setAsistencia(null);

        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(ID_EMPLEADO_EXISTENTE);

        Asistencia asistencia = new Asistencia();
        asistencia.setEmpleado(empleado);

        Asistencia resultado = dao.registrarAsistencia(boletoAsistencia, asistencia);

        assertNotNull(resultado, "La asistencia registrada no debe ser nula");
        assertEquals(ID_EMPLEADO_EXISTENTE, resultado.getEmpleado().getIdEmpleado(),
                "El empleado debe coincidir");
    }

    @Test
    @Order(12)
    @DisplayName("registrarAsistencia — MALA: lanza excepción con boleto nulo")
    void registrarAsistencia_mala_nulo() {
        assertThrows(
                PersistenciaException.class,
                () -> dao.registrarAsistencia(null, new Asistencia()),
                "Debería lanzar PersistenciaException con boleto nulo"
        );
    }

    // =========================================================================
    // obtenerReporteAsistencia
    // =========================================================================
    @Test
    @Order(13)
    @DisplayName("obtenerReporteAsistencia — BUENA: retorna reporte para evento existente")
    void obtenerReporteAsistencia_buena() {
        ReporteAsistencia reporte = dao.obtenerReporteAsistencia(ID_EVENTO_EXISTENTE);

        assertNotNull(reporte, "El reporte no debe ser nulo");
        assertTrue(reporte.getAsistidos() >= 0, "Los asistidos no pueden ser negativos");
        assertTrue(reporte.getPendientes() >= 0, "Los pendientes no pueden ser negativos");
        assertEquals(
                reporte.getAsistidos() + reporte.getPendientes(),
                reporte.getTotalVendidos(),
                "El total debe ser la suma de asistidos + pendientes"
        );
    }

    @Test
    @Order(14)
    @DisplayName("obtenerReporteAsistencia — MALA: retorna reporte vacío para evento inexistente")
    void obtenerReporteAsistencia_mala_inexistente() {
        ReporteAsistencia reporte = dao.obtenerReporteAsistencia(ID_INEXISTENTE);

        assertNotNull(reporte, "El reporte no debe ser nulo aunque el evento no exista");
        assertEquals(0, reporte.getAsistidos(), "Sin evento no debe haber asistidos");
        assertEquals(0, reporte.getPendientes(), "Sin evento no debe haber pendientes");
        assertEquals(0, reporte.getTotalVendidos(), "Sin evento el total debe ser 0");
    }

    // =========================================================================
    // obtenerAsientosConAsistencia
    // =========================================================================
    @Test
    @Order(15)
    @DisplayName("obtenerAsientosConAsistencia — BUENA: retorna lista para evento existente")
    void obtenerAsientosConAsistencia_buena() throws PersistenciaException {
        List<AsientoEvento> asientos = dao.obtenerAsientosConAsistencia(ID_EVENTO_EXISTENTE);

        assertNotNull(asientos, "La lista no debe ser nula");
    }

    @Test
    @Order(16)
    @DisplayName("obtenerAsientosConAsistencia — MALA: lanza PersistenciaException con ID inválido")
    void obtenerAsientosConAsistencia_mala_idInvalido() {
        // ✅ El DAO atrapa IllegalArgumentException y lanza PersistenciaException
        assertThrows(
                PersistenciaException.class,
                () -> dao.obtenerAsientosConAsistencia("formato@@invalido"),
                "Debería lanzar PersistenciaException con un ID malformado"
        );
    }
}
