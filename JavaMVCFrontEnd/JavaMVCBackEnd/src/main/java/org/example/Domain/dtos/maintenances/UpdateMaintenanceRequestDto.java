package org.example.Domain.dtos.maintenances;

public class UpdateMaintenanceRequestDto {
    private Long id;
    private String description;
    private String type;
    private Long carId;
    private String date;

    public UpdateMaintenanceRequestDto() {}

    public UpdateMaintenanceRequestDto(Long id, String description, String type, Long carId, String date) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.carId = carId;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
