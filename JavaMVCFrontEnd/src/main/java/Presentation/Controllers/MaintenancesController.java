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
import java.util.List;

public class MaintenancesController extends Observable {
    private final MainView mainView;
    private final MaintenanceService maintenanceService;
    private final CarService carService;

    public MaintenancesController(MainView mainView, MaintenanceService maintenanceService, CarService carService) {
        this.mainView = mainView;
        this.maintenanceService = maintenanceService;
        this.carService = carService;

        addObserver(mainView.getMaintenancesTableModel());
        loadMaintenancesAsync();
        loadCarsAsync();
        addListeners();
    }

    public void loadCarsAsync() {
        mainView.showMaintenancesLoading(true);
        SwingWorker<List<CarResponseDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CarResponseDto> doInBackground() throws Exception {
                return carService.listCarsAsync(1L).get();
            }

            @Override
            protected void done() {
                try {
                    List<CarResponseDto> cars = get();
                    mainView.setCarOptions(cars);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mainView.showMaintenancesLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void loadMaintenancesAsync() {
        mainView.showMaintenancesLoading(true);
        SwingWorker<List<MaintenanceResponseDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MaintenanceResponseDto> doInBackground() throws Exception {
                return maintenanceService.listMaintenancesAsync(1L).get();
            }

            @Override
            protected void done() {
                try {
                    List<MaintenanceResponseDto> maintenances = get();
                    mainView.getMaintenancesTableModel().setMaintenances(maintenances);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mainView.showMaintenancesLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void addListeners() {
        mainView.getAddMaintenanceButton().addActionListener(e -> handleAddMaintenance());
        mainView.getUpdateMaintenanceButton().addActionListener(e -> handleUpdateMaintenance());
        mainView.getDeleteMaintenanceButton().addActionListener(e -> handleDeleteMaintenance());
        mainView.getClearMaintenanceButton().addActionListener(e -> handleClearFields());
        mainView.getMaintenancesTable().getSelectionModel().addListSelectionListener(e -> handleRowSelection());
    }

    private void handleAddMaintenance() {
        String description = mainView.getDescriptionField().getText();
        String type = (String) mainView.getTypeComboBox().getSelectedItem();
        Long carId = mainView.getSelectedCarId();
        String date = mainView.getSelectedDate();

        if (description.isEmpty() || type == null || carId == null || date.isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Todos los campos son obligatorios", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddMaintenanceRequestDto dto = new AddMaintenanceRequestDto(description, date, carId, type);

        mainView.showMaintenancesLoading(true);
        SwingWorker<MaintenanceResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected MaintenanceResponseDto doInBackground() throws Exception {
                return maintenanceService.addMaintenanceAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    MaintenanceResponseDto maintenance = get();
                    if (maintenance != null) {
                        notifyObservers(EventType.CREATED, maintenance);
                        mainView.clearMaintenanceFields();
                    } else {
                        JOptionPane.showMessageDialog(mainView, "No se pudo agregar el mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
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
        if (selectedRow < 0) return;

        MaintenanceResponseDto selected = mainView.getMaintenancesTableModel().getMaintenances().get(selectedRow);
        String description = mainView.getDescriptionField().getText();
        String type = (String) mainView.getTypeComboBox().getSelectedItem();
        Long carId = mainView.getSelectedCarId();
        String date = mainView.getSelectedDate();

        if (description.isEmpty() || type == null || carId == null || date.isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Todos los campos son obligatorios", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UpdateMaintenanceRequestDto dto = new UpdateMaintenanceRequestDto(selected.getId(), description, date, carId, type);

        mainView.showMaintenancesLoading(true);
        SwingWorker<MaintenanceResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected MaintenanceResponseDto doInBackground() throws Exception {
                return maintenanceService.updateMaintenanceAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    MaintenanceResponseDto updated = get();
                    if (updated != null) {
                        notifyObservers(EventType.UPDATED, updated);
                        mainView.clearMaintenanceFields();
                    } else {
                        JOptionPane.showMessageDialog(mainView, "No se pudo actualizar el mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
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
        if (selectedRow < 0) return;

        MaintenanceResponseDto selected = mainView.getMaintenancesTableModel().getMaintenances().get(selectedRow);
        DeleteMaintenanceRequestDto dto = new DeleteMaintenanceRequestDto(selected.getId());

        mainView.showMaintenancesLoading(true);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return maintenanceService.deleteMaintenanceAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        notifyObservers(EventType.DELETED, selected.getId());
                        mainView.clearMaintenanceFields();
                    } else {
                        JOptionPane.showMessageDialog(mainView, "No se pudo borrar el mantenimiento", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
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

    private void handleRowSelection() {
        int row = mainView.getMaintenancesTable().getSelectedRow();
        if (row >= 0) {
            MaintenanceResponseDto maintenance = mainView.getMaintenancesTableModel().getMaintenances().get(row);
            mainView.populateMaintenanceFields(maintenance);
        }
    }
}






