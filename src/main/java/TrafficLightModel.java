import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrafficLightModel {
    LocalDateTime lastChangeToStatusDate;
    private double id, weight, multiplier;
    private int status, turnsSinceGreen;
    private List<Double> possibilities, conflicts;

    public TrafficLightModel(double id) {
        this.id = id;
        this.weight = 0;
        this.multiplier = 1.5;
        this.status = 0;
        this.lastChangeToStatusDate = LocalDateTime.now();
        this.turnsSinceGreen = 0;
        this.possibilities = new ArrayList<>();
        this.conflicts = new ArrayList<>();
    }

    public TrafficLightModel(double id, double multiplier) {
        this.id = id;
        this.weight = 0;
        this.multiplier = multiplier;
        this.status = 0;
        this.lastChangeToStatusDate = LocalDateTime.now();
        this.turnsSinceGreen = 0;
        this.possibilities = new ArrayList<>();
        this.conflicts = new ArrayList<>();
    }

    /**
     * Return the id.
     * @return id of the traffic light formatted as double.
     */
    public double getId() {
        return id;
    }

    /**
     * Set the id.
     * @param id the id to set.
     */
    public void setId(double id) {
        this.id = id;
    }

    /**
     * Get the weight of the traffic light.
     * @return the weight of the traffic light as double.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Set the weight of the traffic light.
     * @param weight the new weight to be set.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Get the multiplier of the traffic light.
     * @return the multiplier of the traffic light as double.
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Set the multiplier of the traffic light.
     * @param multiplier the new multiplier to be set.
     */
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Get the status of the traffic light.
     * @return the status of the traffic light as int: 0 for red, 1 for orange, 2 for green.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the status of the traffic light.
     * @param status the new status to be set: 0 for red, 1 for orange, 2 for green.
     */
    public void setStatus(int status) {
        this.status = status;
        this.lastChangeToStatusDate = LocalDateTime.now();
        if (status == 2) {
            this.turnsSinceGreen = 0;
        }
    }

    /**
     * Get the list of possibilities.
     * @return list of traffic lights that can also be set to green as double.
     */
    public List<Double> getPossibilities() {
        return this.possibilities;
    }

    /**
     * Set the list of possibilities.
     * @param possibilities the new list of possibilities.
     */
    public void setPossibilities(List<Double> possibilities) {
        this.possibilities = possibilities;
    }

    /**
     * Get the list of conflicts.
     * @return list of conflicting traffic lights when this is set to green.
     */
    public List<Double> getConflicts() {
        return this.conflicts;
    }

    /**
     * Set the list of conflicting traffic lights.
     * @param conflicts the new list of conflicting traffic lights.
     */
    public void setConflicts(List<Double> conflicts) {
        this.conflicts = conflicts;
    }

    /**
     * Get the amount of turns since this light has been set to green.
     * @return the amount of turns since this light has been set to green as int.
     */
    public int getTurnsSinceGreen() {
        return turnsSinceGreen;
    }

    /**
     * Set the amount to turns since this light has been set to green.
     * @param turnsSinceGreen the new amount of turns since this light has been set to green.
     */
    public void setTurnsSinceGreen(int turnsSinceGreen) {
        this.turnsSinceGreen = turnsSinceGreen;
    }

    /**
     * Get the last change to the status object.
     * @return a LocalDateTime object containing the last time the status has been changed.
     */
    public LocalDateTime getLastChangeToStatusDate() {
        return this.lastChangeToStatusDate;
    }

    /**
     * Get the priority of this traffic light based on the amount of turns it has been set to green multiplied by the
     * multiplier.
     * @return the priority as calculated by the amount of turns since this light has been set to green multiplied by
     * the multiplier.
     */
    public double getPriority() {
        double result = weight;
        for (int i = 0; i < turnsSinceGreen; i++) {
            result *= multiplier;
        }
        return result;
    }

    /**
     * Add a possibility.
     * @param id the id to add as a possibility.
     */
    public void addPossibility(double id) {
        this.possibilities.add(id);
    }

    /**
     * Add a conflicting traffic light.
     * @param id the id to add as a conflicting light.
     */
    public void addConflict(double id) {
        this.conflicts.add(id);
    }

    /**
     * Increment the amount of turns this light had been set to green.
     */
    public void incrementTurn() {
        this.turnsSinceGreen++;
    }
}
