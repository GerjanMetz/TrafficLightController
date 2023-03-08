import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.LocalDateTime;
import java.util.Date;

public class TrafficLightStatusVO {
    double id;
    int status;
    LocalDateTime lastChangeToStatusDate;

    public TrafficLightStatusVO() {
        this.status = 0;
        this.lastChangeToStatusDate = LocalDateTime.now();
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
        lastChangeToStatusDate = LocalDateTime.now();
    }

    @JsonIgnore
    public LocalDateTime getLastChangeToStatusDate() {
        return this.lastChangeToStatusDate;
    }
}

