package hack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import hack.GridLock.Paintable;
import hack.Intersection.RoadDirection;
import hack.Intersection.TrafficFlow;

public class Road implements Paintable {
    public static final Road[][] ROADS = new Road[GridLock.GRID_WIDTH + 1][GridLock.GRID_HEIGHT * 2 + 1];

    private static final int SPEED_LIMIT = 45;

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

    private final LinkedList<Car> cars = new LinkedList<>();

    public enum LaneType {
        LEFT_TURN_LANE, RIGHT_TURN_LANE, NW_STRAIGHT_LANE, SE_STRAIGHT_LANE;
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

    public LinkedList<Car> getCars() {
        return cars;
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

    public int getSpeedLimit() {
        return SPEED_LIMIT;
    }

    public Intersection getIntersection(boolean NW) {
        return NW ? NW_intersection : SE_intersection;
    }

    public Intersection getNWIntersection() {
        return NW_intersection;
    }

    public Intersection getSEIntersection() {
        return SE_intersection;
    }

    public byte getLanes(boolean NW) {
        if (NW)
            return (byte) (NW_left_turn_lanes + NW_right_turn_lanes + straight_lanes * 2);
        else
            return (byte) (SE_left_turn_lanes + SE_right_turn_lanes + straight_lanes * 2);
    }

    public LaneType getLaneType(byte lane, boolean NW) {
        if (isRHLane(lane, NW))
            return LaneType.RIGHT_TURN_LANE;
        else if (isLHLane(lane, NW))
            return LaneType.LEFT_TURN_LANE;
        else if (isNWLane(lane, NW))
            return LaneType.NW_STRAIGHT_LANE;
        else if (isSELane(lane, NW))
            return LaneType.SE_STRAIGHT_LANE;
        else
            throw new RuntimeException("This isn't any kind of lane!");
    }

    public boolean hasLaneOpen(byte lane, boolean NW) {
        return // right turns can always go (if they're clear)
        getLaneType(lane, NW) == LaneType.RIGHT_TURN_LANE
                // straights can go if they have a green light
                || getLaneType(lane, NW) == LaneType.NW_STRAIGHT_LANE
                && (getNWIntersection() == null || isNS() && getNWIntersection().getFlow() == TrafficFlow.NORTH_SOUTH || !isNS()
                        && getNWIntersection().getFlow() == TrafficFlow.EAST_WEST)
                || getLaneType(lane, NW) == LaneType.SE_STRAIGHT_LANE
                && (getSEIntersection() == null || isNS() && getSEIntersection().getFlow() == TrafficFlow.NORTH_SOUTH || !isNS()
                        && getSEIntersection().getFlow() == TrafficFlow.EAST_WEST)
                // left turns can go on left turn states
                || getLaneType(lane, NW) == LaneType.LEFT_TURN_LANE
                && NW
                && (getNWIntersection() == null || isNS() && getNWIntersection().getFlow() == TrafficFlow.NORTH_SOUTH_LEFT || !isNS()
                        && getNWIntersection().getFlow() == TrafficFlow.EAST_WEST_LEFT)
                || getLaneType(lane, NW) == LaneType.LEFT_TURN_LANE
                && !NW
                && (getSEIntersection() == null || isNS() && getSEIntersection().getFlow() == TrafficFlow.NORTH_SOUTH_LEFT || !isNS()
                        && getSEIntersection().getFlow() == TrafficFlow.EAST_WEST_LEFT);
    }

    public boolean isLHLane(byte lane, boolean NW) {
        if (NW && isNS())
            return lane >= straight_lanes && lane < straight_lanes + NW_left_turn_lanes;
        else if (NW && !isNS())
            return lane >= straight_lanes + NW_right_turn_lanes && lane < straight_lanes + NW_right_turn_lanes + NW_left_turn_lanes;
        else if (!NW && isNS())
            return lane >= straight_lanes + SE_right_turn_lanes && lane < straight_lanes + SE_right_turn_lanes + SE_left_turn_lanes;
        else
            return lane >= straight_lanes && lane < straight_lanes + SE_left_turn_lanes;
    }

    public boolean isRHLane(byte lane, boolean NW) {
        if (NW && !isNS())
            return lane < NW_right_turn_lanes;
        else if (!NW && isNS())
            return lane < SE_right_turn_lanes;
        else if (NW && isNS())
            return lane >= straight_lanes * 2 + NW_left_turn_lanes;
        else
            return lane >= straight_lanes * 2 + SE_left_turn_lanes;
    }

    public boolean isNWLane(byte lane, boolean NW) {
        if (NW && isNS())
            return lane >= straight_lanes + NW_left_turn_lanes && lane < straight_lanes * 2 + NW_left_turn_lanes;
        else if (!NW && isNS())
            return lane >= SE_right_turn_lanes + straight_lanes + SE_left_turn_lanes;
        else if (NW && !isNS())
            return lane >= NW_right_turn_lanes && lane < NW_right_turn_lanes + straight_lanes;
        else
            return lane < straight_lanes;
    }

    public boolean isSELane(byte lane, boolean NW) {
        if (NW && isNS())
            return lane < straight_lanes;
        else if (NW && !isNS())
            return lane >= NW_right_turn_lanes + straight_lanes + NW_left_turn_lanes;
        else if (!NW && isNS())
            return lane >= SE_right_turn_lanes && lane < SE_right_turn_lanes + straight_lanes;
        else
            return lane >= SE_right_turn_lanes + straight_lanes + SE_left_turn_lanes;
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
            g.fillRect(location.x - LANE_WIDTH * getNWLanes() / 2, low_y, LANE_WIDTH * getNWLanes() - LANE_OFFSET, location.y - low_y);

            // draw the lanes
            for (byte lane = 0; lane < getNWLanes(); lane++) {
                if (getLaneType(lane, true) == LaneType.NW_STRAIGHT_LANE || getLaneType(lane, true) == LaneType.SE_STRAIGHT_LANE)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(Color.DARK_GRAY);
                g.fillRect(location.x - LANE_WIDTH * getNWLanes() / 2 + LANE_WIDTH * lane, low_y, LANE_WIDTH - LANE_OFFSET, location.y - low_y);
            }

            // then, draw the bottom half for the lanes on the south half of the road
            // draw the white background
            g.setColor(Color.WHITE);
            int high_y = SE_intersection == null ? GridLock.WINDOW_HEIGHT : SE_intersection.getLocation().y;
            g.fillRect(location.x - LANE_WIDTH * getSELanes() / 2, location.y, LANE_WIDTH * getSELanes() - LANE_OFFSET, high_y - location.y);

            // draw the lanes
            for (byte lane = 0; lane < getSELanes(); lane++) {
                if (getLaneType(lane, false) == LaneType.NW_STRAIGHT_LANE || getLaneType(lane, false) == LaneType.SE_STRAIGHT_LANE)
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
                if (getLaneType(lane, true) == LaneType.NW_STRAIGHT_LANE || getLaneType(lane, true) == LaneType.SE_STRAIGHT_LANE)
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
                if (getLaneType(lane, false) == LaneType.NW_STRAIGHT_LANE || getLaneType(lane, false) == LaneType.SE_STRAIGHT_LANE)
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
