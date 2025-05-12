package Main; // O el paquete donde tengas tus diálogos y clases de UI

import javax.swing.table.DefaultTableCellRenderer;
import java.text.NumberFormat;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

public class CurrencyRenderer extends DefaultTableCellRenderer {
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(); // Usa la configuración regional por defecto

    public CurrencyRenderer() {
        super();
        setHorizontalAlignment(JLabel.RIGHT); // Alinear moneda a la derecha
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {
        // Llama al método de la superclase para manejar la selección, foco, etc.
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Number) {
            // Formatear el número como moneda
            setText(currencyFormatter.format(value));
        } else if (value != null) {
            // Si no es un número pero no es null, mostrarlo como string
            setText(value.toString());
        } else {
            // Si es null, mostrar un string vacío o un guion
            setText(""); // o "-"
        }
        return cellComponent;
    }
}