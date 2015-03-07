package hack;

import java.awt.*;

public class Car {

    private float acceleration;
    private Point location;
    private double velocity;
    private boolean halfway;

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
}
