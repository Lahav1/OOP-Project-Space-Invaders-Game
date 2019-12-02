package listeners;
import collidables.Collidable;
import sprites.Bullet;
import game.GameLevel;

/**
 * This class is used to create a listener which removes a ball when it went off the screen.
 * @author Lahav Amsalem 204632566
 */
public class PlayersBulletRemover implements HitListener {
    private GameLevel gameLevel;

    /**
     * Constructor creates a ball remover.
     * @param gameLevel - the game we want to count its' balls.
     */
    public PlayersBulletRemover(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
    }

    /**
     * implementation of hitEvent function from HitListener interface.
     * this listener removes the ball when it finds out it's out of frame.
     * @param beingHit the blocks that is being hit.
     * @param hitter - the specific ball that has hit the block.
     */
    public void hitEvent(Collidable beingHit, Bullet hitter) {
        //remove the hitting ball from the game.
        hitter.removeFromGame(this.gameLevel);
        }

}
