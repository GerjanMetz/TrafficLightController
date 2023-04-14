import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class WaitingCarsVO {
    double id, priorityMultiplier;
    int weight;

    public WaitingCarsVO() {
        priorityMultiplier = 1.1;
    }

    @JsonGetter("id")
    public double getId() {
        return this.id;
    }

    @JsonSetter("id")
    public void setId(double value) {
        this.id = value;
    }

    @JsonGetter("weight")
    public int getWeight() {
        return this.weight;
    }

    @JsonSetter("weight")
    public void setWeight(int value) {
        this.weight = value;
    }

    @JsonIgnore
    public double getPriority() {
        return weight * priorityMultiplier;
    }
}
