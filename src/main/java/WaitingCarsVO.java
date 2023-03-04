import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class WaitingCarsVO {
    double id;
    int weight;

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
}
