package game;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import sprites.Counter;
import java.awt.Color;

/**
 * This class is used for creation of the end screen in case the player lost the game.
 * @author Lahav Amsalem 204632566
 */
public class EndScreenLoss implements Animation {
    private KeyboardSensor keyboard;
    private boolean stop;
    private int score;

    /**
     * Constructor creates an end screen object.
     * @param k - keyboard sensor.
     * @param scoreCounter the counter that is in charge of counting the scores of the whole game.
     */
    public EndScreenLoss(KeyboardSensor k, Counter scoreCounter) {
        this.keyboard = k;
        this.stop = false;
        this.score = scoreCounter.getValue();
    }

    /**
     * pause screen logic and drawings.
     * @param d - drawsurface.
     * @param dt ignored.
     */
    public void doOneFrame(DrawSurface d, double dt) {
        // fill the background colors and the frames
        d.setColor(new Color(253, 230, 69));
        d.fillRectangle(0, 0, 800, 600);
        d.setColor(new Color(242, 44, 11));
        d.fillRectangle(25, 25, 750, 550);
        d.fillRectangle(30, 30, 740, 540);
        d.setColor(Color.BLACK);
        d.fillRectangle(30, 30, 740, 540);
        d.setColor(new Color(242, 44, 11));
        d.drawText(240, d.getHeight() / 2 - 40, "Game Over", 60);
        d.setColor(Color.WHITE);
        d.drawText(200, d.getHeight() / 2 + 20, "Your score is: " + this.score, 50);
        d.drawText(265, d.getHeight() / 2 + 75, "Press space key to continue.", 20);
    }

    /**
     * return true if the animation should stop and false if not.
     * @return should stop or not.
     */
    public boolean shouldStop() { return this.stop; }
}
