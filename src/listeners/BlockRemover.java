package listeners;
import collidables.Collidable;
import sprites.Bullet;
import game.GameLevel;

/**
 * This class is used to create a listener which removes a block when it got hit by the ball.
 * @author Lahav Amsalem 204632566
 */
public class BlockRemover implements HitListener {
    private GameLevel gameLevel;

    /**
     * Constructor creates a block remover.
     * @param gameLevel - the game we want to count its' blocks.
     */
    public BlockRemover(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
    }

    /**
     * implementation of hitEvent function from HitListener interface.
     * this listener removes the block when it recognizes a hit of a block with his last hit point.
     * @param beingHit the blocks that is being hit.
     * @param hitter - the specific ball that has hit the block.
     */
    public void hitEvent(Collidable beingHit, Bullet hitter) {
        beingHit.removeFromGame(this.gameLevel);
        }
    }

