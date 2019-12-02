package listeners;
import collidables.Collidable;
import sprites.Bullet;
import sprites.Counter;
import game.GameLevel;

/**
 * This class is used to create a listener which removes a paddle when it got hit by a bullet.
 * @author Lahav Amsalem 204632566
 */
public class PaddleRemover implements HitListener {
    private GameLevel game;
    private Counter paddleCounter;

    /**
     * Constructor creates a paddle remover.
     * @param counter - count of paddles.
     * @param g - game level.
     */
    public PaddleRemover(Counter counter, GameLevel g) {
        this.paddleCounter = counter;
        this.game = g;
    }

    /**
     * implementation of hitEvent function from HitListener interface.
     * this listener removes the block when it recognizes a hit of a block with his last hit point.
     * @param beingHit the blocks that is being hit.
     * @param hitter - the specific ball that has hit the block.
     */
    public void hitEvent(Collidable beingHit, Bullet hitter) {
        beingHit.removeFromGame(this.game);
        this.paddleCounter.decrease(1);
    }
}

