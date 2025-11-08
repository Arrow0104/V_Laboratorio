package Services;

import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import Domain.Dtos.cars.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CarService extends BaseService {
    private final ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

    public CarService(String host, int port) {
        super(host, port);
    }

    public Future<CarResponseDto> addCarAsync(AddCarRequestDto dto, Long userId) {
        return executor.submit(() -> {
            System.out.println("[CarService] Attempting to add car: " + dto.getMake() + " " + dto.getModel() + " " + dto.getYear());
            RequestDto request = new RequestDto("Cars", "add", gson.toJson(dto), userId.toString());
            System.out.println("[CarService] Request JSON: " + gson.toJson(request));

            ResponseDto response = sendRequest(request);

            if (response == null) {
                System.err.println("[CarService] ERROR: Response is NULL - Server may be down or not responding");
                return null;
            }

            System.out.println("[CarService] Response success: " + response.isSuccess());
            System.out.println("[CarService] Response message: " + response.getMessage());
            System.out.println("[CarService] Response data: " + response.getData());

            if (!response.isSuccess()) {
                System.err.println("[CarService] ERROR: Server returned failure - " + response.getMessage());
                return null;
            }

            CarResponseDto carResponse = gson.fromJson(response.getData(), CarResponseDto.class);
            System.out.println("[CarService] Car added successfully with ID: " + (carResponse != null ? carResponse.getId() : "NULL"));
            return carResponse;
        });
    }

    public Future<CarResponseDto> updateCarAsync(UpdateCarRequestDto dto, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Cars", "update", gson.toJson(dto), userId.toString());
            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) return null;
            return gson.fromJson(response.getData(), CarResponseDto.class);
        });
    }

    public Future<Boolean> deleteCarAsync(DeleteCarRequestDto dto, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Cars", "delete", gson.toJson(dto), userId.toString());
            ResponseDto response = sendRequest(request);
            return response.isSuccess();
        });
    }

    public Future<List<CarResponseDto>> listCarsAsync(Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Cars", "list", "", userId.toString());
            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) return null;
            ListCarsResponseDto listResponse = gson.fromJson(response.getData(), ListCarsResponseDto.class);
            return listResponse.getCars();
        });
    }
}