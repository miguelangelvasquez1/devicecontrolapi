package com.devicecontrolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasEspacioDTO {
    
    // Estadísticas generales de espacios
    private Long totalEspacios;
    private Long totalEspaciosActivos;
    private Long totalEspaciosEliminados;
    private Integer capacidadTotal;
    private Double capacidadPromedio;
    
    // Distribución por estado
    private Map<String, Long> espaciosPorEstado;
    
    // Estadísticas de capacidad
    private Integer capacidadMaxima;
    private Integer capacidadMinima;
    private Map<String, Integer> rangoCapacidades; // Ej: "0-20", "21-50", "51-100", ">100"
    
    // Top espacios por capacidad
    private Map<String, Integer> topEspaciosPorCapacidad;
    
    // Análisis de utilización
    private Map<String, Double> porcentajeUtilizacionPorEstado;
    
    // Resumen de espacios activos vs eliminados
    private Double porcentajeEspaciosActivos;
    private Double porcentajeEspaciosEliminados;
}