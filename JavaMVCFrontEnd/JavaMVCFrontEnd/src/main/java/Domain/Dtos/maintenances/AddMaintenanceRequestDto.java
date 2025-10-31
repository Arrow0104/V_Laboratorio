package Domain.Dtos.maintenances;

public class AddMaintenanceRequestDto {
    private String description;
    private String date;
    private Long carId;
    private String type;

    public AddMaintenanceRequestDto() {}

    public AddMaintenanceRequestDto(String description, String date, Long carId, String type) {
        this.description = description;
        this.date = date;
        this.carId = carId;
        this.type = type;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
