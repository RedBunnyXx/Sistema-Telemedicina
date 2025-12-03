package com.grupoTelemedicina.PlataformaTelemedicina.service;

import com.grupoTelemedicina.PlataformaTelemedicina.Model.Cita;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZoomMeetingService {

    private static final Logger logger = LoggerFactory.getLogger(ZoomMeetingService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String token;
    private final String defaultTopic;
    private final String defaultTimeZone;
    private final boolean mockEnabled;

    public ZoomMeetingService(
            @Value("${zoom.api.base-url:https://api.zoom.us/v2}") String baseUrl,
            @Value("${zoom.api.token:}") String token,
            @Value("${zoom.meeting.default-topic:Teleconsulta MediConnect}") String defaultTopic,
            @Value("${zoom.meeting.timezone:America/Lima}") String defaultTimeZone,
            @Value("${zoom.mock.enabled:true}") boolean mockEnabled) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.defaultTopic = defaultTopic;
        this.defaultTimeZone = defaultTimeZone;
        this.mockEnabled = mockEnabled;
    }

    /**
     * Crea una reunión de Zoom para la cita indicada y devuelve el join_url.
     * Si ocurre algún error o no hay token configurado, devuelve null.
     */
    public String crearReunionParaCita(Cita cita) {
        if (mockEnabled) {
            return crearLinkMock(cita);
        }

        if (token == null || token.isBlank()) {
            logger.warn("No se ha configurado zoom.api.token; no se creará reunión de Zoom para la cita.");
            return null;
        }

        try {
            String url = baseUrl + "/users/me/meetings";

            Map<String, Object> body = new HashMap<>();

            String topic = defaultTopic;
            if (cita.getMedico() != null && cita.getMedico().getPersona() != null) {
                topic = "Teleconsulta con " + cita.getMedico().getPersona().getNombres() + " " + cita.getMedico().getPersona().getApellidos();
            }
            body.put("topic", topic);
            body.put("type", 2); // scheduled meeting

            if (cita.getFechaCita() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                var zonedDateTime = cita.getFechaCita().atZone(ZoneId.of(defaultTimeZone));
                body.put("start_time", formatter.format(zonedDateTime));
                body.put("timezone", defaultTimeZone);
            }

            body.put("duration", 30); // minutos

            Map<String, Object> settings = new HashMap<>();
            settings.put("join_before_host", false);
            settings.put("waiting_room", true);
            body.put("settings", settings);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(url, entity, (Class<Map<String, Object>>)(Class<?>)Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object joinUrl = response.getBody().get("join_url");
                if (joinUrl != null) {
                    return joinUrl.toString();
                } else {
                    logger.warn("Respuesta de Zoom sin join_url.");
                }
            } else {
                logger.warn("Error al crear reunión en Zoom. Código de estado: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.error("Error al crear reunión de Zoom", ex);
        }

        return null;
    }

    private String crearLinkMock(Cita cita) {
        int baseHash = cita.getFechaCita() != null
                ? cita.getFechaCita().hashCode()
                : cita.hashCode();
        long normalized = Math.abs((long) baseHash) % 1_000_000_000L;
        String meetingId = String.format("%09d", normalized);
        return "https://zoom.us/j/" + meetingId;
    }
}
