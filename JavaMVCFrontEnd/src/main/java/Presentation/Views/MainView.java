package Presentation.Views;

import Domain.Dtos.cars.CarResponseDto;
import Domain.Dtos.maintenances.MaintenanceResponseDto;
import Presentation.Models.MaintenancesTableModel;
import Presentation.Models.CarsTableModel;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainView extends JFrame {
    // Panels
    private JPanel MainPanel;
    private JPanel CarsPanel;
    private JPanel AddCarsPanel;
    private JPanel TableCarsPanel;
    private JPanel MaintenancesPanel;
    private JPanel AddMaintenancesPanel;
    private JPanel TableMaintenancesPanel;
    private JPanel DatePickerPanel;

    // JLabels
    private JLabel IDLabel;
    private JLabel DescriptionLabel;
    private JLabel TypeLabel;
    private JLabel DateLabel;
    private JLabel AddLabel;
    private JLabel ModelLabel;
    private JLabel YearLabel;

    // TextFields
    private JTextField MakeField;
    private JTextField ModelField;
    private JTextField YearField;
    private JTextField DescriptionField;
    private JTextField CarIDField;

    // Buttons
    private JButton CDeleteButton;
    private JButton CAddButton;
    private JButton CUpdateButton;
    private JButton CClearButton;
    private JButton MDeleteButton;
    private JButton MAddButton;
    private JButton MUpdateButton;
    private JButton MClearButton;
    private JButton ExitButton;

    // ComboBoxes
    private JComboBox<String> TypeComboBox;

    // Tables and ScrollPanes
    private JTable CarsTable;
    private JScrollPane CarScroll;
    private JTable MaintenancesTable;
    private JScrollPane MaintenancesScroll;

    private final CarsTableModel carsTableModel;
    private final MaintenancesTableModel maintenancesTableModel;
    private final LoadingOverlay carsLoadingOverlay;
    private final LoadingOverlay maintenancesLoadingOverlay;
    private JDateChooser DatePicker;

    public MainView() {
        setTitle("Car Maintenances App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        setSize(800, 600);
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

        initTypeComboBox();

        // Configurar colores de selección de tablas
        CarsTable.setSelectionBackground(new Color(52, 152, 219));
        CarsTable.setSelectionForeground(Color.WHITE);
        MaintenancesTable.setSelectionBackground(new Color(52, 152, 219));
        MaintenancesTable.setSelectionForeground(Color.WHITE);

        // Inicializar comportamiento del botón Exit
        initExitButton();
    }

    // --- Métodos de carros ---
    public void showCarsLoading(boolean visible) {
        carsLoadingOverlay.show(visible);
    }

    public CarsTableModel getCarsTableModel() {
        return carsTableModel;
    }

    public JTable getCarsTable() {
        return CarsTable;
    }

    public JButton getAddCarButton() {
        return CAddButton;
    }

    public JButton getDeleteCarButton() {
        return CDeleteButton;
    }

    public JButton getUpdateCarButton() {
        return CUpdateButton;
    }

    public JButton getClearCarButton() {
        return CClearButton;
    }

    public JTextField getCarMakeField() {
        return MakeField;
    }

    public JTextField getCarModelField() {
        return ModelField;
    }

    public JTextField getYearTextField() {
        return YearField;
    }

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
    public void showMaintenancesLoading(boolean visible) {
        maintenancesLoadingOverlay.show(visible);
    }

    public MaintenancesTableModel getMaintenancesTableModel() {
        return maintenancesTableModel;
    }

    public JTable getMaintenancesTable() {
        return MaintenancesTable;
    }

    public JButton getAddMaintenanceButton() {
        return MAddButton;
    }

    public JButton getDeleteMaintenanceButton() {
        return MDeleteButton;
    }

    public JButton getUpdateMaintenanceButton() {
        return MUpdateButton;
    }

    public JButton getClearMaintenanceButton() {
        return MClearButton;
    }

    public JTextField getDescriptionField() {
        return DescriptionField;
    }

    public JComboBox<String> getTypeComboBox() {
        return TypeComboBox;
    }

    // --- Métodos para el botón Exit ---
    public JButton getExitButton() {
        return ExitButton;
    }

    // --- Métodos para el CarIDField ---
    public JTextField getCarIDField() {
        return CarIDField;
    }

    public void setSelectedCarId(Long carId) {
        CarIDField.setText(carId != null ? String.valueOf(carId) : "");
    }

    public Long getSelectedCarId() {
        try {
            String text = CarIDField.getText().trim();
            return (text != null && !text.isEmpty()) ? Long.parseLong(text) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void clearMaintenanceFields() {
        DescriptionField.setText("");
        DatePicker.setDate(null);
        TypeComboBox.setSelectedIndex(0);
        CarIDField.setText("");
        MaintenancesTable.clearSelection();
    }

    public JPanel getAddMaintenancesPanel() {
        return AddMaintenancesPanel;
    }

    public JPanel getTableMaintenancesPanel() {
        return TableMaintenancesPanel;
    }

    public JPanel getMaintenancesPanel() {
        return MaintenancesPanel;
    }

    public JPanel getCarsPanel() {
        return CarsPanel;
    }

    public void populateMaintenanceFields(MaintenanceResponseDto maintenance) {
        DescriptionField.setText(maintenance.getDescription());
        setSelectedCarId(maintenance.getCarId());

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


    public void setCarOptions(java.util.List<CarResponseDto> cars) {

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

    // Inicializa el comportamiento del botón Exit: mensaje en inglés y abre LoginView si confirma
    private void initExitButton() {
        if (ExitButton == null) return;
        ExitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                });
            }
        });
    }
}





