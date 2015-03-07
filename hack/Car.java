package hack;

import hack.GridLock.Paintable;
import hack.Intersection.RoadDirection;

import java.awt.*;
import java.util.ArrayList;

public class Car implements Paintable {
    public static final ArrayList<Car> CARS = new ArrayList<>();

    // private float acceleration;
    private Point location;
    private double velocity;
    private boolean halfway;
    private Road road;
    private Intersection target_intersection;
    private byte lane;

    public Car() {
        // initialize halfway to false since we're starting from the beginning of the road
        halfway = false;

        // pick an edge road to spawn a car at
        boolean collides_with_other_car, NW = true;
        for (int i = 0; i < GridLock.GRID_WIDTH * 2 + GridLock.GRID_HEIGHT * 2 - 4; i++) {
            RoadDirection edge = RoadDirection.values()[(int) (Math.random() * RoadDirection.values().length)];
            road =
                    Road.ROADS[edge == RoadDirection.WEST ? 0 : edge == RoadDirection.EAST ? GridLock.GRID_WIDTH : (int) (Math.random() * (GridLock.GRID_WIDTH - 1))][edge == RoadDirection.NORTH ? 0
                            : edge == RoadDirection.SOUTH ? GridLock.GRID_HEIGHT * 2 : (((int) (Math.random() * GridLock.GRID_HEIGHT)) * 2 + 1)];

            // pick a lane from the far end of that road
            NW = edge == RoadDirection.NORTH || edge == RoadDirection.WEST;
            collides_with_other_car = false;

            lane = (byte) (Math.random() * road.getLanes(NW));

            // derive the location from the lane and road
            location =
                    new Point(edge == RoadDirection.WEST ? 0 : edge == RoadDirection.EAST ? GridLock.WINDOW_WIDTH : road.getLocation().x - road.getLanes(NW) * Road.LANE_WIDTH
                            / 2 + lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2, edge == RoadDirection.NORTH ? 0 : edge == RoadDirection.SOUTH ? GridLock.WINDOW_HEIGHT : road
                            .getLocation().y
                            - road.getLanes(NW) * Road.LANE_WIDTH / 2 + lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2);

            // determine whether or not this car collides with another car
            for (Car car : CARS)
                if (car != this && car.collidesWithCarAt(location)) {
                    collides_with_other_car = true;
                    break;
                }

            if (!collides_with_other_car)
                break;
            // TODO TEMP
            else
                System.out.println("Collides!");
        }

        // figure out the target intersection
        target_intersection = road.getIntersection(!NW /* this NW represents the EDGE ends of the roads */);

        // intialize the velocity to move the car toward the target intersection
        velocity = NW ? -road.getSpeedLimit() : road.getSpeedLimit();

        CARS.add(this);
    }

    public boolean contains(Point point) {
        return point.x >= location.x - Road.LANE_WIDTH / 2 && point.x <= location.x + Road.LANE_WIDTH / 2 && point.y >= location.y - Road.LANE_WIDTH / 2
                && point.y <= location.y + Road.LANE_WIDTH;
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

        return (high - low) * road.getSpeedLimit() / 3600 / GridLock.TICKS_PER_SECOND;
    }

    boolean isHalfWay() {
        return halfway;
    }

    @Override
    public void paint(Graphics g) {
        // represent cars using yellow circles as wide as the lanes
        if (velocity == 0)
            g.setColor(Color.RED);
        else
            g.setColor(Color.YELLOW);
        g.fillOval(location.x - Road.LANE_WIDTH / 2, location.y - Road.LANE_WIDTH / 2, Road.LANE_WIDTH, Road.LANE_WIDTH);
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

    public void start() {
        if (velocity == 0) {
            if (target_intersection == road.getNWIntersection())
                velocity = -road.getSpeedLimit();
            else
                velocity = road.getSpeedLimit();
        }
    }

    private Point move() {
        if (velocity == 0) {
            // move it in the direction of the target intersection
            if (road.isNS() && target_intersection.getLocation().y < location.y || !road.isNS() && target_intersection.getLocation().x < location.x)
                velocity = -road.getSpeedLimit();
            else
                velocity = road.getSpeedLimit();
        }

        Point p = new Point();
        if (getRoad().isNS())
            p.setLocation(getLocation().getX(), getLocation().getY() + getVelocityPPT());
        else
            p.setLocation(getLocation().getX() + getVelocityPPT(), getLocation().getY());
        return p;
    }

    private boolean collidesWithCarAt(Point point) {
        return contains(new Point(point.x + Road.LANE_WIDTH / 2, point.y)) || contains(new Point(point.x - Road.LANE_WIDTH / 2, point.y))
                || contains(new Point(point.x, point.y + Road.LANE_WIDTH / 2)) || contains(new Point(point.x, point.y - Road.LANE_WIDTH / 2));
    }

    public void stop() {
        if (velocity != 0) {
            velocity = 0;
            target_intersection.getWaitingCars().add(this);
        }
    }

    public void tick() {
        Point new_point = move();
        if (new_point.equals(location))
            return;

        if (target_intersection.contains(new_point)
                && target_intersection.hasLaneOpen(lane, getVelocityMPH() < 0 ? road.isNS() ? RoadDirection.NORTH : RoadDirection.WEST : road.isNS() ? RoadDirection.SOUTH
                        : RoadDirection.EAST)) {
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
}
