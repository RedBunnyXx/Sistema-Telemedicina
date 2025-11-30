package com.grupoTelemedicina.PlataformaTelemedicina.service;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaService {

    Cita crearCitaParaPaciente(Integer idPaciente, Integer idMedico, LocalDateTime fechaCita);

    List<Cita> obtenerCitasProximasPorPaciente(Integer idPaciente);

    List<Cita> obtenerHistorialCitasPorPaciente(Integer idPaciente);
}
