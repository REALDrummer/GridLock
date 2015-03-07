package hack;

import hack.Intersection.IntersectionType;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GridLock extends JApplet implements ActionListener{
    private static final long serialVersionUID = 1099492150132430698L;

    public static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 800;
    public static final int GRID_WIDTH = 3, GRID_HEIGHT = 2;

    public static GridLock applet;
    public static Container content;
    public static Graphics2D graphics;

    public static Point mouse_location, last_mouse_location;

    public static Intersection currently_viewed_intersection = null;


    private Timer timer = new Timer(5,this);


    @Override
    public void init() {
        // initialize basic stuff
        applet = this;
        content = getContentPane();
        content.setFocusable(true);
        graphics = (Graphics2D) getGraphics();


        // create the Intersections
        for (int i = 0; i < GRID_WIDTH * GRID_HEIGHT; i++)
            new Intersection(IntersectionType.STOP_LIGHT);

        // create the roads
        for (int i = 0; i < (GRID_WIDTH + 1) * (GRID_HEIGHT * 2 + 1) - (GRID_HEIGHT + 1); i++)
            new Road(0, 1, 0, 0, 0);

        // calculate the width of a lane based on the number of roads and the size of the screen
        /* the scaling below is based on a scaling model in which each road when assumed to be four lanes should have a width equal to 1/3 the length of road between two
         * intersectins; use this to calculate the lane width of both NS and EW roads, then use the smaller of the two values as the global lane width */
        int NS_lane_width = WINDOW_WIDTH / (GRID_WIDTH * 4 + 3) / 4, EW_lane_width = WINDOW_HEIGHT / (GRID_HEIGHT * 4 + 3);
        Road.LANE_WIDTH = NS_lane_width < EW_lane_width ? NS_lane_width : EW_lane_width;

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        mouse_location = applet.getMousePosition();
        last_mouse_location = null;
        setContentPane(content);
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        // make the green background
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // paint the roads
        for (Road[] road_array : Road.ROADS)
            for (Road road : road_array)
                if (road != null)
                    road.paint(g);

        // paint the intersections
        for (int i = 0; i < Intersection.INTERSECTIONS.length; i++)
            for (int j = 0; j < Intersection.INTERSECTIONS[i].length; j++)
                Intersection.INTERSECTIONS[i][j].paint(g);

        // paint the cars
        for (Car car : Car.CARS)
            car.paint(g);
    }

    @Override
    /*
        Event Logic of the Program
    */
    public void actionPerformed(ActionEvent e) {
        for (Car i : Road.cars){
            Point p = new Point();
            if (i.getRoad().isNS()) {
                if(i.velocity()>0) {
                    p.setLocation(i.getLocation().getX(), i.getLocation().getY() + 1.0);
                } else{
                    p.setLocation(i.getLocation().getX(), i.getLocation().getY() - 1.0);
                }
            } else{
                if(i.velocity()>0) {
                    p.setLocation(i.getLocation().getX() - 1.0, i.getLocation().getY());
                } else{
                    p.setLocation(i.getLocation().getX() + 1.0, i.getLocation().getY());
                }
            }
            i.setLocation(p);
        }
        repaint();
    }
}
