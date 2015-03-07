package hack;

import hack.Intersection.IntersectionType;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JApplet;

public class GridLock extends JApplet {
    private static final long serialVersionUID = 1099492150132430698L;

    public static int GRID_WIDTH = 3, GRID_HEIGHT = 2;

    public static GridLock applet;
    public static Container content;
    public static Graphics2D graphics;
    public static Point mouse_location, last_mouse_location;

    public static Intersection currently_viewed_intersection = null;

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
        for (int i = 0; i < (GRID_WIDTH * 2 + 1) * (GRID_HEIGHT * 2 + 1); i++)
            new Road(1, 0, 0, 0, 0);

        setSize(800, 800);

        mouse_location = applet.getMousePosition();
        last_mouse_location = null;
        setContentPane(content);
    }

    @Override
    public void paint(Graphics g) {
        // TODO
    }
}
