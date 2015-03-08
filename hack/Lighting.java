package hack;

import hack.Intersection.TrafficFlow;

import java.util.Random;

public class Lighting {

    private Random rand = new Random();
    private TrafficFlow[] flow = TrafficFlow.values();

    Lighting() {
        baseCaseRandom();
    }

    private void lightingRun() {
        setAllNS();
    }

    private void baseCaseRandom() {
        for (int i = 0; i < GridLock.GRID_WIDTH; i++) {
            for (int j = 0; j < GridLock.GRID_HEIGHT; j++) {
                Intersection.INTERSECTIONS[i][j].setFlow(flow[rand.nextInt(flow.length)]);
            }
        }
    }

    private void setAllNS() {
        for (int i = 0; i < GridLock.GRID_WIDTH; i++) {
            for (int j = 0; j < GridLock.GRID_HEIGHT; j++) {
                Intersection.INTERSECTIONS[i][j].setFlow(TrafficFlow.NORTH_SOUTH);
            }
        }
    }

    private void setALLEW() {
        for (int i = 0; i < GridLock.GRID_WIDTH; i++) {
            for (int j = 0; j < GridLock.GRID_HEIGHT; j++) {
                Intersection.INTERSECTIONS[i][j].setFlow(TrafficFlow.EAST_WEST);
            }
        }
    }

}
