package com.devicecontrolapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.devicecontrolapi.model.Notificacion;
import com.devicecontrolapi.service.NotificacionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Gestión de notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    @Operation(summary = "Obtener todas mis notificaciones")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notificacion>> obtenerMisNotificaciones() {
        List<Notificacion> notificaciones = notificacionService.obtenerMisNotificaciones();
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/paginadas")
    @Operation(summary = "Obtener notificaciones paginadas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Notificacion>> obtenerNotificacionesPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Notificacion> notificaciones = notificacionService.obtenerMisNotificacionesPaginadas(page, size);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/no-leidas")
    @Operation(summary = "Obtener notificaciones no leídas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesNoLeidas() {
        List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesNoLeidas();
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/contador")
    @Operation(summary = "Obtener contador de notificaciones no leídas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> contarNotificacionesNoLeidas() {
        long count = notificacionService.contarNotificacionesNoLeidas();
        Map<String, Long> response = new HashMap<>();
        response.put("noLeidas", count);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/leer")
    @Operation(summary = "Marcar notificación como leída")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        Notificacion notificacion = notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok(notificacion);
    }

    @PutMapping("/leer-todas")
    @Operation(summary = "Marcar todas las notificaciones como leídas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> marcarTodasComoLeidas() {
        notificacionService.marcarTodasComoLeidas();
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Todas las notificaciones han sido marcadas como leídas");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una notificación")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar notificaciones por texto")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notificacion>> buscarNotificaciones(@RequestParam String texto) {
        List<Notificacion> notificaciones = notificacionService.buscarNotificaciones(texto);
        return ResponseEntity.ok(notificaciones);
    }

    @PostMapping
    @Operation(summary = "Crear nueva notificación (Admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notificacion> crearNotificacion(@RequestBody Notificacion notificacion) {
        Notificacion nueva = notificacionService.crearNotificacion(notificacion);
        return ResponseEntity.ok(nueva);
    }
}