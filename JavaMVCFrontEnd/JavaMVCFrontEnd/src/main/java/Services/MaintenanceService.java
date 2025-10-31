// src/main/java/Services/MaintenanceService.java
package Services;

import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import Domain.Dtos.maintenances.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MaintenanceService extends BaseService {
    private final ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

    public MaintenanceService(String host, int port) {
        super(host, port);
    }

    public Future<MaintenanceResponseDto> addMaintenanceAsync(AddMaintenanceRequestDto dto, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Maintenances", "add", gson.toJson(dto), userId.toString());
            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) return null;
            return gson.fromJson(response.getData(), MaintenanceResponseDto.class);
        });
    }

    public Future<MaintenanceResponseDto> updateMaintenanceAsync(UpdateMaintenanceRequestDto dto, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Maintenances", "update", gson.toJson(dto), userId.toString());
            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) return null;
            return gson.fromJson(response.getData(), MaintenanceResponseDto.class);
        });
    }

    public Future<Boolean> deleteMaintenanceAsync(DeleteMaintenanceRequestDto dto, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Maintenances", "delete", gson.toJson(dto), userId.toString());
            ResponseDto response = sendRequest(request);
            return response.isSuccess();
        });
    }

    public Future<List<MaintenanceResponseDto>> listMaintenancesAsync(Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Maintenances", "list", "", userId.toString());
            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) return null;
            ListMaintenancesResponseDto listResponse = gson.fromJson(response.getData(), ListMaintenancesResponseDto.class);
            return listResponse.getMaintenances();
        });
    }
}
