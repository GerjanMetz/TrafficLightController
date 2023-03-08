import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

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
        waitingCars[0] = new WaitingCarsVO() {{
            setId(2.1);
            setWeight(0);
        }};
        waitingCars[1] = new WaitingCarsVO() {{
            setId(5.1);
            setWeight(0);
        }};
        waitingCars[2] = new WaitingCarsVO() {{
            setId(8.1);
            setWeight(0);
        }};
        waitingCars[3] = new WaitingCarsVO() {{
            setId(11.1);
            setWeight(0);
        }};

        Main.setNewLightStatus(trafficLightStatus, waitingCars, 0);

        Assert.assertEquals(trafficLightStatus.get(0).status, 0);
        Assert.assertEquals(trafficLightStatus.get(1).status, 0);
        Assert.assertEquals(trafficLightStatus.get(2).status, 0);
        Assert.assertEquals(trafficLightStatus.get(3).status, 0);
    }

    @Test
    public void shouldPrioritizeLight21() {
        ArrayList<TrafficLightStatusVO> trafficLightStatus = new ArrayList<>();
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(2.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(5.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(8.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(11.1);
            setStatus(0);
        }});

        WaitingCarsVO[] waitingCars = new WaitingCarsVO[4];
        waitingCars[0] = new WaitingCarsVO() {{
            setId(2.1);
            setWeight(80);
        }};
        waitingCars[1] = new WaitingCarsVO() {{
            setId(5.1);
            setWeight(30);
        }};
        waitingCars[2] = new WaitingCarsVO() {{
            setId(8.1);
            setWeight(40);
        }};
        waitingCars[3] = new WaitingCarsVO() {{
            setId(11.1);
            setWeight(60);
        }};

        try {
            Main.setLightStatusAccordingTruthTable(trafficLightStatus, waitingCars[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(trafficLightStatus.get(0).status, 2);
        Assert.assertEquals(trafficLightStatus.get(1).status, 0);
        Assert.assertEquals(trafficLightStatus.get(2).status, 2);
        Assert.assertEquals(trafficLightStatus.get(3).status, 0);
    }

    @Test
    public void shouldPrioritizeLight51() {
        ArrayList<TrafficLightStatusVO> trafficLightStatus = new ArrayList<>();
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(2.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(5.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(8.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(11.1);
            setStatus(0);
        }});

        WaitingCarsVO[] waitingCars = new WaitingCarsVO[4];
        waitingCars[0] = new WaitingCarsVO() {{
            setId(2.1);
            setWeight(10);
        }};
        waitingCars[1] = new WaitingCarsVO() {{
            setId(5.1);
            setWeight(70);
        }};
        waitingCars[2] = new WaitingCarsVO() {{
            setId(8.1);
            setWeight(60);
        }};
        waitingCars[3] = new WaitingCarsVO() {{
            setId(11.1);
            setWeight(0);
        }};

        try {
            Main.setLightStatusAccordingTruthTable(trafficLightStatus, waitingCars[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(trafficLightStatus.get(0).status, 0);
        Assert.assertEquals(trafficLightStatus.get(1).status, 2);
        Assert.assertEquals(trafficLightStatus.get(2).status, 0);
        Assert.assertEquals(trafficLightStatus.get(3).status, 2);
    }

    @Test
    public void shouldPrioritizeLight81() {
        ArrayList<TrafficLightStatusVO> trafficLightStatus = new ArrayList<>();
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(2.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(5.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(8.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(11.1);
            setStatus(0);
        }});

        WaitingCarsVO[] waitingCars = new WaitingCarsVO[4];
        waitingCars[0] = new WaitingCarsVO() {{
            setId(2.1);
            setWeight(10);
        }};
        waitingCars[1] = new WaitingCarsVO() {{
            setId(5.1);
            setWeight(40);
        }};
        waitingCars[2] = new WaitingCarsVO() {{
            setId(8.1);
            setWeight(65);
        }};
        waitingCars[3] = new WaitingCarsVO() {{
            setId(11.1);
            setWeight(10);
        }};

        try {
            Main.setLightStatusAccordingTruthTable(trafficLightStatus, waitingCars[2]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(trafficLightStatus.get(0).status, 2);
        Assert.assertEquals(trafficLightStatus.get(1).status, 0);
        Assert.assertEquals(trafficLightStatus.get(2).status, 2);
        Assert.assertEquals(trafficLightStatus.get(3).status, 0);
    }

    @Test
    public void shouldPrioritizeLight111() {
        ArrayList<TrafficLightStatusVO> trafficLightStatus = new ArrayList<>();
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(2.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(5.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(8.1);
            setStatus(0);
        }});
        trafficLightStatus.add(new TrafficLightStatusVO() {{
            setId(11.1);
            setStatus(0);
        }});

        WaitingCarsVO[] waitingCars = new WaitingCarsVO[4];
        waitingCars[0] = new WaitingCarsVO() {{
            setId(2.1);
            setWeight(80);
        }};
        waitingCars[1] = new WaitingCarsVO() {{
            setId(5.1);
            setWeight(40);
        }};
        waitingCars[2] = new WaitingCarsVO() {{
            setId(8.1);
            setWeight(40);
        }};
        waitingCars[3] = new WaitingCarsVO() {{
            setId(11.1);
            setWeight(90);
        }};

        try {
            Main.setLightStatusAccordingTruthTable(trafficLightStatus, waitingCars[3]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(trafficLightStatus.get(0).status, 0);
        Assert.assertEquals(trafficLightStatus.get(1).status, 2);
        Assert.assertEquals(trafficLightStatus.get(2).status, 0);
        Assert.assertEquals(trafficLightStatus.get(3).status, 2);
    }
}