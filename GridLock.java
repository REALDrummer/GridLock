import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JApplet;

import Intersection.IntersectionType;

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

        // create the Intersection and Road objects to fill the grid
        for (int w = 0; w < GRID_WIDTH; w++)
            for (int h = 0; h < GRID_HEIGHT; h++)
                Intersection.INTERSECTIONS[w][h] =
                        new Intersection(IntersectionType.STOP_LIGHT, content.getWidth() / (GRID_WIDTH + 1), content.getHeight() / (GRID_HEIGHT + 1));

        setSize(800, 800);

        mouse_location = applet.getMousePosition();
        last_mouse_location = null;
        setContentPane(content);
    }

    @Override
    public void paint(Graphics g) {

    }
}
