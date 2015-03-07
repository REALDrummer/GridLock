package hack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import hack.GridLock.Paintable;
import hack.Intersection.RoadDirection;

public class Road implements Paintable {
    public static final Road[][] ROADS = new Road[GridLock.GRID_WIDTH + 1][GridLock.GRID_HEIGHT * 2 + 1];

    public static final int LANE_OFFSET = 2;
    public static int LANE_WIDTH;

    private static int road_index_x = 0, road_index_y = 0;

    private final Intersection NW_intersection;
    private final Intersection SE_intersection;

    private final byte straight_lanes;

    private final byte NW_left_turn_lanes;
    private final byte NW_right_turn_lanes;

    private final byte SE_left_turn_lanes;
    private final byte SE_right_turn_lanes;

    private final Point location, index;

    public final LinkedList<Car> cars = new LinkedList<>();

    public enum Lane {
        LEFT_LANE, STRAIGHT_LANE, RIGHT_LANE
    }

    public Road(int NW_left_turn_lanes, int straight_lanes, int NW_right_turn_lanes, int SE_left_turn_lanes, int SE_right_turn_lanes) {
        this.NW_left_turn_lanes = (byte) NW_left_turn_lanes;
        this.straight_lanes = (byte) straight_lanes;
        this.NW_right_turn_lanes = (byte) NW_right_turn_lanes;

        this.SE_left_turn_lanes = (byte) SE_left_turn_lanes;
        this.SE_right_turn_lanes = (byte) SE_right_turn_lanes;

        index = new Point(road_index_x, road_index_y);

        // determine the intersections that this road is connected to
        if (isNS()) {
            if (index.y == 0)
                NW_intersection = null;
            else {
                NW_intersection = Intersection.INTERSECTIONS[index.x][index.y / 2 - 1];
                NW_intersection.addRoad(this, RoadDirection.SOUTH);
            }
            if (index.y == GridLock.GRID_HEIGHT * 2)
                SE_intersection = null;
            else {
                SE_intersection = Intersection.INTERSECTIONS[index.x][index.y / 2];
                SE_intersection.addRoad(this, RoadDirection.NORTH);
            }
        } else {
            if (index.x == 0)
                NW_intersection = null;
            else {
                NW_intersection = Intersection.INTERSECTIONS[index.x - 1][(index.y - 1) / 2];
                NW_intersection.addRoad(this, RoadDirection.EAST);
            }
            if (index.x == GridLock.GRID_WIDTH)
                SE_intersection = null;
            else {
                SE_intersection = Intersection.INTERSECTIONS[index.x][(index.y - 1) / 2];
                SE_intersection.addRoad(this, RoadDirection.WEST);
            }
        }

        // add the new Road to the array of Roads
        ROADS[road_index_x][road_index_y] = this;

        // increment road indices
        if (road_index_x == ROADS.length - 1 || isNS() && road_index_x == ROADS.length - 2) {
            road_index_y++;
            road_index_x = 0;
        } else
            road_index_x++;

        // calculate the location
        int x = 0, y = 0;
        if (isNS()) {
            int low_y, high_y;
            if (NW_intersection == null)
                low_y = 0;
            else {
                low_y = NW_intersection.getLocation().y;
                x = NW_intersection.getLocation().x;
            }
            if (SE_intersection == null)
                high_y = GridLock.WINDOW_HEIGHT;
            else {
                high_y = SE_intersection.getLocation().y;
                x = SE_intersection.getLocation().x;
            }
            y = (high_y + low_y) / 2;
        } else {
            int low_x, high_x;
            if (NW_intersection == null)
                low_x = 0;
            else {
                low_x = NW_intersection.getLocation().x;
                y = NW_intersection.getLocation().y;
            }
            if (SE_intersection == null)
                high_x = GridLock.WINDOW_WIDTH;
            else {
                high_x = SE_intersection.getLocation().x;
                y = SE_intersection.getLocation().y;
            }
            x = (high_x + low_x) / 2;
        }
        location = new Point(x, y);
    }

