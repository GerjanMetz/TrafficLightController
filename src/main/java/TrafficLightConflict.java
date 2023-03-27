import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TrafficLightConflict {
    private List<Double> possibilities;
    private List<Double> conflicts;

    public TrafficLightConflict(List<Double> possibilities, List<Double> conflicts) {
        this.possibilities = possibilities;
        this.conflicts = conflicts;
    }

    public List<Double> getPossibilities() {
        return this.possibilities;
    }

    public void setPossibilities(List<Double> possibilities) {
        this.possibilities = possibilities;
    }

    public List<Double> getConflicts() {
        return this.conflicts;
    }

    public void setConflicts(List<Double> conflicts) {
        this.conflicts = conflicts;
    }
}
