import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    static Boolean train = false;

    // Example args: 11000 "conflictMatrices/conflict-matrix-v0.6.xlsx"
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        File excelFile = new File(args[1]);

        ServerSocket serverSocket;
        Socket clientSocket;

        MyModel model = new MyModel();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);


        // Read Excel file
        try {
            FileInputStream file = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet setupSheet = workbook.getSheet("Setup");
            for (Row row : setupSheet) {
                model.putLight(new MyModelItem(row.getCell(0).getNumericCellValue(), row.getCell(1).getNumericCellValue()));
            }

            Sheet conflictMatrixSheet = workbook.getSheet("ConflictMatrix");
            MyModelItem currentItem = null;
            for (Row row : conflictMatrixSheet) {
                for (Cell cell : row) {
                    try {
                        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
                        switch (color.getARGBHex()) {
                            case "FFA5A5A5":
                                break;
                            case "FFC6EFCE":
                                currentItem.addPossibility(cell.getNumericCellValue());
                                break;
                            case "FFFFC7CE":
                                currentItem.addConflict(cell.getNumericCellValue());
                                break;
                            case "FFFFEB9C":
                                currentItem = model.getLight(cell.getNumericCellValue());
                                break;
                            default:
                                break;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
        List<MyModelItem> newGreenLights = new ArrayList<>();
        List<MyModelItem> possibilities = new ArrayList<>(model.getStatus().values());
        List<MyModelItem> conflicts = new ArrayList<>();

        MyModelItem warningLights = model.getLight(99.0);
        MyModelItem barriers = model.getLight(100.0);

        if (train) {
            possibilities = new ArrayList<>(model.getLights(model.getLight(152.0).getPossibilities()));
            conflicts = new ArrayList<>(model.getLights(model.getLight(152.0).getConflicts()));

            try {
                // If warning lights are off, set them to on
                if (warningLights.getStatus() == 2 && barriers.getStatus() == 2) {
                    warningLights.setStatus(0);
                } else if (warningLights.getStatus() == 0 && // If warning lights are on for X seconds and barriers are not closed, close the barriers
                        (Duration.between(warningLights.getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 5) &&
                        barriers.getStatus() == 2 && (
                            model.getLight(152.0).getWeight() > 0 ||
                            model.getLight(154.0).getWeight() > 0 ||
                            model.getLight(160.0).getWeight() > 0
                        )) {
                    barriers.setStatus(0);
                } else if (warningLights.getStatus() == 0 && barriers.getStatus() == 0) { // If barriers are closed and weight has been set to 0
                    if (model.getLight(152.0).getWeight() == 0 &&
                        model.getLight(154.0).getWeight() == 0 &&
                        model.getLight(160.0).getWeight() == 0) {
                        if ((Duration.between(model.getLight(152.0).getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 5) &&
                                (Duration.between(model.getLight(154.0).getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 5) &&
                                (Duration.between(model.getLight(160.0).getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 5)) {
                            model.getLight(152.0).setStatus(0);
                            model.getLight(154.0).setStatus(0);
                            model.getLight(160.0).setStatus(0);
                            barriers.setStatus(2);
                        }
                    } else {
                        if      (model.getLight(152.0).getWeight() > 0) model.getLight(152.0).setStatus(2);
                        else if (model.getLight(154.0).getWeight() > 0) model.getLight(154.0).setStatus(2);
                        else if (model.getLight(160.0).getWeight() > 0) model.getLight(160.0).setStatus(2);
                    }
                } else if ((Duration.between(barriers.getLastChangeToStatusDate(), // If barriers have been open for X seconds and warning lights are set to 0, set warning lights to 2
                        LocalDateTime.now()).getSeconds() > 6) &&
                        barriers.getStatus() == 2 &&
                        warningLights.getStatus() == 0) {
                    warningLights.setStatus(2);
                    train = false;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            warningLights.setStatus(2);
            barriers.setStatus(2);
        }

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
                // If train
                if (train ||
                        model.getLight(152.0).getWeight() > 0 ||
                        model.getLight(154.0).getWeight() > 0 ||
                        model.getLight(160.0).getWeight() > 0) {
                    train = true;

                    possibilities = new ArrayList<>(model.getLights(model.getLight(152.0).getPossibilities()));
                    conflicts = new ArrayList<>(model.getLights(model.getLight(152.0).getConflicts()));
                } else {
                    conflicts = new ArrayList<>(model.getLights(List.of(99.0, 100.0, 152.0, 154.0, 160.0)));
                }

                // Check if there are no cars
                if (hasNoWaitingCars(model)) {
                    for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
                        item.getValue().setStatus(0);
                    }
                    return;
                }

                // Get lane with the highest priority
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
            if (item.getKey() == 99.0 || item.getKey() == 100.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160) continue;
            if (item.getValue().getStatus() == lightStatus) return true;
        }
        return false;
    }

    public static boolean hasAllLightsSetToRed(MyModel model) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 100.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160) {
                continue;
            }
            if (item.getValue().getStatus() != 0) {
                return false;
            }
        }
        return true;
    }

    public static long getShortestChangeToStatusDateInSeconds(MyModel model, int lightStatus) {
        long shortestSeconds = Long.MAX_VALUE;
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 100.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160) {
                continue;
            }

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
            if (item.getKey() == 99.0 || item.getKey() == 100.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160) continue;
            if (item.getValue().getStatus() == 2) item.getValue().setStatus(1);
        }
    }

    public static void setOrangeLightsToRed(MyModel model) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 100.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160) continue;
            if (item.getValue().getStatus() == 1) item.getValue().setStatus(0);
        }
    }

    public static void setLightsToGreen(List<MyModelItem> list) {
        for (MyModelItem item : list) {
            item.setStatus(2);
        }
    }
}
