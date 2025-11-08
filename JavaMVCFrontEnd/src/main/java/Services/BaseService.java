package Services;

import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class BaseService {
    protected final String host;
    protected final int port;
    protected final Gson gson = new Gson();

    public BaseService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    protected ResponseDto sendRequest(RequestDto request) {
        System.out.println("[BaseService] Connecting to " + host + ":" + port);
        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String requestJson = gson.toJson(request);
            System.out.println("[BaseService] Sending: " + requestJson);

            writer.write(requestJson);
            writer.newLine();
            writer.flush();
            System.out.println("[BaseService] Request sent, waiting for response...");

            var responseJson = reader.readLine();
            System.out.println("[BaseService] Response received: " + responseJson);

            if (responseJson == null || responseJson.trim().isEmpty()) {
                System.err.println("[BaseService] ERROR: Empty response from server");
                return new ResponseDto(false, "Empty response from server", null);
            }

            ResponseDto response = gson.fromJson(responseJson, ResponseDto.class);
            return response;

        } catch (IOException e) {
            System.err.println("[BaseService] ERROR: Connection failed - " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Connection error: " + e.getMessage(), null);
        }
    }
}