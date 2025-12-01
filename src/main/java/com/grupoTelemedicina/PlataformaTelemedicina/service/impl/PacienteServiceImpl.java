package com.grupoTelemedicina.PlataformaTelemedicina.service.impl;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Paciente;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import com.grupoTelemedicina.PlataformaTelemedicina.dto.PacienteDTO;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PacienteRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PersonaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.PacienteService;
import com.grupoTelemedicina.PlataformaTelemedicina.service.PacienteService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteServiceImpl(PacienteRepository pacienteRepository,
                             PersonaRepository personaRepository,
                             PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List <PacienteDTO> listarPacientes (){
        List<Paciente> pacientes = pacienteRepository.findAll();
        return pacientes.stream().map(this::mapearEntidadADTO).collect(Collectors.toList());
    }

    @Override
    public PacienteDTO obtenerPorId(Integer idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
        return mapearEntidadADTO(paciente);
    }
    @Override
    public PacienteDTO crearPaciente(PacienteDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria para registrar un paciente");
        }

        // Primero creamos Persona
        Persona persona = new Persona();
        persona.setNombres(dto.getNombres());
        persona.setApellidos(dto.getApellidos());
        persona.setDni(dto.getDni());
        persona.setCorreo(dto.getCorreo());
        persona.setTelefono(dto.getTelefono());
        persona.setTipoPersona("Paciente"); // por tu CHECK en tabla Personas
        persona.setPassword(passwordEncoder.encode(dto.getPassword()));

        Persona personaGuardada = personaRepository.save(persona);

        // Luego creamos Paciente usando esa Persona
        Paciente paciente = new Paciente();
        paciente.setPersona(personaGuardada);
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setSexo(dto.getSexo());

        Paciente pacienteGuardado = pacienteRepository.save(paciente);

        return mapearEntidadADTO(pacienteGuardado);
    }

    @Override
    public PacienteDTO actualizarPaciente(Integer idPaciente, PacienteDTO dto) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));

        Persona persona = paciente.getPersona();

        // Actualizar datos de Persona
        persona.setNombres(dto.getNombres());
        persona.setApellidos(dto.getApellidos());
        persona.setDni(dto.getDni());
        persona.setCorreo(dto.getCorreo());
        persona.setTelefono(dto.getTelefono());
        personaRepository.save(persona);

        // Actualizar datos de Paciente
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setSexo(dto.getSexo());
        Paciente pacienteActualizado = pacienteRepository.save(paciente);

        return mapearEntidadADTO(pacienteActualizado);
    }

    @Override
    public void eliminarPaciente(Integer idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));

        // Primero eliminamos Paciente
        pacienteRepository.delete(paciente);

        // Luego, si quieres, también eliminamos la Persona asociada
        personaRepository.delete(paciente.getPersona());
    }

    // ================== MÉTODOS PRIVADOS DE MAPEO ==================

    private PacienteDTO mapearEntidadADTO(Paciente paciente) {
        Persona persona = paciente.getPersona();

        PacienteDTO dto = new PacienteDTO();
        dto.setId_paciente(paciente.getId());
        dto.setNombres(persona.getNombres());
        dto.setApellidos(persona.getApellidos());
        dto.setDni(persona.getDni());
        dto.setCorreo(persona.getCorreo());
        dto.setTelefono(persona.getTelefono());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        dto.setSexo(paciente.getSexo());
        return dto;
    }
}
