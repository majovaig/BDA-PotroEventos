package adaptadores;

import Entitys.AsientoEvento;
import Entitys.Boleto;
import Entitys.ENUMS.EstadoBoleto;
import entidadesmongo.BoletoMongoEntidad;
import entidadesresumenmongo.AsientoEventoResumenMongo;
import entidadesresumenmongo.EventoResumenMongo;
import excepciones.PersistenciaException;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Adaptador de persistencia para la entidad Boleto. Corregido para soportar
 * eventos gratuitos sin asientos asignados.
 *
 * @author maria
 * @author Brian Sandoval - 262741
 */
public class BoletoPersistenciaAdapter {

    public static BoletoMongoEntidad convertirAMongo(Boleto dominio) throws PersistenciaException {
        if (dominio == null) {
            return null;
        }

        BoletoMongoEntidad mongo = new BoletoMongoEntidad();
        mongo.setId(dominio.getIdBoleto());
        mongo.setCodigoQR(dominio.getCodigoQR());
        mongo.setPrecio(dominio.getPrecio());

        if (dominio.getEstadoBoleto() != null) {
            mongo.setEstado(dominio.getEstadoBoleto().name());
        }

        mongo.setToken(dominio.getToken());

        if (dominio.getEvento() != null && dominio.getEvento().getIdEvento() != null) {
            EventoResumenMongo erm = new EventoResumenMongo();
            erm.setId(convertirStringAObjectId(dominio.getEvento().getIdEvento()));
            erm.setNombre(dominio.getEvento().getNombreEvento());
            erm.setFechaHora(dominio.getEvento().getFechaHora());
            mongo.setEvento(erm);
        }

        if (dominio.getAsiento() != null && dominio.getAsiento().getIdAsientoEvento() != null) {
            AsientoEventoResumenMongo aer = new AsientoEventoResumenMongo();
            aer.setIdAsientoEvento(convertirStringAObjectId(dominio.getAsiento().getIdAsientoEvento()));

            if (dominio.getAsiento().getAsiento() != null && dominio.getAsiento().getAsiento().getIdAsiento() != null) {
                aer.setAsiento(convertirStringAObjectId(dominio.getAsiento().getAsiento().getIdAsiento()));
            }

            if (dominio.getEvento() != null && dominio.getEvento().getIdEvento() != null) {
                aer.setEvento(convertirStringAObjectId(dominio.getEvento().getIdEvento()));
            }
            mongo.setAsiento(aer);
        } else {
            mongo.setAsiento(null);
        }

        if (dominio.getAsistencia() != null) {
            mongo.setAsistencia(AsistenciaPersistenciaAdapter.convertirAMongo(dominio.getAsistencia()));
        }

        return mongo;
    }

    public static Boleto convertirADominio(Document mongo) throws PersistenciaException {
        if (mongo == null) {
            return null;
        }

        Boleto dominio = new Boleto();

        if (mongo.get("id") != null) {
            dominio.setIdBoleto(mongo.get("id").toString());
        }

        dominio.setCodigoQR(mongo.getString("codigoQR"));
        dominio.setToken(mongo.getString("token"));

        if (mongo.get("precio") != null) {
            dominio.setPrecio(((Number) mongo.get("precio")).doubleValue());
        }

        String estado = mongo.getString("estado");
        if (estado != null && !estado.isBlank()) {
            dominio.setEstadoBoleto(EstadoBoleto.valueOf(estado));
        }

        // Mapeo de Asiento Resumen
        Document asientoResumenDoc = (Document) mongo.get("asiento");
        if (asientoResumenDoc != null) {
            ObjectId idAsientoEvento = asientoResumenDoc.getObjectId("idAsientoEvento");
            if (idAsientoEvento != null) {
                AsientoEvento ae = new AsientoEvento();
                ae.setIdAsientoEvento(idAsientoEvento.toHexString());
                dominio.setAsiento(ae);
            }
        }

        // Mapeo de Evento
        Document eventoDoc = (Document) mongo.get("evento");
        if (eventoDoc != null) {
            dominio.setEvento(EventoPersistenciaAdapter.convertirADominio(eventoDoc));
        }

        // Mapeo de Asistencia
        Document asistenciaDoc = (Document) mongo.get("asistencia");
        if (asistenciaDoc != null) {
            dominio.setAsistencia(AsistenciaPersistenciaAdapter.convertirADominio(asistenciaDoc));
        }

        return dominio;
    }

    public static List<Boleto> convertirListaADominio(List<Document> lista) throws PersistenciaException {
        List<Boleto> boletos = new ArrayList<>();
        if (lista == null) {
            return boletos;
        }
        for (Document mongo : lista) {
            boletos.add(convertirADominio(mongo));
        }
        return boletos;
    }

    private static ObjectId convertirStringAObjectId(String id) throws PersistenciaException {
        if (id == null || id.isBlank()) {
            return null;
        }
        if (!ObjectId.isValid(id)) {
            throw new PersistenciaException("El id recibido no tiene formato válido de ObjectId.");
        }
        return new ObjectId(id);
    }
}
