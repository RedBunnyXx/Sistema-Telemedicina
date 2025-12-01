/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Paciente;
import com.grupoTelemedicina.PlataformaTelemedicina.dto.RecetaHistorialView;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PacienteRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PersonaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.RecetaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final PersonaRepository personaRepository;
    private final PacienteRepository pacienteRepository;
    private final RecetaService recetaService;

    public RecetaController(PersonaRepository personaRepository,
                            PacienteRepository pacienteRepository,
                            RecetaService recetaService) {
        this.personaRepository = personaRepository;
        this.pacienteRepository = pacienteRepository;
        this.recetaService = recetaService;
    }

    // Historial del paciente autenticado
    @GetMapping("/historial")
    public List<RecetaHistorialView> historial(@AuthenticationPrincipal UserDetails user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");

        Persona persona = personaRepository.findByCorreo(user.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));

        // Si NO usas MapsId, cambia a pacienteRepository.findByPersonaId(persona.getId())
        Paciente paciente = pacienteRepository.findById(persona.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));

        return recetaService.obtenerHistorialPorPaciente(paciente.getId());
    }

    // Atajo de prueba SIN login (útil en desarrollo)
    @GetMapping("/paciente/{id}/historial")
    public List<RecetaHistorialView> historialPorPaciente(@PathVariable Integer id) {
        return recetaService.obtenerHistorialPorPaciente(id);
    }
}
