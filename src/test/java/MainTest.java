import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

//import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    public void shouldSetAllLightsRed() {
        ArrayList<TrafficLightStatusVO> trafficLightStatus = new ArrayList<>();
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(2.1);
            setStatus(1);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(5.1);
            setStatus(1);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(8.1);
            setStatus(1);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(11.1);
            setStatus(1);
        }});

        WaitingCarsVO[] waitingCars = new WaitingCarsVO[4];
        waitingCars[0] = new WaitingCarsVO() {{setId(2.1); setWeight(0);}};
        waitingCars[1] = new WaitingCarsVO() {{setId(5.1); setWeight(0);}};
        waitingCars[2] = new WaitingCarsVO() {{setId(8.1); setWeight(0);}};
        waitingCars[3] = new WaitingCarsVO() {{setId(11.1); setWeight(0);}};

        Main.setNewLightStatus(trafficLightStatus, waitingCars, 0);

        Assert.assertEquals(trafficLightStatus.get(0).status, 0);
        Assert.assertEquals(trafficLightStatus.get(1).status, 0);
        Assert.assertEquals(trafficLightStatus.get(2).status, 0);
        Assert.assertEquals(trafficLightStatus.get(3).status, 0);
    }
}