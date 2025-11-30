package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.dto.PacienteDTO;
import com.grupoTelemedicina.PlataformaTelemedicina.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")

public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listar() {
        return ResponseEntity.ok(pacienteService.listarPacientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PacienteDTO> crear(@RequestBody PacienteDTO dto) {
        return ResponseEntity.ok(pacienteService.crearPaciente(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> actualizar(@PathVariable Integer id,
                                                  @RequestBody PacienteDTO dto) {
        return ResponseEntity.ok(pacienteService.actualizarPaciente(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }

}
