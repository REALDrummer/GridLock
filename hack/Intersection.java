package hack;

import hack.GridLock.Paintable;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.annotation.Retention;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.lang.model.type.IntersectionType;
import javax.naming.spi.DirStateFactory.Result;

public class Intersection implements Paintable {
    public static final Intersection[][] INTERSECTIONS = new Intersection[GridLock.GRID_WIDTH][GridLock.GRID_HEIGHT];

    private static int intersection_index_x = 0, intersection_index_y = 0;

    private final HashSet<Car> north_waiting = new HashSet<>(), south_waiting = new HashSet<>(), west_waiting = new HashSet<>(), east_waiting = new HashSet<>();
    private final IntersectionType type;
    private final Point location, index;  // NOTE: this is the location of the CENTER of the intersection
    private Road north_road = null, south_road = null, east_road = null, west_road = null;
    private int width = 0, height = 0;  // initialize to 0 until it can be calculated based on connecting roads later on

    private TrafficFlow flow = TrafficFlow.NORTH_SOUTH;

    public Intersection(IntersectionType type) {
        this.type = type;

        index = new Point(intersection_index_x, intersection_index_y);
        location =
                new Point((intersection_index_x + 1) * GridLock.WINDOW_WIDTH / (GridLock.GRID_WIDTH + 1), (intersection_index_y + 1) * GridLock.WINDOW_HEIGHT
                        / (GridLock.GRID_HEIGHT + 1));

        // add the new Intersection to the array of Intersections
        INTERSECTIONS[intersection_index_x][intersection_index_y] = this;

        // increment intersection indices
        if (intersection_index_x == GridLock.GRID_WIDTH - 1) {
            intersection_index_y++;
            intersection_index_x = 0;
        } else
            intersection_index_x++;
    }

    public enum IntersectionType {
        STOP_SIGN, STOP_LIGHT, STOPLIGHT_W_LEFT_ARROW/* , STOPLIGHT_W_LEFT_ARROW_REQUIRED */;
    }

    public enum TrafficFlow {
        NORTH_SOUTH, EAST_WEST, NORTH_SOUTH_LEFT, EAST_WEST_LEFT;
    }

    public enum RoadDirection {
        NORTH, SOUTH, EAST, WEST;

        public RoadDirection getOpposite() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case SOUTH:
                    return NORTH;
                case EAST:
                    return WEST;
                case WEST:
                    return EAST;
                default:
                    throw new RuntimeException("What direction is this?!");
            }
        }

        public Road getRoad(Intersection intersection) {
            switch (this) {
                case NORTH:
                    return intersection.north_road;
                case SOUTH:
                    return intersection.south_road;
                case EAST:
                    return intersection.east_road;
                case WEST:
                    return intersection.west_road;
                default:
                    throw new RuntimeException("Cual tipo de camino es esto?!");
            }
        }

        public HashSet<Car> getWaitingCars(Intersection intersection) {
            switch (this) {
                case NORTH:
                    return intersection.getNorthWaitingCars();
                case SOUTH:
                    return intersection.getSouthWaitingCars();
                case WEST:
                    return intersection.getWestWaitingCars();
                case EAST:
                    return intersection.getEastWaitingCars();
                default:
                    throw new RuntimeException("What kind of direction is \"" + this + "?");
            }
        }
    }

    boolean addRoad(Road road, RoadDirection direction) {
        switch (direction) {
            case NORTH:
                if (north_road == null) {
                    north_road = road;
                    return true;
                } else
                    return false;
            case SOUTH:
                if (south_road == null) {
                    south_road = road;
                    return true;
                } else
                    return false;
            case EAST:
                if (east_road == null) {
                    east_road = road;
                    return true;
                } else
                    return false;
            case WEST:
                if (west_road == null) {
                    west_road = road;
                    return true;
                } else
                    return false;
            default:
                throw new RuntimeException("What the hell kind of RoadDirection is " + direction + "?");
        }
    }

    public boolean contains(Point point) {
        return point.x >= location.x - getWidth() / 2 && point.x <= location.x + getWidth() / 2 && point.y >= location.y - getHeight() / 2
                && point.y <= location.y + getHeight() / 2;
    }

    public HashSet<Car> getNorthWaitingCars() {
        return north_waiting;
    }

    public HashSet<Car> getSouthWaitingCars() {
        return south_waiting;
    }

    public HashSet<Car> getEastWaitingCars() {
        return east_waiting;
    }

    public HashSet<Car> getWestWaitingCars() {
        return west_waiting;
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

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    void calcWidthAndHeight() {
        // find the max values of the widths of the adjoining roads
        int N_width = north_road.getSELanes() * Road.LANE_WIDTH - Road.LANE_OFFSET, S_width = south_road.getNWLanes() * Road.LANE_WIDTH - Road.LANE_OFFSET, E_width =
                east_road.getNWLanes() * Road.LANE_WIDTH - Road.LANE_OFFSET, W_width = west_road.getSELanes() * Road.LANE_WIDTH - Road.LANE_OFFSET;
        width = N_width > S_width ? N_width : S_width;
        height = E_width > W_width ? E_width : W_width;
    }

    @Override
    public void paint(Graphics g) {
        // intersections are represented by dark gray squares
        g.setColor(Color.DARK_GRAY);

        // draw the intersection
        g.fillRect(location.x - width / 2, location.y - height / 2, width, height);

        // draw indicators for the flow of traffic
        g.setColor(Color.GREEN);
        final int ARC_WIDTH = getWidth() * 2 / 3, ARC_HEIGHT = getHeight() * 2 / 3;
        switch (flow) {
            case EAST_WEST:
                g.fillRect(location.x - getWidth() / 2, location.y - getHeight() / 3, getWidth(), getHeight() * 2 / 3);
                break;
            case NORTH_SOUTH:
                g.fillRect(location.x - getWidth() / 3, location.y - getHeight() / 2, getWidth() * 2 / 3, getHeight());
                break;
            case EAST_WEST_LEFT:
                g.fillArc(location.x + getWidth() / 2 - ARC_WIDTH, location.y + getHeight() / 2 - ARC_HEIGHT, 2 * ARC_WIDTH, 2 * ARC_HEIGHT, 90, 90);
                g.fillArc(location.x - getWidth() / 2 - ARC_WIDTH, location.y - getHeight() / 2 - ARC_HEIGHT, 2 * ARC_WIDTH, 2 * ARC_HEIGHT, 270, 90);
                break;
            case NORTH_SOUTH_LEFT:
                g.fillArc(location.x + getWidth() / 2 - ARC_WIDTH, location.y - getHeight() / 2 - ARC_HEIGHT, 2 * ARC_WIDTH, 2 * ARC_HEIGHT, 180, 90);
                g.fillArc(location.x - getWidth() / 2 - ARC_WIDTH, location.y + getHeight() / 2 - ARC_HEIGHT, 2 * ARC_WIDTH, 2 * ARC_HEIGHT, 0, 90);
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return index.toString() + ":\n" + "N -> " + north_road.getIndex() + "\nS -> " + south_road.getIndex() + "\nE -> " + east_road.getIndex() + "\nW -> "
                + west_road.getIndex();
    }

    public TrafficFlow getFlow() {
        return flow;
    }

    public void setFlow(TrafficFlow flow) {
        this.flow = flow;
    }

    public boolean hasLaneOpen(byte lane, RoadDirection direction) {
        return direction.getRoad(this).hasLaneOpen(lane, direction == RoadDirection.SOUTH || direction == RoadDirection.EAST);
    }
}
