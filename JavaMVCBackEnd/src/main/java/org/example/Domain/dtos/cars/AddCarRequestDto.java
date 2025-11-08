package org.example.Domain.dtos.cars;

public class AddCarRequestDto {
    private String make;
    private String model;
    private int year;

    public AddCarRequestDto() {}

    public AddCarRequestDto(String make, String model, int year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }

    // Getters & Setters
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    // Validación básica
    public boolean isValid() {
        return make != null && !make.isEmpty() &&
                model != null && !model.isEmpty() &&
                year > 1900;
    }
}
