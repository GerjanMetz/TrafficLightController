import java.util.HashMap;
import java.util.List;

public class MainTest {

    public void shouldLoadVersionZeroDotThree() {
        // Conflict matrix for V0.3: Verkeer kan baan met rechtdoor & afslaan;
        HashMap<Double, TrafficLightConflict> conflictMatrix = new HashMap<Double, TrafficLightConflict>();
        conflictMatrix.put(1.1, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 10.1, 11.1, 12.1), List.of(5.1, 9.1)));
        conflictMatrix.put(2.1, new TrafficLightConflict(List.of(1.1, 7.1, 8.1), List.of(5.1, 6.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(5.1, new TrafficLightConflict(List.of(6.1, 7.1, 10.1, 11.1), List.of(1.1, 2.1, 8.1, 9.1, 12.1)));
        conflictMatrix.put(6.1, new TrafficLightConflict(List.of(1.1, 5.1, 7.1), List.of(2.1, 8.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(7.1, new TrafficLightConflict(List.of(1.1, 5.1, 6.1, 8.1, 9.1, 10.1, 12.1), List.of(2.1, 11.1)));
        conflictMatrix.put(8.1, new TrafficLightConflict(List.of(1.1, 2.1, 7.1, 9.1, 10.1), List.of(5.1, 6.1, 11.1, 12.1)));
        conflictMatrix.put(9.1, new TrafficLightConflict(List.of(7.1, 8.1, 10.1), List.of(1.1, 2.1, 5.1, 6.1, 11.1, 12.1)));
        conflictMatrix.put(10.1, new TrafficLightConflict(List.of(1.1, 5.1, 7.1, 8.1, 9.1, 11.1, 12.1), List.of(2.1, 6.1)));
        conflictMatrix.put(11.1, new TrafficLightConflict(List.of(1.1, 5.1, 10.1, 12.1), List.of(2.1, 6.1, 7.1, 8.1, 9.1)));
        conflictMatrix.put(12.1, new TrafficLightConflict(List.of(1.1, 7.1, 10.1, 11.1), List.of(2.1, 5.1, 6.1, 8.1, 9.1)));
    }

    public void shouldLoadVersionZeroDotSix() {
        // Conflict matrix for V0.5 & v0.6: Fietsers en voetgangers;
        HashMap<Double, TrafficLightConflict> conflictMatrix = new HashMap<Double, TrafficLightConflict>();
        conflictMatrix.put(1.1, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 10.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2), List.of(5.1, 9.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(2.1, new TrafficLightConflict(List.of(1.1, 7.1, 8.1, 88.1, 37.2, 28.1, 38.2), List.of(5.1, 6.1, 9.1, 10.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(5.1, new TrafficLightConflict(List.of(6.1, 7.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2), List.of(1.1, 2.1, 8.1, 9.1, 12.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(6.1, new TrafficLightConflict(List.of(1.1, 5.1, 7.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 8.1, 9.1, 10.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2)));
        conflictMatrix.put(7.1, new TrafficLightConflict(List.of(1.1, 2.1, 5.1, 6.1, 8.1, 9.1, 10.1, 12.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(11.1, 86.1, 35.1, 26.1, 36.2)));
        conflictMatrix.put(8.1, new TrafficLightConflict(List.of(1.1, 2.1, 7.1, 9.1, 10.1, 88.1, 37.2, 28.1, 38.2), List.of(5.1, 6.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(9.1, new TrafficLightConflict(List.of(7.1, 8.1, 10.1, 31.2, 22.0, 32.2), List.of(1.1, 2.1, 5.1, 6.1, 11.1, 12.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2)));
        conflictMatrix.put(10.1, new TrafficLightConflict(List.of(1.1, 5.1, 7.1, 8.1, 9.1, 11.1, 12.1, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2)));
        conflictMatrix.put(11.1, new TrafficLightConflict(List.of(1.1, 5.1, 10.1, 12.1, 86.1, 35.1, 26.1, 36.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 88.1, 37.2, 28.1, 38.2)));
        conflictMatrix.put(12.1, new TrafficLightConflict(List.of(1.1, 7.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2), List.of(2.1, 5.1, 6.1, 8.1, 9.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2)));
        conflictMatrix.put(86.1, new TrafficLightConflict(List.of(1.1, 5.1, 11.1, 12.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 10.1)));
        conflictMatrix.put(35.1, new TrafficLightConflict(List.of(1.1, 5.1, 11.1, 12.1, 86.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 10.1)));
        conflictMatrix.put(26.1, new TrafficLightConflict(List.of(1.1, 5.1, 11.1, 12.1, 86.1, 35.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 10.1)));
        conflictMatrix.put(36.1, new TrafficLightConflict(List.of(1.1, 5.1, 11.1, 12.1, 86.1, 35.1, 26.1, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(2.1, 6.1, 7.1, 8.1, 9.1, 10.1)));
        conflictMatrix.put(88.1, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 86.1, 35.1, 26.1, 36.2, 37.2, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(1.1, 5.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(37.2, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 86.1, 35.1, 26.1, 36.2, 88.1, 28.1, 38.2, 31.2, 22.0, 32.2), List.of(1.1, 5.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(28.1, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 38.2, 31.2, 22.0, 32.2), List.of(1.1, 5.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(38.2, new TrafficLightConflict(List.of(2.1, 6.1, 7.1, 8.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 31.2, 22.0, 32.2), List.of(1.1, 5.1, 9.1, 10.1, 11.1, 12.1)));
        conflictMatrix.put(32.1, new TrafficLightConflict(List.of(6.1, 7.1, 9.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 22.0, 32.2), List.of(1.1, 2.1, 5.1, 8.1, 12.1)));
        conflictMatrix.put(22.0, new TrafficLightConflict(List.of(6.1, 7.1, 9.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 32.2), List.of(1.1, 2.1, 5.1, 8.1, 12.1)));
        conflictMatrix.put(32.2, new TrafficLightConflict(List.of(6.1, 7.1, 9.1, 10.1, 11.1, 86.1, 35.1, 26.1, 36.2, 88.1, 37.2, 28.1, 38.2, 31.2, 22.0), List.of(1.1, 2.1, 5.1, 8.1, 12.1)));
    }

    public void testMain() {
    }
}