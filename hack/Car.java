package hack;

import hack.GridLock.Paintable;
import hack.Intersection.RoadDirection;
import hack.Road.LaneType;

import java.awt.*;
import java.util.ArrayList;

public class Car implements Paintable {
    public static final ArrayList<Car> CARS = new ArrayList<Car>();
    public static final int CAR_BUFFER = 1;
    public static int CAR_SIZE = 0;

    // private float acceleration;
    private Point location;
    private double velocity;
    private boolean stopped = false;
    private boolean halfway;
    private Road road;
    private Intersection target_intersection;
    private byte lane;

    private Car(Road road, byte lane, Point location, boolean NW) {
        this.road = road;
        this.lane = lane;
        this.location = location;

        // TODO TEMP
        System.out.println(location + "::");
        for (Car car : CARS)
            System.out.println(car.getLocation());

        // initialize halfway to false since we're starting from the beginning of the road
        halfway = false;

        // figure out the target intersection
        target_intersection = road.getIntersection(!NW /* this NW represents the EDGE ends of the roads */);

        // intialize the velocity to move the car toward the target intersection
        velocity = NW ? road.getSpeedLimit() : -road.getSpeedLimit();

        CARS.add(this);
    }

    public static final Car spawn() {
        // pick an edge road to spawn a car at
        boolean collides_with_other_car = true, NW = true;
        Road road = null;
        byte lane = -1;
        Point location = null;
        for (int i = 0; i < GridLock.GRID_WIDTH * 2 + GridLock.GRID_HEIGHT * 2 - 4; i++) {
            RoadDirection edge = RoadDirection.values()[(int) (Math.random() * RoadDirection.values().length)];
            road =
                    Road.ROADS[edge == RoadDirection.WEST ? 0 : edge == RoadDirection.EAST ? GridLock.GRID_WIDTH : (int) (Math.random() * (GridLock.GRID_WIDTH - 1))][edge == RoadDirection.NORTH ? 0
                            : edge == RoadDirection.SOUTH ? GridLock.GRID_HEIGHT * 2 : (((int) (Math.random() * GridLock.GRID_HEIGHT)) * 2 + 1)];

            // pick a lane from the far end of that road
            NW = edge == RoadDirection.NORTH || edge == RoadDirection.WEST;
            collides_with_other_car = false;

            lane = (byte) (Math.random() * road.getLanes(NW));

            // ensure that the lane is not travelling in the wrong direction
            if (road.getLaneType(lane, NW) == LaneType.SE_STRAIGHT_LANE ^ NW)
                continue;

            // derive the location from the lane and road
            location =
                    new Point(edge == RoadDirection.WEST ? 0 : edge == RoadDirection.EAST ? GridLock.WINDOW_WIDTH : road.getLocation().x - road.getLanes(NW) * Road.LANE_WIDTH
                            / 2 + lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2, edge == RoadDirection.NORTH ? 0 : edge == RoadDirection.SOUTH ? GridLock.WINDOW_HEIGHT : road
                            .getLocation().y
                            - road.getLanes(NW) * Road.LANE_WIDTH / 2 + lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2);

            // determine whether or not this car collides with another car
            for (Car car : road.getCars())
                if (car.collidesWithCarAt(location)) {
                    collides_with_other_car = true;
                    break;
                }

            if (!collides_with_other_car)
                break;
        }

        if (collides_with_other_car || location == null)
            return null;
        else
            return new Car(road, lane, location, NW);
    }

    public boolean contains(Point point) {
        return point.x >= location.x - CAR_SIZE / 2 && point.x <= location.x + CAR_SIZE / 2 && point.y >= location.y - CAR_SIZE / 2 && point.y <= location.y + CAR_SIZE;
    }

    Point getLocation() {
        return location;
    }

    double getVelocityMPH() {
        return velocity;
    }

    public Intersection getTargetIntersection() {
        return target_intersection;
    }

    public void setTargetIntersection(Intersection target_intersection) {
        this.target_intersection = target_intersection;
    }

