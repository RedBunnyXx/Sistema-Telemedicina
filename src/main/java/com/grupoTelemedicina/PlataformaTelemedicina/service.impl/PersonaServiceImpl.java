package com.grupoTelemedicina.PlataformaTelemedicina.service.impl;

import com.grupoTelemedicina.PlataformaTelemedicina.dto.CrearPersonaRequest;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PersonaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.PersonaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private PersonaRepository repository;

    @Override
    @Transactional
    public Persona crearPersona(CrearPersonaRequest req) {

        // Validación de DNI repetido
        if (repository.findByDni(req.getDni()).isPresent()) {
            throw new RuntimeException("El DNI ya está registrado.");
        }

        // Validación de correo repetido
        if (repository.findByCorreo(req.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        Persona persona = new Persona();
        persona.setNombres(req.getNombres());
        persona.setApellidos(req.getApellidos());
        persona.setDni(req.getDni());
        persona.setCorreo(req.getCorreo());
        persona.setTelefono(req.getTelefono());
        persona.setTipoPersona(req.getTipoPersona());

        return repository.save(persona);
    }


    @Override
    public Persona obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
    }

    @Override
    public List<Persona> listarTodas() {
        return repository.findAll();
    }

    @Override
    public Persona buscarPorDni(String dni) {
        return repository.findByDni(dni).orElse(null);
    }

    @Override
    public Persona buscarPorCorreo(String correo) {
        return repository.findByCorreo(correo).orElse(null);
    }
}
