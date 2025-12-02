package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Medico;
import com.grupoTelemedicina.PlataformaTelemedicina.dto.PaymentIntentResponse;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.MedicoRepository;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final MedicoRepository medicoRepository;

    // Inyectamos el repositorio para poder buscar el precio del médico
    public PagoController(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @PostMapping("/crear-intento")
    public ResponseEntity<PaymentIntentResponse> crearIntentoPago(@RequestBody Map<String, Integer> payload) {
        try {
            // 1. Recibimos el ID del médico desde el Frontend
            Integer medicoId = payload.get("medicoId");
            
            // 2. Buscamos al médico en la BD
            Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
            
            // 3. Obtenemos su costo real (Ej: 120.00)
            BigDecimal costoSoles = medico.getEspecialidad().getCosto();
            
            // 4. Convertimos a centavos para Stripe (120.00 * 100 = 12000L)
            Long montoCentavos = costoSoles.multiply(new BigDecimal(100)).longValue();

            // 5. Enviamos el monto DINÁMICO a Stripe
            PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                    .setAmount(montoCentavos) // <--- ¡AQUÍ ESTÁ EL CAMBIO CLAVE!
                    .setCurrency("pen") 
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return ResponseEntity.ok(new PaymentIntentResponse(intent.getClientSecret()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}