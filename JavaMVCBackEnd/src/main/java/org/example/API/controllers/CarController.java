package org.example.API.controllers;

import com.google.gson.Gson;
import org.example.Domain.dtos.RequestDto;
import org.example.Domain.dtos.ResponseDto;
import org.example.Domain.dtos.auth.UserResponseDto;
import org.example.Domain.dtos.cars.*;
import org.example.Domain.models.Car;
import org.example.Domain.models.User;
import org.example.DataAccess.services.CarService;
import org.example.DataAccess.services.AuthService;

import java.util.List;
import java.util.stream.Collectors;

public class CarController {

    private final CarService carService;
    private final AuthService authService;
    private final Gson gson = new Gson();

    public CarController(CarService carService, AuthService authService) {
        this.carService = carService;
        this.authService = authService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "add":
                    return handleAddCar(request);
                case "update":
                    return handleUpdateCar(request);
                case "delete":
                    return handleDeleteCar(request);
                case "list":
                    return handleListCars(request);
                case "get":
                    return handleGetCar(request);
                default:
                    return new ResponseDto(false, "Unknown request: " + request.getRequest(), null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, e.getMessage(), null);
        }
    }

    // --- ADD CAR ---
    private ResponseDto handleAddCar(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }
            User user = authService.getUserByToken(request.getToken());
            if (user == null) {
                return new ResponseDto(false, "Invalid token", null);
            }
            AddCarRequestDto dto = gson.fromJson(request.getData(), AddCarRequestDto.class);
            if (dto == null || !dto.isValid()) {
                return new ResponseDto(false, "Invalid car data", null);
            }
            Car car = carService.createCar(dto.getMake(), dto.getModel(), dto.getYear(), user.getId());
            CarResponseDto response = toResponseDto(car);
            return new ResponseDto(true, "Car added successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleAddCar: " + e.getMessage());
            return new ResponseDto(false, "Could not add car: " + e.getMessage(), null);
        }
    }

    // --- UPDATE CAR ---
    private ResponseDto handleUpdateCar(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }
            User user = authService.getUserByToken(request.getToken());
            if (user == null) {
                return new ResponseDto(false, "Invalid token", null);
            }
            UpdateCarRequestDto dto = gson.fromJson(request.getData(), UpdateCarRequestDto.class);
            Car car = carService.getCarById(dto.getId());
            if (car == null) {
                return new ResponseDto(false, "Car not found", null);
            }
            if (!car.getOwner().getId().equals(user.getId())) {
                return new ResponseDto(false, "Forbidden: not your car", null);
            }
            Car updated = carService.updateCar(dto.getId(), dto.getMake(), dto.getModel(), dto.getYear());
            CarResponseDto response = toResponseDto(updated);
            return new ResponseDto(true, "Car updated successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleUpdateCar: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // --- DELETE CAR ---
    private ResponseDto handleDeleteCar(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }
            User user = authService.getUserByToken(request.getToken());
            if (user == null) {
                return new ResponseDto(false, "Invalid token", null);
            }
            DeleteCarRequestDto dto = gson.fromJson(request.getData(), DeleteCarRequestDto.class);
            Car car = carService.getCarById(dto.getId());
            if (car == null) {
                return new ResponseDto(false, "Car not found", null);
            }
            if (!car.getOwner().getId().equals(user.getId())) {
                return new ResponseDto(false, "Forbidden: not your car", null);
            }
            boolean deleted = carService.deleteCar(dto.getId());
            if (!deleted) {
                return new ResponseDto(false, "Car could not be deleted", null);
            }
            return new ResponseDto(true, "Car deleted successfully", null);
        } catch (Exception e) {
            System.out.println("Error in handleDeleteCar: " + e.getMessage());
            throw e;
        }
    }

    // --- LIST CARS ---
    private ResponseDto handleListCars(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }
            User user = authService.getUserByToken(request.getToken());
            if (user == null) {
                return new ResponseDto(false, "Invalid token", null);
            }
            List<Car> cars = carService.getCarsByUser(user);
            List<CarResponseDto> carDtos = cars.stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
            ListCarsResponseDto response = new ListCarsResponseDto(carDtos);
            return new ResponseDto(true, "Cars retrieved successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleListCars: " + e.getMessage());
            throw e;
        }
    }

    // --- GET SINGLE CAR ---
    private ResponseDto handleGetCar(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }
            User user = authService.getUserByToken(request.getToken());
            if (user == null) {
                return new ResponseDto(false, "Invalid token", null);
            }
            DeleteCarRequestDto dto = gson.fromJson(request.getData(), DeleteCarRequestDto.class);
            Car car = carService.getCarById(dto.getId());
            if (car == null) {
                return new ResponseDto(false, "Car not found", null);
            }
            if (!car.getOwner().getId().equals(user.getId())) {
                return new ResponseDto(false, "Forbidden: not your car", null);
            }
            CarResponseDto response = toResponseDto(car);
            return new ResponseDto(true, "Car retrieved successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleGetCar: " + e.getMessage());
            throw e;
        }
    }

    // --- Helper method ---
    private CarResponseDto toResponseDto(Car car) {
        var owner =  new UserResponseDto(
                car.getOwner().getId(),
                car.getOwner().getUsername(),
                car.getOwner().getEmail(),
                car.getOwner().getRole(),
                car.getOwner().getCreatedAt().toString(),
                car.getOwner().getUpdatedAt().toString()
        );
        return new CarResponseDto(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getYear(),
                owner,
                car.getCreatedAt().toString(),
                car.getUpdatedAt().toString()
        );
    }
}


