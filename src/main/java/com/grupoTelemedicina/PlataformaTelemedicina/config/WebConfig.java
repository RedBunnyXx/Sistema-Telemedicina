package com.grupoTelemedicina.PlataformaTelemedicina.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Expone los archivos subidos de foto de perfil desde la carpeta local
    // "uploads/perfiles" para que puedan ser accedidos vía HTTP como
    // /uploads/perfiles/{nombreArchivo}
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads", "perfiles");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/perfiles/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
