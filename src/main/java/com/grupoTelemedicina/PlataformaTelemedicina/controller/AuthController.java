package com.grupoTelemedicina.PlataformaTelemedicina.controller;

import com.grupoTelemedicina.PlataformaTelemedicina.dto.PacienteDTO;
import com.grupoTelemedicina.PlataformaTelemedicina.dto.RegistroPacienteForm;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PersonaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final PacienteService pacienteService;
    private final PersonaRepository personaRepository;

    public AuthController(PacienteService pacienteService, PersonaRepository personaRepository) {
        this.pacienteService = pacienteService;
        this.personaRepository = personaRepository;
    }

    /**
     * Muestra la página de login.
     *
     * Nota importante: el procesamiento del formulario de login (POST /login)
     * lo maneja directamente Spring Security según lo definido en SecurityConfig,
     * por eso aquí solo devolvemos la vista.
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "login";
    }

    /**
     * Muestra el formulario de registro de paciente.
     *
     * Si es la primera vez que se carga, inicializa un objeto RegistroPacienteForm
     * que se enlaza con los campos del formulario en registro.html.
     */
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        if (!model.containsAttribute("registroForm")) {
            model.addAttribute("registroForm", new RegistroPacienteForm());
        }
        return "registro";
    }

    /**
     * Procesa el envío del formulario de registro.
     *
     * Flujo de registro:
     * - Valida que las contraseñas coincidan y que el DNI/correo no estén repetidos.
     * - Si hay errores, vuelve a la vista de registro mostrando los mensajes.
     * - Si todo es correcto, construye un PacienteDTO y llama a PacienteService.crearPaciente,
     *   que es donde se crean las entidades Persona y Paciente y se encripta la contraseña.
     * - Finalmente redirige al login con un mensaje de registro exitoso.
     */
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("registroForm") RegistroPacienteForm form,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (!form.getPassword().equals(form.getConfirm_password())) {
            bindingResult.rejectValue("confirm_password", "password.mismatch", "Las contraseñas no coinciden");
        }

        if (personaRepository.findByDni(form.getDni()).isPresent()) {
            bindingResult.rejectValue("dni", "dni.duplicado", "El DNI ya está registrado");
        }

        if (personaRepository.findByCorreo(form.getCorreo()).isPresent()) {
            bindingResult.rejectValue("correo", "correo.duplicado", "El correo ya está registrado");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("registroForm", form);
            return "registro";
        }

        PacienteDTO dto = new PacienteDTO();
        dto.setNombres(form.getNombres());
        dto.setApellidos(form.getApellidos());
        dto.setDni(form.getDni());
        dto.setCorreo(form.getCorreo());
        dto.setTelefono(form.getTelefono());
        dto.setPassword(form.getPassword());
        dto.setFechaNacimiento(form.getFecha_nacimiento());
        dto.setSexo(form.getSexo());

        pacienteService.crearPaciente(dto);

        redirectAttributes.addFlashAttribute("registroExitoso", true);

        return "redirect:/login";
    }
}
