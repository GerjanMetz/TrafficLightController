import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

public class IntersectionVO {
    private List<TrafficLightStatusVO> status;
    private TimerVO timer;

    public IntersectionVO() {
        status = new ArrayList<>();
    }

    @JsonGetter("trafficlights")
    public List<TrafficLightStatusVO> getStatus() {
        return status;
    }

    @JsonSetter("trafficlights")
    public void setStatus(List<TrafficLightStatusVO> status) {
        this.status = status;
    }

    @JsonGetter("timer")
    public TimerVO getTimer() {
        return timer;
    }

    @JsonSetter("timer")
    public void setTimer(TimerVO timer) {
        this.timer = timer;
    }
}
