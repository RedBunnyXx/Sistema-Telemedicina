package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Medico;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Paciente;
import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.MedicoRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PacienteRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PersonaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.CitaService;
import com.grupoTelemedicina.PlataformaTelemedicina.service.RecetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

/**
 * Controlador principal del área de paciente.
 *
 * Gestiona las vistas de:
 * - Dashboard de inicio (/inicio).
 * - Perfil del usuario (/perfil) y actualización de datos/foto.
 * - Listado de citas (/citas) y flujo de agendamiento (/agendamiento).
 */
@Controller
public class inicioController {

    private static final Logger logger = LoggerFactory.getLogger(inicioController.class);

    private final PersonaRepository personaRepository;
    private final PacienteRepository pacienteRepository;
    private final UserDetailsService userDetailsService;
    private final MedicoRepository medicoRepository;
    private final CitaService citaService;
    private final RecetaService recetaService;


    private static final long MAX_FOTO_SIZE = 2 * 1024 * 1024; // 2 MB
    private static final String CONTENT_TYPE_JPEG = "image/jpeg";
    private static final String CONTENT_TYPE_PNG = "image/png";

    public inicioController(PersonaRepository personaRepository,
                            PacienteRepository pacienteRepository,
                            UserDetailsService userDetailsService,
                            MedicoRepository medicoRepository,
                            CitaService citaService,
                            RecetaService recetaService) {
        this.personaRepository = personaRepository;
        this.pacienteRepository = pacienteRepository;
        this.userDetailsService = userDetailsService;
        this.medicoRepository = medicoRepository;
        this.citaService = citaService;
        this.recetaService = recetaService;
    }



    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    /**
     * Dashboard principal del paciente después de un login exitoso.
     *
     * Usa el usuario autenticado (userDetails) para:
     * - Buscar la Persona y el Paciente en base de datos.
     * - Cargar en el modelo los contadores y la lista de próximas citas que se muestran en inicio.html.
     */
    @GetMapping("/inicio")
    public String inicio(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        int proximasCitasCount = 0;
        int recetasActivasCount = 0;
        int resultadosNuevosCount = 0;
        int consultasTotalesCount = 0;

        java.util.List<Cita> citasProximasDashboard = java.util.Collections.emptyList();
        java.util.List<?> recetasRecientes = java.util.Collections.emptyList();
        java.util.List<?> resultadosLaboratorio = java.util.Collections.emptyList();

        if (userDetails != null) {
            String correo = userDetails.getUsername();
            Persona persona = personaRepository.findByCorreo(correo).orElse(null);
            if (persona != null) {
                model.addAttribute("persona", persona);
                model.addAttribute("nombreCompleto", persona.getNombres() + " " + persona.getApellidos());
                model.addAttribute("tipoPersona", persona.getTipoPersona());
                model.addAttribute("activePage", "inicio");

                Paciente paciente = pacienteRepository.findById(persona.getId()).orElse(null);
                if (paciente != null) {
                    java.util.List<Cita> citasProximas = citaService.obtenerCitasProximasPorPaciente(paciente.getId());
                    java.util.List<Cita> citasHistorial = citaService.obtenerHistorialCitasPorPaciente(paciente.getId());

                    if (citasProximas != null) {
                        proximasCitasCount = citasProximas.size();
                        consultasTotalesCount += citasProximas.size();
                        citasProximasDashboard = citasProximas;
                    }
                    if (citasHistorial != null) {
                        consultasTotalesCount += citasHistorial.size();
                    }
                }
            }
        }

        model.addAttribute("proximasCitasCount", proximasCitasCount);
        model.addAttribute("recetasActivasCount", recetasActivasCount);
        model.addAttribute("resultadosNuevosCount", resultadosNuevosCount);
        model.addAttribute("consultasTotalesCount", consultasTotalesCount);

        model.addAttribute("citasProximasDashboard", citasProximasDashboard);
        model.addAttribute("recetasRecientes", recetasRecientes);
        model.addAttribute("resultadosLaboratorio", resultadosLaboratorio);

        return "inicio";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String correo = userDetails.getUsername();
            Persona persona = personaRepository.findByCorreo(correo).orElse(null);
            if (persona != null) {
                model.addAttribute("persona", persona);
                model.addAttribute("nombreCompleto", persona.getNombres() + " " + persona.getApellidos());
                model.addAttribute("tipoPersona", persona.getTipoPersona());
                model.addAttribute("activePage", "perfil");

                Paciente paciente = pacienteRepository.findById(persona.getId()).orElse(null);
                if (paciente != null) {
                    model.addAttribute("paciente", paciente);
                }

                if (persona.getFotoPerfil() != null && !persona.getFotoPerfil().isEmpty()) {
                    Path uploadDir = Paths.get("uploads", "perfiles");
                    Path fotoPath = uploadDir.resolve(persona.getFotoPerfil());
                    if (!Files.exists(fotoPath)) {
                        persona.setFotoPerfil(null);
                        personaRepository.save(persona);
                    }
                }
            }
        }
        return "perfil";
    }

    /**
     * Vista de "Mis Citas" para el paciente.
     *
     * Aquí solo se listan las citas:
     * - Llama a CitaService.obtenerCitasProximasPorPaciente y obtenerHistorialCitasPorPaciente
     *   para mostrar tanto las próximas citas como el historial en citas.html.
     */
    @GetMapping("/citas")
    public String citas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String correo = userDetails.getUsername();
            Persona persona = personaRepository.findByCorreo(correo).orElse(null);
            if (persona != null) {
                model.addAttribute("persona", persona);
                model.addAttribute("nombreCompleto", persona.getNombres() + " " + persona.getApellidos());
                model.addAttribute("tipoPersona", persona.getTipoPersona());
                model.addAttribute("activePage", "citas");

                Paciente paciente = pacienteRepository.findById(persona.getId()).orElse(null);
                if (paciente != null) {
                    java.util.List<Cita> citasProximas = citaService.obtenerCitasProximasPorPaciente(paciente.getId());
                    java.util.List<Cita> citasHistorial = citaService.obtenerHistorialCitasPorPaciente(paciente.getId());
                    model.addAttribute("citasProximas", citasProximas);
                    model.addAttribute("citasHistorial", citasHistorial);
                }
            }
        }
        return "citas";
    }
    
