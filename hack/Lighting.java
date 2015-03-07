package hack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Lighting {

    private Random rand = new Random();
    private Intersection.TrafficFlow[] flow = Intersection.TrafficFlow.values();

    Lighting() {
        for (int i = 0; i < GridLock.GRID_WIDTH; i++) {
            for (int j = 0; j < GridLock.GRID_HEIGHT; j++) {
                Intersection.INTERSECTIONS[i][j].setFlow(flow[rand.nextInt(flow.length)]);
            }
        }
    }

    private void run(){

    }
}
