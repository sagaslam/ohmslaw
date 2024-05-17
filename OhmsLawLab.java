import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class OhmsLawLab extends JFrame {
    private JTextField voltageField;
    private JTextField currentField;
    private JButton addButton;
    private JButton saveButton;
    private JTable dataTable;
    private DataTableModel tableModel;
    private GraphPanel graphPanel;

    public OhmsLawLab() {
        setTitle("Ohm's Law Virtual Lab");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Voltage (V):"));
        voltageField = new JTextField();
        inputPanel.add(voltageField);
        inputPanel.add(new JLabel("Current (A):"));
        currentField = new JTextField();
        inputPanel.add(currentField);
        addButton = new JButton("Add Data");
        inputPanel.add(addButton);
        saveButton = new JButton("Save Data");
        inputPanel.add(saveButton);

        // Table panel
        tableModel = new DataTableModel();
        dataTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);

        // Graph panel
        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(800, 300));

        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(graphPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addData();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });
    }

    private void addData() {
        try {
            double voltage = Double.parseDouble(voltageField.getText());
            double current = Double.parseDouble(currentField.getText());
            tableModel.addData(voltage, current);
            graphPanel.updateGraph(tableModel.getData());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("ohms_law_data.txt", true))) {
            for (DataPoint dp : tableModel.getData()) {
                writer.println(dp.getVoltage() + "," + dp.getCurrent());
            }
            JOptionPane.showMessageDialog(this, "Data saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OhmsLawLab lab = new OhmsLawLab();
            lab.setVisible(true);
        });
    }
}

class DataPoint {
    private final double voltage;
    private final double current;

    public DataPoint(double voltage, double current) {
        this.voltage = voltage;
        this.current = current;
    }

    public double getVoltage() {
        return voltage;
    }

    public double getCurrent() {
        return current;
    }
}

class DataTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Voltage (V)", "Current (A)"};
    private final ArrayList<DataPoint> data = new ArrayList<>();

    public void addData(double voltage, double current) {
        data.add(new DataPoint(voltage, current));
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public ArrayList<DataPoint> getData() {
        return data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DataPoint dp = data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return dp.getVoltage();
            case 1:
                return dp.getCurrent();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}

class GraphPanel extends JPanel {
    private ArrayList<DataPoint> data;

    public GraphPanel() {
        this.data = new ArrayList<>();
    }

    public void updateGraph(ArrayList<DataPoint> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int labelPadding = 20;

        // Calculate maximum values for scaling
        double maxVoltage = data.stream().mapToDouble(DataPoint::getVoltage).max().orElse(1);
        double maxCurrent = data.stream().mapToDouble(DataPoint::getCurrent).max().orElse(1);

        // Draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding, padding, width - 2 * padding, height - 2 * padding);
        g2.setColor(Color.BLACK);

        // Draw axes
        g2.drawLine(padding, height - padding, padding, padding);
        g2.drawLine(padding, height - padding, width - padding, height - padding);

        // Draw labels
        g2.drawString("Voltage (V)", width / 2, height - labelPadding);
        g2.drawString("Current (A)", labelPadding, height / 2);

        // Draw data points and lines
        int pointRadius = 5;
        for (int i = 0; i < data.size(); i++) {
            DataPoint dp = data.get(i);
            int x = (int) (padding + dp.getVoltage() / maxVoltage * (width - 2 * padding));
            int y = (int) (height - padding - dp.getCurrent() / maxCurrent * (height - 2 * padding));
            g2.fillOval(x - pointRadius, y - pointRadius, pointRadius * 2, pointRadius * 2);
            if (i > 0) {
                DataPoint prev = data.get(i - 1);
                int prevX = (int) (padding + prev.getVoltage() / maxVoltage * (width - 2 * padding));
                int prevY = (int) (height - padding - prev.getCurrent() / maxCurrent * (height - 2 * padding));
                g2.drawLine(prevX, prevY, x, y);
            }
        }
    }
}
