package com.devicecontrolapi.util.reports.espacioMovsReports;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/movEspacios")
@RequiredArgsConstructor
@Tag(name = "Solicitudes de Espacio", description = "Gestión de solicitudes de espacios")
public class SolicitudEspacioController {

    private final SolicitudEspacioService solicitudEspacioService;

@GetMapping("/exportar/test")
public String test() {
    return "Funciona exportar GET";
}

    @GetMapping("/{id}/comprobante")
    @Operation(summary = "Descargar comprobante PDF")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ByteArrayResource> descargarComprobante(@PathVariable Integer id) {
        byte[] pdfBytes = solicitudEspacioService.generarComprobantePDF(id);
        
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobante_solicitud_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }

    @PostMapping("/exportar")
    // @Operation(summary = "Exportar historial a Excel", 
    //            description = "Exporta el historial de solicitudes a un archivo Excel. Si se envían solicitudes específicas, exporta solo esas.")
    public ResponseEntity<ByteArrayResource> exportarHistorial(@RequestBody(required = false) ExportarHistorialDTO exportarDTO) {
        try {
            ByteArrayOutputStream outputStream;
            
            if (exportarDTO != null && exportarDTO.getSolicitudes() != null && !exportarDTO.getSolicitudes().isEmpty()) {
                // Exportar solicitudes específicas
                outputStream = solicitudEspacioService.exportarHistorialExcel(exportarDTO.getSolicitudes());
            } else {
                // Exportar todas las solicitudes del usuario
                outputStream = solicitudEspacioService.exportarHistorialExcel(null);
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            
            String filename = "historial_solicitudes_" + 
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                            ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(outputStream.size())
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}