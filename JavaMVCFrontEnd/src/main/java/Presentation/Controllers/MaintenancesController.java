package Presentation.Controllers;

import Domain.Dtos.maintenances.AddMaintenanceRequestDto;
import Domain.Dtos.maintenances.DeleteMaintenanceRequestDto;
import Domain.Dtos.maintenances.MaintenanceResponseDto;
import Domain.Dtos.maintenances.UpdateMaintenanceRequestDto;
import Domain.Dtos.cars.CarResponseDto;
import Presentation.Observable;
import Presentation.Views.MainView;
import Services.MaintenanceService;
import Services.CarService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.List;

public class MaintenancesController extends Observable {
    private final MainView mainView;
    private final MaintenanceService maintenanceService;
    private final CarService carService;
    private final Long currentUserId;
    private Long currentCarId = null;

    public MaintenancesController(MainView mainView, MaintenanceService maintenanceService,
                                  CarService carService, Long currentUserId) {
        this.mainView = mainView;
        this.maintenanceService = maintenanceService;
        this.carService = carService;
        this.currentUserId = currentUserId;

        addObserver(mainView.getMaintenancesTableModel());
        loadCarsForComboBoxAsync();
        addListeners();
        clearMaintenancesTable();
        setControlsEnabled(false);
    }

    // Para actualizar el combo de autos
    public void updateCarOptions(List<CarResponseDto> cars) {
        mainView.setCarOptions(cars);
    }

