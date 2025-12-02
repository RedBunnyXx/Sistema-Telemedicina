package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.*;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.*;
import com.grupoTelemedicina.PlataformaTelemedicina.service.CitaService;
import com.grupoTelemedicina.PlataformaTelemedicina.service.EmailService;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;
    private final PersonaRepository personaRepository;
    private final PacienteRepository pacienteRepository;
    private final PagoRepository pagoRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository; 
    private final EmailService emailService; 

    public CitaController(CitaService citaService,
                          PersonaRepository personaRepository,
                          PacienteRepository pacienteRepository,
                          PagoRepository pagoRepository,
                          MedicoRepository medicoRepository,
                          CitaRepository citaRepository,
                          EmailService emailService) { 
        this.citaService = citaService;
        this.personaRepository = personaRepository;
        this.pacienteRepository = pacienteRepository;
        this.pagoRepository = pagoRepository;
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> crearCita(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestBody CrearCitaRequest request) {
        
        // 1. Validaciones (SIN CAMBIOS)
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String correoUsuario = userDetails.getUsername(); // Este es el correo para enviar
        Optional<Persona> personaOpt = personaRepository.findByCorreo(correoUsuario);
        if (personaOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Persona personaPaciente = personaOpt.get();
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(personaPaciente.getId());
        if (pacienteOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no válido");

        LocalDateTime fechaCita;
        try { fechaCita = LocalDateTime.parse(request.getFechaHoraIso()); } 
        catch (Exception e) { return ResponseEntity.badRequest().body("Fecha inválida"); }
        if (fechaCita.isBefore(LocalDateTime.now())) return ResponseEntity.badRequest().body("Fecha debe ser futura");

        // 2. Lógica Principal (SIN CAMBIOS)
        Cita cita = citaService.crearCitaParaPaciente(pacienteOpt.get().getId(), request.getMedicoId(), fechaCita);
        
        Medico medico = medicoRepository.findById(request.getMedicoId()).orElse(null);
        BigDecimal costoReal = (medico != null) ? medico.getEspecialidad().getCosto() : new BigDecimal("80.00");
        String nombreMedico = (medico != null) ? medico.getPersona().getNombres() + " " + medico.getPersona().getApellidos() : "Dr. Desconocido";
        String nombreEspecialidad = (medico != null) ? medico.getEspecialidad().getNombre() : "General";

        cita.setCosto(costoReal);
        citaRepository.save(cita);

        Pago pago = new Pago();
        pago.setCita(cita);
        pago.setMonto(costoReal);
        pago.setFechaPago(LocalDateTime.now());
        pago.setEstadoPago("Aprobado");
        pago.setIdTransaccionStripe(request.getIdTransaccion()); 
        pago.setMetodoPago("Tarjeta");
        pagoRepository.save(pago);
        
        // 3. --- ENVIAR CORREO (NUEVO) ---
        // Se envía de forma asíncrona para no ralentizar la respuesta al usuario
        emailService.sendCitaConfirmation(
                correoUsuario, // El correo del usuario logueado
                personaPaciente.getNombres() + " " + personaPaciente.getApellidos(),
                nombreMedico,
                nombreEspecialidad,
                cita.getFechaCita(),
                costoReal,
                request.getIdTransaccion()
        );

        // 4. Respuesta (SIN CAMBIOS)
        CrearCitaResponse response = new CrearCitaResponse();
        response.setIdCita(cita.getId());
        response.setEstado(cita.getEstado());
        response.setFechaHora(cita.getFechaCita());

        return ResponseEntity.ok(response);
    }

    // Clases estáticas Request/Response (SIN CAMBIOS)
    public static class CrearCitaRequest {
        private Integer medicoId; 
        private String fechaHoraIso; 
        private String idTransaccion; 
        private String metodoPago;

        public Integer getMedicoId() {
            return medicoId;
        }

        public void setMedicoId(Integer medicoId) {
            this.medicoId = medicoId;
        }

        public String getFechaHoraIso() {
            return fechaHoraIso;
        }

        public void setFechaHoraIso(String fechaHoraIso) {
            this.fechaHoraIso = fechaHoraIso;
        }

        public String getIdTransaccion() {
            return idTransaccion;
        }

        public void setIdTransaccion(String idTransaccion) {
            this.idTransaccion = idTransaccion;
        }

        public String getMetodoPago() {
            return metodoPago;
        }

        public void setMetodoPago(String metodoPago) {
            this.metodoPago = metodoPago;
        }
        
       
    }
    public static class CrearCitaResponse {
        private Integer idCita; 
        private String estado; 
        private LocalDateTime fechaHora;

        public Integer getIdCita() {
            return idCita;
        }

        public void setIdCita(Integer idCita) {
            this.idCita = idCita;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public LocalDateTime getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(LocalDateTime fechaHora) {
            this.fechaHora = fechaHora;
        }
       
    }
}