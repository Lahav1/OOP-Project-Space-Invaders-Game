package sprites;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import geometry.Line;
import geometry.Point;
import geometry.Rectangle;
import collidables.Collidable;
import game.GameLevel;
import listeners.HitListener;
import listeners.HitNotifier;

/**
 * This class is used for creation of a collidable moving keyboard-controlled paddle on the screen.
 * @author Lahav Amsalem 204632566
 */
public class Paddle implements Collidable, Sprite, HitNotifier {
    private Rectangle rectangle;
    private java.awt.Color color;
    private biuoop.KeyboardSensor keyboard;
    private int rightFrame;
    private int leftFrame;
    private List<HitListener> hitListeners = new ArrayList<>();
    private double speed;
    private GameLevel game;

    /**
     * Constructor creates a paddle, given a rectangle and color.
     * @param rectangle - point, height and width
     * @param color  - color of the block.
     * @param padSpeed  - not actual speed but the "dx" of paddle.
     * @param g  - game level.
     */
    public Paddle(Rectangle rectangle, java.awt.Color color, double padSpeed, GameLevel g) {
        this.rectangle = rectangle;
        this.color = color;
        this.speed = padSpeed;
        this.updateKeyboardSensor(keyboard);
        this.game = g;
    }

    /**
     * function for updating the frame borders to the ball.
     * @param upper - top frame border
     * @param lower - bottom frame border
     * @param right - right frame border
     * @param left - left frame border
     */
    public void updateFrame(int upper, int lower, int right, int left) {
        this.rightFrame = right;
        this.leftFrame = left;
    }

    /**
     * function for updating the frame borders to the ball.
     * @param keysensor - keyboard sensor
     */
    public void updateKeyboardSensor(biuoop.KeyboardSensor keysensor) {
        this.keyboard = keysensor;
    }

    /**
     * implementation of getCollisionRectangle function from Collidable interface.
     * @return the rectangle that the block is made of.
     */
    public Rectangle getCollisionRectangle() {
        return this.rectangle;
    }


    /**
     * function uses the color we inserted and the rectangle, and draws the paddle on a given surface.
     * @param surface the surface where we draw the block
     */
    public void drawOn(DrawSurface surface) {
        //create a color variable and set the drawsurface to this color.
        java.awt.Color paddleColor = this.color;
        int x = (int) this.rectangle.getUpperLeft().getX();
        int y = (int) this.rectangle.getUpperLeft().getY();
        int width = (int) this.rectangle.getWidth();
        int height = (int) this.rectangle.getHeight();
        //draw a circle on the surface using the ball's details
        surface.setColor(color);
        surface.fillRectangle(x, y, width, height);
        surface.setColor(Color.BLACK);
        surface.drawRectangle(x, y, width, height);
    }

    /**
     * add the block to the game.
     * @param g - the game we want to add paddle to.
     */
    public void addToGame(GameLevel g) {
        g.addCollidable(this);
        g.addSprite(this);
        this.game = g;
    }

    /**
     * calls the onestepleft function in rectangle class, which changes the upperleft point and then updates the rest
     * of the points and also updates the lines.
     * @param dx - the location change rate of paddle.
     * @param dt - the time change rate.
     */
    public void moveLeft(double dx, double dt) {
        this.rectangle.oneStepLeft(dx, dt);
    }

    /**
     * calls the onestepright function in rectangle class, which changes the upperleft point and then updates the rest
     * of the points and also updates the lines.
     * @param dx - the location change rate of paddle.
     * @param dt - the time change rate.
     */
    public void moveRight(double dx, double dt) {
        this.rectangle.oneStepRight(dx, dt);
    }

    /**
     * implementation of sprite interface.
     * if the left key was pressed, call the moveleft function to move the paddle to the left.
     * if the right key was pressed, call the moveright function to move the paddle to the right.
     * @param dt - the amount of seconds passed since the last call.
     */
    public void timePassed(double dt) {
        // if the left key was pressed, call the moveleft function to move the paddle to the left.
        if (this.keyboard.isPressed(KeyboardSensor.LEFT_KEY)) {
            if (this.rectangle.getUpperLeft().getX() - this.speed * dt > (this.leftFrame)) {
                this.moveLeft(this.speed, dt);
            }
        }
        // if the right key was pressed, call the moveright function to move the paddle to the right.
        if (this.keyboard.isPressed(KeyboardSensor.RIGHT_KEY)) {
            if (this.rectangle.getUpperRight().getX() + this.speed * dt < (this.rightFrame)) {
                this.moveRight(this.speed, dt);
            }
        }
        return;
    }

    /**
     * implementation of hit function from Collidable interface.
     * after we already know the ball collides with the paddle, return an updated velocity.
     * @param hitter the ball that hitts the paddle.
     * @param collisionPoint the point of collision.
     */
    public void hit(Bullet hitter, Point collisionPoint) {
        Line upper = this.rectangle.getUpper();
        if (collisionPoint.isPointOnLineSegment(upper)) {
            this.notifyHit(hitter);
            this.removeFromGame(this.game);
        }
    }

    /**
     * remove the paddle from game (usually, will be called when the player lost 1 life).
     * @param gameLevel - the game we want to remove the paddle from.
     */
    public void removeFromGame(GameLevel gameLevel) {
        gameLevel.removeCollidable(this);
        gameLevel.removeSprite(this);
    }

    /**
     * add a listener to the paddle's listeners list.
     * @param hl - the listener we want to add.
     */
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    /**
     * remove a listener to the paddle's listeners list.
     * @param hl - the listener we want to remove.
     */
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * after the paddle was hit, the function calls hitevent for every listener in the paddle's listeners list.
     * @param hitter - the ball that hit the block.
     */
    private void notifyHit(Bullet hitter) {
        // Make a copy of the hitListeners before iterating over them.
        List<HitListener> listeners = new ArrayList<>();
        listeners.addAll(this.hitListeners);
        // Notify all listeners about a hit event:
        for (HitListener hl : listeners) {
            hl.hitEvent(this, hitter);
        }
    }

    /**
     * create a bullet and shoot it.
     * @return the bullet.
     */
    public Bullet shootOneBullet() {
        double midOfPadX = this.getCollisionRectangle().getUpper().middle().getX();
        double midOfPadY = this.getCollisionRectangle().getUpper().middle().getY();
        Point bulletCreation = new Point(midOfPadX, midOfPadY - 5);
        Velocity v = new Velocity(0, -300);
        Bullet bullet = new Bullet(bulletCreation, 4, Color.white, v, this.game);
        return bullet;
    }
}
