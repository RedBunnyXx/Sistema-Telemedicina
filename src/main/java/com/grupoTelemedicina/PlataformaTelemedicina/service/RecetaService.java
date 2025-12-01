/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoTelemedicina.PlataformaTelemedicina.service;

import com.grupoTelemedicina.PlataformaTelemedicina.dto.RecetaHistorialView;
import java.util.List;

public interface RecetaService {
    List<RecetaHistorialView> obtenerHistorialPorPaciente(Integer idPaciente);
}
