package com.grupoTelemedicina.PlataformaTelemedicina.service;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para el agendamiento y consulta de citas médicas del paciente.
 */
public interface CitaService {

    /**
     * Crea una nueva cita para un paciente y un médico concretos en la fecha/hora indicada.
     */
    Cita crearCitaParaPaciente(Integer idPaciente, Integer idMedico, LocalDateTime fechaCita);

    List<Cita> obtenerCitasProximasPorPaciente(Integer idPaciente);

    List<Cita> obtenerHistorialCitasPorPaciente(Integer idPaciente);
}
