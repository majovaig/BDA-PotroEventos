package pago;

import adaptadores.BancoAdapter;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import dtos.CobroDTO;
import dtos.PaymentDTO;
import dtos.RefundDTO;
import dtos.StripeChargeDTO;
import dtos.TarjetaDTO;
import excepciones.PagoException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase de control encargada de gestionar el procesamiento de pagos mediante
 * Stripe.
 *
 * Implementa el patrón Singleton para mantener una sola instancia durante toda
 * la ejecución del sistema.
 *
 * Los datos de tarjeta capturados se convierten internamente a tokens de prueba
 * compatibles con Stripe.
 *
 * @author Aaron Burciaga - 262788
 * @author Brian Sandoval - 262741
 * @author Dayanara Peralta - 262695
 * @author María Valdez - 262775
 */
public class ControlPago {

    /**
     * Logger del subsistema.
     */
    private static final Logger LOG = Logger.getLogger(ControlPago.class.getName());

    /**
     * Instancia única del controlador.
     */
    private static ControlPago instancia;

    private static final String STRIPE_SECRET_KEY = "clave_privada";

    /**
     * Constructor privado.
     */
    private ControlPago() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    /**
     * Obtiene la instancia única.
     *
     * @return instancia única
     */
    public static ControlPago getInstance() {
        if (instancia == null) {
            instancia = new ControlPago();
        }
        return instancia;
    }

    /**
     * Procesa un pago usando tarjeta y cobro.
     *
     * @param tarjetaDTO datos de tarjeta
     * @param cobroDTO datos del cobro
     * @return true si fue exitoso
     * @throws PagoException si ocurre error
     */
    protected PaymentDTO realizarPago(TarjetaDTO tarjetaDTO, CobroDTO cobroDTO) throws PagoException {
        try {
            validarDatos(tarjetaDTO, cobroDTO);

            String token = convertirTarjetaAToken(tarjetaDTO);

            StripeChargeDTO stripeDTO = BancoAdapter.dtoAInfraestructura(cobroDTO, token);

            Charge cargo = crearCargo(stripeDTO);
            
            /*
            esto es para obtener el método de pago
            */
            Object source = cargo.getSource();
            String tipoPago = "desconocido";
            if (source instanceof Card) {
                tipoPago = "tarjeta";
            } else {
                tipoPago = "alternativo";
            }
            if (transaccionExitosa(cargo)) {
                LOG.log(Level.INFO,
                        "Pago exitoso. ID: {0}, Estado: {1}",
                        new Object[]{cargo.getId(), cargo.getStatus()});
                return new PaymentDTO(
                        cargo.getId(), 
                        LocalDateTime.now(), 
                        cobroDTO.getMonto().doubleValue(), 
                        tipoPago);
            }

            throw new PagoException("La transacción no fue aprobada.");

        } catch (StripeException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            throw new PagoException("Error al procesar pago: " + ex.getMessage());

        } catch (IllegalArgumentException ex) {
            throw new PagoException(ex.getMessage());
        }
    }

    /**
     * Validaciones básicas.
     */
    private void validarDatos(TarjetaDTO tarjetaDTO, CobroDTO cobroDTO) {
        if (tarjetaDTO == null) {
            throw new IllegalArgumentException("La tarjeta es obligatoria.");
        }

        if (cobroDTO == null) {
            throw new IllegalArgumentException("El cobro es obligatorio.");
        }

        if (cobroDTO.getMonto() == null || cobroDTO.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        if (tarjetaDTO.getNumero() == null || tarjetaDTO.getNumero().isBlank()) {
            throw new IllegalArgumentException("El número de tarjeta es obligatorio.");
        }
    }

    /**
     * Convierte tarjeta a token de prueba.
     */
    private String convertirTarjetaAToken(TarjetaDTO tarjetaDTO) {
        String numero = tarjetaDTO.getNumero().replace(" ", "");

        switch (numero) {
            case "4242424242424242":
                return "tok_visa";

            case "4000000000009995":
                return "tok_chargeDeclinedInsufficientFunds";

            case "4000000000000002":
                return "tok_chargeDeclined";

            default:
                throw new IllegalArgumentException("Tarjeta no válida.");
        }
    }

    /**
     * Crea cargo en Stripe.
     */
    private Charge crearCargo(StripeChargeDTO stripeDTO) throws StripeException {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("amount", stripeDTO.getAmount() * 100);
        parametros.put("currency", stripeDTO.getCurrency());
        parametros.put("description", stripeDTO.getDescription());
        parametros.put("source", stripeDTO.getSource());

        return Charge.create(parametros);
    }

    /**
     * Verifica si la transacción fue exitosa.
     */
    private boolean transaccionExitosa(Charge cargo) {
        return cargo != null
                && Boolean.TRUE.equals(cargo.getPaid())
                && "succeeded".equalsIgnoreCase(cargo.getStatus());
    }
    
    private boolean reembolsoExitoso(Refund reembolso){
        return reembolso != null
                && "succeeded".equalsIgnoreCase(reembolso.getStatus());
    }
    
    protected RefundDTO crearReembolso(String idOperacion) throws PagoException {
        try {
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("charge", idOperacion);

            Refund reembolso = Refund.create(parametros);
            if (reembolsoExitoso(reembolso)) {
                LOG.log(Level.INFO,
                        "Reembolso exitoso. ID: {0}, Estado: {1}");
                return new RefundDTO(
                        reembolso.getId(),
                        LocalDateTime.now(),
                        reembolso.getAmount().doubleValue(),
                        "Tarjeta");
            }
            throw new PagoException("La operación no fue realizada.");
        } catch (StripeException se) {
            throw new PagoException("No se pudo realizar el reembolso: " + se.getMessage());
        }
    }
}
