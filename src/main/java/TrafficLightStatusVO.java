import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class TrafficLightStatusVO {
    double id;
    int status;

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

