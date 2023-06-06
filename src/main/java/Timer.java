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

    /**
     * Get the remaining seconds in the timer.
     * @return the remaining amount of seconds in the timer formatted as int.
     */
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    /**
     * Get the status of the timer.
     * @return the status of the timer formatted as int: 0 for not running, 1 for paused, 2 for running.
     */
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

        if (status == 2 || status == 1) {
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

    /**
     * Get the TrafficLightModel with the longest time on red from a specified list.
     * @param list the list to look through.
     * @return the TrafficLightModel that has the longest red time.
     */
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

    /**
     * Get the maximum remaining time for a red light to turn to green.
     * @param light the light to get the maximum remaining time to green.
     * @return the maximum remaining time for the specified light to turn to green. 0 if the light is not set to red.
     */
    private int getRemainingTime(TrafficLightModel light) {
        if (light.getStatus() == 0) {
            Duration difference = Duration.between(light.getLastChangeToStatusDate(), LocalDateTime.now());
            return (int) (120 - difference.getSeconds());
        } else {
            return 0;
        }
    }

    /**
     * Remove one from the remaining seconds.
     */
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
}

