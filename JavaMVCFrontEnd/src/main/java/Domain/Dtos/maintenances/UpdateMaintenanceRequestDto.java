package Domain.Dtos.maintenances;

public class UpdateMaintenanceRequestDto {
    private Long id;
    private String description;
    private String date;
    private Long carId;
    private Long userId;
    private String type; // <-- Agrega este campo

    public UpdateMaintenanceRequestDto() {}

    public UpdateMaintenanceRequestDto(Long id, String description, String date, Long carId, String type) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.carId = carId;
        this.type = type;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}