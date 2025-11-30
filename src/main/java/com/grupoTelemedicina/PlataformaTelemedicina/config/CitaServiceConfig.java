package com.grupoTelemedicina.PlataformaTelemedicina.config;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Medico;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Paciente;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.CitaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.MedicoRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PacienteRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.CitaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class CitaServiceConfig {

    @Bean
    public CitaService citaService(CitaRepository citaRepository,
                                   PacienteRepository pacienteRepository,
                                   MedicoRepository medicoRepository) {
        return new CitaService() {
            private final BigDecimal COSTO_BASE = new BigDecimal("80.00");

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
        };
    }
}
