import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntersectionModel {

    private HashMap<Double, TrafficLightModel> status;

    public IntersectionModel() {
        status = new HashMap<>();
    }

    public IntersectionModel(List<TrafficLightModel> list) {
        status = new HashMap<>();
        putLights(list);
    }

    public HashMap<Double, TrafficLightModel> getStatus() {
        return status;
    }

    public void putLight(TrafficLightModel newLight) {
        status.put(newLight.getId(), newLight);
    }

    public void putLights(List<TrafficLightModel> list) {
        list.forEach((listItem) -> putLight(listItem));
    }

    public TrafficLightModel getLight(double id) {
        return status.get(id);
    }

    public List<TrafficLightModel> getLights(List<Double> ids) {
        List<TrafficLightModel> results = new ArrayList<>();
        for (Double id : ids) {
            results.add(status.get(id));
        }
        return results;
    }

    public TrafficLightModel removeLight(double id) {
        return status.remove(id);
    }

    public void clear() {
        this.status.clear();
    }

    public List<TrafficLightStatusVO> getSimulatorJSON() {
        List<TrafficLightStatusVO> result = new ArrayList<>();
        for (Map.Entry<Double, TrafficLightModel> item : status.entrySet()) {
            result.add(new TrafficLightStatusVO() {{
                setId(item.getValue().getId());
                setStatus(item.getValue().getStatus());
            }});
        }
        return result;
    }

    public void setSimulatorJSON(List<WaitingCarsVO> newStatus) {
        for (WaitingCarsVO item : newStatus) {
            status.get(item.getId()).setWeight(item.getWeight());
        }
    }

    public void incrementTurns() {
        for (Map.Entry<Double, TrafficLightModel> item : status.entrySet()) {
            item.getValue().incrementTurn();
        }
    }
}
