/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CambioAsiento;

//import Exception.CambioAsientoException;
//import dtos.BoletoDTO;
//import excepciones.NegocioException;
//import interfaces.IReservacionBO;
//import objetosNegocio.AsientoEventoBO;
//import objetosNegocio.BoletoBO;
//import objetosNegocio.ReservacionBO;
//
///**
// *
// * @author Dayanara Peralta G
// */
//public class ControlCambioAsiento {
//    private IReservacionBO reservacionBO;
//    private IAsientoEventoBO asientoEventoBO;
//    private IBoletoBO boletoBO;
//    
//    private static ControlCambioAsiento instance;
//
//    public ControlCambioAsiento() {
//        this.reservacionBO = ReservacionBO.getInstance();
//        this.asientoEventoBO = AsientoEventoBO.getInstance();
//        this.boletoBO = BoletoBO.getInstance();
//    }
//    
//    public static ControlCambioAsiento getInstance(){
//        if(instance == null){
//            instance = new ControlCambioAsiento();
//        }
//        return instance;
//    }
//    
//    public BoletoDTO obtenerBoleto(String idReservacion) throws CambioAsientoException{
//        try{
//            return reservacionBO.obtenerBoletoPorReservacion(idReservacion);
//        }catch(Exception e ){
//            throw new CambioAsientoException("Error al obtener el boleto: " + e.getMessage());
//        }
//    }
//    
//    public boolean cambiarAsiento(String idReservacion, String idAsientoNuevo) throws CambioAsientoException{
//        String idAsientoActual = null;
//        try{
//            BoletoDTO boleto = reservacionBO.obtenerBoletoPorReservacion(idReservacion);
//            if (boleto == null) {
//                throw new CambioAsientoException("No se encontró el boleto para la reservación.");
//            }
//            
//            if (boleto.getAsiento() != null) {
//                idAsientoActual = boleto.getAsiento().getIdAsientoEvento();
//            } else {
//                System.out.println("El boleto no tenía un asiento previo registrado");
//            }
//                        
//            if (idAsientoActual != null && !idAsientoActual.trim().isEmpty()) {
//                asientoEventoBO.liberarAsiento(idAsientoActual);
//                System.out.println("Asiento anterior liberado en BD.");
//            }
//            
//            asientoEventoBO.ocuparAsiento(idAsientoNuevo);
//            System.out.println("Nuevo asiento ocupado");
//            
//            boletoBO.actualizarAsiento(idReservacion, idAsientoNuevo);
//            System.out.println("Se regresó de boletoBO.actualizarAsiento sin lanzar excepciones");
//            return true;
//        }catch(Exception e){
//            throw new CambioAsientoException("Error al cambiar el asiento: " + e.getMessage());
//        }
//    }
//}
