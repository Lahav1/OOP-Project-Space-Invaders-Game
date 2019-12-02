package sprites;
import biuoop.DrawSurface;
import geometry.Line;
import geometry.Point;
import collidables.Collidable;
import collidables.CollisionInfo;
import game.GameLevel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import listeners.HitListener;

/**
 * This class is used for constructing a ball, returning it's paramaters and drawing it.
 * @author Lahav Amsalem 204632566
 */
public class Bullet implements Sprite {
    private double r = 0;
    private Point center;
    private java.awt.Color color;
    private Velocity velocity;
    private GameLevel game;
    private List<HitListener> hitListeners;
    private boolean shotByPlayer;
    private boolean shotByAlien;


    /**
     * Constructor creates a bullet given x,y of center, radius and color.
     * @param center - x,y of the center of the circle.
     * @param r      - radius of the circle.
     * @param color  - color of the circle.
     * @param v  - velocity.
     * @param gl  - game level.
     */
    public Bullet(Point center, int r, java.awt.Color color, Velocity v, GameLevel gl) {
        this.center = new Point(center.getX(), center.getY());
        this.r = r;
        this.color = color;
        this.game = gl;
        this.velocity = v;
        this.hitListeners = new ArrayList<>();
        this.shotByAlien = false;
        this.shotByPlayer = false;
    }

    /**
     * function uses the color we inserted and the size and center point, and draws the circle on a given surface.
     * @param surface the surface where we draw the circle
     */
    public void drawOn(DrawSurface surface) {
        //create a color variable and set the drawsurface to this color.
        java.awt.Color drawColor = this.getColor();
        surface.setColor(drawColor);
        //draw a circle on the surface using the ball's details
        surface.fillCircle(this.getX(), this.getY(), this.getSize());
        surface.setColor(Color.BLACK);
        surface.drawCircle(this.getX(), this.getY(), this.getSize());
    }

    /**
     * function adds the velocity's dx and dy to the bullet's x and y so it moves one step away.
     * @param dt - the amount of seconds passed since the last call.
     */
    public void moveOneStep(double dt) {
        Point bulletTarget = this.velocity.applyToPoint(this.center, dt);
        //create a new line which represents the ball's trajectory if there were no collidables on it's path
        Line trajectory = new Line(this.center, bulletTarget);
        //get the closest collision's info of the ball and closest object in trajectory
        CollisionInfo collisionInfo = this.game.getGE().getClosestCollision(trajectory);
        this.center = this.velocity.applyToPoint(this.center, dt);
        if (collisionInfo != null) {
            //find the closest collision point
            Point collisionPoint = collisionInfo.collisionPoint();
            //find the collidable that the ball is going to collide with.
            Collidable collisionObject = collisionInfo.collisionObject();
            collisionObject.hit(this, collisionPoint);
        }
    }

    /**
     * function activates moveonestep.
     * @param dt - the amount of seconds passed since the last call.
     */
    public void timePassed(double dt) {
        this.moveOneStep(dt);
    }

    /**
     * @return x value of the center.
     */
    public int getX() {
        return (int) this.center.getX();
    }

    /**
     * @return y value of the center.
     */
    public int getY() {
        return (int) this.center.getY();
    }

    /**
     * @return circle's center.
     */
    public Point getCenter() {
        return this.center;
    }

    /**
     * @return radius of the circle.
     */
    public int getSize() {
        return (int) this.r;
    }

    /**
     * @return color of the circle.
     */
    public java.awt.Color getColor() {
        return this.color;
    }

    /**
     * @return accessor to velocity.
     */
    public Velocity getVelocity() {
        return this.velocity;
    }

    /**
     * add the bullet to the game.
     * @param g - the game we want to add the ball to.
     */
    public void addToGame(GameLevel g) {
        g.getBulletList().add(this);
        g.addSprite(this);
    }

    /**
     * remove a bullet from game.
     * @param g - the game we want to remove the block from.
     */
    public void removeFromGame(GameLevel g) {
        g.getBulletList().remove(this);
        g.removeSprite(this);
    }

    /**
     * add a listener to the bullets's listeners list.
     * @param hl - the listener we want to add.
     */
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    /**
     * turn shotbyplayer to true.
     */
    public void announcePlayerIsShooter() {
        this.shotByPlayer = true;
    }

    /**
     * turn shotbyalien to true.
     */
    public void announceAlienIsShooter() {
        this.shotByAlien = true;
    }

    /**
     * return true if the player is the shooter.
     * @return true of false.
     */
    public boolean checkIfPlayerIsShooter() {
        return this.shotByPlayer;
    }

    /**
     * return true if the alien is the shooter.
     * @return true of false.
     */
    public boolean checkIfAlienIsShooter() {
        return this.shotByAlien;
    }
}
