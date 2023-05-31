import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Director {
    private static boolean train;
    private static TrainLightStage trainLightStage = TrainLightStage.OPEN;

    public static void Decide(IntersectionModel intersectionModel) {
        List<TrafficLightModel> newGreenLights = new ArrayList<>();
        List<TrafficLightModel> possibilities = new ArrayList<>(intersectionModel.getStatus().values());
        List<TrafficLightModel> conflicts = new ArrayList<>();

        TrafficLightModel trainCrossing = intersectionModel.getLight(99.0);

        if (train) {
            possibilities = new ArrayList<>(intersectionModel.getLights(intersectionModel.getLight(152.0).getPossibilities()));
            conflicts = new ArrayList<>(intersectionModel.getLights(intersectionModel.getLight(152.0).getConflicts()));

            switch (trainLightStage) {
                case OPEN -> {
                    trainCrossing.setStatus(1);
                    trainLightStage = TrainLightStage.CLOSING;
                }
                case CLOSING -> {
                    if ((Duration.between(trainCrossing.getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 5)) {
                        trainCrossing.setStatus(0);
                        trainLightStage = TrainLightStage.CLOSED;
                    }
                }
                case CLOSED -> {
                    if ((Duration.between(trainCrossing.getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 5) &&
                            (intersectionModel.getLight(152.0).getWeight() > 0 ||
                                    intersectionModel.getLight(154.0).getWeight() > 0 ||
                                    intersectionModel.getLight(160.0).getWeight() > 0)) {
                        if (intersectionModel.getLight(152.0).getWeight() > 0)
                            intersectionModel.getLight(152.0).setStatus(2);
                        else if (intersectionModel.getLight(154.0).getWeight() > 0)
                            intersectionModel.getLight(154.0).setStatus(2);
                        else if (intersectionModel.getLight(160.0).getWeight() > 0)
                            intersectionModel.getLight(160.0).setStatus(2);
                    }

                    if ((Duration.between(trainCrossing.getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 20) &&
                            intersectionModel.getLight(152.0).getWeight() == 0 &&
                            intersectionModel.getLight(154.0).getWeight() == 0 &&
                            intersectionModel.getLight(160.0).getWeight() == 0) {

                        trainCrossing.setStatus(1);

                        intersectionModel.getLight(152.0).setStatus(0);
                        intersectionModel.getLight(154.0).setStatus(0);
                        intersectionModel.getLight(160.0).setStatus(0);

                        trainLightStage = TrainLightStage.OPENING;
                    }
                }
                case OPENING -> {
                    if ((Duration.between(trainCrossing.getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 5)) {
                        trainCrossing.setStatus(2);
                        train = false;
                        trainLightStage = TrainLightStage.OPEN;
                    }
                }
            }
        } else {
            trainCrossing.setStatus(2);
        }

        // Check if most recent change to green lights is at least 10 seconds
        if (hasLightActive(intersectionModel, 2)) {
            if (getShortestChangeToStatusDateInSeconds(intersectionModel, 2) > 10) {
                setGreenLightsToOrange(intersectionModel);
                return;
            }
        }

        // Check if most recent change to orange lights is at least 3 seconds
        if (hasLightActive(intersectionModel, 1)) {
            if (getShortestChangeToStatusDateInSeconds(intersectionModel, 1) > 3) {
                setOrangeLightsToRed(intersectionModel);
                return;
            }
        }

        // Check if most recent change to red lights is at least 2 seconds
        if (hasAllLightsSetToRed(intersectionModel)) {
            if (getShortestChangeToStatusDateInSeconds(intersectionModel, 0) > 2) {
                // If train
                if (train ||
                        intersectionModel.getLight(152.0).getWeight() > 0 ||
                        intersectionModel.getLight(154.0).getWeight() > 0 ||
                        intersectionModel.getLight(160.0).getWeight() > 0) {
                    train = true;

                    possibilities = new ArrayList<>(intersectionModel.getLights(intersectionModel.getLight(152.0).getPossibilities()));
                    conflicts = new ArrayList<>(intersectionModel.getLights(intersectionModel.getLight(152.0).getConflicts()));
                } else {
                    conflicts = new ArrayList<>(intersectionModel.getLights(List.of(99.0, 152.0, 154.0, 160.0)));
                }

                // Check if there are no cars
                if (hasNoWaitingCars(intersectionModel)) {
                    for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
                        item.getValue().setStatus(0);
                    }
                    return;
                }

                // Get lane with the highest priority
                findNextPriority(intersectionModel, newGreenLights, possibilities, conflicts);

                intersectionModel.incrementTurns();

                // Set new lights to green
                setLightsToGreen(newGreenLights);
            }
        }
    }

    public static List<TrafficLightModel> findNextPriority(IntersectionModel intersectionModel, List<TrafficLightModel> results, List<TrafficLightModel> possibilities, List<TrafficLightModel> conflicts) {
        // Clean possibilities list from entries found in the conflicts list
        List<TrafficLightModel> possibilitiesCopy = new ArrayList<>(possibilities); // Hotfix to prevent concurrent exception
        for (TrafficLightModel item : possibilitiesCopy) {
            if (conflicts.contains(item)) {
                possibilities.remove(item);
            }
        }
        if (possibilities.size() == 0) return results;

        // Find next priority
        TrafficLightModel highestPriority = possibilities.get(0);
        for (TrafficLightModel item : possibilities) {
            if (!conflicts.contains(item) && item.getPriority() > highestPriority.getPriority()) highestPriority = item;
        }

        // Add next priority to results list, remove next priority from possibilities and add conflicts from next priority to conflicts list
        results.add(possibilities.remove(possibilities.indexOf(highestPriority))); // Convoluted way to pop by index from list
        conflicts.addAll(intersectionModel.getLights(highestPriority.getConflicts()));
        return findNextPriority(intersectionModel, results, possibilities, conflicts);
    }

    public static boolean hasNoWaitingCars(IntersectionModel intersectionModel) {
        for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
            if (item.getValue().getWeight() > 0) return false;
        }
        return true;
    }

    public static boolean hasLightActive(IntersectionModel intersectionModel, int lightStatus) {
        for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160)
                continue;
            if (item.getValue().getStatus() == lightStatus) return true;
        }
        return false;
    }

    public static boolean hasAllLightsSetToRed(IntersectionModel intersectionModel) {
        for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160) {
                continue;
            }
            if (item.getValue().getStatus() != 0) {
                return false;
            }
        }
        return true;
    }

    public static long getShortestChangeToStatusDateInSeconds(IntersectionModel intersectionModel, int lightStatus) {
        long shortestSeconds = Long.MAX_VALUE;
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160) {
                continue;
            }

            if (item.getValue().getStatus() == lightStatus && (Duration.between(item.getValue().getLastChangeToStatusDate(), now)).getSeconds() < shortestSeconds) {
                shortestSeconds = (Duration.between(item.getValue().getLastChangeToStatusDate(), now)).getSeconds();
            }
        }
        if (shortestSeconds == Long.MAX_VALUE) return -1;
        return shortestSeconds;
    }

    public static long getLongestChangeToStatusDateInSeconds(IntersectionModel intersectionModel, int lightStatus) {
        long shortestSeconds = Long.MIN_VALUE;
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
            if (item.getValue().getStatus() == lightStatus && (Duration.between(item.getValue().getLastChangeToStatusDate(), now)).getSeconds() > shortestSeconds) {
                shortestSeconds = (Duration.between(item.getValue().getLastChangeToStatusDate(), now)).getSeconds();
            }
        }
        if (shortestSeconds == Long.MAX_VALUE) return -1;
        return shortestSeconds;
    }

    public static TrafficLightModel findHighestWeight(IntersectionModel intersectionModel) {
        TrafficLightModel result = intersectionModel.getStatus().entrySet().iterator().next().getValue();
        for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
            if (item.getValue().getWeight() > result.getWeight()) {
                result = item.getValue();
            }
        }
        return result;
    }

    public static void setGreenLightsToOrange(IntersectionModel intersectionModel) {
        for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160)
                continue;
            if (item.getValue().getStatus() == 2) item.getValue().setStatus(1);
        }
    }

    public static void setOrangeLightsToRed(IntersectionModel intersectionModel) {
        for (Map.Entry<Double, TrafficLightModel> item : intersectionModel.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160)
                continue;
            if (item.getValue().getStatus() == 1) item.getValue().setStatus(0);
        }
    }

    public static void setLightsToGreen(List<TrafficLightModel> list) {
        for (TrafficLightModel item : list) {
            item.setStatus(2);
        }
    }

    enum TrainLightStage {
        OPEN,
        CLOSING,
        OPENING,
        CLOSED
    }
}
