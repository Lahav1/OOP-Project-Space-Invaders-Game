package listeners;
import collidables.Collidable;
import game.GameLevel;
import sprites.Bullet;
import sprites.Counter;

/**
 * This class is used to create a listener which counts the score of the player.
 * block hit - 5 points.
 * block destruction - 10 points.
 * level pass - 100 points.
 * @author Lahav Amsalem 204632566
 */
public class ScoreTrackingListener implements HitListener {
    private Counter currentScore;
    private GameLevel gameLevel;

    /**
     * Constructor creates a score tracker.
     * @param scoreCounter - (usually) a new counter.
     */
    public ScoreTrackingListener(Counter scoreCounter) {
        this.currentScore = scoreCounter;
    }

    /**
     * implementation of hitEvent function from HitListener interface.
     * this listener gives the player a score for the hit, depending on the way it affected the block.
     * @param beingHit the blocks that is being hit.
     * @param hitter - the specific ball that has hit the block.
     */
    public void hitEvent(Collidable beingHit, Bullet hitter) {
        // if an enemy was hit, add 100 points.
        this.currentScore.increase(100);
    }
}