    public double getVelocityPPT() {
        int low = 0, high = road.isNS() ? GridLock.WINDOW_HEIGHT : GridLock.WINDOW_WIDTH;
        if (road != null)
            if (road.isNS()) {
                if (road.getNWIntersection() != null)
                    low = road.getNWIntersection().getLocation().y;
                else
                    low = 0;
                if (road.getSEIntersection() != null)
                    high = road.getSEIntersection().getLocation().y;
                else
                    high = GridLock.WINDOW_HEIGHT;
            } else {
                if (road.getNWIntersection() != null)
                    low = road.getNWIntersection().getLocation().x;
                else
                    low = 0;
                if (road.getSEIntersection() != null)
                    high = road.getSEIntersection().getLocation().x;
                else
                    high = GridLock.WINDOW_WIDTH;
            }

        return (high - low) * velocity / 3600 / (1000 / GridLock.ALGO_TICK_TIME);
    }

    boolean isHalfWay() {
        return halfway;
    }

    @Override
    public void paint(Graphics g) {
        // represent cars using yellow circles as wide as the lanes
        if (stopped)
            g.setColor(Color.RED);
        else
            g.setColor(Color.YELLOW);
        g.fillOval(location.x - CAR_SIZE / 2, location.y - CAR_SIZE / 2, CAR_SIZE, CAR_SIZE);
    }

    Road getRoad() {
        return road;
    }

    void setRoad(Road r) {
        road = r;
    }

    void setLocation(Point p) {
        location.setLocation(p.getX(), p.getY());
    }

    void setVelocity(double v) {
        this.velocity = v;
    }

    public void setLane(byte lane) {
        this.lane = lane;
    }

    public int getLane() {
        return lane;
    }

    private Point move() {
        // "unstop" the car
        stopped = false;
        target_intersection.getNorthWaitingCars().remove(this);

        Point p = new Point();
        if (getRoad().isNS())
            p.setLocation(getLocation().getX(), getLocation().getY() + getVelocityPPT());
        else
            p.setLocation(getLocation().getX() + getVelocityPPT(), getLocation().getY());

        // handle turns
        LaneType current_lane_type = road.getLaneType(lane, road.isNS() && location.y >= road.getLocation().y || !road.isNS() && location.x < road.getLocation().x);
        Road new_road;
        byte new_lane;
        if (current_lane_type == LaneType.LEFT_TURN_LANE) {
            if (road.isNS())
                if (velocity < 0 && road.getNWIntersection() != null) {
                    // coming from the south
                    road = road.getNWIntersection().getWestRoad();
                    // new_lane = (byte) () TODO
                } else {
                    // TODO
                }
        } else if (current_lane_type == LaneType.RIGHT_TURN_LANE) {
            // TODO
        }

        return p;
    }

    private boolean collidesWithCarAt(Point point) {
        return contains(new Point(point.x + CAR_SIZE / 2 - CAR_BUFFER, point.y)) || contains(new Point(point.x - CAR_SIZE / 2 + CAR_BUFFER, point.y))
                || contains(new Point(point.x, point.y + CAR_SIZE / 2 - CAR_BUFFER)) || contains(new Point(point.x, point.y - CAR_SIZE / 2 + CAR_BUFFER));
    }

    private void stop() {
        target_intersection.getNorthWaitingCars().add(this);  // the list of waiting cars is actually a SET; no need to check if it was already waiting!
        stopped = true;
    }

    public void tick() {
        Point new_point = move();
        if (new_point.equals(location))
            return;

        if (target_intersection.contains(new_point)
                && !target_intersection.hasLaneOpen(lane, getVelocityMPH() < 0 ? road.isNS() ? RoadDirection.SOUTH : RoadDirection.EAST : road.isNS() ? RoadDirection.NORTH
                        : RoadDirection.WEST)) {
            stop();
            return;
        } else
            for (Car car : CARS)
                if (car != this && car.collidesWithCarAt(new_point)) {
                    stop();
                    return;
                }

        location = new_point;
    }

    @Override
    public String toString() {
        return location.toString();
    }
}
