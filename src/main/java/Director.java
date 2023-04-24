import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Director {
    enum TrainLightStage {
        OPEN,
        CLOSING,
        OPENING,
        CLOSED
    }

    private static boolean train;
    private static TrainLightStage trainLightStage = TrainLightStage.OPEN;


    public static void Decide(MyModel model) {
        List<MyModelItem> newGreenLights = new ArrayList<>();
        List<MyModelItem> possibilities = new ArrayList<>(model.getStatus().values());
        List<MyModelItem> conflicts = new ArrayList<>();

        MyModelItem trainCrossing = model.getLight(99.0);

        if (train) {
            possibilities = new ArrayList<>(model.getLights(model.getLight(152.0).getPossibilities()));
            conflicts = new ArrayList<>(model.getLights(model.getLight(152.0).getConflicts()));

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
                            (model.getLight(152.0).getWeight() > 0 ||
                            model.getLight(154.0).getWeight() > 0 ||
                            model.getLight(160.0).getWeight() > 0)) {
                        if (model.getLight(152.0).getWeight() > 0) model.getLight(152.0).setStatus(2);
                        else if (model.getLight(154.0).getWeight() > 0) model.getLight(154.0).setStatus(2);
                        else if (model.getLight(160.0).getWeight() > 0) model.getLight(160.0).setStatus(2);
                    }

                    if ((Duration.between(trainCrossing.getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() > 20) &&
                            model.getLight(152.0).getWeight() == 0 &&
                            model.getLight(154.0).getWeight() == 0 &&
                            model.getLight(160.0).getWeight() == 0) {

                        trainCrossing.setStatus(1);

                        model.getLight(152.0).setStatus(0);
                        model.getLight(154.0).setStatus(0);
                        model.getLight(160.0).setStatus(0);

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
                    conflicts = new ArrayList<>(model.getLights(List.of(99.0, 152.0, 154.0, 160.0)));
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
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160)
                continue;
            if (item.getValue().getStatus() == lightStatus) return true;
        }
        return false;
    }

    public static boolean hasAllLightsSetToRed(MyModel model) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160) {
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
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160)
                continue;
            if (item.getValue().getStatus() == 2) item.getValue().setStatus(1);
        }
    }

    public static void setOrangeLightsToRed(MyModel model) {
        for (Map.Entry<Double, MyModelItem> item : model.getStatus().entrySet()) {
            if (item.getKey() == 99.0 || item.getKey() == 152.0 || item.getKey() == 154.0 || item.getKey() == 160)
                continue;
            if (item.getValue().getStatus() == 1) item.getValue().setStatus(0);
        }
    }

    public static void setLightsToGreen(List<MyModelItem> list) {
        for (MyModelItem item : list) {
            item.setStatus(2);
        }
    }
}
