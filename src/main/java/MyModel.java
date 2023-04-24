import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyModel {

    private HashMap<Double, MyModelItem> status;

    public MyModel() {
        status = new HashMap<>();
    }

    public MyModel(List<MyModelItem> list) {
        status = new HashMap<>();
        putLights(list);
    }

    public HashMap<Double, MyModelItem> getStatus() {
        return status;
    }

    public void putLight(MyModelItem newLight) {
        status.put(newLight.getId(), newLight);
    }

    public void putLights(List<MyModelItem> list) {
        list.forEach((listItem) -> putLight(listItem));
    }

    public MyModelItem getLight(double id) {
        return status.get(id);
    }

    public List<MyModelItem> getLights(List<Double> ids) {
        List<MyModelItem> results = new ArrayList<>();
        for (Double id : ids) {
            results.add(status.get(id));
        }
        return results;
    }

    public MyModelItem removeLight(double id) {
        return status.remove(id);
    }

    public void clear() {
        this.status.clear();
    }

    public List<TrafficLightStatusVO> getSimulatorJSON() {
        List<TrafficLightStatusVO> result = new ArrayList<>();
        for (Map.Entry<Double, MyModelItem> item : status.entrySet()) {
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
        for (Map.Entry<Double, MyModelItem> item : status.entrySet()) {
            item.getValue().incrementTurn();
        }
    }
}
