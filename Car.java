import java.awt.*;

public class Car {

    private float acceleration;
    private Point location;
    private double velocity;

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
}
