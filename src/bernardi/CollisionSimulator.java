package bernardi;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.ThreadLocalRandom;

/**
 *  Simulates the random motion of idealized particles colliding within a rectangular
 *  container. The user can modify the simulation using mouse buttons as follows:
 *
 *  Left button: decrease radius of particles.
 *  Right button: increase radius of particles.
 *  Middle button: toggles run/pause state of animation
 *
 * @author Brett Bernardi
 */
public class CollisionSimulator extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        final int width = 1200;
        final int height = 850;
        root.setPrefSize(width,height);
        Scene scene = new Scene(root);

        String style = "-fx-background-color: ALICEBLUE; "
                + "-fx-border-style: solid; -fx-border-width: 4; "
                + "-fx-border-insets: 1; -fx-border-color: DARKSLATEGRAY; ";
        root.setStyle(style);

        final int numParticles = 1500;
        Particle particles[] = new Particle[numParticles];

        // Instantiate the particles and add them to the root node
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int delay = 50; // milliseconds
        for(int i = 0; i < particles.length; i ++) {

            // Calculates the initial position of particle at random, at least two
            // diameters away from the boundaries of the viewing area.
            final int radius = 8;
            int x = rand.nextInt(4 * radius, width - (4 * radius));
            int y = rand.nextInt(4 * radius, height - (4 * radius));

            Color c = randomDarkColor();

            particles[i] = new Particle(x,y,radius,c,width,height,delay);
            root.getChildren().add(particles[i]);
        }

        // Check for collisions among each pair of particles. When two particles
        // collide, each bounces away in a random direction.
        KeyFrame kf = new KeyFrame(Duration.millis(delay), e -> {
            for(int i = 0; i < numParticles; i ++) {
                for(int j = i + 1; j < numParticles; j++) {
                    if(particles[i].intersects(particles[j].getBoundsInLocal())) {
                        particles[i].meander();
                        particles[j].meander();
                    }
                }
            }
        });

        Timeline timeline = new Timeline(kf);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // The user can manipluate particle size and pause/play using mouse clicks
        int minRadius = 5;
        int maxRadius = 50;
        root.setOnMouseClicked(event -> {
            double currentRadius = particles[0].getRadius();
            int radiusChange = 1;
            for(Particle p: particles) {
                switch(event.getButton()) {
                    case SECONDARY:
                        if(currentRadius - radiusChange >= minRadius) {
                            p.setRadius(currentRadius - radiusChange);
                        }
                        break;
                    case PRIMARY:
                        if(currentRadius + radiusChange <= maxRadius) {
                            p.setRadius(currentRadius + radiusChange);
                        }
                        break;
                    default:
                        p.toggleRunState();
                }
            }
        });

        // Boilerplate JavaFX code
        primaryStage.setScene(scene);
        primaryStage.setTitle("Collision Simulator");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     *  Returns a randomly generated dark color
     */
    private static Color randomDarkColor() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return Color.rgb(rand.nextInt(128),rand.nextInt(128),rand.nextInt(128));
    }
    public static void main(String[] args) {
        launch(args);
    }
}
