package com.grupoTelemedicina.PlataformaTelemedicina.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class RegistroPacienteForm {

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    @NotBlank
    @Size(min = 8, max = 8)
    private String dni;

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String telefono;

    @NotBlank
    private String password;

    @NotBlank
    private String confirm_password;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha_nacimiento;

    @NotBlank
    private String sexo;

    @AssertTrue(message = "Debe aceptar los Términos y Condiciones")
    private boolean aceptaTerminos;

    @AssertTrue(message = "Debe aceptar la Política de Privacidad")
    private boolean aceptaPrivacidad;

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    public LocalDate getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(LocalDate fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public boolean isAceptaTerminos() {
        return aceptaTerminos;
    }

    public void setAceptaTerminos(boolean aceptaTerminos) {
        this.aceptaTerminos = aceptaTerminos;
    }

    public boolean isAceptaPrivacidad() {
        return aceptaPrivacidad;
    }

    public void setAceptaPrivacidad(boolean aceptaPrivacidad) {
        this.aceptaPrivacidad = aceptaPrivacidad;
    }
}
