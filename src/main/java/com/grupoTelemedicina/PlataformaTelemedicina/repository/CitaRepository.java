package com.grupoTelemedicina.PlataformaTelemedicina.repository;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

    @Query("SELECT c FROM Cita c " +
           "JOIN FETCH c.medico m " +
           "JOIN FETCH m.persona p " +
           "JOIN FETCH m.especialidad e " +
           "WHERE c.paciente.id = :pacienteId AND c.fechaCita > :fechaActual " +
           "ORDER BY c.fechaCita ASC")
    List<Cita> findProximasPorPaciente(@Param("pacienteId") Integer pacienteId,
                                       @Param("fechaActual") LocalDateTime fechaActual);

    @Query("SELECT c FROM Cita c " +
           "JOIN FETCH c.medico m " +
           "JOIN FETCH m.persona p " +
           "JOIN FETCH m.especialidad e " +
           "WHERE c.paciente.id = :pacienteId AND c.fechaCita <= :fechaActual " +
           "ORDER BY c.fechaCita DESC")
    List<Cita> findHistorialPorPaciente(@Param("pacienteId") Integer pacienteId,
                                        @Param("fechaActual") LocalDateTime fechaActual);
}
