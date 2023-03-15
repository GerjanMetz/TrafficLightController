import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        HashMap<Double, TrafficLightConflict> conflictMatrix = new HashMap<Double, TrafficLightConflict>();
        conflictMatrix.put(1.1, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 10.1, 11.1, 12.1), List.of(5.1, 9.1)));
        conflictMatrix.put(2.1, new TrafficLightConflict(List.of(1.1, 7.1, 8.1), List.of(5.1, 6.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(5.1, new TrafficLightConflict(List.of(6.1, 7.1, 10.1, 11.1), List.of(1.1, 2.1, 8.1, 9.1, 12.1)));
        conflictMatrix.put(6.1, new TrafficLightConflict(List.of(1.1, 5.1, 7.1), List.of(2.1, 8.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(7.1, new TrafficLightConflict(List.of(1.1, 5.1, 6.1, 8.1, 9.1, 10.1, 12.1), List.of(2.1, 11.1)));
        conflictMatrix.put(8.1, new TrafficLightConflict(List.of(1.1, 2.1, 7.1, 9.1, 10.1), List.of(5.1, 6.1, 11.1, 12.1)));
        conflictMatrix.put(9.1, new TrafficLightConflict(List.of(7.1, 8.1, 10.1), List.of(1.1, 2.1, 5.1, 6.1, 11.1, 12.1)));
        conflictMatrix.put(10.1,new TrafficLightConflict(List.of(1.1, 5.1, 7.1, 8.1, 9.1, 11.1, 12.1), List.of(2.1, 6.1)));
        conflictMatrix.put(11.1,new TrafficLightConflict(List.of(1.1, 5.1, 10.1, 12.1), List.of(2.1, 6.1, 7.1, 8.1, 9.1)));
        conflictMatrix.put(12.1,new TrafficLightConflict(List.of(1.1, 7.1, 10.1, 11.1), List.of(2.1, 5.1, 6.1, 8.1, 9.1)));

        ArrayList<TrafficLightStatusVO> trafficLightStatus = new ArrayList<>();
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(1.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(2.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(5.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(6.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(7.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(8.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(9.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(10.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(11.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(12.1);
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
        ArrayList<WaitingCarsVO> waitingCarsVOArrayList = new ArrayList<>(List.of(waitingCars));

        // Check if most recent change to green lights is at least 10 seconds
        if (hasLightActive(currentTrafficLightStatus, 2)) {
            if (getShortestChangeToStatusDateInSeconds(currentTrafficLightStatus, 2) > 10) {
                setGreenLightsToOrange(currentTrafficLightStatus);
                return;
            }
        }

        // Check if most recent change to orange lights is at least 3 seconds
        if (hasLightActive(currentTrafficLightStatus, 1)) {
            if (getShortestChangeToStatusDateInSeconds(currentTrafficLightStatus, 1) > 3) {
                setOrangeLightsToRed(currentTrafficLightStatus);
                return;
            }
        }

        // Check if most recent change to red lights is at least 2 seconds
        if (hasAllLightsSetToRed(currentTrafficLightStatus)) {
            if (getShortestChangeToStatusDateInSeconds(currentTrafficLightStatus, 0) > 2) {
                // Check if there are no cars
                if (hasNoWaitingCars(waitingCarsVOArrayList)) {
                    currentTrafficLightStatus.forEach(trafficLightStatusVO -> trafficLightStatusVO.status = 0);
                    return;
                }

                // Find lane with the highest weight number
                WaitingCarsVO highestWeight = findHighestWeight(waitingCarsVOArrayList);
                // Set light status according to truth table
                try {
                    setLightStatusAccordingTruthTable(currentTrafficLightStatus, highestWeight);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

//        // Check if there is no light set to red for more than 1 minute and 40 seconds
//        if (hasLightActive(currentTrafficLightStatus, 0)) {
//            if (getLongestChangeToStatusDateInSeconds(currentTrafficLightStatus, 0) > 100) {
//                System.out.println(new Exception("Traffic light was set to red for more than 100 seconds"));
//            }
//        }
    }
    
    public static boolean hasNoWaitingCars(ArrayList<WaitingCarsVO> waitingCars) {
        for (WaitingCarsVO waitingCarsVO : waitingCars) {
            if (waitingCarsVO.weight > 0)
                return false;
        }
        return true;
    }

    public static boolean hasLightActive(ArrayList<TrafficLightStatusVO> currentTrafficLightStatus, int lightStatus) {
        for (TrafficLightStatusVO lightStatusVO : currentTrafficLightStatus) {
            if (lightStatusVO.getStatus() == lightStatus)
                return true;
        }
        return false;
    }

    public static boolean hasAllLightsSetToRed(ArrayList<TrafficLightStatusVO> currentTrafficLightStatus) {
        for (TrafficLightStatusVO lightStatusVO : currentTrafficLightStatus) {
            if (lightStatusVO.getStatus() != 0)
                return false;
        }
        return true;
    }

    public static long getShortestChangeToStatusDateInSeconds(ArrayList<TrafficLightStatusVO> currentTrafficLightStatus, int lightStatus) {
        long shortestSeconds = Long.MAX_VALUE;
        LocalDateTime now = LocalDateTime.now();

        for (TrafficLightStatusVO lightStatusVO : currentTrafficLightStatus) {
            if (lightStatusVO.getStatus() == lightStatus &&
                    (Duration.between(lightStatusVO.getLastChangeToStatusDate(), now)).getSeconds() < shortestSeconds) {
                shortestSeconds = (Duration.between(lightStatusVO.getLastChangeToStatusDate(), now)).getSeconds();
            }
        }
        if (shortestSeconds == Long.MAX_VALUE)
            return -1;
        return shortestSeconds;
    }

    public static long getLongestChangeToStatusDateInSeconds(ArrayList<TrafficLightStatusVO> currentTrafficLightStatus, int lightStatus) {
        long shortestSeconds = Long.MIN_VALUE;
        LocalDateTime now = LocalDateTime.now();

        for (TrafficLightStatusVO lightStatusVO : currentTrafficLightStatus) {
            if (lightStatusVO.getStatus() == lightStatus &&
                    (Duration.between(lightStatusVO.getLastChangeToStatusDate(), now)).getSeconds() > shortestSeconds) {
                shortestSeconds = (Duration.between(lightStatusVO.getLastChangeToStatusDate(), now)).getSeconds();
            }
        }
        if (shortestSeconds == Long.MAX_VALUE)
            return -1;
        return shortestSeconds;
    }

    public static WaitingCarsVO findHighestWeight(ArrayList<WaitingCarsVO> waitingCars) {
        WaitingCarsVO result = waitingCars.get(0);
        for (WaitingCarsVO waitingCarsVO : waitingCars) {
            if (waitingCarsVO.weight > result.weight) {
                result = waitingCarsVO;
            }
        }
        return result;
    }

    public static void setLightStatusAccordingTruthTable(ArrayList<TrafficLightStatusVO> trafficLightStatus, WaitingCarsVO highestPriorityLane) throws Exception {
        if (highestPriorityLane.getId() == 1.1 || highestPriorityLane.getId() == 2.1) {
            trafficLightStatus.get(0).setStatus(2); // Light 1.1
            trafficLightStatus.get(1).setStatus(2); // Light 2.1
            trafficLightStatus.get(2).setStatus(0); // Light 5.1
            trafficLightStatus.get(3).setStatus(0); // Light 6.1
            trafficLightStatus.get(4).setStatus(2); // Light 7.1
            trafficLightStatus.get(5).setStatus(2); // Light 8.1
            trafficLightStatus.get(6).setStatus(0); // Light 9.1
            trafficLightStatus.get(7).setStatus(0); // Light 10.1
            trafficLightStatus.get(8).setStatus(0); // Light 11.1
            trafficLightStatus.get(9).setStatus(0); // Light 12.1
        } else if (highestPriorityLane.getId() == 5.1 || highestPriorityLane.getId() == 6.1) {
            trafficLightStatus.get(0).setStatus(0); // Light 1.1
            trafficLightStatus.get(1).setStatus(0); // Light 2.1
            trafficLightStatus.get(2).setStatus(2); // Light 5.1
            trafficLightStatus.get(3).setStatus(2); // Light 6.1
            trafficLightStatus.get(4).setStatus(2); // Light 7.1
            trafficLightStatus.get(5).setStatus(0); // Light 8.1
            trafficLightStatus.get(6).setStatus(0); // Light 9.1
            trafficLightStatus.get(7).setStatus(0); // Light 10.1
            trafficLightStatus.get(8).setStatus(0); // Light 11.1
            trafficLightStatus.get(9).setStatus(0); // Light 12.1
        } else if (highestPriorityLane.getId() == 7.1 || highestPriorityLane.getId() == 8.1 || highestPriorityLane.getId() == 9.1) {
            trafficLightStatus.get(0).setStatus(0); // Light 1.1
            trafficLightStatus.get(1).setStatus(0); // Light 2.1
            trafficLightStatus.get(2).setStatus(0); // Light 5.1
            trafficLightStatus.get(3).setStatus(0); // Light 6.1
            trafficLightStatus.get(4).setStatus(2); // Light 7.1
            trafficLightStatus.get(5).setStatus(2); // Light 8.1
            trafficLightStatus.get(6).setStatus(2); // Light 9.1
            trafficLightStatus.get(7).setStatus(2); // Light 10.1
            trafficLightStatus.get(8).setStatus(0); // Light 11.1
            trafficLightStatus.get(9).setStatus(0); // Light 12.1
        } else if (highestPriorityLane.getId() == 10.1 || highestPriorityLane.getId() == 11.1 || highestPriorityLane.getId() == 12.1) {
            trafficLightStatus.get(0).setStatus(2); // Light 1.1
            trafficLightStatus.get(1).setStatus(0); // Light 2.1
            trafficLightStatus.get(2).setStatus(0); // Light 5.1
            trafficLightStatus.get(3).setStatus(0); // Light 6.1
            trafficLightStatus.get(4).setStatus(0); // Light 7.1
            trafficLightStatus.get(5).setStatus(0); // Light 8.1
            trafficLightStatus.get(6).setStatus(0); // Light 9.1
            trafficLightStatus.get(7).setStatus(2); // Light 10.1
            trafficLightStatus.get(8).setStatus(2); // Light 11.1
            trafficLightStatus.get(9).setStatus(2); // Light 12.1
        } else {
            throw new Exception("Undefined traffic light ID supplied, can be 1.1, 2.1, 5.1, 6.1, 7.1, 8.1, 9.1, 10.1, 11.1 or 12.1, but was: " + highestPriorityLane.getId());
        }
    }

    public static void setGreenLightsToOrange(ArrayList<TrafficLightStatusVO> trafficLightStatus) {
        trafficLightStatus.forEach(trafficLightStatusVO -> {
            if (trafficLightStatusVO.getStatus() == 2)
                trafficLightStatusVO.setStatus(1);
        });
    }

    public static void setOrangeLightsToRed(ArrayList<TrafficLightStatusVO> trafficLightStatus) {
        trafficLightStatus.forEach(trafficLightStatusVO -> {
            if (trafficLightStatusVO.getStatus() == 1)
                trafficLightStatusVO.setStatus(0);
        });
    }
}