    public boolean isNS() {
        return index.y % 2 == 0;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void removeCar(Car car) {
        cars.remove(car);
    }

    public Point getIndex() {
        return index;
    }

    public Point getLocation() {
        return location;
    }

    public byte getNWLanes() {
        return getLanes(true);
    }

    public byte getSELanes() {
        return getLanes(false);
    }

    public byte getLanes(boolean NW) {
        if (NW)
            return (byte) (NW_left_turn_lanes + NW_right_turn_lanes + straight_lanes * 2);
        else
            return (byte) (SE_left_turn_lanes + SE_right_turn_lanes + straight_lanes * 2);
    }

    public Lane getLaneType(byte lane, boolean NW) {
        if (NW) {
            if (isRHLane(lane, NW))
                return Lane.RIGHT_LANE;
            else if (isLHLane(lane, NW))
                return Lane.LEFT_LANE;
            else if (isSLane(lane, NW))
                return Lane.STRAIGHT_LANE;
            else
                throw new RuntimeException("This isn't any kind of lane!");
        } else {
            if (isRHLane(lane, NW))
                return Lane.RIGHT_LANE;
            else if (isLHLane(lane, NW))
                return Lane.LEFT_LANE;
            else if (isSLane(lane, NW))
                return Lane.STRAIGHT_LANE;
            else
                throw new RuntimeException("This isn't any kind of lane!");
        }
    }

    public boolean isRHLane(byte lane, boolean NW) {
        if (NW) {
            if (NW_right_turn_lanes == 1 && lane == getNWLanes()) {
                return true;
            } else if (NW_right_turn_lanes == 2 && lane == getNWLanes() || lane == getNWLanes() - 1) {
                return true;
            } else {
                return false;
            }
        } else if (!NW) {
            if (SE_right_turn_lanes == 1 && lane == 0) {
                return true;
            } else if (SE_right_turn_lanes == 2 && lane == 0 || lane == 1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isLHLane(byte lane, boolean NW) {
        if (NW) {
            if (NW_left_turn_lanes == 2 && lane == straight_lanes || lane == (straight_lanes + 1)) {
                return true;
            } else if (NW_left_turn_lanes == 1 && lane == straight_lanes) {
                return true;
            } else {
                return false;
            }
        } else if (!NW) {
            if (NW_left_turn_lanes == 2 && lane == straight_lanes || lane == (straight_lanes + 1)) {
                return true;
            } else if (NW_left_turn_lanes == 1 && lane == straight_lanes) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isSLane(byte lane, boolean NW) {
        if (NW) {
            if (lane < straight_lanes || lane == (straight_lanes + NW_left_turn_lanes) && lane < (2 * straight_lanes + NW_left_turn_lanes)) {
                return true;
            } else {
                return false;
            }
        } else if (!NW) {
            if (lane < straight_lanes || lane == (straight_lanes + NW_left_turn_lanes) && lane < (2 * straight_lanes + NW_left_turn_lanes)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void paint(Graphics g) {
        // roads are represented by black rectangles with lanes separated by white lines
        g.setColor(Color.WHITE);

        // divide the task in half for the two halves of the road, which may have different numbers of lanes

        // draw NS roads
        if (isNS()) {
            // first, draw the top half for the lanes on the north half of the road
            // draw the white background
            int low_y = NW_intersection == null ? 0 : NW_intersection.getLocation().y;
            g.fillRect(location.x - LANE_WIDTH * getNWLanes() / 2, low_y, LANE_WIDTH * getNWLanes() - LANE_OFFSET, low_y - location.y);

            // draw the lanes
            for (byte lane = 0; lane < getNWLanes(); lane++) {
                if (getLaneType(lane, true) == Lane.STRAIGHT_LANE)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(Color.DARK_GRAY);
                g.fillRect(location.x - LANE_WIDTH * getNWLanes() / 2 + LANE_WIDTH * lane, low_y, LANE_WIDTH - LANE_OFFSET, location.y - low_y);
            }

            // then, draw the bottom half for the lanes on the south half of the road
            // draw the white background
            g.setColor(Color.WHITE);
            int high_y = SE_intersection == null ? GridLock.WINDOW_HEIGHT : SE_intersection.getLocation().y;
            g.fillRect(location.x - LANE_WIDTH * getSELanes() / 2, location.y, LANE_WIDTH * getSELanes(), high_y - location.y);

            // draw the lanes
            for (byte lane = 0; lane < getSELanes(); lane++) {
                if (getLaneType(lane, false) == Lane.STRAIGHT_LANE)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(Color.DARK_GRAY);
                g.fillRect(location.x - LANE_WIDTH * getSELanes() / 2 + LANE_WIDTH * lane, location.y, LANE_WIDTH - LANE_OFFSET, high_y - location.y);
            }
        } // draw EW roads
        else {
            // first, draw the left half for the lanes on the west half of the road
            // draw the white background
            int low_x = NW_intersection == null ? 0 : NW_intersection.getLocation().x;
            g.fillRect(low_x, location.y - LANE_WIDTH * getNWLanes() / 2, location.x - low_x, LANE_WIDTH * getNWLanes() - LANE_OFFSET);

            // draw the lanes
            for (byte lane = 0; lane < getNWLanes(); lane++) {
                if (getLaneType(lane, true) == Lane.STRAIGHT_LANE)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(Color.DARK_GRAY);
                g.fillRect(low_x, location.y - LANE_WIDTH * getNWLanes() / 2 + LANE_WIDTH * lane, location.x - low_x, LANE_WIDTH - LANE_OFFSET);
            }

            // then, draw the bottom half for the lanes on the south half of the road
            // draw the white background
            g.setColor(Color.WHITE);
            int high_x = SE_intersection == null ? GridLock.WINDOW_HEIGHT : SE_intersection.getLocation().x;
            g.fillRect(location.x, location.y - LANE_WIDTH * getSELanes() / 2, high_x - location.x, LANE_WIDTH * getSELanes() - LANE_OFFSET);

            // draw the lanes
            for (byte lane = 0; lane < getSELanes(); lane++) {
                if (getLaneType(lane, false) == Lane.STRAIGHT_LANE)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(Color.DARK_GRAY);
                g.fillRect(location.x, location.y - LANE_WIDTH * getSELanes() / 2 + LANE_WIDTH * lane, high_x - location.x, LANE_WIDTH - LANE_OFFSET);
            }
        }
    }

    @Override
    public String toString() {
        return index.toString() + ": " + NW_intersection + " -> " + SE_intersection;
    }
}