    private void loadCarsForComboBoxAsync() {
        SwingWorker<List<CarResponseDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CarResponseDto> doInBackground() throws Exception {
                return carService.listCarsAsync(currentUserId).get();
            }

            @Override
            protected void done() {
                try {
                    List<CarResponseDto> cars = get();
                    mainView.setCarOptions(cars);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainView,
                            "Error loading cars for dropdown: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void showMaintenancesForCar(Long carId) {
        this.currentCarId = carId;
        mainView.setSelectedCarId(carId); // <-- Esto actualiza el label
        mainView.showMaintenancesLoading(true);
        SwingWorker<List<MaintenanceResponseDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MaintenanceResponseDto> doInBackground() throws Exception {
                return maintenanceService.listMaintenancesByCarAsync(carId, currentUserId).get();
            }
            @Override
            protected void done() {
                try {
                    List<MaintenanceResponseDto> maintenances = get();
                    mainView.getMaintenancesTableModel().setMaintenances(maintenances);
                } catch (Exception e) {
                    mainView.getMaintenancesTableModel().setMaintenances(List.of());
                } finally {
                    mainView.showMaintenancesLoading(false);
                }
            }
        };
        worker.execute();
    }


    // Limpia la tabla y los campos
    public void clearMaintenancesTable() {
        mainView.getMaintenancesTableModel().setMaintenances(List.of());
        mainView.clearMaintenanceFields();
        currentCarId = null;
    }

    // Habilita/deshabilita los controles de mantenimientos
    public void setControlsEnabled(boolean enabled) {
        mainView.getAddMaintenanceButton().setEnabled(enabled);
        mainView.getUpdateMaintenanceButton().setEnabled(enabled);
        mainView.getDeleteMaintenanceButton().setEnabled(enabled);
        mainView.getClearMaintenanceButton().setEnabled(enabled);
        mainView.getDescriptionField().setEnabled(enabled);
        mainView.getTypeComboBox().setEnabled(enabled);
        // Ya no hay ComboBox de CarID, solo un label
        // Si tienes un date picker:
        // mainView.getDatePicker().setEnabled(enabled);
    }

    private void addListeners() {
        mainView.getAddMaintenanceButton().addActionListener(e -> handleAddMaintenance());
        mainView.getUpdateMaintenanceButton().addActionListener(e -> handleUpdateMaintenance());
        mainView.getDeleteMaintenanceButton().addActionListener(e -> handleDeleteMaintenance());
        mainView.getClearMaintenanceButton().addActionListener(e -> handleClearFields());
        mainView.getMaintenancesTable().getSelectionModel().addListSelectionListener(this::handleRowSelection);
    }

    private void handleAddMaintenance() {
        if (currentCarId == null) {
            JOptionPane.showMessageDialog(mainView,
                    "Select a car first",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String description = mainView.getDescriptionField().getText().trim();
        String type = (String) mainView.getTypeComboBox().getSelectedItem();
        Long carId = mainView.getSelectedCarId();
        String date = mainView.getSelectedDate();

        if (description.isEmpty() || type == null || carId == null || date.isEmpty()) {
            JOptionPane.showMessageDialog(mainView,
                    "All fields are required",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddMaintenanceRequestDto dto = new AddMaintenanceRequestDto(description, date, carId, type);

        mainView.showMaintenancesLoading(true);
        SwingWorker<MaintenanceResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected MaintenanceResponseDto doInBackground() throws Exception {
                return maintenanceService.addMaintenanceAsync(dto, currentUserId).get();
            }

            @Override
            protected void done() {
                try {
                    MaintenanceResponseDto maintenance = get();
                    if (maintenance != null) {
                        notifyObservers(EventType.CREATED, maintenance);
                        mainView.clearMaintenanceFields();
                        showMaintenancesForCar(currentCarId);
                        JOptionPane.showMessageDialog(mainView,
                                "Maintenance added successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainView,
                                "Could not add maintenance",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainView,
                            "Error adding maintenance: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    mainView.showMaintenancesLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleUpdateMaintenance() {
        int selectedRow = mainView.getMaintenancesTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainView,
                    "Please select a maintenance to update",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MaintenanceResponseDto selected = mainView.getMaintenancesTableModel().getMaintenances().get(selectedRow);
        String description = mainView.getDescriptionField().getText().trim();
        String type = (String) mainView.getTypeComboBox().getSelectedItem();
        Long carId = mainView.getSelectedCarId();
        String date = mainView.getSelectedDate();

        if (description.isEmpty() || type == null || carId == null || date.isEmpty()) {
            JOptionPane.showMessageDialog(mainView,
                    "All fields are required",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UpdateMaintenanceRequestDto dto = new UpdateMaintenanceRequestDto(
                selected.getId(), description, date, carId, type
        );

        mainView.showMaintenancesLoading(true);
        SwingWorker<MaintenanceResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected MaintenanceResponseDto doInBackground() throws Exception {
                return maintenanceService.updateMaintenanceAsync(dto, currentUserId).get();
            }

            @Override
            protected void done() {
                try {
                    MaintenanceResponseDto updated = get();
                    if (updated != null) {
                        notifyObservers(EventType.UPDATED, updated);
                        mainView.clearMaintenanceFields();
                        showMaintenancesForCar(currentCarId);
                        JOptionPane.showMessageDialog(mainView,
                                "Maintenance updated successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainView,
                                "Could not update maintenance",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainView,
                            "Error updating maintenance: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    mainView.showMaintenancesLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleDeleteMaintenance() {
        int selectedRow = mainView.getMaintenancesTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainView,
                    "Please select a maintenance to delete",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MaintenanceResponseDto selected = mainView.getMaintenancesTableModel().getMaintenances().get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(mainView,
                "Are you sure you want to delete this maintenance?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        DeleteMaintenanceRequestDto dto = new DeleteMaintenanceRequestDto(selected.getId());

        mainView.showMaintenancesLoading(true);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return maintenanceService.deleteMaintenanceAsync(dto, currentUserId).get();
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        notifyObservers(EventType.DELETED, selected.getId());
                        mainView.clearMaintenanceFields();
                        showMaintenancesForCar(currentCarId);
                        JOptionPane.showMessageDialog(mainView,
                                "Maintenance deleted successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainView,
                                "Could not delete maintenance",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainView,
                            "Error deleting maintenance: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    mainView.showMaintenancesLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleClearFields() {
        mainView.clearMaintenanceFields();
    }

    private void handleRowSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        int row = mainView.getMaintenancesTable().getSelectedRow();
        if (row >= 0) {
            MaintenanceResponseDto maintenance = mainView.getMaintenancesTableModel().getMaintenances().get(row);
            mainView.populateMaintenanceFields(maintenance);
        }
    }
}







