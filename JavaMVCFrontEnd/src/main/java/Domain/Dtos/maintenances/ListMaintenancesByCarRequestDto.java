package Domain.Dtos.maintenances;

public class ListMaintenancesByCarRequestDto {
    private Long carId;

    public ListMaintenancesByCarRequestDto() {}

    public ListMaintenancesByCarRequestDto(Long carId) {
        this.carId = carId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}
