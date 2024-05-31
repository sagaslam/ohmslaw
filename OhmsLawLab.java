
//Ihsaan Aslam H00399721

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class OhmsLawLab extends JFrame { // define the public class and create the GUI window
    private JTextField voltageField; // add voltage field and repeat for other entered values
    private JTextField currentField;
    private JButton addButton;
    private JTable dataTable;
    private DataTableModel tableModel; // added table to display these values
    private GraphPanel graphPanel; // add the graph panel to demonstrate the relationship between both

    // contains the GUI components
    public OhmsLawLab() { // constructor initialises GUI components
        setTitle("Ohm's Law Virtual Lab"); // title of the virtual lab
        setSize(600, 800); // size of window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ensures GUI window closure
        setLocationRelativeTo(null); // centre screen placenment

        // create input components and event listeners and assign names
        JLabel labelA = new JLabel("Current (A):");
        JLabel labelV = new JLabel("Voltage (V):");
        currentField = new JTextField(10);
        voltageField = new JTextField(10);
        addButton = new JButton("Add"); // button to add entries

        // create panel for input components and event handlers
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20)); // using flowlayout for organisation
        inputPanel.add(labelV);
        inputPanel.add(voltageField); // add all the variables as panels
        inputPanel.add(labelA);
        inputPanel.add(currentField);
        inputPanel.add(addButton);
        inputPanel.setPreferredSize(new Dimension(600, 30));

        // create table panel
        tableModel = new DataTableModel();
        dataTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(dataTable); // allow scrolling on the table panel
        tableScrollPane.setPreferredSize(new Dimension(600, 140));

        // create graph panel
        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(600, 400)); // graph size

        // create panel for save and open buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // arranges horizontally to the right
        JButton resetButton = new JButton("Reset");
        JButton openButton = new JButton("Open"); // add required buttons
        JButton saveButton = new JButton("Save");
        buttonPanel.add(resetButton);
        buttonPanel.add(openButton); // add these to panels
        buttonPanel.add(saveButton);
        buttonPanel.setPreferredSize(new Dimension(600, 30)); // panel size

        // add event listeners for all buttons
        resetButton.addActionListener(e -> resetData());
        addButton.addActionListener(e -> addData());
        saveButton.addActionListener(e -> saveData());
        openButton.addActionListener(e -> loadData());

        // create a main panel with BoxLayout to arrange all components vertically
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // vertical allignment
        mainPanel.add(Box.createVerticalStrut(1)); // add spacers to seperate components
        mainPanel.add(inputPanel); // add all the panels developed above
        mainPanel.add(Box.createVerticalStrut(1));
        mainPanel.add(tableScrollPane);
        mainPanel.add(Box.createVerticalStrut(1));
        mainPanel.add(graphPanel);
        mainPanel.add(Box.createVerticalStrut(1));
        mainPanel.add(buttonPanel);

        // add the main panel to the frame
        getContentPane().add(mainPanel);

        setVisible(true); // now the window appears on screen

    }

    // method to add data to the table and update the graph
    private void addData() {
        try {
            double voltage = Double.parseDouble(voltageField.getText());
            double current = Double.parseDouble(currentField.getText());

            tableModel.addData(voltage, current);
            graphPanel.updateGraph(tableModel.getData());
        } catch (NumberFormatException ex) { // error message shown if invalid number
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // method to clear the data
    private void resetData() {
        tableModel.clearData(); // clears data in table
        graphPanel.updateGraph(tableModel.getData()); // in turn updating and clearing the graph
    }

    // method to save data to a user selected file
    private void saveData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(".")); // sets current directory as the working directory
        int option = fileChooser.showSaveDialog(this); // allows you to save .txt file wherever in your files
        if (option == JFileChooser.APPROVE_OPTION) { // approve after making decision
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getParentFile(), file.getName() + ".txt"); // adds .txt extension if not there
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (DataPoint dp : tableModel.getData()) {
                    writer.println(dp.getVoltage() + "," + dp.getCurrent()); // writes data from table to txt file
                }
                JOptionPane.showMessageDialog(this, "File saved successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) { // catches exception and sends either message if success or failure
                JOptionPane.showMessageDialog(this, "Error saving file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // method to load data from a selected file
    private void loadData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(".")); // set current directory
        int option = fileChooser.showOpenDialog(this); // captures users choice of file
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) { // reads final line by line
                String line;
                ArrayList<DataPoint> dataPoints = new ArrayList<>(); // stores data points
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        double voltage = Double.parseDouble(parts[0]);
                        double current = Double.parseDouble(parts[1]);

                        dataPoints.add(new DataPoint(voltage, current));
                    }
                }
                tableModel.setData(dataPoints); // adds this data to table
                tableModel.fireTableDataChanged(); // notifies table that data has updated
                graphPanel.updateGraph(dataPoints); // therefore update the graph
                JOptionPane.showMessageDialog(this, "Data loaded successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | NumberFormatException ex) { // success or error messages
                JOptionPane.showMessageDialog(this, "Error loading file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) { // where the program starts
        SwingUtilities.invokeLater(() -> {
            OhmsLawLab lab = new OhmsLawLab(); // opens the ohms law class
            lab.setVisible(true); // lab object becomes true and the window is visible
        });
    }
}

// class defining a data point with voltage, current, and resistance values
class DataPoint {
    private final double voltage; // voltage value
    private final double current; // current value

