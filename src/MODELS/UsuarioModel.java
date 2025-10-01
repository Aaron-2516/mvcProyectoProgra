package MODELS;

import java.sql.*;

public class UsuarioModel {

    public static User getUser(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("getUser: conexión a BD es null");
                return null;
            }

            String query = "SELECT u.username, r.rol FROM usuarios u " +
                           "JOIN roles r ON u.rol_id = r.id_rol WHERE u.username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String user = rs.getString("username");
                        String rolStr = rs.getString("rol");
                        if (rolStr == null) {
                            return new User(user, null);
                        }
                        try {
                            // Normalizar cadena para evitar errors (espacios, mayúsc/minúsc)
                            String normalized = rolStr.trim().toUpperCase().replace(' ', '_');
                            Role rol = Role.valueOf(normalized);
                            return new User(user, rol);
                        } catch (IllegalArgumentException ex) {
                            System.err.println("Rol desconocido en BD: " + rolStr);
                            return new User(user, null);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean validarLogin(String username, String contrasena) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("validarLogin: conexión a BD es null");
                return false;
            }

            String query = "SELECT contrasena FROM usuarios WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String stored = rs.getString("contrasena");
                        return contrasena != null && contrasena.equals(stored);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
