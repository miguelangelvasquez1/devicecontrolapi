package com.devicecontrolapi.util.reports.espacioMovsReports;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devicecontrolapi.model.MovEspacio;
import com.devicecontrolapi.service.MovEspacioService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SolicitudEspacioService {

    private final MovEspacioService movEspacioService;
    private final PDFService pdfService; // Servicio para generar PDFs

    /**
     * Exporta el historial de solicitudes a Excel
     */
    public ByteArrayOutputStream exportarHistorialExcel(List<MovEspacio> solicitudesEspecificas) throws IOException {
        List<MovEspacio> solicitudes;
        
        if (solicitudesEspecificas != null && !solicitudesEspecificas.isEmpty()) {
            solicitudes = solicitudesEspecificas;
        } else {
            solicitudes = Collections.emptyList();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historial de Solicitudes");
            
            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle pendingStyle = createStatusStyle(workbook, IndexedColors.GOLD);
            CellStyle approvedStyle = createStatusStyle(workbook, IndexedColors.GREEN);
            CellStyle rejectedStyle = createStatusStyle(workbook, IndexedColors.RED);
            CellStyle cancelledStyle = createStatusStyle(workbook, IndexedColors.GREY_50_PERCENT);
            CellStyle completedStyle = createStatusStyle(workbook, IndexedColors.BLUE);

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "ID", "Espacio", "Ubicación", "Capacidad", "Fecha Solicitud",
                "Fecha Préstamo", "Fecha Devolución", "Duración", "Motivo",
                "Estado", "Solicitante", "Email"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (MovEspacio solicitud : solicitudes) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(solicitud.getIdmov());
                row.createCell(1).setCellValue(solicitud.getEspacio().getNombre());
                row.createCell(2).setCellValue(solicitud.getEspacio().getDescripcion());
                row.createCell(3).setCellValue(solicitud.getEspacio().getCapacidad());
                
                Cell fechaSolicitudCell = row.createCell(4);
                fechaSolicitudCell.setCellValue(solicitud.getFechaSolicitud().format(dateFormatter));
                fechaSolicitudCell.setCellStyle(dateStyle);
                
                Cell fechaPresCell = row.createCell(5);
                fechaPresCell.setCellValue(solicitud.getFechaPres().format(dateFormatter));
                fechaPresCell.setCellStyle(dateStyle);
                
                Cell fechaDevolCell = row.createCell(6);
                fechaDevolCell.setCellValue(solicitud.getFechaDevol().format(dateFormatter));
                fechaDevolCell.setCellStyle(dateStyle);
                
                // Calcular duración
                LocalDateTime inicio = solicitud.getFechaPres();
                LocalDateTime fin = solicitud.getFechaDevol();
                long horas = java.time.Duration.between(inicio, fin).toHours();
                row.createCell(7).setCellValue(horas + " horas");
                
                row.createCell(8).setCellValue(solicitud.getMotivo());
                
                // Estado con estilo
                Cell estadoCell = row.createCell(9);
                estadoCell.setCellValue(solicitud.getEstado());
                switch (solicitud.getEstado()) {
                    case "Pendiente":
                        estadoCell.setCellStyle(pendingStyle);
                        break;
                    case "Aprobado":
                        estadoCell.setCellStyle(approvedStyle);
                        break;
                    case "Rechazado":
                        estadoCell.setCellStyle(rejectedStyle);
                        break;
                    case "Cancelado":
                        estadoCell.setCellStyle(cancelledStyle);
                        break;
                    case "Completado":
                        estadoCell.setCellStyle(completedStyle);
                        break;
                }
                
                row.createCell(10).setCellValue(solicitud.getUsuario().getNombre());
                row.createCell(11).setCellValue(solicitud.getUsuario().getEmail());
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Agregar filtros
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(
                0, rowNum - 1, 0, headers.length - 1
            ));

            // Escribir el archivo
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
            
        } catch (IOException e) {
            log.error("Error al generar el archivo Excel", e);
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }

    /**
     * Crea el estilo para los encabezados
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * Crea el estilo para las fechas
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * Crea el estilo para los estados
     */
    private CellStyle createStatusStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(color.getIndex());
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * Genera el comprobante PDF
     */
    public byte[] generarComprobantePDF(Integer id) {
        MovEspacio solicitud = movEspacioService.obtenerMovEspacioPorId(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));
        
        if (!"Aprobado".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden generar comprobantes de solicitudes aprobadas");
        }
        
        return pdfService.generarComprobanteSolicitud(solicitud);
    }
}