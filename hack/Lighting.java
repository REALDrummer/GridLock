package hack;

import hack.Intersection.TrafficFlow;

import java.util.Random;

public class Lighting implements Runnable {

    private static final int CHANGE_TIME = 200;

    private Random rand = new Random();

    private int tick_counter = 0;

    Lighting() {

    }

    public void run() {
        tick_counter++;

        if (tick_counter == CHANGE_TIME) {
            baseCaseRandom();
            tick_counter = 0;
        }
    }

    private void baseCaseRandom() {
        for (int i = 0; i < GridLock.GRID_WIDTH; i++) {
            for (int j = 0; j < GridLock.GRID_HEIGHT; j++) {
                Intersection.INTERSECTIONS[i][j].setFlow(TrafficFlow.values()[rand.nextInt(TrafficFlow.values().length)]);
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
