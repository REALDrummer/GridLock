package hack;

import java.awt.CardLayout;
import java.awt.Point;
import java.util.LinkedList;

import javax.lang.model.type.IntersectionType;
import javax.naming.spi.DirStateFactory.Result;

public class Intersection {
    public static final Intersection[][] INTERSECTIONS = new Intersection[GridLock.GRID_WIDTH][GridLock.GRID_HEIGHT];

    private static int intersection_index1 = 0, intersection_index2 = 0;

    private final LinkedList<Car> waiting = new LinkedList<Car>();
    private final IntersectionType type;
    private final Point location, index;  // NOTE: this is the location of the CENTER of the intersection
    private Road north_road = null, south_road = null, east_road = null, west_road = null;

    private TrafficFlow flow = TrafficFlow.NORTH_SOUTH;

    public Intersection(IntersectionType type) {
        this.type = type;

        index = new Point(intersection_index1, intersection_index2);
        location =
                new Point((intersection_index1 + 1) * GridLock.content.getWidth() / (GridLock.GRID_WIDTH + 1), (intersection_index2 + 1) * GridLock.content.getHeight()
                        / (GridLock.GRID_HEIGHT + 1));

        // add the new Intersection to the array of Intersections
        INTERSECTIONS[intersection_index1][intersection_index2] = this;

        // increment intersection indices
        if (intersection_index2 == GridLock.GRID_WIDTH - 1) {
            intersection_index1++;
            intersection_index2 = 0;
        } else
            intersection_index2++;
    }

    public enum IntersectionType {
        STOP_SIGN, STOP_LIGHT, STOPLIGHT_W_LEFT_ARROW/* , STOPLIGHT_W_LEFT_ARROW_REQUIRED */;
    }

    public enum TrafficFlow {
        NORTH_SOUTH, EAST_WEST, NORTH_SOUTH_LEFT, EAST_WEST_LEFT;
    }

    public enum RoadDirection {
        NORTH, SOUTH, EAST, WEST;
    }

    boolean addRoad(Road road, RoadDirection direction) {
        switch (direction) {
            case NORTH:
                if (north_road != null) {
                    north_road = road;
                    return true;
                } else
                    return false;
            case SOUTH:
                if (south_road != null) {
                    south_road = road;
                    return true;
                } else
                    return false;
            case EAST:
                if (east_road != null) {
                    east_road = road;
                    return true;
                } else
                    return false;
            case WEST:
                if (west_road != null) {
                    west_road = road;
                    return true;
                } else
                    return false;
            default:
                throw new RuntimeException("What the hell kind of RoadDirection is " + direction + "?");
        }
    }

    public LinkedList<Car> getWaitingCars() {
        return waiting;
    }

    public IntersectionType getType() {
        return type;
    }

    public Point getLocation() {
        return location;
    }

    public Road getRoad(RoadDirection direction) {
        switch (direction) {
            case NORTH:
                return north_road;
            case SOUTH:
                return south_road;
            case EAST:
                return east_road;
            case WEST:
                return west_road;
            default:
                throw new RuntimeException("What the hell kind of RoadDirection is " + direction + "?");
        }
    }

    public Road getNorthRoad() {
        return north_road;
    }

    public Road getSouthRoad() {
        return south_road;
    }

    public Road getEastRoad() {
        return east_road;
    }

    public Road getWestRoad() {
        return west_road;
    }

    public TrafficFlow getFlow(){ return flow;}

    public void setFlow(TrafficFlow flow){this.flow = flow;}
}
