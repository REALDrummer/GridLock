package hack;

import hack.GridLock.Paintable;

import java.awt.*;
import java.util.ArrayList;

public class Car implements Paintable {
    public static final ArrayList<Car> CARS = new ArrayList<>();

    private float acceleration;
    private Point location;
    private double velocity;
    private boolean halfway;
    private Road road;

    Car(float acceleration, Point location, double velocity) {
        this.acceleration = acceleration;
        this.location = location;
        this.velocity = velocity;
    }

    float getAcceleration() {
        return acceleration;
    }

    Point getLocation() {
        return location;
    }

    double velocity() {
        return velocity;
    }

    boolean isHalfWay() {
        return halfway;
    }

    @Override
    public void paint(Graphics g) {
        // represent cars using yellow circles as wide as the lanes
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
}
