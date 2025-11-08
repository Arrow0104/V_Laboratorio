package Domain.Dtos;

public class ResponseDto {
    private boolean success;   // true si la operación fue exitosa
    private String message;    // descripción o mensaje de error
    private String data;       // JSON del DTO de respuesta (UserResponseDto, AuthResponseData, etc.)

    public ResponseDto() {}

    public ResponseDto(boolean success, String message, String data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor para manejar solo success y message (data puede ser null)
    public ResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

