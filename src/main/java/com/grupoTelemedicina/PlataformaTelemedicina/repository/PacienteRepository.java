package com.grupoTelemedicina.PlataformaTelemedicina.repository;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository <Paciente,Integer> {

    /*Optional <Paciente> findByeDni (String dni);
    Optional <Paciente> findByeCorreo (String correo);
    Optional <Paciente> findByNombres (String nombres);
    Optional <Paciente> finByeApellidos (String apellidoss);*/

}
