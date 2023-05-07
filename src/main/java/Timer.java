import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Timer extends TrafficLightModel {
    TrafficLightModel trafficLightModel;
    List<TrafficLightModel> linkedTrafficLightModels, interruptingTrafficLightModels;

    public Timer(double id, double multiplier, TrafficLightModel trafficLightModel, List<TrafficLightModel> linkedTrafficLightModels, List<TrafficLightModel> interruptingTrafficLightModels) {
        super(id, multiplier);
        this.trafficLightModel = trafficLightModel;
        this.linkedTrafficLightModels = linkedTrafficLightModels;
        this.interruptingTrafficLightModels = interruptingTrafficLightModels;
    }

    public int getRemainingSeconds() {
        // Get light with the longest red time
        TrafficLightModel longestChange = getLongestRedTime(linkedTrafficLightModels);

        // Return remaining time if light is red
        return getRemainingTime(longestChange);
    }

    public int getStatus() {
        int result = 0;
        for (TrafficLightModel light : linkedTrafficLightModels) {
            if (light.getStatus() == 1 || light.getStatus() == 2) {
                result = 0;
            } else {
                result = 2;
            }
        }

        if (result ==  2 || result == 1) {
            for (TrafficLightModel light : interruptingTrafficLightModels) {
                if (light.getStatus() == 2) {
                    result = 1;
                }
            }
        }
        return result;
    }

    private TrafficLightModel getLongestRedTime(List<TrafficLightModel> list) {
        TrafficLightModel longestChange = list.get(0);
        for (TrafficLightModel light : list) {
            if (Duration.between(light.getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds() >
                    Duration.between(longestChange.getLastChangeToStatusDate(), LocalDateTime.now()).getSeconds()) {
                longestChange = light;
            }
        }
        return longestChange;
    }

    private int getRemainingTime(TrafficLightModel light) {
        if (light.getStatus() == 0) {
            Duration difference = Duration.between(light.getLastChangeToStatusDate(), LocalDateTime.now());
            return (int) (120 - difference.getSeconds());
        } else {
            return 0;
        }
    }
}
