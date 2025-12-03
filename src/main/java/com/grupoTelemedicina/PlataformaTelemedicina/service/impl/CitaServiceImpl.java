package com.grupoTelemedicina.PlataformaTelemedicina.service.impl;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Medico;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Paciente;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.CitaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.MedicoRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PacienteRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.CitaService;
import com.grupoTelemedicina.PlataformaTelemedicina.service.ZoomMeetingService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ZoomMeetingService zoomMeetingService;

    private static final BigDecimal COSTO_BASE = new BigDecimal("80.00");

    public CitaServiceImpl(CitaRepository citaRepository,
                           PacienteRepository pacienteRepository,
                           MedicoRepository medicoRepository,
                           ZoomMeetingService zoomMeetingService) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.zoomMeetingService = zoomMeetingService;
    }

    /**
     * Implementa el servicio de agendamiento de una cita.
     *
     * Flujo de agendamiento:
     * - Un controlador del flujo de agendamiento/pago llama a este método
     *   pasando el id del paciente, el id del médico y la fecha/hora escogida.
     * - Se validan paciente y médico, se arma la entidad Cita con estado y costo.
     * - Se solicita a ZoomMeetingService crear la reunión de videollamada
     *   y se guarda la URL generada en el campo zoomUrl.
     * - Finalmente se persiste la cita en la base de datos.
     */
    @Override
    public Cita crearCitaParaPaciente(Integer idPaciente, Integer idMedico, LocalDateTime fechaCita) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setFechaCita(fechaCita);
        cita.setEstado("Pendiente");
        cita.setCosto(COSTO_BASE);

        String zoomUrl = zoomMeetingService.crearReunionParaCita(cita);
        if (zoomUrl != null && !zoomUrl.isBlank()) {
            cita.setZoomUrl(zoomUrl);
        }

        return citaRepository.save(cita);
    }

    @Override
    public List<Cita> obtenerCitasProximasPorPaciente(Integer idPaciente) {
        LocalDateTime ahora = LocalDateTime.now();
        return citaRepository.findProximasPorPaciente(idPaciente, ahora);
    }

    @Override
    public List<Cita> obtenerHistorialCitasPorPaciente(Integer idPaciente) {
        LocalDateTime ahora = LocalDateTime.now();
        return citaRepository.findHistorialPorPaciente(idPaciente, ahora);
    }
}
