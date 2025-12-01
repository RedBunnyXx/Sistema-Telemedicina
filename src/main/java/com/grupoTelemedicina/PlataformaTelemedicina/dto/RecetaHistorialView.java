/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoTelemedicina.PlataformaTelemedicina.dto;

import java.time.LocalDateTime;

// Proyección para query nativa (nombres deben coincidir con alias del SELECT)
public interface RecetaHistorialView {
    Integer getIdReceta();
    String getInstrucciones();
    LocalDateTime getFechaEmision();
}
