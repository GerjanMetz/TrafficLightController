import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Object to be used to serialized into JSON for the simulator.
 */
public class TrafficLightStatusVO {
    double id;
    int status;

    public TrafficLightStatusVO() {
        this.status = 0;
    }

    @JsonGetter("id")
    public double getId() {
        return this.id;
    }

    @JsonSetter("id")
    public void setId(double value) {
        this.id = value;
    }

    @JsonGetter("status")
    public int getStatus() {
        return this.status;
    }

    @JsonSetter("status")
    public void setStatus(int value) {
        this.status = value;
    }
}
