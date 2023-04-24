import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;
    ObjectMapper objectMapper;
    private int port;
    private MyModel model;

    public Server(int port, MyModel model) {
        this.port = port;
        this.model = model;
        this.objectMapper = new ObjectMapper();

        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();

            System.out.println("Client connected using port: " + clientSocket.getPort());

            while (true) {
                // Get status of waiting cars
                WaitingCarsVO[] waitingCars = objectMapper.readValue(clientSocket.getInputStream(), WaitingCarsVO[].class);

                // Update model with waiting cars
                model.setSimulatorJSON(List.of(waitingCars));

                // Calculate new status of lights
                Director.Decide(model);

                // Send new status
                objectMapper.writeValue(clientSocket.getOutputStream(), model.getSimulatorJSON());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}