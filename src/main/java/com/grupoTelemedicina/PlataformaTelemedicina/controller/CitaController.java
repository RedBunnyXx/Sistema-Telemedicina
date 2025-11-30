package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Paciente;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PacienteRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PersonaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.CitaService;
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

/**
 * Controlador REST para la gestión de citas.
 *
 * Actualmente expone el endpoint POST /api/citas que permite al paciente
 * autenticado crear una nueva cita a partir de los datos enviados por
 * el frontend de agendamiento.
 */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;
    private final PersonaRepository personaRepository;
    private final PacienteRepository pacienteRepository;

    public CitaController(CitaService citaService,
                          PersonaRepository personaRepository,
                          PacienteRepository pacienteRepository) {
        this.citaService = citaService;
        this.personaRepository = personaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearCita(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestBody CrearCitaRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String correo = userDetails.getUsername();
        Optional<Persona> personaOpt = personaRepository.findByCorreo(correo);
        if (personaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Persona persona = personaOpt.get();
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(persona.getId());
        if (pacienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario autenticado no es un paciente válido");
        }

        LocalDateTime fechaCita;
        try {
            fechaCita = LocalDateTime.parse(request.getFechaHoraIso());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de fecha/hora inválido");
        }

        if (fechaCita.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La fecha de la cita debe ser futura");
        }

        Cita cita = citaService.crearCitaParaPaciente(pacienteOpt.get().getId(), request.getMedicoId(), fechaCita);

        CrearCitaResponse response = new CrearCitaResponse();
        response.setIdCita(cita.getId());
        response.setEstado(cita.getEstado());
        response.setFechaHora(cita.getFechaCita());

        return ResponseEntity.ok(response);
    }

    public static class CrearCitaRequest {
        private Integer medicoId;
        private String fechaHoraIso;

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
