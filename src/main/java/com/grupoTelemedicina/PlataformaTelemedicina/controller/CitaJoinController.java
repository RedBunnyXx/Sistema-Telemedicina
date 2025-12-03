package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.CitaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PersonaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.ZoomMeetingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador que permite al paciente unirse a la videollamada de una cita.
 *
 * Flujo general del endpoint /citas/{id}/join:
 * - Verifica que el usuario esté autenticado y resuelve su Persona.
 * - Valida que la cita exista y pertenezca a ese paciente.
 * - Si la cita no tiene enlace Zoom, lo solicita a ZoomMeetingService.
 * - Finalmente redirige al navegador hacia la URL de la reunión Zoom.
 */
@Controller
public class CitaJoinController {

    private final CitaRepository citaRepository;
    private final PersonaRepository personaRepository;
    private final ZoomMeetingService zoomMeetingService;

    public CitaJoinController(CitaRepository citaRepository,
                              PersonaRepository personaRepository,
                              ZoomMeetingService zoomMeetingService) {
        this.citaRepository = citaRepository;
        this.personaRepository = personaRepository;
        this.zoomMeetingService = zoomMeetingService;
    }

    @GetMapping("/citas/{id}/join")
    public String joinCita(@PathVariable("id") Integer idCita,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        String correo = userDetails.getUsername();
        Optional<Persona> personaOpt = personaRepository.findByCorreo(correo);
        if (personaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("joinError", "No se pudo identificar al paciente autenticado.");
            return "redirect:/inicio";
        }

        Persona persona = personaOpt.get();

        Optional<Cita> citaOpt = citaRepository.findById(idCita);
        if (citaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("joinError", "La cita seleccionada no existe o ya no está disponible.");
            return "redirect:/inicio";
        }

        Cita cita = citaOpt.get();

        if (cita.getPaciente() == null || cita.getPaciente().getPersona() == null
                || !cita.getPaciente().getPersona().getId().equals(persona.getId())) {
            redirectAttributes.addFlashAttribute("joinError", "No tienes permiso para unirte a esta cita.");
            return "redirect:/inicio";
        }

        String zoomUrl = cita.getZoomUrl();
        if (zoomUrl == null || zoomUrl.isBlank()) {
            zoomUrl = zoomMeetingService.crearReunionParaCita(cita);
            if (zoomUrl != null && !zoomUrl.isBlank()) {
                cita.setZoomUrl(zoomUrl);
                citaRepository.save(cita);
            } else {
                redirectAttributes.addFlashAttribute("joinError", "Esta cita aún no tiene un enlace de Zoom asignado. Intenta nuevamente más tarde.");
                return "redirect:/inicio";
            }
        }

        return "redirect:" + zoomUrl;
    }
}
