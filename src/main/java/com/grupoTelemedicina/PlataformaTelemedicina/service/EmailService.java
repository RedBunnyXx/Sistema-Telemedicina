package com.grupoTelemedicina.PlataformaTelemedicina.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Usamos @Async para que el envío del correo no "congele" la respuesta al usuario
    @Async
    public void sendCitaConfirmation(String to,
                                     String pacienteNombre,
                                     String doctorNombre,
                                     String especialidad,
                                     java.time.LocalDateTime fechaHora,
                                     BigDecimal monto,
                                     String idTransaccion) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Formatear la fecha bonito (ej: Viernes, 05 de Diciembre a las 16:00)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'a las' HH:mm", new Locale("es", "ES"));
            String fechaFormateada = fechaHora.format(formatter);
            // Capitalizar primera letra
            fechaFormateada = fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);

            // Construir el HTML (Diseño Azul Solicitado)
            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px;'>" +
                    "  <div style='max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                    "    " +
                    "    " +
                    "    <div style='background-color: #0d6efd; color: white; padding: 20px; text-align: center;'>" +
                    "      <h1 style='margin: 0; font-size: 24px;'>¡Cita Confirmada y Pagada!</h1>" +
                    "      <p style='margin: 5px 0 0; opacity: 0.9;'>Resumen de tu Teleconsulta</p>" +
                    "    </div>" +
                    "    " +
                    "    <div style='padding: 30px;'>" +
                    "      <p style='font-size: 16px; color: #333;'>Hola <strong>" + pacienteNombre + "</strong>:</p>" +
                    "      <p style='color: #555;'>Tu reserva ha sido procesada con éxito. Aquí tienes los detalles de tu próxima atención:</p>" +
                    "      " +
                    "      " +
                    "      <table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>" +
                    "        <tr>" +
                    "          <td style='padding: 10px 0; border-bottom: 1px solid #eee; font-weight: bold; color: #333;'>Doctor:</td>" +
                    "          <td style='padding: 10px 0; border-bottom: 1px solid #eee; color: #555; text-align: right;'>" + doctorNombre + "</td>" +
                    "        </tr>" +
                    "        <tr>" +
                    "          <td style='padding: 10px 0; border-bottom: 1px solid #eee; font-weight: bold; color: #333;'>Especialidad:</td>" +
                    "          <td style='padding: 10px 0; border-bottom: 1px solid #eee; color: #555; text-align: right;'>" + especialidad + "</td>" +
                    "        </tr>" +
                    "        <tr>" +
                    "          <td style='padding: 10px 0; border-bottom: 1px solid #eee; font-weight: bold; color: #333;'>Fecha y Hora:</td>" +
                    "          <td style='padding: 10px 0; border-bottom: 1px solid #eee; color: #0d6efd; font-weight: bold; text-align: right;'>" + fechaFormateada + "</td>" +
                    "        </tr>" +
                    "      </table>" +
                    "      " +
                    "      " +
                    "      <div style='background-color: #f0f7ff; border: 1px solid #cce5ff; border-radius: 8px; padding: 20px; margin-top: 30px;'>" +
                    "        <h3 style='margin-top: 0; color: #0d6efd; font-size: 18px;'>Detalles del Pago</h3>" +
                    "        <div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;'>" +
                    "          <span style='font-weight: bold; font-size: 18px; color: #333;'>Monto Total:</span>" +
                    "          <span style='font-weight: bold; font-size: 20px; color: #28a745;'>S/ " + monto.setScale(2) + "</span>" +
                    "        </div>" +
                    "        <p style='margin: 5px 0; color: #666; font-size: 14px;'>Método: Tarjeta (Procesado por Stripe)</p>" +
                    "        <p style='margin: 5px 0; color: #666; font-size: 14px;'>ID Transacción: <span style='font-family: monospace;'>" + idTransaccion + "</span></p>" +
                    "      </div>" +
                    "      " +
                    "      <p style='text-align: center; margin-top: 30px; color: #888; font-size: 14px;'>" +
                    "        Recibirás un enlace para la videollamada antes de la cita.<br>¡Gracias por confiar en nosotros!" +
                    "      </p>" +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true); // true indica que es HTML
            helper.setTo(to);
            helper.setSubject("Confirmación de Cita - Telemedicina");
            // helper.setFrom("tu_correo@gmail.com"); // Opcional, Gmail suele sobrescribirlo

            mailSender.send(mimeMessage);
            System.out.println("Correo de confirmación enviado a: " + to);

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            // No lanzamos la excepción para no romper el flujo principal del usuario
        }
    }
}
