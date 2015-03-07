import java.awt.Point;
import java.util.LinkedList;

import Intersection.RoadDirection;

public class Road {
    public static final Road[][] ROADS = new Road[GridLock.GRID_WIDTH * 2 + 1][GridLock.GRID_HEIGHT * 2 + 1];

    private static int road_index1 = 0, road_index2 = 0;

    private final Intersection Int1;
    private final Intersection Int2;

    private final byte Int1_LL;
    private final byte straight_lanes;
    private final byte Int1_RL;

    private final byte Int2_LL;
    private final byte Int2_RL;

    private final Point location, index;

    public final LinkedList<Car> cars = new LinkedList<>();

    public enum Lane {
        LEFT_LANE, STRAIGHT_LANE, RIGHT_LANE
    }

    public Road(int Int1_LL, int Int1_SL, int Int1_RL, int Int2_LL, int Int2_RL) {
        this.Int1_LL = (byte) Int1_LL;
        this.straight_lanes = (byte) Int1_SL;
        this.Int1_RL = (byte) Int1_RL;

        this.Int2_LL = (byte) Int2_LL;
        this.Int2_RL = (byte) Int2_RL;

        index = new Point(road_index1, road_index2);

        // determine the intersections that this road is connected to
        // TODO: account for indices out of range
        if (isNS()) {
            if (index.y == 0)
                Int1 = null;
            else {
                Int1 = Intersection.INTERSECTIONS[index.x][index.y / 2 + 1];
                Int1.addRoad(this, RoadDirection.SOUTH);
            }
            if (index.y == GridLock.GRID_HEIGHT * 2)
                Int2 = null;
            else {
                Int2 = Intersection.INTERSECTIONS[index.x][index.y / 2];
                Int2.addRoad(this, RoadDirection.NORTH);
            }
        } else {
            if (index.x == 0)
                Int1 = null;
            else {
                Int1 = Intersection.INTERSECTIONS[index.x - 1][(index.y - 1) / 2];
                Int1.addRoad(this, RoadDirection.EAST);
            }
            if (index.x == GridLock.GRID_WIDTH)
                Int2 = null;
            else {
                Int2 = Intersection.INTERSECTIONS[index.x][(index.y - 1) / 2];
                Int2.addRoad(this, RoadDirection.WEST);
            }
        }

        // add the new Road to the array of Roads
        ROADS[road_index1][road_index2] = this;

        // increment road indices
        if (road_index2 == ROADS[0].length - 1 || isNS() && road_index2 == ROADS[0].length - 2) {
            road_index1++;
            road_index2 = 0;
        } else
            road_index2++;

        // calculate the location
        int x = 0, y = 0;
        if (isNS()) {
            int low_y, high_y;
            if (Int1 == null)
                low_y = 0;
            else {
                low_y = Int1.getLocation().y;
                x = Int1.getLocation().x;
            }
            if (Int2 == null)
                high_y = GridLock.content.getHeight();
            else {
                high_y = Int2.getLocation().y;
                x = Int2.getLocation().x;
            }
            y = (high_y + low_y) / 2;
        } else {
            int low_x, high_x;
            if (Int1 == null)
                low_x = 0;
            else {
                low_x = Int1.getLocation().x;
                y = Int1.getLocation().y;
            }
            if (Int2 == null)
                high_x = GridLock.content.getWidth();
            else {
                high_x = Int2.getLocation().x;
                y = Int2.getLocation().y;
            }
            x = (high_x + low_x) / 2;
        }
        location = new Point(x, y);
    }

    public boolean isNS() {
        return (index.y & 1) == 0;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void removeCar(Car car) {
        cars.remove(car);
    }

    public Lane getLaneType(byte lane, Intersection intersection) {
        // TODO
    }

    public Lane isRHLane(byte lane, boolean NS) {
        // TODO
    }
}
