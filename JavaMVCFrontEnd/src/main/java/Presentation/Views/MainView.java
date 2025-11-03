// src/main/java/Presentation/Views/MainView.java
package Presentation.Views;

import Domain.Dtos.cars.CarResponseDto;
import Domain.Dtos.maintenances.MaintenanceResponseDto;
import Presentation.Models.CarsTableModel;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainView extends JFrame {
    private JPanel MainPanel;
    private JPanel CarsPanel;
    private JPanel MaintenancesPanel;
    private JPanel AddCarsPanel;
    private JPanel TableCarsPanel;
    private JTextField MakeField;
    private JTextField ModelField;
    private JTextField YearField;
    private JButton CDeleteButton;
    private JButton CAddButton;
    private JButton CUpdateButton;
    private JButton CClearButton;
    private JPanel AddMaintenancesPanel;
    private JPanel TableMaintenancesPanel;
    private JComboBox<String> TypeComboBox; // comboBox1
    private JTextField DescriptionField;    // textField4
    private JComboBox<CarResponseDto> CarIDComboBox; // comboBox2
    private JLabel AddLabel;
    private JLabel ModelLabel;
    private JLabel YearLabel;
    private JTable CarsTable;   // table1
    private JTable MaintenancesTable; // table2
    private JButton MDeleteButton;
    private JButton MAddButton;
    private JButton MUpdateButton;
    private JButton MClearButton;
    private JPanel DatePickerPanel;
    private JTabbedPane MainTabbedPanel;
    private JTextArea MessageTextArea;

    private final CarsTableModel carsTableModel;
    private final MaintenancesTableModel maintenancesTableModel;
    private final LoadingOverlay carsLoadingOverlay;
    private final LoadingOverlay maintenancesLoadingOverlay;
    private JDateChooser DatePicker;

    public MainView() {
        setTitle("Car Maintenances App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        setSize(700, 500);
        setLocationRelativeTo(null);

        carsTableModel = new CarsTableModel();
        maintenancesTableModel = new MaintenancesTableModel();
        CarsTable.setModel(carsTableModel);
        MaintenancesTable.setModel(maintenancesTableModel);
        carsLoadingOverlay = new LoadingOverlay(this);
        maintenancesLoadingOverlay = new LoadingOverlay(this);

        DatePicker = new JDateChooser();
        DatePicker.setDateFormatString("yyyy-MM-dd");
        DatePickerPanel.setLayout(new BorderLayout());
        DatePickerPanel.add(DatePicker, BorderLayout.CENTER);

        TypeComboBox = (JComboBox<String>) TypeComboBox;
        CarIDComboBox = (JComboBox<CarResponseDto>) CarIDComboBox;
        DescriptionField = DescriptionField;

        initTypeComboBox();

        MessageTextArea.setEditable(false);
        MessageTextArea.setLineWrap(true);
        MessageTextArea.setWrapStyleWord(true);
        MessageTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    // --- Métodos de carros ---
    public void showCarsLoading(boolean visible) { carsLoadingOverlay.show(visible); }
    public CarsTableModel getCarsTableModel() { return carsTableModel; }
    public JTable getCarsTable() { return CarsTable; }
    public JButton getAddCarButton() { return CAddButton; }
    public JButton getDeleteCarButton() { return CDeleteButton; }
    public JButton getUpdateCarButton() { return CUpdateButton; }
    public JButton getClearCarButton() { return CClearButton; }
    public JTextField getCarMakeField() { return MakeField; }
    public JTextField getCarModelField() { return ModelField; }
    public JTextField getYearTextField() { return YearField; }
    public void clearCarFields() {
        MakeField.setText("");
        ModelField.setText("");
        YearField.setText("");
        CarsTable.clearSelection();
    }
    public void populateCarFields(CarResponseDto car) {
        MakeField.setText(car.getMake());
        ModelField.setText(car.getModel());
        YearField.setText(String.valueOf(car.getYear()));
    }

    // --- Métodos de mantenimientos ---
    public void showMaintenancesLoading(boolean visible) { maintenancesLoadingOverlay.show(visible); }
    public MaintenancesTableModel getMaintenancesTableModel() { return maintenancesTableModel; }
    public JTable getMaintenancesTable() { return MaintenancesTable; }
    public JButton getAddMaintenanceButton() { return MAddButton; }
    public JButton getDeleteMaintenanceButton() { return MDeleteButton; }
    public JButton getUpdateMaintenanceButton() { return MUpdateButton; }
    public JButton getClearMaintenanceButton() { return MClearButton; }
    public JTextField getDescriptionField() { return DescriptionField; }
    public JComboBox<String> getTypeComboBox() { return TypeComboBox; }
    public JComboBox<CarResponseDto> getCarIDComboBox() { return CarIDComboBox; }
    public void clearMaintenanceFields() {
        DescriptionField.setText("");
        DatePicker.setDate(null);
        TypeComboBox.setSelectedIndex(0);
        if (CarIDComboBox.getItemCount() > 0) CarIDComboBox.setSelectedIndex(0);
        MaintenancesTable.clearSelection();
    }
    public void populateMaintenanceFields(MaintenanceResponseDto maintenance) {
        DescriptionField.setText(maintenance.getDescription());
        for (int i = 0; i < CarIDComboBox.getItemCount(); i++) {
            CarResponseDto car = CarIDComboBox.getItemAt(i);
            if (car.getId().equals(maintenance.getCarId())) {
                CarIDComboBox.setSelectedIndex(i);
                break;
            }
        }
        try {
            if (maintenance.getDate() != null && !maintenance.getDate().isEmpty()) {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(maintenance.getDate());
                DatePicker.setDate(date);
            } else {
                DatePicker.setDate(null);
            }
        } catch (Exception e) {
            DatePicker.setDate(null);
        }
        if (maintenance.getType() != null) {
            TypeComboBox.setSelectedItem(maintenance.getType());
        } else {
            TypeComboBox.setSelectedIndex(0);
        }
    }
    public void setCarOptions(List<CarResponseDto> cars) {
        CarIDComboBox.removeAllItems();
        for (CarResponseDto car : cars) {
            CarIDComboBox.addItem(car);
        }
    }
    public Long getSelectedCarId() {
        CarResponseDto selected = (CarResponseDto) CarIDComboBox.getSelectedItem();
        return selected != null ? selected.getId() : null;
    }
    public String getSelectedDate() {
        if (DatePicker.getDate() == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(DatePicker.getDate());
    }
    private void initTypeComboBox() {
        TypeComboBox.removeAllItems();
        TypeComboBox.addItem("Routine");
        TypeComboBox.addItem("Modification");
        TypeComboBox.addItem("Repair");
    }

    // --- Mensajes generales ---
    public JTextArea getMessageTextArea() { return MessageTextArea; }

    // --- Tabs ---
    public void AddTabs(java.util.Dictionary<String, JPanel> tabs) {
        MainTabbedPanel.removeAll();
        for (java.util.Enumeration<String> e = tabs.keys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            JPanel panel = tabs.get(key);
            MainTabbedPanel.addTab(key, panel);
        }
    }
}



