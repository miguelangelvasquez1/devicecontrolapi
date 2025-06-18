package com.devicecontrolapi.util.reports.activosMvsReports;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

import com.devicecontrolapi.model.MovActivo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportarHistorialActivosDTO {
    private List<MovActivo> solicitudes;
}