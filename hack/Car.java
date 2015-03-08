package hack;

import hack.GridLock.Paintable;
import hack.Intersection.RoadDirection;
import hack.Road.LaneType;

import java.awt.*;
import java.security.AlgorithmConstraints;
import java.util.ArrayList;

public class Car implements Paintable {
    public static final ArrayList<Car> CARS = new ArrayList<Car>();
    public static final int CAR_BUFFER = 2;
    public static int CAR_SIZE = 0;

    // private float acceleration;
    private Point location;
    private double velocity;
    private boolean stopped = false;
    private Road road;
    private Intersection target_intersection;
    private byte lane;

    private Car(Road road, byte lane, Point location, boolean NW) {
        this.road = road;
        this.lane = lane;
        this.location = location;

        // figure out the target intersection
        target_intersection = road.getIntersection(!NW /* this NW represents the EDGE ends of the roads */);

        // intialize the velocity to move the car toward the target intersection
        velocity = NW ? road.getSpeedLimit() : -road.getSpeedLimit();

        road.addCar(this);
        CARS.add(this);
    }

    public static final Car spawn() {
        // pick an edge road to spawn a car at
        boolean collides_with_other_car = true, NW = true;
        Road road = null;
        byte lane = -1;
        Point location = null;
        for (int i = 0; i < GridLock.GRID_WIDTH * 2 + GridLock.GRID_HEIGHT * 2 - 4; i++) {
            location = null;

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

    public RoadDirection getDirection() {
        if (road.isNS())
            if (velocity > 0)
                return RoadDirection.SOUTH;
            else
                return RoadDirection.NORTH;
        else if (velocity > 0)
            return RoadDirection.EAST;
        else
            return RoadDirection.WEST;
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

        Point new_location = new Point();
        if (getRoad().isNS())
            new_location.setLocation(getLocation().getX(), getLocation().getY() + getVelocityPPT());
        else
            new_location.setLocation(getLocation().getX() + getVelocityPPT(), getLocation().getY());

        // handle turns
        LaneType current_lane_type = road.getLaneType(lane, road.isNS() && location.y >= road.getLocation().y || !road.isNS() && location.x < road.getLocation().x);
        Road new_road = null;
        byte new_lane = -1;
        int distance_until_turn = 0;
        boolean NW_after_turn = false;
        if (current_lane_type == LaneType.LEFT_TURN_LANE) {
            // choose the destination road and the innermost lane on that road leading away from the target intersection
            if (road.isNS()) {
                if (velocity < 0 && road.getNWIntersection() != null) {
                    // coming from the south
                    NW_after_turn = true;
                    new_road = road.getNWIntersection().getWestRoad();
                    new_lane = (byte) (new_road.getStraightLanes() - 1);
                    distance_until_turn =
                            location.y - (new_road.getLocation().y - new_road.getSELanes() * Road.LANE_WIDTH / 2 + new_lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2);
                } else if (velocity > 0 && road.getSEIntersection() != null) {
                    // coming from the north
                    NW_after_turn = false;
                    new_road = road.getSEIntersection().getEastRoad();
                    new_lane = (byte) (new_road.getStraightLanes() + new_road.getNWLeftTurnLanes() + new_road.getNWRightTurnLanes());
                    distance_until_turn =
                            (new_road.getLocation().y - new_road.getSELanes() * Road.LANE_WIDTH / 2 + new_lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2) - location.y;
                }
            } else if (velocity < 0 && road.getNWIntersection() != null) {
                // coming from the east
                NW_after_turn = false;
                new_road = road.getNWIntersection().getSouthRoad();
                new_lane = (byte) (new_road.getStraightLanes() - 1);
                distance_until_turn = location.x - (new_road.getLocation().x - new_road.getNWLanes() * Road.LANE_WIDTH / 2 + new_lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2);
            } else if (velocity > 0 && road.getSEIntersection() != null) {
                // coming from the west
                NW_after_turn = true;
                new_road = road.getSEIntersection().getNorthRoad();
                new_lane = (byte) (new_road.getStraightLanes() + new_road.getSELeftTurnLanes() + new_road.getSERightTurnLanes());
                distance_until_turn = (new_road.getLocation().x - new_road.getNWLanes() * Road.LANE_WIDTH + new_lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2) - location.x;
            }
        } else if (current_lane_type == LaneType.RIGHT_TURN_LANE) {
            // choose the destination road and the innermost lane on that road leading away from the target intersection
            if (road.isNS()) {
                if (velocity < 0 && road.getNWIntersection() != null) {
                    // coming from the south
                    NW_after_turn = false;
                    new_road = road.getNWIntersection().getEastRoad();
                    new_lane = (byte) (new_road.getNWLanes() - 1);
                    distance_until_turn =
                            location.y - (new_road.getLocation().y - new_road.getNWLanes() * Road.LANE_WIDTH / 2 + new_lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2);
                } else if (velocity > 0 && road.getSEIntersection() != null) {
                    // coming from the north
                    NW_after_turn = true;
                    new_road = road.getSEIntersection().getWestRoad();
                    new_lane = (byte) 0;
                    distance_until_turn =
                            (new_road.getLocation().y - new_road.getSELanes() * Road.LANE_WIDTH / 2 + new_lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2) - location.y;
                }
            } else if (velocity < 0 && road.getNWIntersection() != null) {
                // coming from the east
                NW_after_turn = true;
                new_road = road.getNWIntersection().getNorthRoad();
                new_lane = (byte) (new_road.getNWLanes() - 1);
                distance_until_turn = location.x - (new_road.getLocation().x - new_road.getNWLanes() * Road.LANE_WIDTH / 2 + new_lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2);
            } else if (velocity > 0 && road.getSEIntersection() != null) {
                // coming from the west
                NW_after_turn = false;
                new_road = road.getSEIntersection().getSouthRoad();
                new_lane = (byte) 0;
                distance_until_turn = (new_road.getLocation().x - new_road.getSELanes() * Road.LANE_WIDTH / 2 + new_lane * Road.LANE_WIDTH + Road.LANE_WIDTH / 2) - location.x;
            }
        }

        if (new_road != null && distance_until_turn <= 0) {
            // make the turn!
            // if the car passed the turn lane, translate the extra distance into movement in the new lane
            int translation_distance = -distance_until_turn;
            if (road.isNS())
                if (velocity < 0) {
                    // moving north
                    new_location.y -= translation_distance;
                    new_location.x += current_lane_type == LaneType.LEFT_TURN_LANE ? -translation_distance : translation_distance;
                } else {
                    // moving south
                    new_location.y -= -translation_distance;
                    new_location.x += current_lane_type == LaneType.LEFT_TURN_LANE ? translation_distance : -translation_distance;
                }
            else if (velocity < 0) {
                // moving west
                new_location.x -= -translation_distance;
                new_location.y += current_lane_type == LaneType.LEFT_TURN_LANE ? translation_distance : -translation_distance;
            } else {
                // moving east
                new_location.x -= translation_distance;
                new_location.y += current_lane_type == LaneType.LEFT_TURN_LANE ? -translation_distance : translation_distance;
            }

            road.getCars().remove(this);
            new_road.getCars().add(this);
            road = new_road;
            lane = new_lane;
            velocity = NW_after_turn ? -road.getSpeedLimit() : road.getSpeedLimit();
        }

        return new_location;
    }

    private boolean collidesWithCarAt(Point point) {
        return contains(new Point(point.x + CAR_SIZE / 2 - CAR_BUFFER, point.y)) || contains(new Point(point.x - CAR_SIZE / 2 + CAR_BUFFER, point.y))
                || contains(new Point(point.x, point.y + CAR_SIZE / 2 - CAR_BUFFER)) || contains(new Point(point.x, point.y - CAR_SIZE / 2 + CAR_BUFFER));
    }

    private void delete() {
        road.getCars().remove(this);

        CARS.remove(this);
    }

    private void stop() {
        target_intersection.getNorthWaitingCars().add(this);  // the list of waiting cars is actually a SET; no need to check if it was already waiting!
        stopped = true;

        // add this car to the list of waiting cars for the target intersection
        getDirection().getOpposite().getWaitingCars(target_intersection).add(this);
    }

    public void tick() {
        Point new_location = move();
        Point new_nose_location =
                new Point(new_location.x + (getDirection() == RoadDirection.WEST ? -CAR_SIZE / 2 : getDirection() == RoadDirection.EAST ? CAR_SIZE / 2 : 0), new_location.y
                        + (getDirection() == RoadDirection.NORTH ? -CAR_SIZE / 2 : getDirection() == RoadDirection.SOUTH ? CAR_SIZE / 2 : 0));

        if (new_location.equals(location))
            return;

        if (!target_intersection.contains(location))
            if (target_intersection.contains(new_nose_location)
                    && !target_intersection.hasLaneOpen(lane, getVelocityMPH() < 0 ? road.isNS() ? RoadDirection.SOUTH : RoadDirection.EAST
                            : road.isNS() ? RoadDirection.NORTH : RoadDirection.WEST)) {
                stop();
                return;
            } else
                for (Car car : road.getCars())
                    if (car != this && lane == car.getLane() && !target_intersection.contains(car.getLocation()) && car.collidesWithCarAt(new_location)) {
                        stop();
                        return;
                    }

        if (location.x < 0 || location.y < 0 || location.x > GridLock.WINDOW_WIDTH || location.y > GridLock.WINDOW_HEIGHT)
            delete();
        else {
            location = new_location;
            // remove this car from the list of waiting cars for the target intersection
            getDirection().getOpposite().getWaitingCars(target_intersection).remove(this);
        }
    }

    @Override
    public String toString() {
        return location.toString();
    }
}
