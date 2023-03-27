import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);

        ServerSocket serverSocket;
        Socket clientSocket;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        // Conflict matrix for V0.3: Verkeer kan baan met rechtdoor & afslaan;
//        HashMap<Double, TrafficLightConflict> conflictMatrix = new HashMap<Double, TrafficLightConflict>();
//        conflictMatrix.put(1.1, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 10.1, 11.1, 12.1), List.of(5.1, 9.1)));
//        conflictMatrix.put(2.1, new TrafficLightConflict(List.of(1.1, 7.1, 8.1), List.of(5.1, 6.1, 9.1, 10.1, 11.1, 12.1)));
//        conflictMatrix.put(5.1, new TrafficLightConflict(List.of(6.1, 7.1, 10.1, 11.1), List.of(1.1, 2.1, 8.1, 9.1, 12.1)));
//        conflictMatrix.put(6.1, new TrafficLightConflict(List.of(1.1, 5.1, 7.1), List.of(2.1, 8.1, 9.1, 10.1, 11.1, 12.1)));
//        conflictMatrix.put(7.1, new TrafficLightConflict(List.of(1.1, 5.1, 6.1, 8.1, 9.1, 10.1, 12.1), List.of(2.1, 11.1)));
//        conflictMatrix.put(8.1, new TrafficLightConflict(List.of(1.1, 2.1, 7.1, 9.1, 10.1), List.of(5.1, 6.1, 11.1, 12.1)));
//        conflictMatrix.put(9.1, new TrafficLightConflict(List.of(7.1, 8.1, 10.1), List.of(1.1, 2.1, 5.1, 6.1, 11.1, 12.1)));
//        conflictMatrix.put(10.1, new TrafficLightConflict(List.of(1.1, 5.1, 7.1, 8.1, 9.1, 11.1, 12.1), List.of(2.1, 6.1)));
//        conflictMatrix.put(11.1, new TrafficLightConflict(List.of(1.1, 5.1, 10.1, 12.1), List.of(2.1, 6.1, 7.1, 8.1, 9.1)));
//        conflictMatrix.put(12.1, new TrafficLightConflict(List.of(1.1, 7.1, 10.1, 11.1), List.of(2.1, 5.1, 6.1, 8.1, 9.1)));

        // Conflict matrix for V0.5 & v0.6: Fietsers en voetgangers;
        HashMap<Double, TrafficLightConflict> conflictMatrix = new HashMap<Double, TrafficLightConflict>();
        conflictMatrix.put(1.1, new TrafficLightConflict( List.of(2.1, 6.1, 7.1, 8.1, 10.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2), List.of(5.1, 9.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(2.1, new TrafficLightConflict( List.of(1.1, 7.1, 8.1, 88.1, 37.2, 28.1, 38.2), List.of(5.1, 6.1, 9.1, 10.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(5.1, new TrafficLightConflict( List.of(6.1, 7.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2), List.of(1.1, 2.1, 8.1, 9.1, 12.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(6.1, new TrafficLightConflict( List.of(1.1, 5.1, 7.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 8.1, 9.1, 10.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2)));
        conflictMatrix.put(7.1, new TrafficLightConflict( List.of(1.1, 2.1, 5.1, 6.1, 8.1, 9.1, 10.1, 12.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(11.1, 86.1, 35.1, 26.1, 36.2)));
        conflictMatrix.put(8.1, new TrafficLightConflict( List.of(1.1, 2.1, 7.1, 9.1, 10.1, 88.1, 37.2, 28.1, 38.2), List.of(5.1, 6.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(9.1, new TrafficLightConflict( List.of(7.1, 8.1, 10.1, 31.2, 22.0, 32.2), List.of(1.1, 2.1, 5.1, 6.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2)));
        conflictMatrix.put(10.1, new TrafficLightConflict(List.of(1.1, 5.1, 7.1, 8.1, 9.1, 11.1, 12.1, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2)));
        conflictMatrix.put(11.1, new TrafficLightConflict(List.of(1.1, 5.1, 10.1, 12.1, 86.1, 35.1, 26.1, 36.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 88.1, 37.2, 28.1, 38.2)));
        conflictMatrix.put(12.1, new TrafficLightConflict(List.of(1.1, 7.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2), List.of(2.1, 5.1, 6.1, 8.1, 9.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(86.1, new TrafficLightConflict(List.of(1.1, 5.1, 11.1, 12.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 10.1)));
        conflictMatrix.put(35.1, new TrafficLightConflict(List.of(1.1, 5.1, 11.1, 12.1, 86.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 10.1)));
        conflictMatrix.put(26.1, new TrafficLightConflict(List.of(1.1, 5.1, 11.1, 12.1, 86.1, 35.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 10.1)));
        conflictMatrix.put(36.1, new TrafficLightConflict(List.of(1.1, 5.1, 11.1, 12.1, 86.1, 35.1, 26.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 10.1)));
        conflictMatrix.put(88.1, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 86.1, 35.1, 26.1, 36.2, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(1.1, 5.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(37.2, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 86.1, 35.1, 26.1, 36.2, 88.1, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(1.1, 5.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(28.1, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 38.2, 31.2, 22.0, 32.2), List.of(1.1, 5.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(38.2, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 31.2, 22.0, 32.2), List.of(1.1, 5.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(32.1, new TrafficLightConflict(List.of(6.1, 7.1, 9.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 22.0, 32.2), List.of(1.1, 2.1, 5.1, 8.1, 12.1)));
        conflictMatrix.put(22.0, new TrafficLightConflict(List.of(6.1, 7.1, 9.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 32.2), List.of(1.1, 2.1, 5.1, 8.1, 12.1)));
        conflictMatrix.put(32.2, new TrafficLightConflict(List.of(6.1, 7.1, 9.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0), List.of(1.1, 2.1, 5.1, 8.1, 12.1)));


        // Model setup for V0.1: Eenvoudig kruispunt. Verkeer kan alleen rechtdoor;
        MyModel model = new MyModel();
        model.putLight(new MyModelItem(2.1) {{
            setPossibilities(List.of(8.1));
            setConflicts(List.of(5.1, 11.1));
        }});
        model.putLight(new MyModelItem(5.1) {{
            setPossibilities(List.of(11.1));
            setConflicts(List.of(2.1, 8.1));
        }});
        model.putLight(new MyModelItem(8.1) {{
            setPossibilities(List.of(2.1));
            setConflicts(List.of(5.1, 11.1));
        }});
        model.putLight(new MyModelItem(11.1) {{
            setPossibilities(List.of(5.1));
            setConflicts(List.of(2.1, 8.1));
        }});


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
                setNewLightStatus(model);

                // Send new status
                objectMapper.writeValue(clientSocket.getOutputStream(), model.getSimulatorJSON());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setNewLightStatus(MyModel model) {
        // Check if most recent change to green lights is at least 10 seconds
        if (hasLightActive(model, 2)) {
            if (getShortestChangeToStatusDateInSeconds(model, 2) > 10) {
                setGreenLightsToOrange(model);
                return;
            }
        }

        // Check if most recent change to orange lights is at least 3 seconds
        if (hasLightActive(model, 1)) {
            if (getShortestChangeToStatusDateInSeconds(model, 1) > 3) {
                setOrangeLightsToRed(model);
                return;
            }
        }

        // Check if most recent change to red lights is at least 2 seconds
        if (hasAllLightsSetToRed(model)) {
            if (getShortestChangeToStatusDateInSeconds(model, 0) > 2) {
                // Check if there are no cars
                if (hasNoWaitingCars(model)) {
                    for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
                        item.getValue().setStatus(0);
                    }
                    return;
                }

                // Get lane with the highest priority
                List<MyModelItem> newGreenLights = new ArrayList<>();
                List<MyModelItem> possibilities = new ArrayList<>(model.getStatus().values());
                List<MyModelItem> conflicts = new ArrayList<>();
                findNextPriority(model, newGreenLights, possibilities, conflicts);

                model.incrementTurns();

                // Set new lights to green
                setLightsToGreen(newGreenLights);
            }
        }
    }

    public static List<MyModelItem> findNextPriority(MyModel model, List<MyModelItem> results, List<MyModelItem> possibilities, List<MyModelItem> conflicts) {
        // Clean possibilities list from entries found in the conflicts list
        List<MyModelItem> possibilitiesCopy = new ArrayList<>(possibilities); // Hotfix to prevent concurrent exception
        for (MyModelItem item : possibilitiesCopy) {
            if (conflicts.contains(item)) {
                possibilities.remove(item);
            }
        }
        if (possibilities.size() == 0) return results;

        // Find next priority
        MyModelItem highestPriority = possibilities.get(0);
        for (MyModelItem item : possibilities) {
            if (!conflicts.contains(item) && item.getPriority() > highestPriority.getPriority()) highestPriority = item;
        }

        // Add next priority to results list, remove next priority from possibilities and add conflicts from next priority to conflicts list
        results.add(possibilities.remove(possibilities.indexOf(highestPriority))); // Convoluted way to pop by index from list
        conflicts.addAll(model.getLights(highestPriority.getConflicts()));
        return findNextPriority(model, results, possibilities, conflicts);
    }

    public static boolean hasNoWaitingCars(MyModel model) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getValue().getWeight() > 0) return false;
        }
        return true;
    }

    public static boolean hasLightActive(MyModel model, int lightStatus) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getValue().getStatus() == lightStatus) return true;
        }
        return false;
    }

    public static boolean hasAllLightsSetToRed(MyModel model) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getValue().getStatus() != 0) return false;
        }
        return true;
    }

    public static long getShortestChangeToStatusDateInSeconds(MyModel model, int lightStatus) {
        long shortestSeconds = Long.MAX_VALUE;
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getValue().getStatus() == lightStatus && (Duration.between(item.getValue().getLastChangeToStatusDate(), now)).getSeconds() < shortestSeconds) {
                shortestSeconds = (Duration.between(item.getValue().getLastChangeToStatusDate(), now)).getSeconds();
            }
        }
        if (shortestSeconds == Long.MAX_VALUE) return -1;
        return shortestSeconds;
    }

    public static long getLongestChangeToStatusDateInSeconds(MyModel model, int lightStatus) {
        long shortestSeconds = Long.MIN_VALUE;
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getValue().getStatus() == lightStatus && (Duration.between(item.getValue().getLastChangeToStatusDate(), now)).getSeconds() > shortestSeconds) {
                shortestSeconds = (Duration.between(item.getValue().getLastChangeToStatusDate(), now)).getSeconds();
            }
        }
        if (shortestSeconds == Long.MAX_VALUE) return -1;
        return shortestSeconds;
    }

    public static MyModelItem findHighestWeight(MyModel model) {
        MyModelItem result = model.getStatus().entrySet().iterator().next().getValue();
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getValue().getWeight() > result.getWeight()) {
                result = item.getValue();
            }
        }
        return result;
    }

    public static void setGreenLightsToOrange(MyModel model) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getValue().getStatus() == 2) item.getValue().setStatus(1);
        }
    }

    public static void setOrangeLightsToRed(MyModel model) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getValue().getStatus() == 1) item.getValue().setStatus(0);
        }
    }

    public static void setLightsToGreen(List<MyModelItem> list) {
        for (MyModelItem item : list) {
            item.setStatus(2);
        }
    }
}
