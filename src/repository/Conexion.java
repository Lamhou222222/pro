package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/amigospeludos_db"; // BD de tus scripts SQL
    private static final String USER = "root"; // <<< REEMPLAZA
    private static final String PASSWORD = "Lamhour2000"; // <<< REEMPLAZA
    private static Connection connection = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("CONEXION: MySQL JDBC Driver cargado.");
        } catch (ClassNotFoundException e) {
            System.err.println("CONEXION: ERROR CRÍTICO - Driver MySQL no encontrado en classpath.");
            throw new RuntimeException("Driver MySQL no encontrado", e);
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("CONEXION: Obteniendo nueva conexión a: " + URL);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("CONEXION: Conexión establecida.");
            }
        } catch (SQLException e) {
            System.err.println("CONEXION: Error al conectar: " + e.getMessage() + " (SQLState: " + e.getSQLState() + ")");
            connection = null;
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("CONEXION: Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("CONEXION: Error al cerrar: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}