@GetMapping("/recetas")
    public String recetas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String correo = userDetails.getUsername();
            Persona persona = personaRepository.findByCorreo(correo).orElse(null);
            if (persona != null) {
                model.addAttribute("persona", persona);
                model.addAttribute("nombreCompleto", persona.getNombres() + " " + persona.getApellidos());
                model.addAttribute("tipoPersona", persona.getTipoPersona());
                model.addAttribute("activePage", "recetas");

                Paciente paciente = pacienteRepository.findById(persona.getId()).orElse(null);
                if (paciente != null) {
                    var recetas = recetaService.obtenerHistorialPorPaciente(paciente.getId());
                    model.addAttribute("recetas", recetas);
                } else {
                    model.addAttribute("recetas", java.util.Collections.emptyList());
                }
            }
        }
        return "recetas"; // templates/recetas.html
}



    /**
     * Punto de entrada al flujo de agendamiento de una nueva cita.
     *
     * Flujo a alto nivel:
     * - Obtiene al paciente autenticado a partir de userDetails.
     * - Carga en el modelo la lista de médicos disponibles para que el usuario elija.
     * - La vista agendamiento.html usa estos datos y, al confirmar la reserva
     *   (junto con el pago), se llamará a CitaService.crearCitaParaPaciente desde
     *   el controlador de pago para crear efectivamente la cita en base de datos.
     */
    @GetMapping("/agendamiento")
    public String agendamiento(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String correo = userDetails.getUsername();
            Persona persona = personaRepository.findByCorreo(correo).orElse(null);
            if (persona != null) {
                model.addAttribute("persona", persona);
                model.addAttribute("nombreCompleto", persona.getNombres() + " " + persona.getApellidos());
                model.addAttribute("tipoPersona", persona.getTipoPersona());
                // Seguimos resaltando 'Mis Citas' en el sidebar durante el flujo de agendamiento
                model.addAttribute("activePage", "citas");

                List<Medico> medicos;
                try {
                    medicos = medicoRepository.findAllConPersonaYEspecialidad();
                } catch (Exception e) {
                    logger.error("Error cargando lista de médicos para la vista de agendamiento", e);
                    medicos = java.util.Collections.emptyList();
                }
                model.addAttribute("medicos", medicos);
            }
        }
        return "agendamiento";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam String telefono,
                                   @RequestParam String correo,
                                   @RequestParam(required = false) String direccion,
                                   RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        String correoActual = userDetails.getUsername();
        Persona persona = personaRepository.findByCorreo(correoActual).orElse(null);
        if (persona == null) {
            return "redirect:/login";
        }

        if (!correo.equalsIgnoreCase(persona.getCorreo())) {
            Optional<Persona> existente = personaRepository.findByCorreo(correo);
            if (existente.isPresent() && !existente.get().getId().equals(persona.getId())) {
                redirectAttributes.addFlashAttribute("errorPerfil", "El correo ya está registrado por otro usuario.");
                return "redirect:/perfil";
            }
        }

        persona.setTelefono(telefono);
        persona.setCorreo(correo);
        persona.setDireccion(direccion);
        personaRepository.save(persona);

        if (!correo.equalsIgnoreCase(correoActual)) {
            UserDetails nuevoUserDetails = userDetailsService.loadUserByUsername(correo);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    nuevoUserDetails,
                    userDetails.getPassword(),
                    nuevoUserDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        redirectAttributes.addFlashAttribute("perfilActualizado", true);
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/foto")
    public String actualizarFotoPerfil(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam("foto") MultipartFile foto,
                                       RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        if (foto == null || foto.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorFoto", "Debe seleccionar una imagen.");
            return "redirect:/perfil";
        }

        if (foto.getSize() > MAX_FOTO_SIZE) {
            redirectAttributes.addFlashAttribute("errorFoto", "La imagen no debe superar 2 MB.");
            return "redirect:/perfil";
        }

        String contentType = foto.getContentType();
        if (contentType == null ||
                (!CONTENT_TYPE_JPEG.equalsIgnoreCase(contentType) && !CONTENT_TYPE_PNG.equalsIgnoreCase(contentType))) {
            redirectAttributes.addFlashAttribute("errorFoto", "Solo se permiten imágenes JPG o PNG.");
            return "redirect:/perfil";
        }

        String correoActual = userDetails.getUsername();
        Persona persona = personaRepository.findByCorreo(correoActual).orElse(null);
        if (persona == null) {
            return "redirect:/login";
        }

        try {
            Path uploadDir = Paths.get("uploads", "perfiles");
            Files.createDirectories(uploadDir);

            if (persona.getFotoPerfil() != null && !persona.getFotoPerfil().isEmpty()) {
                Path fotoAnterior = uploadDir.resolve(persona.getFotoPerfil());
                Files.deleteIfExists(fotoAnterior);
            }

            String originalFilename = foto.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String nuevoNombre = "perfil_" + persona.getId() + "_" + System.currentTimeMillis() + extension;
            Path destino = uploadDir.resolve(nuevoNombre);
            try (InputStream inputStream = foto.getInputStream()) {
                Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            }

            persona.setFotoPerfil(nuevoNombre);
            personaRepository.save(persona);

            redirectAttributes.addFlashAttribute("fotoActualizada", true);
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorFoto", "Ocurrió un error al guardar la imagen.");
        }

        return "redirect:/perfil";
    }
}
