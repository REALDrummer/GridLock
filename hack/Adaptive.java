package hack;

import java.util.LinkedList;

import javax.swing.Timer;

public class Adaptive {
    public byte ncars;
    public byte scars;
    public byte ecars;
    public byte wcars;
    public byte ratio;

    public static final int STRAIGHT_DELAY = 10000;
    public static final int LEFT_DELAY = 6000;
    public int next_delay = 0;

    private Timer timer;

    public int runLighting(Intersection this_intersection) {
        // returns a value to set the traffic signal cycle delay to and
        // changes flow of intersection
        switch (this_intersection.getFlow()) {
            case NORTH_SOUTH:
                next_delay = LEFT_DELAY * getNSratio(this_intersection);
                this_intersection.setFlow(Intersection.TrafficFlow.NORTH_SOUTH_LEFT);
                break;
            case NORTH_SOUTH_LEFT:
                next_delay = STRAIGHT_DELAY * getEWratio(this_intersection);
                this_intersection.setFlow(Intersection.TrafficFlow.EAST_WEST);
                break;
            case EAST_WEST:
                next_delay = LEFT_DELAY * getEWratio(this_intersection);
                this_intersection.setFlow(Intersection.TrafficFlow.EAST_WEST_LEFT);
                break;
            case EAST_WEST_LEFT:
                next_delay = STRAIGHT_DELAY * getNSratio(this_intersection);
                this_intersection.setFlow(Intersection.TrafficFlow.NORTH_SOUTH);
            default:
                System.out.println("Adaptive aint adapting");
                break;
        }
        return next_delay;
    }

    public void initializeRandomIntersections() {
        for (int i = 0; i < GridLock.GRID_WIDTH; i++) {
            for (int j = 0; j < GridLock.GRID_HEIGHT; j++) {
                // set intersections to random traffic flows
                Intersection.INTERSECTIONS[i][j].setFlow(randomFlow());
            }
        }
    }

    public byte getNScars(Intersection this_intersection) {
        // count total N-S cars through an intersection
        ncars = (byte) this_intersection.getRoad(Intersection.RoadDirection.NORTH).numCars();
        scars = (byte) this_intersection.getRoad(Intersection.RoadDirection.SOUTH).numCars();
        return (byte) (ncars + scars);
    }

    public byte getEWcars(Intersection this_intersection) {
        // count total E-W cars through an intersection
        ecars = (byte) this_intersection.getRoad(Intersection.RoadDirection.EAST).numCars();
        wcars = (byte) this_intersection.getRoad(Intersection.RoadDirection.WEST).numCars();
        return (byte) (ecars + wcars);
    }

    public byte getNSratio(Intersection this_intersection) {
        return (byte) ((byte) (getNScars(this_intersection) - getEWcars(this_intersection)) / 2);
    }

    public byte getEWratio(Intersection this_intersection) {
        return (byte) ((byte) (getEWcars(this_intersection) - getNScars(this_intersection)) / 2);
    }

    public Intersection.TrafficFlow randomFlow() {
        // pick a random traffic flow
        int rand = (int) Math.random() * 4;
        switch (rand) {
            case 0:
                return Intersection.TrafficFlow.NORTH_SOUTH;
            case 1:
                return Intersection.TrafficFlow.EAST_WEST;
            case 2:
                return Intersection.TrafficFlow.NORTH_SOUTH_LEFT;
            case 3:
                return Intersection.TrafficFlow.EAST_WEST_LEFT;
            default:
                System.out.println("Something went wrong in [randomIntersection]");
                return Intersection.TrafficFlow.NORTH_SOUTH;
        }
    }
}