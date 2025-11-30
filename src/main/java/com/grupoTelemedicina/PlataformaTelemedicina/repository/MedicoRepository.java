package com.grupoTelemedicina.PlataformaTelemedicina.repository;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {

    @Query("SELECT m FROM Medico m JOIN FETCH m.persona p JOIN FETCH m.especialidad e WHERE p.tipoPersona = 'Medico'")
    List<Medico> findAllConPersonaYEspecialidad();
}
