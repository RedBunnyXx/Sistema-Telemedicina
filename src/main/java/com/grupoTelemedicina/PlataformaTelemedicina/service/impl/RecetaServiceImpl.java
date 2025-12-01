/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoTelemedicina.PlataformaTelemedicina.service.impl;

import com.grupoTelemedicina.PlataformaTelemedicina.dto.RecetaHistorialView;
import com.grupoTelemedicina.PlataformaTelemedicina.repository.RecetaRepository;
import com.grupoTelemedicina.PlataformaTelemedicina.service.RecetaService;
import com.grupoTelemedicina.PlataformaTelemedicina.service.RecetaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;

    public RecetaServiceImpl(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    @Override
    public List<RecetaHistorialView> obtenerHistorialPorPaciente(Integer idPaciente) {
        return recetaRepository.findHistorialPorPaciente(idPaciente);
    }
}
