package Domain.Dtos.maintenances;

import java.util.List;

public class ListMaintenancesResponseDto {
    private List<MaintenanceResponseDto> maintenances;

    public ListMaintenancesResponseDto() {}

    public List<MaintenanceResponseDto> getMaintenances() { return maintenances; }
    public void setMaintenances(List<MaintenanceResponseDto> maintenances) { this.maintenances = maintenances; }
}
