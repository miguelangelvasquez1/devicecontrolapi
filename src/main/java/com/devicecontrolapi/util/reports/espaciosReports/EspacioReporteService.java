package com.devicecontrolapi.util.reports.espaciosReports;

import com.devicecontrolapi.dto.EstadisticasEspacioDTO;
import com.devicecontrolapi.model.Espacio;
import com.devicecontrolapi.repository.EspacioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EspacioReporteService {
    
    private final EspacioRepository espacioRepository;
    private final EspacioEstadisticasService estadisticasService;
    private final EspacioPDFGenerator pdfGenerator;
    private final EspacioExcelGenerator excelGenerator;
    
    /**
     * Genera un reporte PDF con estadísticas de espacios
     */
    public ByteArrayOutputStream generarReporteEstadisticasPDF() {
        try {
            log.info("Iniciando generación de reporte PDF de estadísticas...");
            EstadisticasEspacioDTO estadisticas = estadisticasService.generarEstadisticas();
            ByteArrayOutputStream reporte = pdfGenerator.generarReporteEstadisticas(estadisticas);
            log.info("Reporte PDF de estadísticas generado exitosamente");
            return reporte;
        } catch (Exception e) {
            log.error("Error al generar reporte PDF de estadísticas: ", e);
            throw new RuntimeException("Error al generar reporte PDF de estadísticas", e);
        }
    }
    
    /**
     * Genera un reporte Excel con estadísticas de espacios
     */
    public ByteArrayOutputStream generarReporteEstadisticasExcel() {
        try {
            log.info("Iniciando generación de reporte Excel de estadísticas...");
            EstadisticasEspacioDTO estadisticas = estadisticasService.generarEstadisticas();
            ByteArrayOutputStream reporte = excelGenerator.generarReporteEstadisticas(estadisticas);
            log.info("Reporte Excel de estadísticas generado exitosamente");
            return reporte;
        } catch (Exception e) {
            log.error("Error al generar reporte Excel de estadísticas: ", e);
            throw new RuntimeException("Error al generar reporte Excel de estadísticas", e);
        }
    }
    
    /**
     * Genera un reporte PDF con el listado completo de espacios
     */
    public ByteArrayOutputStream generarReporteEspaciosPDF() {
        try {
            log.info("Iniciando generación de reporte PDF de espacios...");
            List<Espacio> espacios = espacioRepository.findAll();
            ByteArrayOutputStream reporte = pdfGenerator.generarReporteEspacios(espacios);
            log.info("Reporte PDF de espacios generado exitosamente");
            return reporte;
        } catch (Exception e) {
            log.error("Error al generar reporte PDF de espacios: ", e);
            throw new RuntimeException("Error al generar reporte PDF de espacios", e);
        }
    }
    
    /**
     * Genera un reporte Excel con el listado completo de espacios
     */
    public ByteArrayOutputStream generarReporteEspaciosExcel() {
        try {
            log.info("Iniciando generación de reporte Excel de espacios...");
            List<Espacio> espacios = espacioRepository.findAll();
            ByteArrayOutputStream reporte = excelGenerator.generarReporteEspacios(espacios);
            log.info("Reporte Excel de espacios generado exitosamente");
            return reporte;
        } catch (Exception e) {
            log.error("Error al generar reporte Excel de espacios: ", e);
            throw new RuntimeException("Error al generar reporte Excel de espacios", e);
        }
    }
    
    /**
     * Genera un reporte PDF con espacios filtrados por estado
     */
    public ByteArrayOutputStream generarReporteEspaciosPorEstadoPDF(String estado) {
        try {
            log.info("Iniciando generación de reporte PDF de espacios por estado: {}", estado);
            List<Espacio> espacios = espacioRepository.findByEstadoAndEliminado(estado, 0);
            ByteArrayOutputStream reporte = pdfGenerator.generarReporteEspacios(espacios);
            log.info("Reporte PDF de espacios por estado generado exitosamente");
            return reporte;
        } catch (Exception e) {
            log.error("Error al generar reporte PDF de espacios por estado: ", e);
            throw new RuntimeException("Error al generar reporte PDF de espacios por estado", e);
        }
    }
    
    /**
     * Genera un reporte Excel con espacios filtrados por estado
     */
    public ByteArrayOutputStream generarReporteEspaciosPorEstadoExcel(String estado) {
        try {
            log.info("Iniciando generación de reporte Excel de espacios por estado: {}", estado);
            List<Espacio> espacios = espacioRepository.findByEstadoAndEliminado(estado, 0);
            ByteArrayOutputStream reporte = excelGenerator.generarReporteEspacios(espacios);
            log.info("Reporte Excel de espacios por estado generado exitosamente");
            return reporte;
        } catch (Exception e) {
            log.error("Error al generar reporte Excel de espacios por estado: ", e);
            throw new RuntimeException("Error al generar reporte Excel de espacios por estado", e);
        }
    }
}