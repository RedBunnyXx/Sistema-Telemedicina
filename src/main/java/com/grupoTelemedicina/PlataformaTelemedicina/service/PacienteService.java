package com.grupoTelemedicina.PlataformaTelemedicina.service;

import com.grupoTelemedicina.PlataformaTelemedicina.dto.PacienteDTO;

import java.util.List;

public interface PacienteService {

    List<PacienteDTO> listarPacientes();

    PacienteDTO obtenerPorId(Integer idPaciente);

    PacienteDTO crearPaciente(PacienteDTO dto);

    PacienteDTO actualizarPaciente(Integer idPaciente, PacienteDTO dto);

    void eliminarPaciente(Integer idPaciente);
}