    public DataPoint(double voltage, double current) { // initialises the values
        this.voltage = voltage;
        this.current = current;
    }

    public double getVoltage() { // getter methods to return voltage value
        return voltage;
    }

    public double getCurrent() { // repeat for current value
        return current;
    }

    public double getResistance() {
        return Math.floor(voltage / current * 100) / 100; // calculate resistance using Ohms Law V=IR
    }

}

// class for adding and displaying data to a table
class DataTableModel extends AbstractTableModel { // manages data for table
    private final String[] columnNames = { "Voltage (V)", "Current (A)", "Resistance (Ω)" }; // stores table names
    private ArrayList<DataPoint> data = new ArrayList<>(); // adds data to an array

    public void addData(double voltage, double current) { // adds to data list
        data.add(new DataPoint(voltage, current));
        fireTableRowsInserted(data.size() - 1, data.size() - 1); // new row has been added
    }

    public ArrayList<DataPoint> getData() { // returns list of data points
        return data;
    }

    public void setData(ArrayList<DataPoint> newArrayList) {
        data = newArrayList;
    }

    public void clearData() { // clears data from table and alerts table of changed data
        data.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() { // returns number of rows in table
        return data.size();
    }

    @Override
    public int getColumnCount() { // returns number of columns in table
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) { // returns to value at specific row and column
        DataPoint dp = data.get(rowIndex);
        switch (columnIndex) { // switch to return to appropriate value based on column index
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
    public String getColumnName(int column) { // get name of column
        return columnNames[column];
    }
}

// class to show graph
class GraphPanel extends JPanel {
    private ArrayList<DataPoint> data; // holds data points that will be plotted

    public GraphPanel() {
        this.data = new ArrayList<>(); // empty array list
    }

    public void updateGraph(ArrayList<DataPoint> data) { // updates graph with new data points and updates graph
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) { // renders the panel
        super.paintComponent(g);
        if (data == null || data.isEmpty()) // checks data is there before it renders
            return;

        Graphics2D g2 = (Graphics2D) g; // for advanced drawing
        int width = getWidth();
        int height = getHeight();
        int padding = 50; // adds graph padding
        int labelPadding = 20;

        // calculate maximum values for scaling
        double maxVoltage = data.stream().mapToDouble(DataPoint::getVoltage).max().orElse(1); // calculate maximum
                                                                                              // values for scale
        double maxCurrent = data.stream().mapToDouble(DataPoint::getCurrent).max().orElse(1);

        // draw white background
        g2.setColor(Color.WHITE); // background is white
        g2.fillRect(padding, padding, width - 2 * padding, height - 2 * padding);
        g2.setColor(Color.BLACK); // draw in black

        // draw ticks and grid lines on X axis
        int xTicks = (int) Math.ceil(maxVoltage);
        int xTickSpacing = (width - 100) / xTicks;
        for (int i = 0; i <= xTicks; i++) {
            int x = 50 + i * xTickSpacing;
            g2.drawLine(x, height - 50, x, height - 45);
            g2.drawString(String.valueOf(i), x - 5, height - 30);

            // draw vertical grid lines
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(x, height - 50, x, 50);
            g2.setColor(Color.BLACK);
        }

        // Draw ticks and grid lines on Y axis
        int yTicks = (int) Math.ceil(maxCurrent);
        int yTickSpacing = (height - 100) / yTicks;
        for (int i = 0; i <= yTicks; i++) {
            int y = height - 50 - i * yTickSpacing;
            g2.drawLine(50, y, 55, y);
            g2.drawString(String.valueOf(i), 30, y + 5);

            // Draw horizontal grid lines
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(50, y, width - 50, y);
            g2.setColor(Color.BLACK);
        }

        // draw axes
        g2.drawLine(padding, height - padding, padding, padding);
        g2.drawLine(padding, height - padding, width - padding, height - padding);

        g2.drawString("Voltage (V)", (width / 2) - 32, height - labelPadding + 6);

        // rotate 'Current' vertically
        g2.rotate(-Math.PI / 2);
        g2.drawString("Current (A)", (-height / 2) - 24, labelPadding - 4);
        g2.rotate(Math.PI / 2);

        // draw data points and lines
        int pointRadius = 5;
        for (int i = 0; i < data.size(); i++) {
            DataPoint dp = data.get(i);
            int x = (int) (padding + dp.getVoltage() / ((maxVoltage <= 1) ? 1 : maxVoltage) * (width - 2.1 * padding));
            int y = (int) (height - padding
                    - dp.getCurrent() / ((maxCurrent <= 1) ? 1 : maxCurrent) * (height - 2.1 * padding));
            g2.setColor(Color.RED);
            g2.fillOval(x - pointRadius, y - pointRadius, pointRadius * 2, pointRadius * 2);
            if (i > 0) {
                DataPoint prev = data.get(i - 1);
                int prevX = (int) (padding
                        + prev.getVoltage() / ((maxVoltage <= 1) ? 1 : maxVoltage) * (width - 2.1 * padding));
                int prevY = (int) (height - padding
                        - prev.getCurrent() / ((maxCurrent <= 1) ? 1 : maxCurrent) * (height - 2.1 * padding));
                g2.setColor(Color.BLACK);
                g2.drawLine(prevX, prevY, x, y);
            }
        }

    }

}