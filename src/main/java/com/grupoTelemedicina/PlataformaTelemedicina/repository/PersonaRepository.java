package com.grupoTelemedicina.PlataformaTelemedicina.repository;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonaRepository extends JpaRepository <Persona ,Integer>{

    Optional <Persona> findByDni (String dni);
    Optional <Persona> findByCorreo (String correo);
}
