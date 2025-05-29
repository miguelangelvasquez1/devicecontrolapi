package com.devicecontrolapi.util.reports.espaciosReports;

import com.devicecontrolapi.dto.EstadisticasEspacioDTO;
import com.devicecontrolapi.model.Espacio;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EspacioPDFGenerator {
    
    public ByteArrayOutputStream generarReporteEstadisticas(EstadisticasEspacioDTO estadisticas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);
            
            // Título principal
            agregarTitulo(document);
            
            // Sección: Resumen General
            agregarSeccionResumenGeneral(document, estadisticas);
            
            // Sección: Distribución por Estado
            agregarSeccionDistribucionEstado(document, estadisticas);
            
            // Sección: Análisis de Capacidad
            agregarSeccionAnalisisCapacidad(document, estadisticas);
            
            // Sección: Top Espacios
            agregarSeccionTopEspacios(document, estadisticas);
            
            // Sección: Análisis de Utilización
            agregarSeccionUtilizacion(document, estadisticas);
            
            document.close();
            
        } catch (Exception e) {
            log.error("Error al generar PDF de estadísticas: ", e);
        }
        
        return baos;
    }
    
    public ByteArrayOutputStream generarReporteEspacios(List<Espacio> espacios) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate()); // Horizontal para más columnas
            document.setMargins(20, 20, 20, 20);
            
            // Título
            Paragraph titulo = new Paragraph("LISTADO COMPLETO DE ESPACIOS")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(titulo);
            
            // Fecha
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph fechaDoc = new Paragraph("Fecha de generación: " + fecha)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(fechaDoc);
            
            // Tabla de espacios
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{10, 20, 30, 15, 10, 15}))
                    .setWidth(UnitValue.createPercentValue(100));
            
            // Encabezados
            agregarEncabezadoTabla(tabla, "ID");
            agregarEncabezadoTabla(tabla, "Nombre");
            agregarEncabezadoTabla(tabla, "Descripción");
            agregarEncabezadoTabla(tabla, "Estado");
            agregarEncabezadoTabla(tabla, "Capacidad");
            agregarEncabezadoTabla(tabla, "Activo");
            
            // Datos
            for (Espacio espacio : espacios) {
                tabla.addCell(new Cell().add(new Paragraph(espacio.getIdespacio().toString())));
                tabla.addCell(new Cell().add(new Paragraph(espacio.getNombre() != null ? espacio.getNombre() : "")));
                tabla.addCell(new Cell().add(new Paragraph(espacio.getDescripcion() != null ? espacio.getDescripcion() : "")));
                tabla.addCell(new Cell().add(new Paragraph(espacio.getEstado() != null ? espacio.getEstado() : "")));
                tabla.addCell(new Cell().add(new Paragraph(espacio.getCapacidad() != null ? espacio.getCapacidad().toString() : "0")));
                
                String activo = espacio.getEliminado() == null || espacio.getEliminado() == 0 ? "Sí" : "No";
                Cell cellActivo = new Cell().add(new Paragraph(activo));
                if ("No".equals(activo)) {
                    cellActivo.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                }
                tabla.addCell(cellActivo);
            }
            
            document.add(tabla);
            
            // Resumen al final
            document.add(new Paragraph("\n"));
            Paragraph resumen = new Paragraph("Total de espacios: " + espacios.size())
                    .setFontSize(12)
                    .setBold();
            document.add(resumen);
            
            document.close();
            
        } catch (Exception e) {
            log.error("Error al generar PDF de espacios: ", e);
        }
        
        return baos;
    }
    
    private void agregarTitulo(Document document) {
        Paragraph titulo = new Paragraph("REPORTE DE ESTADÍSTICAS DE ESPACIOS")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);
        
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph subtitulo = new Paragraph("Sistema de Control de Dispositivos")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(subtitulo);
        
        Paragraph fechaDoc = new Paragraph("Fecha de generación: " + fecha)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(fechaDoc);
    }
    
    private void agregarSeccionResumenGeneral(Document document, EstadisticasEspacioDTO estadisticas) {
        agregarTituloSeccion(document, "RESUMEN GENERAL");
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100));
        
        agregarFilaTabla(tabla, "Total de Espacios:", estadisticas.getTotalEspacios().toString());
        agregarFilaTabla(tabla, "Espacios Activos:", estadisticas.getTotalEspaciosActivos().toString());
        agregarFilaTabla(tabla, "Espacios Eliminados:", estadisticas.getTotalEspaciosEliminados().toString());
        agregarFilaTabla(tabla, "Capacidad Total:", estadisticas.getCapacidadTotal().toString());
        agregarFilaTabla(tabla, "Capacidad Promedio:", String.format("%.2f", estadisticas.getCapacidadPromedio()));
        agregarFilaTabla(tabla, "Porcentaje Espacios Activos:", String.format("%.2f%%", estadisticas.getPorcentajeEspaciosActivos()));
        agregarFilaTabla(tabla, "Porcentaje Espacios Eliminados:", String.format("%.2f%%", estadisticas.getPorcentajeEspaciosEliminados()));
        
        document.add(tabla);
    }
    
    private void agregarSeccionDistribucionEstado(Document document, EstadisticasEspacioDTO estadisticas) {
        agregarTituloSeccion(document, "DISTRIBUCIÓN POR ESTADO");
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        agregarEncabezadoTabla(tabla, "Estado");
        agregarEncabezadoTabla(tabla, "Cantidad");
        
        // Datos
        for (Map.Entry<String, Long> entry : estadisticas.getEspaciosPorEstado().entrySet()) {
            tabla.addCell(new Cell().add(new Paragraph(entry.getKey())));
            tabla.addCell(new Cell().add(new Paragraph(entry.getValue().toString()))
                    .setTextAlignment(TextAlignment.CENTER));
        }
        
        document.add(tabla);
    }
    
    private void agregarSeccionAnalisisCapacidad(Document document, EstadisticasEspacioDTO estadisticas) {
        agregarTituloSeccion(document, "ANÁLISIS DE CAPACIDAD");
        
        // Estadísticas básicas
        Table tablaBasica = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100));
        
        agregarFilaTabla(tablaBasica, "Capacidad Máxima:", estadisticas.getCapacidadMaxima().toString());
        agregarFilaTabla(tablaBasica, "Capacidad Mínima:", estadisticas.getCapacidadMinima().toString());
        
        document.add(tablaBasica);
        
        // Distribución por rangos
        document.add(new Paragraph("Distribución por Rangos de Capacidad:")
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(5));
        
        Table tablaRangos = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100));
        
        agregarEncabezadoTabla(tablaRangos, "Rango");
        agregarEncabezadoTabla(tablaRangos, "Cantidad");
        
        for (Map.Entry<String, Integer> entry : estadisticas.getRangoCapacidades().entrySet()) {
            tablaRangos.addCell(new Cell().add(new Paragraph(entry.getKey())));
            tablaRangos.addCell(new Cell().add(new Paragraph(entry.getValue().toString()))
                    .setTextAlignment(TextAlignment.CENTER));
        }
        
        document.add(tablaRangos);
    }
    
    private void agregarSeccionTopEspacios(Document document, EstadisticasEspacioDTO estadisticas) {
        if (estadisticas.getTopEspaciosPorCapacidad() != null && !estadisticas.getTopEspaciosPorCapacidad().isEmpty()) {
            agregarTituloSeccion(document, "TOP 10 ESPACIOS POR CAPACIDAD");
            
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                    .setWidth(UnitValue.createPercentValue(100));
            
            agregarEncabezadoTabla(tabla, "Espacio");
            agregarEncabezadoTabla(tabla, "Capacidad");
            
            for (Map.Entry<String, Integer> entry : estadisticas.getTopEspaciosPorCapacidad().entrySet()) {
                tabla.addCell(new Cell().add(new Paragraph(entry.getKey())));
                tabla.addCell(new Cell().add(new Paragraph(entry.getValue().toString()))
                        .setTextAlignment(TextAlignment.CENTER));
            }
            
            document.add(tabla);
        }
    }
    
    private void agregarSeccionUtilizacion(Document document, EstadisticasEspacioDTO estadisticas) {
        if (estadisticas.getPorcentajeUtilizacionPorEstado() != null && !estadisticas.getPorcentajeUtilizacionPorEstado().isEmpty()) {
            agregarTituloSeccion(document, "ANÁLISIS DE UTILIZACIÓN");
            
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                    .setWidth(UnitValue.createPercentValue(100));
            
            agregarEncabezadoTabla(tabla, "Estado");
            agregarEncabezadoTabla(tabla, "Porcentaje");
            
            for (Map.Entry<String, Double> entry : estadisticas.getPorcentajeUtilizacionPorEstado().entrySet()) {
                tabla.addCell(new Cell().add(new Paragraph(entry.getKey())));
                tabla.addCell(new Cell().add(new Paragraph(String.format("%.2f%%", entry.getValue())))
                        .setTextAlignment(TextAlignment.CENTER));
            }
            
            document.add(tabla);
        }
    }
    
    private void agregarTituloSeccion(Document document, String titulo) {
        Paragraph seccion = new Paragraph(titulo)
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5);
        document.add(seccion);
    }
    
    private void agregarFilaTabla(Table tabla, String etiqueta, String valor) {
        tabla.addCell(new Cell().add(new Paragraph(etiqueta).setBold()));
        tabla.addCell(new Cell().add(new Paragraph(valor)));
    }
    
    private void agregarEncabezadoTabla(Table tabla, String texto) {
        Cell cell = new Cell()
                .add(new Paragraph(texto).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        tabla.addHeaderCell(cell);
    }
}