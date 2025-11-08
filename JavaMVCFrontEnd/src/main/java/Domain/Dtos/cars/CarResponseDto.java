package Domain.Dtos.cars;

import Domain.Dtos.auth.UserResponseDto;

public class CarResponseDto {
    private Long id;
    private String make;
    private String model;
    private int year;
    private Long ownerId;
    private UserResponseDto owner;

    public CarResponseDto() {}

    public CarResponseDto(Long id, String make, String model, int year, Long ownerId, UserResponseDto owner) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
        this.owner = owner;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public UserResponseDto getOwner() { return owner; }
    public void setOwner(UserResponseDto owner) { this.owner = owner; }
    @Override
    public String toString() {
        return "ID: " + getId() + " - Model: " + getModel();
    }

}


