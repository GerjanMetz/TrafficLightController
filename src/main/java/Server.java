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
    private IntersectionModel intersectionModel;

    /**
     *
     * @param port the port to use for the server.
     * @param intersectionModel the model of the intersection.
     */
    public Server(int port, IntersectionModel intersectionModel) {
        this.port = port;
        this.intersectionModel = intersectionModel;
        this.objectMapper = new ObjectMapper();

        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    }

    /**
     * Main loop of the application.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();

            System.out.println("Client connected using port: " + clientSocket.getPort());

            while (true) {
                // Get status of waiting cars
                WaitingCarsVO[] waitingCars = objectMapper.readValue(clientSocket.getInputStream(), WaitingCarsVO[].class);

                // Update model with waiting cars
                intersectionModel.setSimulatorJSON(List.of(waitingCars));

                // Calculate new status of lights
                Director.Decide(intersectionModel);

                // Send new status
                objectMapper.writeValue(clientSocket.getOutputStream(), intersectionModel.getSimulatorJSON());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
