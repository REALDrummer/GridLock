import java.awt.CardLayout;
import java.awt.Point;
import java.util.LinkedList;

import javax.lang.model.type.IntersectionType;
import javax.naming.spi.DirStateFactory.Result;

public class Intersection {
    public static final Intersection[][] INTERSECTIONS;
    
    private final LinkedList<Car> waiting = new LinkedList<>();
    private final IntersectionType type;
    private final Point location;  // NOTE: this is the location of the CENTER of the intersection
    private final Road north_road = null, south_road = null, east_road = null, west_road = null;

    public Intersection(IntersectionType type, int x, int y) {
        this.type = type;
        location = new Point(x, y);
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
                break;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Road getRoad(RoadDirection direction) {
        switch (direction) {
            case NORTH:
                return north_road;
                break;
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
}
