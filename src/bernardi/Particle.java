package bernardi;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.concurrent.ThreadLocalRandom;

/**
 *  An animated circle that moves in a straight line at constant velocity within a
 *  rectangluar container whose sides are parallel to the coordinate axes and whose
 *  top-left corner is at the origin (0,0). The particle bounces off the sides of
 *  container in accordance with the law of Reflection.
 *
 * @author Brett Bernardi
 */

public class Particle extends Circle {

    // distance (in pixels) traveled per unit of time
    private static final int DISTANCE = 10;

    private final int containerWidth;
    private final int containerHeight;

    // The particles current direction of motion
    private int dx; // change in x-coordinate of particle's center
    private int dy; // change in y-coorindate of particle's center

    // updates position of discrete intervals
    private final Timeline timeline;

    /**
     *  Creates a particle object with the specified parameters.
     *
     * @param x - x coordinate of this particle's center
     * @param y - y coordinate of this particle's center
     * @param r - radius of this particle
     * @param c - color of this particle
     * @param w - width of bounding box
     * @param h - height of bounding box
     * @param t - milliseconds between position updates
     */
    public Particle(int x, int y, int r, Color c, int w, int h, int t) {
        super(x,y,r,c); // creates a circle with the specified parameters
        this.containerHeight = h;
        this.containerWidth = w;
        meander(); // randomize direction

        KeyFrame kf = new KeyFrame(Duration.millis(t), event -> move());
        timeline = new Timeline(kf);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     *  Randomizes the direction of this particle. The direction is stored in the two
     *  fields dx and dy, which are the change in x and y each movement.
     */
    public void meander() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        // The total distance traveled by the particle during each movement must be
        // exactly 10 pixels. This means that we can create a random dx from (1-10)
        // inclusive, and then, using the pythagorean theorem, calculate our dy. 10 is
        // the hypotenuse or distance. Thus, dy = sqrt(10^2 - dx^2)
        dx = rand.nextInt(1,DISTANCE + 1);
        dy = (int)(Math.sqrt((DISTANCE * DISTANCE) - (dx * dx)));

        // all of our calculations up to this point have been with positives only. This
        // will cause are particle to only travel up and to the right. Thus, we can
        // make it a 50% chance of both dx and dy being negative.
        if(rand.nextBoolean()) {
            dx = -dx;
        }
        if(rand.nextBoolean()) {
            dy = -dy;
        }

    }

    private void move() {
        // Because particle extends Circle, I have access to these public methods from
        // the circle class. They are inherited.
        double x = getCenterX();
        double y = getCenterY();
        double r = getRadius();

        // check is moving to next position will make you cross the boundary of the
        // container top bottom left right. If it does, then you must reverse dx or dy
        // to go the opposite direction.
        if(((x + dx) + r >= containerWidth ) || ((x + dx) - r < 0)){
            dx = -dx;
        }
        if(((y + dy) + r >= containerHeight) || ((y + dy) - r < 0)) {
            dy = -dy;
        }

        // calculate the next x and y coordinates of the move.
        double nextX = x + dx;
        double nextY = y + dy;

        // If the particles movement caused it to go outside the boundary of the
        // container, bring it back inside the container

        if(nextX < r) {
            nextX = r;
        }
        if(nextY < r) {
            nextY = r;
        }

        if(nextX + r > containerWidth) {
            nextX = containerWidth - r;
        }

        if(nextY + r > containerHeight) {
            nextY = containerHeight - r;
        }

        // Finally, set the coordinates.
        setCenterX(nextX);
        setCenterY(nextY);
    }

    /**
     *  Toggles the pause/play state of this particle's animation.
     */
    public void toggleRunState() {
        if(timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
        }
        else {
            timeline.play();
        }
    }



}
