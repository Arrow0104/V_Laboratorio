package org.example.API.controllers;

import com.google.gson.Gson;
import org.example.Domain.dtos.RequestDto;
import org.example.Domain.dtos.ResponseDto;
import org.example.Domain.dtos.maintenances.AddMaintenanceRequestDto;
import org.example.Domain.dtos.maintenances.UpdateMaintenanceRequestDto;
import org.example.Domain.dtos.maintenances.DeleteMaintenanceRequestDto;
import org.example.Domain.dtos.maintenances.MaintenanceResponseDto;
import org.example.Domain.dtos.maintenances.ListMaintenancesResponseDto;
import org.example.DataAccess.services.MaintenanceService;
import org.example.DataAccess.services.CarService;
import org.example.Domain.models.Maintenance;
import org.example.Domain.models.Car;
import org.example.Domain.models.MaintenanceType;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceController {
    private final MaintenanceService maintenanceService;
    private final CarService carService;
    private final Gson gson = new Gson();

    public MaintenanceController(MaintenanceService maintenanceService, CarService carService) {
        this.maintenanceService = maintenanceService;
        this.carService = carService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "add":
                    return handleAddMaintenance(request);
                case "update":
                    return handleUpdateMaintenance(request);
                case "delete":
                    return handleDeleteMaintenance(request);
                case "list":
                    return handleListMaintenances(request);
                case "get":
                    return handleGetMaintenance(request);
                case "listByCar":
                    return handleListByCar(request);
                default:
                    return new ResponseDto(false, "Unknown request: " + request.getRequest(), null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, e.getMessage(), null);
        }
    }

    // --- ADD MAINTENANCE ---
    private ResponseDto handleAddMaintenance(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            AddMaintenanceRequestDto dto = gson.fromJson(request.getData(), AddMaintenanceRequestDto.class);
            Car car = carService.getCarById(dto.getCarId());
            if (car == null) return new ResponseDto(false, "Car not found", null);

            MaintenanceType type;
            try {
                type = MaintenanceType.valueOf(dto.getType().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return new ResponseDto(false, "Invalid maintenance type: " + dto.getType(), null);
            }

            Date cardate = Date.valueOf(dto.getDate());

            Maintenance maintenance = maintenanceService.createMaintenance(dto.getDescription(), type, car, cardate);

            MaintenanceResponseDto responseDto = toResponseDto(maintenance);
            return new ResponseDto(true, "Maintenance created", gson.toJson(responseDto));
        } catch (Exception e) {
            System.out.println("Error in handleAddMaintenance: " + e.getMessage());
            return new ResponseDto(false, "Error creating maintenance: " + e.getMessage(), null);
        }
    }

    // --- UPDATE MAINTENANCE ---
    private ResponseDto handleUpdateMaintenance(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            UpdateMaintenanceRequestDto dto = gson.fromJson(request.getData(), UpdateMaintenanceRequestDto.class);
            Car car = carService.getCarById(dto.getCarId());
            if (car == null) return new ResponseDto(false, "Car not found", null);

            MaintenanceType type;
            try {
                type = MaintenanceType.valueOf(dto.getType().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return new ResponseDto(false, "Invalid maintenance type: " + dto.getType(), null);
            }

            Date cardate = Date.valueOf(dto.getDate());

            Maintenance maintenance = maintenanceService.updateMaintenance(dto.getId(), dto.getDescription(), type, car, cardate);
            if (maintenance == null) {
                return new ResponseDto(false, "Maintenance not found", null);
            }

            MaintenanceResponseDto responseDto = toResponseDto(maintenance);
            return new ResponseDto(true, "Maintenance updated", gson.toJson(responseDto));
        } catch (Exception e) {
            System.out.println("Error in handleUpdateMaintenance: " + e.getMessage());
            return new ResponseDto(false, "Error updating maintenance: " + e.getMessage(), null);
        }
    }

    // --- DELETE MAINTENANCE ---
    private ResponseDto handleDeleteMaintenance(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            DeleteMaintenanceRequestDto dto = gson.fromJson(request.getData(), DeleteMaintenanceRequestDto.class);
            boolean success = maintenanceService.deleteMaintenance(dto.getId());
            return new ResponseDto(success, success ? "Maintenance deleted" : "Maintenance not found", null);
        } catch (Exception e) {
            System.out.println("Error in handleDeleteMaintenance: " + e.getMessage());
            return new ResponseDto(false, "Error deleting maintenance: " + e.getMessage(), null);
        }
    }

    // --- LIST MAINTENANCES FOR A CAR ---
    private ResponseDto handleListMaintenances(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            Long carId = Long.valueOf(request.getData());
            List<Maintenance> maintenances = maintenanceService.getAllMaintenanceByCarId(carId);
            List<MaintenanceResponseDto> dtos = maintenances.stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());

            ListMaintenancesResponseDto response = new ListMaintenancesResponseDto(dtos);
            return new ResponseDto(true, "Maintenances retrieved successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleListMaintenances: " + e.getMessage());
            return new ResponseDto(false, "Error listing maintenances: " + e.getMessage(), null);
        }
    }

    // --- GET SINGLE MAINTENANCE ---
    private ResponseDto handleGetMaintenance(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            Long id = Long.valueOf(request.getData());
            Maintenance maintenance = maintenanceService.getMaintenanceById(id);
            if (maintenance == null) {
                return new ResponseDto(false, "Maintenance not found", null);
            }
            MaintenanceResponseDto dto = toResponseDto(maintenance);
            return new ResponseDto(true, "Maintenance found", gson.toJson(dto));
        } catch (Exception e) {
            System.out.println("Error in handleGetMaintenance: " + e.getMessage());
            return new ResponseDto(false, "Error getting maintenance: " + e.getMessage(), null);
        }
    }

    // --- LIST MAINTENANCES BY CAR (para listByCar) ---
    private ResponseDto handleListByCar(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }
            AddMaintenanceRequestDto dto = gson.fromJson(request.getData(), AddMaintenanceRequestDto.class);
            Long carId = dto.getCarId();
            List<Maintenance> maintenances = maintenanceService.getAllMaintenanceByCarId(carId);
            List<MaintenanceResponseDto> dtos = maintenances.stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
            ListMaintenancesResponseDto response = new ListMaintenancesResponseDto(dtos);
            return new ResponseDto(true, "Maintenances retrieved successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleListByCar: " + e.getMessage());
            return new ResponseDto(false, "Error listing maintenances: " + e.getMessage(), null);
        }
    }

    // --- Helper ---
    private MaintenanceResponseDto toResponseDto(Maintenance maintenance) {
        return new MaintenanceResponseDto(
                maintenance.getId(),
                maintenance.getDescription(),
                maintenance.getType() != null ? maintenance.getType().name() : null,
                maintenance.getCarMaintenance() != null ? maintenance.getCarMaintenance().getId() : null,
                maintenance.getCardate() != null ? maintenance.getCardate().toString() : null,
                maintenance.getCreatedAt() != null ? maintenance.getCreatedAt().toString() : null,
                maintenance.getUpdatedAt() != null ? maintenance.getUpdatedAt().toString() : null
        );
    }
}




