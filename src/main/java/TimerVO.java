import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Timer object to be serialized into JSON for the simulator.
 */
public class TimerVO {
    double id;
    int status;
    int remainingSeconds;


    @JsonGetter("id")
    public double getId() {
        return id;
    }

    @JsonSetter("id")
    public void setId(double id) {
        this.id = id;
    }

    @JsonGetter("status")
    public int getStatus() {
        return status;
    }

    @JsonSetter("status")
    public void setStatus(int status) {
        this.status = status;
    }

    @JsonGetter("remainingTime")
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    @JsonSetter("remainingTime")
    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
}
