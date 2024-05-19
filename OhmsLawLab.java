import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class OhmsLawLab extends JFrame {
    private JTextField voltageField;
    private JTextField currentField;
    private JButton addButton;
    private JTable dataTable;
    private DataTableModel tableModel;
    private GraphPanel graphPanel;

    public OhmsLawLab() {
        setTitle("Ohm's Law Virtual Lab");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create input components
        JLabel labelA = new JLabel("Current (A):");
        JLabel labelV = new JLabel("Voltage (V):");
        // JLabel labelResistance = new JLabel("Resistance (Ω):");
        currentField = new JTextField(10);
        voltageField = new JTextField(10);
        addButton = new JButton("Add");

        // Create panel for input components
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        inputPanel.add(labelV); // Add label for "V"
        inputPanel.add(voltageField); // Add text field for "V"
        inputPanel.add(labelA);
        inputPanel.add(currentField);

        inputPanel.add(addButton);
        inputPanel.setPreferredSize(new Dimension(600, 30));

        // Table panel
        tableModel = new DataTableModel();
        dataTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 140));

        // Graph panel
        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(600, 400));

        ;
        // Create panel for save and open buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton openButton = new JButton("Open");
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);

        buttonPanel.setPreferredSize(new Dimension(600, 30));

        addButton.addActionListener(e -> addData());
        saveButton.addActionListener(e -> saveData());
        openButton.addActionListener(e -> loadData());

        // Create a main panel with BoxLayout to arrange all components vertically
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalStrut(1));
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(1)); // Add vertical spacing between panels
        mainPanel.add(tableScrollPane);
        mainPanel.add(Box.createVerticalStrut(1)); // Add vertical spacing between panels
        mainPanel.add(graphPanel);
        mainPanel.add(Box.createVerticalStrut(1)); // Add vertical spacing between panels
        mainPanel.add(buttonPanel);

        // Add the main panel to the frame
        getContentPane().add(mainPanel);

        setVisible(true);

    }

    private void addData() {
        try {
            double voltage = Double.parseDouble(voltageField.getText());
            double current = Double.parseDouble(currentField.getText());
            double resistance = calculateResistance(voltage, current);

            tableModel.addData(voltage, current, resistance);
            graphPanel.updateGraph(tableModel.getData());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getParentFile(), file.getName() + ".txt");
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (DataPoint dp : tableModel.getData()) {
                    writer.println(dp.getVoltage() + "," + dp.getCurrent() + "," + dp.getResistance());
                }
                JOptionPane.showMessageDialog(this, "File saved successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(".")); // Set current directory
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                ArrayList<DataPoint> dataPoints = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        double voltage = Double.parseDouble(parts[0]);
                        double current = Double.parseDouble(parts[1]);
                        double resistance = Double.parseDouble(parts[2]);
                        dataPoints.add(new DataPoint(voltage, current, resistance));
                    }
                }
                // tableModel.setData(dataPoints);
                tableModel.setData(dataPoints);
                tableModel.fireTableDataChanged();
                graphPanel.updateGraph(dataPoints);
                JOptionPane.showMessageDialog(this, "Data loaded successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error loading data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private double calculateResistance(double voltage, double current) {
        double resistance;
        resistance = voltage / current;
        return Math.floor(resistance * 100) / 100;
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
    private final double resistance;

    public DataPoint(double voltage, double current, double resistance) {
        this.voltage = voltage;
        this.current = current;
        this.resistance = resistance;
    }

    public double getVoltage() {
        return voltage;
    }

    public double getCurrent() {
        return current;
    }

    public double getResistance() {
        return resistance;
    }
}

class DataTableModel extends AbstractTableModel {
    private final String[] columnNames = { "Voltage (V)", "Current (A)", "Resistance (Ω)" };
    private ArrayList<DataPoint> data = new ArrayList<>();

    public void addData(double voltage, double current, double resistance) {
        data.add(new DataPoint(voltage, current, resistance));
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public ArrayList<DataPoint> getData() {
        return data;
    }

    public void setData(ArrayList<DataPoint> newArrayList) {
        data = newArrayList;
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
            case 2:
                return dp.getResistance();
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
        if (data == null || data.isEmpty())
            return;

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

        // Draw ticks and grid lines on X axis
        int xTicks = (int) maxVoltage;
        int xTickSpacing = (width - 100) / xTicks;
        for (int i = 0; i <= xTicks; i++) {
            int x = 50 + i * xTickSpacing;
            g2.drawLine(x, height - 50, x, height - 45); // Short tick
            g2.drawString(String.valueOf(i), x - 5, height - 30); // Tick label

            // Draw vertical grid lines
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(x, height - 50, x, 50);
            g2.setColor(Color.BLACK); // Reset color for next tick
        }

        // Draw ticks and grid lines on Y axis
        int yTicks = (int) maxCurrent;
        int yTickSpacing = (height - 100) / yTicks;
        for (int i = 0; i <= yTicks; i++) {
            int y = height - 50 - i * yTickSpacing;
            g2.drawLine(50, y, 55, y); // Short tick
            g2.drawString(String.valueOf(i), 30, y + 5); // Tick label

            // Draw horizontal grid lines
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(50, y, width - 50, y);
            g2.setColor(Color.BLACK); // Reset color for next tick
        }

        //

        // Draw axes
        g2.drawLine(padding, height - padding, padding, padding);
        g2.drawLine(padding, height - padding, width - padding, height - padding);

        // Draw labels

        // Draw labels
        g2.drawString("Voltage (V)", (width / 2) - 32, height - labelPadding + 6);

        g2.rotate(-Math.PI / 2);
        g2.drawString("Current (A)", (-height / 2) - 24, labelPadding - 4); // y-axis label
        g2.rotate(Math.PI / 2); // Reset rotation

        // Draw data points and lines
        int pointRadius = 5;
        for (int i = 0; i < data.size(); i++) {
            DataPoint dp = data.get(i);
            int x = (int) (padding + dp.getVoltage() / maxVoltage * (width - 2.1 * padding));
            int y = (int) (height - padding - dp.getCurrent() / maxCurrent * (height - 2.1 * padding));
            g2.fillOval(x - pointRadius, y - pointRadius, pointRadius * 2, pointRadius * 2);
            if (i > 0) {
                DataPoint prev = data.get(i - 1);
                int prevX = (int) (padding + prev.getVoltage() / maxVoltage * (width - 2.1 * padding));
                int prevY = (int) (height - padding - prev.getCurrent() / maxCurrent * (height - 2.1 * padding));
                g2.drawLine(prevX, prevY, x, y);
            }
        }
    }
}