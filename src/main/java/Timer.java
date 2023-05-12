import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimerTask;

public class Timer extends TrafficLightModel {
    TrafficLightModel trafficLightModel;
    List<TrafficLightModel> linkedTrafficLightModels, interruptingTrafficLightModels;
    int status, remainingSeconds, weight = 0;
    java.util.Timer timer;

    CountdownTask task;

    public Timer(double id, double multiplier, TrafficLightModel trafficLightModel, List<TrafficLightModel> linkedTrafficLightModels, List<TrafficLightModel> interruptingTrafficLightModels) {
        super(id, multiplier);
        this.trafficLightModel = trafficLightModel;
        this.linkedTrafficLightModels = linkedTrafficLightModels;
        this.interruptingTrafficLightModels = interruptingTrafficLightModels;
        this.remainingSeconds = 120;
        this.task = new CountdownTask();
        this.timer = new java.util.Timer();
    }

    public class CountdownTask extends TimerTask {
        private boolean isRunning = false;
        @Override
        public void run() {
            remainingSeconds--;
            this.isRunning = true;
        }

        public boolean isRunning() {
            return this.isRunning;
        }
    }


    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public int getStatus() {
        for (TrafficLightModel light : linkedTrafficLightModels) {
            if (light.getStatus() == 1 || light.getStatus() == 2) {
                status = 0;
                remainingSeconds = 120;
                task.isRunning = false;
                timer.cancel();
                timer.purge();
            } else {
                status = 2;
                if (!task.isRunning()) {
                    task = new CountdownTask();
                    timer = new java.util.Timer();
                    timer.scheduleAtFixedRate(task, 0, 1000);
                    task.isRunning = true;
                }
            }
        }

        if (status ==  2 || status == 1) {
            for (TrafficLightModel light : interruptingTrafficLightModels) {
                if (light.getStatus() == 1 || light.getStatus() == 2) {
                    status = 1;
                    if (task.isRunning()) {
                        task.isRunning = false;
                        timer.cancel();
                        timer.purge();
                    }
                }
            }
        }
        return status;
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

