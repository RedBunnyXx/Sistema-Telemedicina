/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoTelemedicina.PlataformaTelemedicina.repository;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Receta;
import com.grupoTelemedicina.PlataformaTelemedicina.dto.RecetaHistorialView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Integer> {

    // Historial de recetas por paciente SIN tocar entidades/relaciones ajenas.
    @Query(value = """
        SELECT
          r.id_receta   AS idReceta,
          r.instrucciones AS instrucciones,
          r.fecha_emision AS fechaEmision
        FROM Recetas r
        JOIN Consultas co ON co.id_consulta = r.id_consulta
        JOIN Citas c      ON c.id_cita     = co.id_cita
        WHERE c.id_paciente = :pacienteId
        ORDER BY r.fecha_emision DESC
    """, nativeQuery = true)
    List<RecetaHistorialView> findHistorialPorPaciente(@Param("pacienteId") Integer pacienteId);
}
