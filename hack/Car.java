package hack;

import java.awt.*;

public class Car {

    private float acceleration;
    private Point location;
    private double velocity;
    private boolean halfway;
    private Road road;

    Car(float acceleration, Point location, double velocity){
        this.acceleration = acceleration;
        this.location = location;
        this.velocity = velocity;
    }

    float getAcceleration(){
        return acceleration;
    }

    Point getLocation(){
        return location;
    }

    double velocity(){
        return velocity;
    }

    boolean isHalfWay(){
        return halfway;
    }

    Road getRoad(){ return road;}

    void setRoad(Road r){
        road = r;
    }

    void setLocation(Point p){
        location.setLocation(p.getX(),p.getY());
    }

    void setVelocity(double v){
        this.velocity = v;
    }
}
