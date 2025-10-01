/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODELS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author ADMIN
 */
public class AdminAgregar {
    
    public boolean agregarUsuario(String nombre, String apellido, String username, 
                                String contrasena, String correo, int rolId) {
        String sql = "INSERT INTO public.usuarios (nombre, apellido, username, contrasena, correo, rol_id, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, true)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, username);
            pstmt.setString(4, contrasena);
            pstmt.setString(5, correo);
            pstmt.setInt(6, rolId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar usuario: " + e.getMessage(), e);
        }
    }
    
    public boolean validarCampos(String nombre, String apellido, String username, 
                               String contrasena, String correo) {
        return nombre != null && !nombre.trim().isEmpty() &&
               apellido != null && !apellido.trim().isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               contrasena != null && !contrasena.trim().isEmpty() &&
               correo != null && !correo.trim().isEmpty() &&
               correo.contains("@"); // Validación básica de email
    }
    
    public boolean validarUsuarioExistente(String username) {
        String sql = "SELECT COUNT(*) FROM public.usuarios WHERE username = ? AND activo = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            var rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar usuario existente: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    public int convertirRol(String rolSeleccionado) {
        switch (rolSeleccionado) {
            case "Empleado": return 3; // Usuario
            case "Tecnico": return 2;  // Técnico
            case "Programador": return 1; // Administrador
            default: return 3; // Por defecto Usuario
        }
    }

    public void limpiarCampos(javax.swing.JTextField... campos) {
        for (javax.swing.JTextField campo : campos) {
            campo.setText("");
        }
    }
    
    public void limpiarContrasena(javax.swing.JPasswordField campoContrasena) {
        campoContrasena.setText("");
    }
    
}
