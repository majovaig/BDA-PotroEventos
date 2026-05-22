/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidadesresumenmongo;

import org.bson.types.ObjectId;

/**
 *
 * @author maria
 */
public class AsientoEventoResumenMongo {

    private ObjectId idAsientoEvento;
    private ObjectId asiento;
    private ObjectId evento;

    public AsientoEventoResumenMongo() {
    }

    public AsientoEventoResumenMongo(ObjectId idAsientoEvento, ObjectId asiento, ObjectId evento) {
        this.idAsientoEvento = idAsientoEvento;
        this.asiento = asiento;
        this.evento = evento;
    }

    public ObjectId getIdAsientoEvento() {
        return idAsientoEvento;
    }

    public void setIdAsientoEvento(ObjectId idAsientoEvento) {
        this.idAsientoEvento = idAsientoEvento;
    }

    public String getIdComoTexto() {
        if (idAsientoEvento == null) {
            return null;
        }
        return idAsientoEvento.toHexString();
    }

    public ObjectId getAsiento() {
        return asiento;
    }

    public void setAsiento(ObjectId asiento) {
        this.asiento = asiento;
    }

    public ObjectId getEvento() {
        return evento;
    }

    public void setEvento(ObjectId evento) {
        this.evento = evento;
    }

}
