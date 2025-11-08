package Presentation.Controllers;

import Domain.Dtos.cars.AddCarRequestDto;
import Domain.Dtos.cars.CarResponseDto;
import Domain.Dtos.cars.DeleteCarRequestDto;
import Domain.Dtos.cars.UpdateCarRequestDto;
import Presentation.Observable;
import Presentation.Views.MainView;
import Services.CarService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.List;

public class CarsController extends Observable {
    private final MainView mainView;
    private final CarService carService;
    private final Long currentUserId;
    private MaintenancesController maintenancesController;

    public CarsController(MainView mainView, CarService carService, Long currentUserId) {
        this.mainView = mainView;
        this.carService = carService;
        this.currentUserId = currentUserId;

        addObserver(mainView.getCarsTableModel());
        loadCarsAsync();
        addListeners();

        // Panel de mantenimientos deshabilitado y vacío al inicio
        if (maintenancesController != null) {
            maintenancesController.clearMaintenancesTable();
            maintenancesController.setControlsEnabled(false);
        }
    }

    public void setMaintenancesController(MaintenancesController maintenancesController) {
        this.maintenancesController = maintenancesController;
        // Asegura estado inicial
        maintenancesController.clearMaintenancesTable();
        maintenancesController.setControlsEnabled(false);
    }

    private void loadCarsAsync() {
        mainView.showCarsLoading(true);

        SwingWorker<List<CarResponseDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CarResponseDto> doInBackground() throws Exception {
                return carService.listCarsAsync(currentUserId).get();
            }

            @Override
            protected void done() {
                try {
                    List<CarResponseDto> cars = get();
                    mainView.getCarsTableModel().setCars(cars);
                    if (maintenancesController != null) {
                        maintenancesController.updateCarOptions(cars);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainView,
                            "Error loading cars: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    mainView.showCarsLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void addListeners() {
        mainView.getAddCarButton().addActionListener(e -> handleAddCar());
        mainView.getUpdateCarButton().addActionListener(e -> handleUpdateCar());
        mainView.getDeleteCarButton().addActionListener(e -> handleDeleteCar());
        mainView.getClearCarButton().addActionListener(e -> handleClearFields());
        mainView.getCarsTable().getSelectionModel().addListSelectionListener(this::handleRowSelection);
    }

    private void handleAddCar() {
        String make = mainView.getCarMakeField().getText().trim();
        String model = mainView.getCarModelField().getText().trim();
        String yearStr = mainView.getYearTextField().getText().trim();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(mainView,
                    "All fields are required",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainView,
                    "Year must be a number",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddCarRequestDto dto = new AddCarRequestDto(make, model, year, currentUserId);

        mainView.showCarsLoading(true);
        SwingWorker<CarResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected CarResponseDto doInBackground() throws Exception {
                return carService.addCarAsync(dto, currentUserId).get();
            }

            @Override
            protected void done() {
                try {
                    CarResponseDto car = get();
                    if (car != null) {
                        notifyObservers(EventType.CREATED, car);
                        mainView.clearCarFields();
                        if (maintenancesController != null) {
                            maintenancesController.updateCarOptions(mainView.getCarsTableModel().getCars());
                        }
                        JOptionPane.showMessageDialog(mainView,
                                "Car added successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainView,
                                "Could not add car",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainView,
                            "Error adding car: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    mainView.showCarsLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleUpdateCar() {
        int selectedRow = mainView.getCarsTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainView,
                    "Please select a car to update",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CarResponseDto selectedCar = mainView.getCarsTableModel().getCars().get(selectedRow);
        String make = mainView.getCarMakeField().getText().trim();
        String model = mainView.getCarModelField().getText().trim();
        String yearStr = mainView.getYearTextField().getText().trim();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(mainView,
                    "All fields are required",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainView,
                    "Year must be a number",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UpdateCarRequestDto dto = new UpdateCarRequestDto(
                selectedCar.getId(), make, model, year, selectedCar.getOwnerId()
        );

        mainView.showCarsLoading(true);
        SwingWorker<CarResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected CarResponseDto doInBackground() throws Exception {
                return carService.updateCarAsync(dto, currentUserId).get();
            }

            @Override
            protected void done() {
                try {
                    CarResponseDto updated = get();
                    if (updated != null) {
                        notifyObservers(EventType.UPDATED, updated);
                        mainView.clearCarFields();
                        if (maintenancesController != null) {
                            maintenancesController.updateCarOptions(mainView.getCarsTableModel().getCars());
                        }
                        JOptionPane.showMessageDialog(mainView,
                                "Car updated successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainView,
                                "Could not update car",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainView,
                            "Error updating car: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    mainView.showCarsLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleDeleteCar() {
        int selectedRow = mainView.getCarsTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainView,
                    "Please select a car to delete",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CarResponseDto selectedCar = mainView.getCarsTableModel().getCars().get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(mainView,
                "Are you sure you want to delete this car?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        DeleteCarRequestDto dto = new DeleteCarRequestDto(selectedCar.getId());

        mainView.showCarsLoading(true);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return carService.deleteCarAsync(dto, currentUserId).get();
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        notifyObservers(EventType.DELETED, selectedCar.getId());
                        mainView.clearCarFields();
                        if (maintenancesController != null) {
                            maintenancesController.updateCarOptions(mainView.getCarsTableModel().getCars());
                        }
                        JOptionPane.showMessageDialog(mainView,
                                "Car deleted successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainView,
                                "Could not delete car",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainView,
                            "Error deleting car: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    mainView.showCarsLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleClearFields() {
        mainView.clearCarFields();
        if (maintenancesController != null) {
            maintenancesController.clearMaintenancesTable();
            maintenancesController.setControlsEnabled(false);
        }
    }

    private void handleRowSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = mainView.getCarsTable().getSelectedRow();
            if (row >= 0) {
                CarResponseDto car = mainView.getCarsTableModel().getCars().get(row);
                mainView.populateCarFields(car);
                if (maintenancesController != null) {
                    maintenancesController.showMaintenancesForCar(car.getId());
                    maintenancesController.setControlsEnabled(true);
                }
            } else {
                if (maintenancesController != null) {
                    maintenancesController.clearMaintenancesTable();
                    maintenancesController.setControlsEnabled(false);
                }
            }
        }
    }
}


