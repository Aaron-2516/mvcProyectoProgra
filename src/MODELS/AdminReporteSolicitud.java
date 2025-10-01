/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODELS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


/**
 *
 * @author ADMIN
 */
public class AdminReporteSolicitud {
    
    public static class DatosReporte {
        private int totalSolicitudes;
        private int solicitudesPendientes;
        private int solicitudesCerradas;
        
        public DatosReporte(int total, int pendientes, int cerradas) {
            this.totalSolicitudes = total;
            this.solicitudesPendientes = pendientes;
            this.solicitudesCerradas = cerradas;
        }
        
        // Getters
        public int getTotalSolicitudes() { return totalSolicitudes; }
        public int getSolicitudesPendientes() { return solicitudesPendientes; }
        public int getSolicitudesCerradas() { return solicitudesCerradas; }
    }
    
    public DatosReporte generarReporte(Date fechaInicio, Date fechaFin, String tipoSolicitud) {
        String sql = construirQuery(fechaInicio, fechaFin, tipoSolicitud);
        System.out.println("Query SQL: " + sql);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            System.out.println("Conexión a BD establecida correctamente");
            
            // Configurar parámetros
            int paramIndex = 1;
            if (fechaInicio != null) {
                pstmt.setDate(paramIndex++, new java.sql.Date(fechaInicio.getTime()));
            }
            if (fechaFin != null) {
                pstmt.setDate(paramIndex++, new java.sql.Date(fechaFin.getTime()));
            }
            if (!tipoSolicitud.equals("Todas las Solicitudes")) {
                int categoriaId = obtenerIdCategoria(tipoSolicitud);
                pstmt.setInt(paramIndex++, categoriaId);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total_solicitudes");
                int pendientes = rs.getInt("solicitudes_pendientes");
                int cerradas = rs.getInt("solicitudes_cerradas");
                
                System.out.println("=== RESULTADOS OBTENIDOS ===");
                System.out.println("Total: " + total);
                System.out.println("Pendientes: " + pendientes);
                System.out.println("Cerradas: " + cerradas);
                
                return new DatosReporte(total, pendientes, cerradas);
            } else {
                System.out.println("No se obtuvieron resultados de la consulta");
                return new DatosReporte(0, 0, 0);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR SQL: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar reporte: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar reporte: " + e.getMessage(), e);
        }
    }
    
    private String construirQuery(Date fechaInicio, Date fechaFin, String tipoSolicitud) {
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "COUNT(*) as total_solicitudes, " +
            "SUM(CASE WHEN estado_id = 2 THEN 1 ELSE 0 END) as solicitudes_pendientes, " +
            "SUM(CASE WHEN estado_id = 3 THEN 1 ELSE 0 END) as solicitudes_cerradas " +
            "FROM public.solicitudes WHERE 1=1"
        );
        
        if (fechaInicio != null) {
            sql.append(" AND fecha_registro >= ?");
        }
        if (fechaFin != null) {
            sql.append(" AND fecha_registro <= ?");
        }
        if (!tipoSolicitud.equals("Todas las Solicitudes")) {
            sql.append(" AND categoria_id = ?");
        }
        
        System.out.println("Query construido: " + sql.toString());
        return sql.toString();
    }
    
    private int obtenerIdCategoria(String tipoSolicitud) {
        System.out.println("Convirtiendo tipo solicitud: " + tipoSolicitud);
        
        switch (tipoSolicitud) {
            case "Solicitud de Hardware": 
                return 1;
            case "Solicitud de Software y Aplicaciones": 
                return 2;
            case "Solicitud de Redes y Conectividad": 
                return 3;
            case "Solicitud de Cuentas y Accesos": 
                return 4;
            default: 
                System.out.println("Tipo de solicitud no reconocido: " + tipoSolicitud);
                return 0;
        }
    }
    
    public boolean validarFechas(Date fechaInicio, Date fechaFin) {
        if (fechaInicio != null && fechaFin != null) {
            boolean valido = !fechaInicio.after(fechaFin);
            return valido;
        }
        return true;
    }
}
