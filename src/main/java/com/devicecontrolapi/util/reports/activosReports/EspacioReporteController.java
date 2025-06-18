// package com.devicecontrolapi.util.reports.espaciosReports;

// import com.devicecontrolapi.dto.EstadisticasEspacioDTO;
// import lombok.RequiredArgsConstructor;
// import org.springframework.core.io.ByteArrayResource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.io.ByteArrayOutputStream;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

// @RestController
// @RequestMapping("/api/espacios/reportes")
// @RequiredArgsConstructor
// public class EspacioReporteController {
    
//     private final EspacioReporteService reporteService;
//     private final EspacioEstadisticasService estadisticasService;
    
//     @GetMapping("/estadisticas")
//     public ResponseEntity<EstadisticasEspacioDTO> obtenerEstadisticas() {
//         return ResponseEntity.ok(estadisticasService.generarEstadisticas());
//     }
    
//     @GetMapping("/estadisticas/pdf")
//     public ResponseEntity<ByteArrayResource> generarEstadisticasPDF() {
//         ByteArrayOutputStream baos = reporteService.generarReporteEstadisticasPDF();
//         ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        
//         String filename = "estadisticas_espacios_" + 
//                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
//                          ".pdf";
        
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                 .contentType(MediaType.APPLICATION_PDF)
//                 .contentLength(baos.size())
//                 .body(resource);
//     }
    
//     @GetMapping("/estadisticas/excel")
//     public ResponseEntity<ByteArrayResource> generarEstadisticasExcel() {
//         ByteArrayOutputStream baos = reporteService.generarReporteEstadisticasExcel();
//         ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        
//         String filename = "estadisticas_espacios_" + 
//                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
//                          ".xlsx";
        
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                 .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                 .contentLength(baos.size())
//                 .body(resource);
//     }
    
//     @GetMapping("/listado/pdf")
//     public ResponseEntity<ByteArrayResource> generarListadoPDF() {
//         ByteArrayOutputStream baos = reporteService.generarReporteEspaciosPDF();
//         ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        
//         String filename = "listado_espacios_" + 
//                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
//                          ".pdf";
        
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                 .contentType(MediaType.APPLICATION_PDF)
//                 .contentLength(baos.size())
//                 .body(resource);
//     }
    
//     @GetMapping("/listado/excel")
//     public ResponseEntity<ByteArrayResource> generarListadoExcel() {
//         ByteArrayOutputStream baos = reporteService.generarReporteEspaciosExcel();
//         ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        
//         String filename = "listado_espacios_" + 
//                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
//                          ".xlsx";
        
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                 .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                 .contentLength(baos.size())
//                 .body(resource);
//     }
// }
    
// //     @GetMapping("/estado/{estado}/pdf")
// //     public ResponseEntity<ByteArrayResource> generarReportePorEstadoPDF(@PathVariable String estado) {
// //         ByteArrayOutputStream baos = reporteService.generarReporteEspaciosPorEstadoPDF(estado);
// //         ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        
// //         String filename = "espacios_" + estado.toLowerCase() + "_" +
// //                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
// //                          ".xlsx";
        
// //         return ResponseEntity.ok()
// //                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
// //                 .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
// //                 .contentLength(baos.size())
// //                 .body(resource);
// //     }
// // }d_HHmmss")) + 
// //                          ".pdf";
        
// //         return ResponseEntity.ok()
// //                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
// //                 .contentType(MediaType.APPLICATION_PDF)
// //                 .contentLength(baos.size())
// //                 .body(resource);
// //     }
    
// //     @GetMapping("/estado/{estado}/excel")
// //     public ResponseEntity<ByteArrayResource> generarReportePorEstadoExcel(@PathVariable String estado) {
// //         ByteArrayOutputStream baos = reporteService.generarReporteEspaciosPorEstadoExcel(estado);
// //         ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        
// //         String filename = "espacios_" + estado.toLowerCase() + "_" +
// //                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMd