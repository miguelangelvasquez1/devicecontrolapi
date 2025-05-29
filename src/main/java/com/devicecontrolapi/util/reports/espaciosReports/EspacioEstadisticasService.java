package com.devicecontrolapi.util.reports.espaciosReports;

import com.devicecontrolapi.dto.EstadisticasEspacioDTO;
import com.devicecontrolapi.model.Espacio;
import com.devicecontrolapi.repository.EspacioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EspacioEstadisticasService {
    
    private final EspacioRepository espacioRepository;
    
    public EstadisticasEspacioDTO generarEstadisticas() {
        log.info("Generando estadísticas de espacios...");
        
        List<Espacio> todosEspacios = espacioRepository.findAll();
        
        // Filtrar espacios activos (no eliminados)
        List<Espacio> espaciosActivos = todosEspacios.stream()
                .filter(e -> e.getEliminado() == null || e.getEliminado() == 0)
                .collect(Collectors.toList());
        
        // Estadísticas básicas
        Long totalEspacios = (long) todosEspacios.size();
        Long totalEspaciosActivos = (long) espaciosActivos.size();
        Long totalEspaciosEliminados = totalEspacios - totalEspaciosActivos;
        
        // Capacidad
        Integer capacidadTotal = espaciosActivos.stream()
                .mapToInt(e -> e.getCapacidad() != null ? e.getCapacidad() : 0)
                .sum();
        
        Double capacidadPromedio = espaciosActivos.isEmpty() ? 0.0 : 
                capacidadTotal.doubleValue() / espaciosActivos.size();
        
        Integer capacidadMaxima = espaciosActivos.stream()
                .mapToInt(e -> e.getCapacidad() != null ? e.getCapacidad() : 0)
                .max()
                .orElse(0);
        
        Integer capacidadMinima = espaciosActivos.stream()
                .filter(e -> e.getCapacidad() != null && e.getCapacidad() > 0)
                .mapToInt(Espacio::getCapacidad)
                .min()
                .orElse(0);
        
        // Distribución por estado
        Map<String, Long> espaciosPorEstado = espaciosActivos.stream()
                .filter(e -> e.getEstado() != null)
                .collect(Collectors.groupingBy(
                        Espacio::getEstado,
                        Collectors.counting()
                ));
        
        // Agregar estados sin espacios
        if (espaciosPorEstado.isEmpty()) {
            espaciosPorEstado.put("SIN ESTADO", (long) espaciosActivos.size());
        }
        
        // Rangos de capacidad
        Map<String, Integer> rangoCapacidades = calcularRangosCapacidad(espaciosActivos);
        
        // Top 10 espacios por capacidad
        Map<String, Integer> topEspacios = espaciosActivos.stream()
                .filter(e -> e.getCapacidad() != null && e.getCapacidad() > 0)
                .sorted((e1, e2) -> e2.getCapacidad().compareTo(e1.getCapacidad()))
                .limit(10)
                .collect(Collectors.toMap(
                        e -> e.getNombre() != null ? e.getNombre() : "Espacio ID " + e.getIdespacio(),
                        e -> e.getCapacidad().intValue(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        
        // Porcentajes
        Double porcentajeActivos = totalEspacios > 0 ? 
                (totalEspaciosActivos * 100.0) / totalEspacios : 0.0;
        Double porcentajeEliminados = totalEspacios > 0 ? 
                (totalEspaciosEliminados * 100.0) / totalEspacios : 0.0;
        
        // Porcentaje de utilización por estado
        Map<String, Double> porcentajeUtilizacion = new HashMap<>();
        for (Map.Entry<String, Long> entry : espaciosPorEstado.entrySet()) {
            double porcentaje = (entry.getValue() * 100.0) / totalEspaciosActivos;
            porcentajeUtilizacion.put(entry.getKey(), porcentaje);
        }
        
        return EstadisticasEspacioDTO.builder()
                .totalEspacios(totalEspacios)
                .totalEspaciosActivos(totalEspaciosActivos)
                .totalEspaciosEliminados(totalEspaciosEliminados)
                .capacidadTotal(capacidadTotal)
                .capacidadPromedio(capacidadPromedio)
                .capacidadMaxima(capacidadMaxima)
                .capacidadMinima(capacidadMinima)
                .espaciosPorEstado(espaciosPorEstado)
                .rangoCapacidades(rangoCapacidades)
                .topEspaciosPorCapacidad(topEspacios)
                .porcentajeEspaciosActivos(porcentajeActivos)
                .porcentajeEspaciosEliminados(porcentajeEliminados)
                .porcentajeUtilizacionPorEstado(porcentajeUtilizacion)
                .build();
    }
    
    private Map<String, Integer> calcularRangosCapacidad(List<Espacio> espacios) {
        Map<String, Integer> rangos = new LinkedHashMap<>();
        
        int rango0_20 = 0;
        int rango21_50 = 0;
        int rango51_100 = 0;
        int rangoMas100 = 0;
        
        for (Espacio espacio : espacios) {
            if (espacio.getCapacidad() != null) {
                int capacidad = espacio.getCapacidad();
                if (capacidad <= 20) {
                    rango0_20++;
                } else if (capacidad <= 50) {
                    rango21_50++;
                } else if (capacidad <= 100) {
                    rango51_100++;
                } else {
                    rangoMas100++;
                }
            }
        }
        
        rangos.put("0-20 personas", rango0_20);
        rangos.put("21-50 personas", rango21_50);
        rangos.put("51-100 personas", rango51_100);
        rangos.put("Más de 100 personas", rangoMas100);
        
        return rangos;
    }
}