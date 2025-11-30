package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.dto.CrearPersonaRequest;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import com.grupoTelemedicina.PlataformaTelemedicina.service.PersonaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping ("/Persona")
public class PersonaController {

    @Autowired
    private PersonaService service;

    @PostMapping

    public ResponseEntity <Persona> crear (@Valid @RequestBody CrearPersonaRequest  req){
        return ResponseEntity.ok(service.crearPersona(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Persona>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/buscar/dni/{dni}")
    public ResponseEntity<Persona> buscarPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(service.buscarPorDni(dni));
    }

    @GetMapping("/buscar/correo/{correo}")
    public ResponseEntity<Persona> buscarPorCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(service.buscarPorCorreo(correo));
    }

}
