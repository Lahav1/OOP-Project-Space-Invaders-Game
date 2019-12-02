package listeners;
import collidables.Collidable;
import sprites.Bullet;
import sprites.Counter;
import game.GameLevel;

/**
 * This class is used to create a listener which removes an enemy when it got hit by a bullet.
 * @author Lahav Amsalem 204632566
 */
public class AlienRemover implements HitListener {
    private GameLevel game;
    private Counter remainingEnemies;


    /**
     * Constructor creates an enemy remover.
     * @param remaining - how many enemies left.
     * @param g - game level.
     */
    public AlienRemover(Counter remaining, GameLevel g) {
        this.remainingEnemies = remaining;
        this.game = g;
    }

    /**
     * implementation of hitEvent function from HitListener interface.
     * this listener removes the block when it recognizes a hit of a block with his last hit point.
     * @param beingHit the blocks that is being hit.
     * @param hitter - the specific ball that has hit the block.
     */
    public void hitEvent(Collidable beingHit, Bullet hitter) {
        if (hitter.checkIfPlayerIsShooter()) {
            remainingEnemies.decrease(1);
            beingHit.removeFromGame(this.game);
            this.game.getEnemyList().remove(this);
        }
    }
}

