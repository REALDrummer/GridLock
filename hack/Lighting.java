package hack;

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

    private void lightingRun(){
        setAllNS();
    }

    private void setAllNS(){
        for (int i = 0; i < GridLock.GRID_WIDTH; i++) {
            for (int j = 0; j < GridLock.GRID_HEIGHT; j++) {
                Intersection.INTERSECTIONS[i][j].setFlow(Intersection.TrafficFlow.NORTH_SOUTH);
            }
        }
    }

    private void setALLEW(){
        for (int i = 0; i < GridLock.GRID_WIDTH; i++) {
            for (int j = 0; j < GridLock.GRID_HEIGHT; j++) {
                Intersection.INTERSECTIONS[i][j].setFlow(Intersection.TrafficFlow.EAST_WEST);
            }
        }
    }

}
