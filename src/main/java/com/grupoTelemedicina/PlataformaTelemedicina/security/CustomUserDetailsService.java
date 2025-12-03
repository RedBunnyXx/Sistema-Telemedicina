package com.grupoTelemedicina.PlataformaTelemedicina.security;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Persona;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.PersonaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonaRepository personaRepository;

    public CustomUserDetailsService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Método llamado por Spring Security durante el proceso de login.
     *
     * Recibe el correo electrónico ingresado en el formulario y:
     * - Busca la entidad Persona correspondiente en la base de datos.
     * - A partir de ella construye un User de Spring con la contraseña encriptada y el rol.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Persona persona = personaRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String role = "ROLE_" + persona.getTipoPersona().toUpperCase();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new User(persona.getCorreo(), persona.getPassword(), authorities);
    }
}
