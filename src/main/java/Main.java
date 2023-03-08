import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);

        ServerSocket serverSocket;
        Socket clientSocket;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        int tickNumber = 0;
        ArrayList<TrafficLightStatusVO> trafficLightStatus = new ArrayList<>();
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(2.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(5.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(8.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(11.1);
            setStatus(0);
        }});

        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();

            System.out.println("Client connected using port: " + clientSocket.getPort());

            while (true) {
                // Get status of waiting cars
                WaitingCarsVO[] waitingCars = objectMapper.readValue(clientSocket.getInputStream(), WaitingCarsVO[].class);

                // Calculate new status of lights
                setNewLightStatus(trafficLightStatus, waitingCars, tickNumber);

                // Send new status
                objectMapper.writeValue(clientSocket.getOutputStream(), trafficLightStatus);

                // Update tick
                tickNumber++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Quick and dirty way to alternate traffic lights
     *
     * @param tick The current tick number used to alternate traffic lights
     * @return ArrayList of TrafficLightVO containing the new status of the traffic lights
     */
    public static void setNewLightStatus(ArrayList<TrafficLightStatusVO> currentTrafficLightStatus, WaitingCarsVO[] waitingCars,  int tick) {
        ArrayList<TrafficLightStatusVO> newTrafficLightStatus = new ArrayList<>();
        ArrayList<WaitingCarsVO> waitingCarsVOArrayList = new ArrayList<>(List.of(waitingCars));
        
        // Check if there are no cars
        if (checkIfNoCars(waitingCarsVOArrayList)) {
            currentTrafficLightStatus.forEach(trafficLightStatusVO -> trafficLightStatusVO.status = 0);
            return;
        }

        return;
    }
    
    public static boolean checkIfNoCars(ArrayList<WaitingCarsVO> waitingCars) {
        for (WaitingCarsVO waitingCarsVO : waitingCars) {
            if (waitingCarsVO.weight > 0)
                return false;
        }
        return true;
    }
}
