package hack;

import hack.Intersection.IntersectionType;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class GridLock extends Canvas implements ActionListener {
    private static final long serialVersionUID = 1099492150132430698L;

    public static GridLock GRIDLOCK;

    public static final float SIMULATION_SPEED_MULTIPLIER = 2;

    public static final int GRID_WIDTH = 3, GRID_HEIGHT = 3;
    public static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 800;
    public static final int PAINT_TIME = 650, ALGO_TICK_TIME = 500;
    public static final int MAX_CARS = 10;
    public static final double CARS_SPAWNED_PER_SECOND = 1, CARS_PER_SECOND_VARIANCE = 0.5;

    public static final Runnable algorithm = new Lighting();

    public static final AlgoRunner algo_runner = new AlgoRunner();
    public Timer paint_timer = new Timer(PAINT_TIME, this), run_timer = new Timer((int) (ALGO_TICK_TIME / SIMULATION_SPEED_MULTIPLIER), algo_runner);

    public static interface Paintable {
        void paint(Graphics g);
    }

    public static void main(String[] args) {
        // initialize basic stuff
        JFrame window = new JFrame("Gridlock");
        window.add(GRIDLOCK = new GridLock());

        // create the Intersections
        for (int i = 0; i < GRID_WIDTH * GRID_HEIGHT; i++)
            new Intersection(IntersectionType.STOP_LIGHT);

        // create the roads
        for (int i = 0; i < (GRID_WIDTH + 1) * (GRID_HEIGHT * 2 + 1) - (GRID_HEIGHT + 1); i++)
            new Road(1, 2, 0, 1, 0);

        // calculate the width of a lane based on the number of roads and the size of the screen
        /* the scaling below is based on a scaling model in which each road when assumed to be four lanes should have a width equal to 1/3 the length of road between two
         * intersectins; use this to calculate the lane width of both NS and EW roads, then use the smaller of the two values as the global lane width */
        int NS_lane_width = WINDOW_WIDTH / (GRID_WIDTH * 4 + 3) / 4, EW_lane_width = WINDOW_HEIGHT / (GRID_HEIGHT * 4 + 3) / 4;
        Road.LANE_WIDTH = NS_lane_width < EW_lane_width ? NS_lane_width : EW_lane_width;

        Car.CAR_SIZE = Road.LANE_WIDTH - Road.LANE_OFFSET - 2;

        // calculate the intersections' dimensions using the added roads
        for (Intersection[] intersections : Intersection.INTERSECTIONS)
            for (Intersection intersection : intersections)
                intersection.calcWidthAndHeight();

        GRIDLOCK.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.pack();
        window.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                //
            }

            @Override
            public void windowIconified(WindowEvent e) {
                //
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                //
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                //
            }

            @Override
            public void windowClosing(WindowEvent e) {
                //
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                //
            }
        });
        window.setVisible(true);

        GRIDLOCK.paint_timer.start();
        GRIDLOCK.run_timer.start();
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
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
