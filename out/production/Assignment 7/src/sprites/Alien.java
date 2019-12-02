package sprites;
import biuoop.DrawSurface;
import collidables.Collidable;
import game.GameLevel;
import geometry.Line;
import geometry.Point;
import geometry.Rectangle;
import levelsio.Fill;
import levelsio.FillImage;
import listeners.HitListener;
import listeners.HitNotifier;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;
import java.awt.Image;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * This class is used to create a single alien.
 * @author Lahav Amsalem 204632566
 */
public class Alien implements Collidable, Sprite, HitNotifier {
    private Block block;
    private String path;
    private Image image;
    private Point upperLeft;
    private GameLevel game;
    private List<HitListener> hitListeners;
    private Velocity velocity;
    private int width;
    private int height;
    private double initialDx;
    private Point initialUpperLeft;
    private Block initialBlock;

    /**
     * Constructor creates an alien.
     *
     * @param b  - block.
     * @param dx - x change rate.
     * @param im - path of image.
     */
    public Alien(Block b, double dx, String im) {
        this.block = b;
        this.upperLeft = block.getCollisionRectangle().getUpperLeft();
        this.path = im;
        this.velocity = new Velocity(dx, 0);
        this.hitListeners = new ArrayList<>();
        this.initialDx = dx;
        this.initialUpperLeft = this.upperLeft;
        this.initialBlock = this.block;
    }

    /**
     * handles a situation when the alien getts hit by a bullet.
     * @param hitter         - the bullet.
     * @param collisionPoint the point of collision.
     */
    public void hit(Bullet hitter, Point collisionPoint) {
        //create the lines of the rectangle
        Line lower = this.block.getCollisionRectangle().getLower();
        // dx and dy of current velocity
        //if the intersection point is on the upper or lower side, change direction of dy
        if (collisionPoint.isPointOnLineSegment(lower)) {
            // notify the listeners that the block was hit (it will cause block's removal).
            // also remove hitter from the game.
            this.notifyHit(hitter);
        }
    }

    /**
     * function uses the color we inserted and the rectangle, and draws the alien on a given surface.
     * @param surface the surface where we draw the alien.
     */
    public void drawOn(DrawSurface surface) {
        // try drawing the enemy using the file path. else throw exception and catch it.
        try {
            this.image = ImageIO.read(new File(this.path));
        } catch (IOException e) {
            System.out.println("File does not exist");
        }
        // if the image was found, draw it on the surface.
        if (this.image != null) {
            Fill fill = new FillImage(this.image);
            fill.drawFill(surface, this.getCollisionRectangle());
        }
    }

    /**
     * call moveonestep function.
     * @param dt - time change rate.
     */
    public void timePassed(double dt) {
        this.moveOneStep(dt);
    }

    /**
     * add the alien to the game.
     * @param g - the game we want to add the alien to.
     */
    public void addToGame(GameLevel g) {
        g.addCollidable(this);
        g.addSprite(this);
        this.game = g;
    }

    /**
     * remove an alien from game (usually, will be called when alien getts hit).
     * @param gameLevel - the game we want to remove the alien from.
     */
    public void removeFromGame(GameLevel gameLevel) {
        gameLevel.removeCollidable(this);
        gameLevel.removeSprite(this);
        this.game.getEnemyList().remove(this);
        this.game = null;
    }

    /**
     * after the alien was hit, the function calls hitevent for every listener in the alien's listeners list.
     * so everylistener will do the necessary operation.
     * @param hitter - the bullet that hit the alien (which should be removed too).
     */
    private void notifyHit(Bullet hitter) {
        // Make a copy of the hitListeners before iterating over them.
        List<HitListener> listeners = new ArrayList<HitListener>(this.hitListeners);
        // Notify all listeners about a hit event:
        for (HitListener hl : listeners) {
            hl.hitEvent(this, hitter);
        }
    }

    /**
     * add a listener to the alien's listeners list.
     * @param hl - the listener we want to add.
     */
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    /**
     * remove a listener to the alien's listeners list.
     * @param hl - the listener we want to remove.
     */
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * get the alien's block.
     * @return  - block.
     */
    public Block getBlock() {
        return this.block;
    }

    /**
     * moves a single enemy one step.
     * @param dt - the amount of seconds passed since the last call.
     */
    public void moveOneStep(double dt) {
        // update the location according to velocity.
        this.upperLeft = this.velocity.applyToPoint(this.upperLeft, dt);
        // update the block according to the new location.
        Block updated = new Block(new Rectangle(this.upperLeft, this.block.getCollisionRectangle().getWidth(),
                this.block.getCollisionRectangle().getHeight()));
        this.block = updated;
    }

    /**
     * moves the enemy lower by one line (in case the group has reached the screen frame).
     */
    public void goLower() {
        // updated x and y.
        double newUpperLeftX = this.upperLeft.getX();
        double newUpperLeftY = this.upperLeft.getY() + this.block.getCollisionRectangle().getHeight();
        // update upperleft and the block accordingly.
        this.upperLeft = new Point(newUpperLeftX, newUpperLeftY);
        Block updated = new Block(new Rectangle(this.upperLeft, this.block.getCollisionRectangle().getWidth(),
                this.block.getCollisionRectangle().getHeight()));
        this.block = updated;
        // increase speed by 10%.
        this.updateVelocity(1.1 * this.velocity.getDX(), 1.1 * this.velocity.getDY());

    }

    /**
     * @return enemy's velocity.
     */
    public Velocity getVelocity() {
        return this.velocity;
    }

    /**
     * update the enemy's velocity.
     * @param dx - new dx.
     * @param dy - new dy.
     */
    public void updateVelocity(double dx, double dy) {
        this.velocity = new Velocity(dx, dy);
    }

    /**
     * update the enemy's location.
     * @param p - new point.
     * @param b - new block.
     */
    public void updatePointAndBlock(Point p, Block b) {
        this.upperLeft = p;
        this.block = b;
    }

    /**
     * implementation of getCollisionRectangle function from Collidable interface.
     * @return the rectangle that the block is made of.
     */
    public Rectangle getCollisionRectangle() {
        return this.block.getCollisionRectangle();
    }

    /**
     * return initial dx.
     * @return the initial dx of the alien.
     */
    public double getInitialDx() {
        return this.initialDx;
    }

    /**
     * return initial point.
     * @return the initial upper left point of the alien.
     */
    public Point getInitialPoint() {
        return this.initialUpperLeft;
    }

    /**
     * return initial block.
     * @return the initial block of the alien.
     */
    public Block getInitialBlock() {
        return this.initialBlock;
    }

    /**
     * create a bullet and shoot it.
     * @return the bullet.
     */
    public Bullet shootOneBullet() {
        double midOfEnemyX = this.getCollisionRectangle().getLower().middle().getX();
        double midOfEnemyY = this.getCollisionRectangle().getLower().middle().getY();
        Point bulletCreation = new Point(midOfEnemyX, midOfEnemyY + 7);
        Velocity v = new Velocity(0, 300);
        Bullet bullet = new Bullet(bulletCreation, 4, Color.red, v, this.game);
        return bullet;
    }
}
