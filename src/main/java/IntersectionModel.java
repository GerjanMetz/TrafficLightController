import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntersectionModel {

    private HashMap<Double, TrafficLightModel> status;
    private Timer timer;

    public IntersectionModel() {
        status = new HashMap<>();
    }

    public IntersectionModel(List<TrafficLightModel> list) {
        status = new HashMap<>();
        putLights(list);
    }

    /**
     * Get the status object.
     * @return the status object.
     */
    public HashMap<Double, TrafficLightModel> getStatus() {
        return status;
    }

    /**
     * Put a light in the status HashMap
     * @param newLight the light to be put in the status HashMap. If a light with the same id already exists, it will be
     *                 replaced. If it doesn't exist yet, the light will simply be added.
     */
    public void putLight(TrafficLightModel newLight) {
        status.put(newLight.getId(), newLight);
    }

    /**
     * Put a list of lights in the status HashMap
     * @param list list of lights to put in the status HashMap. If a light with the same id already exists, it will be
     *             replaced. If it doesn't exist yet, the light will simply be added.
     */
    public void putLights(List<TrafficLightModel> list) {
        list.forEach((listItem) -> putLight(listItem));
    }

    /**
     * Set a Timer object.
     * @param timer the Timer object to set.
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * Get a TrafficLightModel by id formatted as double.
     * @param id the id to look for.
     * @return TrafficLightModel with the specified id or null if it wasn't found.
     */
    public TrafficLightModel getLight(double id) {
        return status.get(id);
    }

    /**
     * Get a list of TrafficLightModel with a list of id's formatted as double.
     * @param ids list of id's to look for.
     * @return list of TrafficLightModel with found id's.
     */
    public List<TrafficLightModel> getLights(List<Double> ids) {
        List<TrafficLightModel> results = new ArrayList<>();
        for (Double id : ids) {
            results.add(status.get(id));
        }
        return results;
    }

    /**
     * Remove a light from the status HashMap.
     * @param id the id to remove.
     * @return the TrafficLightModel that was removed from the HashMap.
     */
    public TrafficLightModel removeLight(double id) {
        return status.remove(id);
    }

    /**
     * Clear all entries from the status HashMap
     */
    public void clear() {
        this.status.clear();
    }

    /**
     * Get an object that can be serialized into JSON to send to the simulator.
     * @return IntersectionVO structured to be serialized into JSON.
     */
    public IntersectionVO getSimulatorJSON() {
        IntersectionVO result = new IntersectionVO();
        for (Map.Entry<Double, TrafficLightModel> item : status.entrySet()) {
            result.getStatus().add(new TrafficLightStatusVO() {{
                setId(item.getValue().getId());
                setStatus(item.getValue().getStatus());
            }});
        }
        result.setTimer(new TimerVO() {{
            setId(timer.getId());
            setStatus(timer.getStatus());
            setRemainingSeconds(timer.getRemainingSeconds());
        }});
        return result;
    }

    /**
     * Update the weights status HashMap.
     * @param newStatus the deserialized object to use to update the weights in the status HashMap.
     */
    public void setSimulatorJSON(List<WaitingCarsVO> newStatus) {
        for (WaitingCarsVO item : newStatus) {
            status.get(item.getId()).setWeight(item.getWeight());
        }
    }

    /**
     * Increment the turns of all lanes by one.
     */
    public void incrementTurns() {
        for (Map.Entry<Double, TrafficLightModel> item : status.entrySet()) {
            item.getValue().incrementTurn();
        }
    }
}
