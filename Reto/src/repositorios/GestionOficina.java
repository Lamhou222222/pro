package repositorios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GestionOficina {

    public static void mostrarOficinaViviendasBD(int id) {
        System.out.println("Lista de Viviendas");

        String Select = "SELECT * FROM mr_robot.vivienda WHERE IdOficina = ?";
        try {
            PreparedStatement statement = ConectorBD.conexion.prepareStatement(Select);
            // Establecer el parámetro de la consulta
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                System.out.println("Codigo Vivienda: " + rs.getInt("CodVivienda") +
                        ", Ciudad: " + rs.getString("Ciudad") + ", Direccion: " + rs.getString("Direccion") +
                        ", Numero Habitantes: " + rs.getInt("NumHab") + ", Descripcion: " + rs.getString("descripcion") +
                        ", Precio/dia: " + rs.getDouble("precio_Dia") + ", Tipo Vivienda: " + rs.getString("tipo_Vivienda") +
                        ", Dias: " + rs.getInt("dias") + ", Semanas: " + rs.getInt("semanas"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al hacer la consulta: " + Select);
        }
    }

    public static void DatosOficina() {
        System.out.println("Lista de viviendas");

        String Select = "SELECT * FROM mr_robot.oficina";
        try {
            PreparedStatement statement = ConectorBD.conexion.prepareStatement(Select);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("Id") +
                        ", telefono: " + rs.getInt("telefono") +
                        ", Ubicacion: " + rs.getString("ubicacion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al hacer la consulta: " + Select);
        }
    }
}

