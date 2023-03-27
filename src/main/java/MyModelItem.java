import java.time.LocalDateTime;
import java.util.List;

public class MyModelItem {
    private double id, weight, multiplier;
    private int status, turnsSinceGreen;
    private List<Double> possibilities, conflicts;
    LocalDateTime lastChangeToStatusDate;

    public MyModelItem(double id) {
        this.id = id;
        this.weight = 0;
        this.multiplier = 1.5;
        this.status = 0;
        this.lastChangeToStatusDate = LocalDateTime.now();
        this.turnsSinceGreen = 0;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        this.lastChangeToStatusDate = LocalDateTime.now();
        if (status == 2) {
            this.turnsSinceGreen = 0;
        }
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

    public int getTurnsSinceGreen() {
        return turnsSinceGreen;
    }

    public void setTurnsSinceGreen(int turnsSinceGreen) {
        this.turnsSinceGreen = turnsSinceGreen;
    }

    public LocalDateTime getLastChangeToStatusDate() {
        return this.lastChangeToStatusDate;
    }

    public double getPriority() {
        double result = weight;
        for (int i = 0; i < turnsSinceGreen; i++) {
            weight *= multiplier;
        }
        return result;
    }

    public void incrementTurn() {
        this.turnsSinceGreen++;
    }
}
