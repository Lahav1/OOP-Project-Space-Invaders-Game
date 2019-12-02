package listeners;
import collidables.Collidable;
import sprites.Bullet;

/**
 * interface for listeners of hits between balls and collidables.
 * @author Lahav Amsalem 204632566
 */
public interface HitListener {

    /**
     * implementation of hitEvent function from HitListener interface.
     * @param beingHit the blocks that is being hit.
     * @param hitter - the specific ball that has hit the block.
     */
    void hitEvent(Collidable beingHit, Bullet hitter);
}
