package com.devicecontrolapi.util.reports.espacioMovsReports;

import com.devicecontrolapi.model.MovEspacio;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PDFService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public byte[] generarComprobanteSolicitud(MovEspacio solicitud) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Título
            Paragraph title = new Paragraph("COMPROBANTE DE SOLICITUD DE ESPACIO")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);
            
            // Información de la solicitud
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60}));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // ID de solicitud
            addTableRow(table, "ID de Solicitud:", "#" + solicitud.getIdmov());
            
            // Estado
            addTableRow(table, "Estado:", solicitud.getEstado().toString());
            
            // Fecha de solicitud
            addTableRow(table, "Fecha de Solicitud:", 
                    solicitud.getFechaSolicitud().format(DATE_FORMATTER));
            
            // Espacio
            addTableRow(table, "Espacio:", solicitud.getEspacio().getNombre());
            
            // Ubicación
            addTableRow(table, "Descripción:", solicitud.getEspacio().getDescripcion());
            
            // Capacidad
            addTableRow(table, "Capacidad:", 
                    solicitud.getEspacio().getCapacidad() + " personas");
            
            // Fecha y hora de préstamo
            LocalDateTime fechaPres = solicitud.getFechaPres();
            addTableRow(table, "Fecha y Hora de Inicio:", 
                    fechaPres.format(DATE_FORMATTER));
            
            // Fecha y hora de devolución
            LocalDateTime fechaDevol = solicitud.getFechaDevol();
            addTableRow(table, "Fecha y Hora de Fin:", 
                    fechaDevol.format(DATE_FORMATTER));
            
            // Duración
            long horas = java.time.Duration.between(fechaPres, fechaDevol).toHours();
            addTableRow(table, "Duración:", horas + " horas");
            
            // Solicitante
            addTableRow(table, "Solicitante:", solicitud.getUsuario().getNombre());
            
            // Email
            addTableRow(table, "Email:", solicitud.getUsuario().getEmail());
            
            // Motivo
            addTableRow(table, "Motivo:", solicitud.getMotivo());
            
            document.add(table);
            
            // Pie de página
            Paragraph footer = new Paragraph()
                    .add("\n\n")
                    .add("Este comprobante es válido como constancia de aprobación de la solicitud.")
                    .add("\n")
                    .add("Fecha de generación: " + LocalDateTime.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);
            
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error al generar PDF", e);
            throw new RuntimeException("Error al generar el comprobante PDF", e);
        }
    }
    
    private void addTableRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label))
                .setBold()
                .setBackgroundColor(new DeviceRgb(240, 240, 240))
                .setPadding(5);
        
        Cell valueCell = new Cell()
                .add(new Paragraph(value))
                .setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}