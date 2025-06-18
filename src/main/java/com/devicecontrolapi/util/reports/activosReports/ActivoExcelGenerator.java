package com.devicecontrolapi.util.reports.activosReports;

import com.devicecontrolapi.dto.EstadisticasEspacioDTO;
import com.devicecontrolapi.model.Espacio;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ActivoExcelGenerator {
    
    public ByteArrayOutputStream generarReporteEstadisticas(EstadisticasEspacioDTO estadisticas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle percentageStyle = createPercentageStyle(workbook);
            @SuppressWarnings("unused")
            CellStyle highlightStyle = createHighlightStyle(workbook);
            
            // Hoja 1: Resumen General
            Sheet resumenSheet = workbook.createSheet("Resumen General");
            crearHojaResumen(resumenSheet, estadisticas, titleStyle, headerStyle, dataStyle, percentageStyle);
            
            // Hoja 2: Distribución por Estado
            Sheet estadoSheet = workbook.createSheet("Distribución por Estado");
            crearHojaDistribucionEstado(estadoSheet, estadisticas, titleStyle, headerStyle, dataStyle);
            
            // Hoja 3: Análisis de Capacidad
            Sheet capacidadSheet = workbook.createSheet("Análisis de Capacidad");
            crearHojaAnalisisCapacidad(capacidadSheet, estadisticas, titleStyle, headerStyle, dataStyle);
            
            // Hoja 4: Top Espacios
            if (estadisticas.getTopEspaciosPorCapacidad() != null && !estadisticas.getTopEspaciosPorCapacidad().isEmpty()) {
                Sheet topSheet = workbook.createSheet("Top Espacios");
                crearHojaTopEspacios(topSheet, estadisticas, titleStyle, headerStyle, dataStyle);
            }
            
            // Ajustar columnas automáticamente
            autoSizeColumns(resumenSheet, 2);
            autoSizeColumns(estadoSheet, 3);
            autoSizeColumns(capacidadSheet, 2);
            if (workbook.getSheet("Top Espacios") != null) {
                autoSizeColumns(workbook.getSheet("Top Espacios"), 2);
            }
            
            workbook.write(baos);
            
        } catch (Exception e) {
            log.error("Error al generar Excel de estadísticas: ", e);
        }
        
        return baos;
    }
    
    public ByteArrayOutputStream generarReporteEspacios(List<Espacio> espacios) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle inactiveStyle = createInactiveStyle(workbook);
            
            // Hoja principal
            Sheet sheet = workbook.createSheet("Listado de Espacios");
            
            int rowNum = 0;
            
            // Título
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("LISTADO COMPLETO DE ESPACIOS");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
            
            // Fecha
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Fecha de generación: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
            
            rowNum++; // Línea en blanco
            
            // Encabezados
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"ID", "Nombre", "Descripción", "Estado", "Capacidad", "Activo"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Datos
            int totalActivos = 0;
            int totalEliminados = 0;
            
            for (Espacio espacio : espacios) {
                Row dataRow = sheet.createRow(rowNum++);
                boolean esActivo = espacio.getEliminado() == null || espacio.getEliminado() == 0;
                
                if (esActivo) {
                    totalActivos++;
                } else {
                    totalEliminados++;
                }
                
                // ID
                Cell idCell = dataRow.createCell(0);
                idCell.setCellValue(espacio.getIdespacio());
                idCell.setCellStyle(esActivo ? dataStyle : inactiveStyle);
                
                // Nombre
                Cell nombreCell = dataRow.createCell(1);
                nombreCell.setCellValue(espacio.getNombre() != null ? espacio.getNombre() : "");
                nombreCell.setCellStyle(esActivo ? dataStyle : inactiveStyle);
                
                // Descripción
                Cell descCell = dataRow.createCell(2);
                descCell.setCellValue(espacio.getDescripcion() != null ? espacio.getDescripcion() : "");
                descCell.setCellStyle(esActivo ? dataStyle : inactiveStyle);
                
                // Estado
                Cell estadoCell = dataRow.createCell(3);
                estadoCell.setCellValue(espacio.getEstado() != null ? espacio.getEstado() : "");
                estadoCell.setCellStyle(esActivo ? dataStyle : inactiveStyle);
                
                // Capacidad
                Cell capacidadCell = dataRow.createCell(4);
                capacidadCell.setCellValue(espacio.getCapacidad() != null ? espacio.getCapacidad() : 0);
                capacidadCell.setCellStyle(esActivo ? dataStyle : inactiveStyle);
                
                // Activo
                Cell activoCell = dataRow.createCell(5);
                activoCell.setCellValue(esActivo ? "Sí" : "No");
                activoCell.setCellStyle(esActivo ? dataStyle : inactiveStyle);
            }
            
            // Resumen al final
            rowNum++; // Línea en blanco
            Row summaryRow1 = sheet.createRow(rowNum++);
            summaryRow1.createCell(0).setCellValue("RESUMEN");
            summaryRow1.getCell(0).setCellStyle(headerStyle);
            
            Row summaryRow2 = sheet.createRow(rowNum++);
            summaryRow2.createCell(0).setCellValue("Total de espacios:");
            summaryRow2.createCell(1).setCellValue(espacios.size());
            
            Row summaryRow3 = sheet.createRow(rowNum++);
            summaryRow3.createCell(0).setCellValue("Espacios activos:");
            summaryRow3.createCell(1).setCellValue(totalActivos);
            
            Row summaryRow4 = sheet.createRow(rowNum++);
            summaryRow4.createCell(0).setCellValue("Espacios eliminados:");
            summaryRow4.createCell(1).setCellValue(totalEliminados);
            
            // Ajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(baos);
            
        } catch (Exception e) {
            log.error("Error al generar Excel de espacios: ", e);
        }
        
        return baos;
    }
    
    private void crearHojaResumen(Sheet sheet, EstadisticasEspacioDTO estadisticas,
                                 CellStyle titleStyle, CellStyle headerStyle, 
                                 CellStyle dataStyle, CellStyle percentageStyle) {
        int rowNum = 0;
        
        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ESTADÍSTICAS DE ESPACIOS - RESUMEN GENERAL");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        rowNum++; // Línea en blanco
        
        // Datos generales
        agregarFilaDatos(sheet, rowNum++, "Total de Espacios", estadisticas.getTotalEspacios(), dataStyle);
        agregarFilaDatos(sheet, rowNum++, "Espacios Activos", estadisticas.getTotalEspaciosActivos(), dataStyle);
        agregarFilaDatos(sheet, rowNum++, "Espacios Eliminados", estadisticas.getTotalEspaciosEliminados(), dataStyle);
        agregarFilaDatos(sheet, rowNum++, "Capacidad Total", estadisticas.getCapacidadTotal(), dataStyle);
        
        Row capacidadPromRow = sheet.createRow(rowNum++);
        capacidadPromRow.createCell(0).setCellValue("Capacidad Promedio");
        Cell capacidadPromCell = capacidadPromRow.createCell(1);
        capacidadPromCell.setCellValue(estadisticas.getCapacidadPromedio());
        capacidadPromCell.setCellStyle(dataStyle);
        
        rowNum++; // Línea en blanco
        
        // Porcentajes
        Row porcTitleRow = sheet.createRow(rowNum++);
        porcTitleRow.createCell(0).setCellValue("ANÁLISIS PORCENTUAL");
        porcTitleRow.getCell(0).setCellStyle(headerStyle);
        
        Row activosRow = sheet.createRow(rowNum++);
        activosRow.createCell(0).setCellValue("Porcentaje Espacios Activos");
        Cell activosCell = activosRow.createCell(1);
        activosCell.setCellValue(estadisticas.getPorcentajeEspaciosActivos() / 100);
        activosCell.setCellStyle(percentageStyle);
        
        Row eliminadosRow = sheet.createRow(rowNum++);
        eliminadosRow.createCell(0).setCellValue("Porcentaje Espacios Eliminados");
        Cell eliminadosCell = eliminadosRow.createCell(1);
        eliminadosCell.setCellValue(estadisticas.getPorcentajeEspaciosEliminados() / 100);
        eliminadosCell.setCellStyle(percentageStyle);
    }
    
    private void crearHojaDistribucionEstado(Sheet sheet, EstadisticasEspacioDTO estadisticas,
                                           CellStyle titleStyle, CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = 0;
        
        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DISTRIBUCIÓN DE ESPACIOS POR ESTADO");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        
        rowNum++; // Línea en blanco
        
        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Estado");
        headerRow.createCell(1).setCellValue("Cantidad");
        headerRow.createCell(2).setCellValue("Porcentaje");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        headerRow.getCell(2).setCellStyle(headerStyle);
        
        // Datos
        long total = estadisticas.getTotalEspacios();
        for (Map.Entry<String, Long> entry : estadisticas.getEspaciosPorEstado().entrySet()) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(entry.getKey());
            dataRow.createCell(1).setCellValue(entry.getValue());
            dataRow.getCell(1).setCellStyle(dataStyle);
            
            Cell porcCell = dataRow.createCell(2);
            porcCell.setCellValue(entry.getValue().doubleValue() / total);
            porcCell.setCellStyle(createPercentageStyle(sheet.getWorkbook()));
        }
        
        // Total
        Row totalRow = sheet.createRow(rowNum++);
        totalRow.createCell(0).setCellValue("TOTAL");
        totalRow.createCell(1).setCellValue(total);
        totalRow.createCell(2).setCellValue(1.0);
        totalRow.getCell(0).setCellStyle(headerStyle);
        totalRow.getCell(1).setCellStyle(headerStyle);
        totalRow.getCell(2).setCellStyle(createPercentageStyle(sheet.getWorkbook()));
    }
    
    private void crearHojaAnalisisCapacidad(Sheet sheet, EstadisticasEspacioDTO estadisticas,
                                          CellStyle titleStyle, CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = 0;
        
        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ANÁLISIS DE CAPACIDAD");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        rowNum++; // Línea en blanco
        
        // Estadísticas básicas
        Row basicTitleRow = sheet.createRow(rowNum++);
        basicTitleRow.createCell(0).setCellValue("ESTADÍSTICAS BÁSICAS");
        basicTitleRow.getCell(0).setCellStyle(headerStyle);
        
        agregarFilaDatos(sheet, rowNum++, "Capacidad Máxima", estadisticas.getCapacidadMaxima(), dataStyle);
        agregarFilaDatos(sheet, rowNum++, "Capacidad Mínima", estadisticas.getCapacidadMinima(), dataStyle);
        agregarFilaDatos(sheet, rowNum++, "Capacidad Total", estadisticas.getCapacidadTotal(), dataStyle);
        
        Row promRow = sheet.createRow(rowNum++);
        promRow.createCell(0).setCellValue("Capacidad Promedio");
        Cell promCell = promRow.createCell(1);
        promCell.setCellValue(estadisticas.getCapacidadPromedio());
        promCell.setCellStyle(dataStyle);
        
        rowNum++; // Línea en blanco
        
        // Distribución por rangos
        Row rangosTitleRow = sheet.createRow(rowNum++);
        rangosTitleRow.createCell(0).setCellValue("DISTRIBUCIÓN POR RANGOS DE CAPACIDAD");
        rangosTitleRow.getCell(0).setCellStyle(headerStyle);
        
        Row rangosHeaderRow = sheet.createRow(rowNum++);
        rangosHeaderRow.createCell(0).setCellValue("Rango");
        rangosHeaderRow.createCell(1).setCellValue("Cantidad");
        rangosHeaderRow.getCell(0).setCellStyle(headerStyle);
        rangosHeaderRow.getCell(1).setCellStyle(headerStyle);
        
        if (estadisticas.getRangoCapacidades() != null) {
            for (Map.Entry<String, Integer> entry : estadisticas.getRangoCapacidades().entrySet()) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(entry.getKey());
                dataRow.createCell(1).setCellValue(entry.getValue());
                dataRow.getCell(1).setCellStyle(dataStyle);
            }
        }
    }
    
    private void crearHojaTopEspacios(Sheet sheet, EstadisticasEspacioDTO estadisticas,
                                    CellStyle titleStyle, CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = 0;
        
        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("TOP 10 ESPACIOS POR CAPACIDAD");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        rowNum++; // Línea en blanco
        
        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Espacio");
        headerRow.createCell(1).setCellValue("Capacidad");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        
        // Datos
        int posicion = 1;
        for (Map.Entry<String, Integer> entry : estadisticas.getTopEspaciosPorCapacidad().entrySet()) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(posicion + ". " + entry.getKey());
            dataRow.createCell(1).setCellValue(entry.getValue());
            dataRow.getCell(1).setCellStyle(dataStyle);
            posicion++;
        }
    }
    
    private void agregarFilaDatos(Sheet sheet, int rowNum, String etiqueta, Object valor, CellStyle dataStyle) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(etiqueta);
        Cell valueCell = row.createCell(1);
        
        if (valor instanceof Integer) {
            valueCell.setCellValue((Integer) valor);
        } else if (valor instanceof Long) {
            valueCell.setCellValue((Long) valor);
        } else if (valor instanceof Double) {
            valueCell.setCellValue((Double) valor);
        } else {
            valueCell.setCellValue(valor.toString());
        }
        
        valueCell.setCellStyle(dataStyle);
    }
    
    // Estilos
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createPercentageStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("0.00%"));
        return style;
    }
    
    private CellStyle createHighlightStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    
    private CellStyle createInactiveStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        return style;
    }
    
    private void autoSizeColumns(Sheet sheet, int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}