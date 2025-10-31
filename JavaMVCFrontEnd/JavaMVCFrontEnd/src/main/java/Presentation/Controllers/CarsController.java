package Presentation.Controllers;

import Domain.Dtos.cars.AddCarRequestDto;
import Domain.Dtos.cars.CarResponseDto;
import Domain.Dtos.cars.DeleteCarRequestDto;
import Domain.Dtos.cars.UpdateCarRequestDto;
import Presentation.Observable;
import Presentation.Views.CarsView;
import Services.CarService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;

public class CarsController extends Observable {
    private final CarsView carsView;
    private final CarService carService;
    private MaintenancesController maintenancesController; // Referencia opcional

    public CarsController(CarsView carsView, CarService carService) {
        this.carsView = carsView;
        this.carService = carService;

        addObserver(carsView.getTableModel());
        loadCarsAsync();
        addListeners();
    }

    // Permite inyectar la referencia después de crear ambos controllers
    public void setMaintenancesController(MaintenancesController maintenancesController) {
        this.maintenancesController = maintenancesController;
    }

    private void loadCarsAsync() {
        carsView.showLoading(true);
        SwingWorker<List<CarResponseDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CarResponseDto> doInBackground() throws Exception {
                return carService.listCarsAsync(1L).get();
            }

            @Override
            protected void done() {
                try {
                    List<CarResponseDto> cars = get();
                    carsView.getTableModel().setCars(cars);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    carsView.showLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void addListeners() {
        carsView.getAgregarButton().addActionListener(e -> handleAddCar());
        carsView.getUpdateButton().addActionListener(e -> handleUpdateCar());
        carsView.getBorrarButton().addActionListener(e -> handleDeleteCar());
        carsView.getClearButton().addActionListener(e -> handleClearFields());
        carsView.getCarsTable().getSelectionModel().addListSelectionListener(e -> handleRowSelection());
    }

    private void handleAddCar() {
        String make = carsView.getCarMakeField().getText();
        String model = carsView.getCarModelField().getText();
        String yearStr = carsView.getYearTextField().getText();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(carsView.getContentPanel(), "All fields are required", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(carsView.getContentPanel(), "Year must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AddCarRequestDto dto = new AddCarRequestDto(make, model, year, 1L);

        carsView.showLoading(true);
        SwingWorker<CarResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected CarResponseDto doInBackground() throws Exception {
                return carService.addCarAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    CarResponseDto car = get();
                    if (car != null) {
                        notifyObservers(EventType.CREATED, car);
                        carsView.clearFields();
                        // Notificar a MaintenancesController para actualizar la lista de carros
                        if (maintenancesController != null) {
                            maintenancesController.loadCarsAsync();
                        }
                    } else {
                        JOptionPane.showMessageDialog(carsView.getContentPanel(), "Failed to add car", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    carsView.showLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleUpdateCar() {
        int selectedRow = carsView.getCarsTable().getSelectedRow();
        if (selectedRow < 0) return;

        CarResponseDto selectedCar = carsView.getTableModel().getCars().get(selectedRow);
        String make = carsView.getCarMakeField().getText();
        String model = carsView.getCarModelField().getText();
        String yearStr = carsView.getYearTextField().getText();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(carsView.getContentPanel(), "All fields are required", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(carsView.getContentPanel(), "Year must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UpdateCarRequestDto dto = new UpdateCarRequestDto(selectedCar.getId(), make, model, year, selectedCar.getOwnerId());

        carsView.showLoading(true);
        SwingWorker<CarResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected CarResponseDto doInBackground() throws Exception {
                return carService.updateCarAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    CarResponseDto updatedCar = get();
                    if (updatedCar != null) {
                        notifyObservers(EventType.UPDATED, updatedCar);
                        carsView.clearFields();
                        // También podrías actualizar la lista en mantenimientos si lo deseas
                        if (maintenancesController != null) {
                            maintenancesController.loadCarsAsync();
                        }
                    } else {
                        JOptionPane.showMessageDialog(carsView.getContentPanel(), "Failed to update car", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    carsView.showLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleDeleteCar() {
        int selectedRow = carsView.getCarsTable().getSelectedRow();
        if (selectedRow < 0) return;

        CarResponseDto selectedCar = carsView.getTableModel().getCars().get(selectedRow);
        DeleteCarRequestDto dto = new DeleteCarRequestDto(selectedCar.getId());

        carsView.showLoading(true);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return carService.deleteCarAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        notifyObservers(EventType.DELETED, selectedCar.getId());
                        carsView.clearFields();
                        if (maintenancesController != null) {
                            maintenancesController.loadCarsAsync();
                        }
                    } else {
                        JOptionPane.showMessageDialog(carsView.getContentPanel(), "Failed to delete car", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    carsView.showLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleClearFields() {
        carsView.clearFields();
    }

    private void handleRowSelection() {
        int row = carsView.getCarsTable().getSelectedRow();
        if (row >= 0) {
            CarResponseDto car = carsView.getTableModel().getCars().get(row);
            carsView.populateFields(car);
        }
    }
}
