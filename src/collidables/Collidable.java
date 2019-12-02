package collidables;
import game.GameLevel;
import geometry.Point;
import geometry.Rectangle;
import sprites.Bullet;

/**
 * interface for collidable objects in the game.
 * @author Lahav Amsalem 204632566
 */
public interface Collidable {

    /**
     * @return the collision object.
     */
    Rectangle getCollisionRectangle();

    /**
     * Notify the object that we collided with it at collisionPoint with a given velocity.
     * @param hitter the ball that hit the collidable.
     * @param collisionPoint the point of the collision.
     */
    void hit(Bullet hitter, Point collisionPoint);

    /**
     * remove the collidable from a given game.
     * @param g the game level.
     */
    void removeFromGame(GameLevel g);
}