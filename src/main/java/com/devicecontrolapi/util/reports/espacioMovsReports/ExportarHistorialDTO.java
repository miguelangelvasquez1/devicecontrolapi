package com.devicecontrolapi.util.reports.espacioMovsReports;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

import com.devicecontrolapi.model.MovEspacio;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportarHistorialDTO {
    private List<MovEspacio> solicitudes;
}