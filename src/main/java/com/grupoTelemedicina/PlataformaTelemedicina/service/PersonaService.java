package com.grupoTelemedicina.PlataformaTelemedicina.service;

import com.grupoTelemedicina.PlataformaTelemedicina.dto.CrearPersonaRequest;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;

import javax.swing.event.ListSelectionEvent;
import java.util.List;

public interface PersonaService {
    Persona crearPersona (CrearPersonaRequest  request);
    Persona obtenerPorId (Integer id);
    List<Persona> listarTodas ();
    Persona buscarPorDni (String dni);
    Persona buscarPorCorreo (String correo);


}
