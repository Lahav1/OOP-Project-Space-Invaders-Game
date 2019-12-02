package sprites;
import biuoop.DrawSurface;
import game.GameLevel;
import geometry.Rectangle;
import geometry.Point;
import levelsio.Fill;
import levelsio.FillColor;
import listeners.HitListener;
import listeners.HitNotifier;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;


/**
 * a shield is a list of small blocks.
 * @author Lahav Amsalem 204632566
 */
public class Shield implements Sprite, HitNotifier {
    private Color color;
    private Point start;
    private List<Block> blocks;

    /**
     * Constructor creates a shields and adds it to a given game.
     * @param c - color.
     * @param s - start point.
     * @param gl - game level.
     * @param blockWidth - width of a single block.
     * @param blockHeight - height of a single block.
     */
    public Shield(Color c, Point s, GameLevel gl, double blockWidth, double blockHeight) {
        Fill fill = new FillColor(c);
        this.blocks = new ArrayList<>();
        this.color = c;
        this.start = s;
        int blocksInLine = 35;
        int numOfLines = 5;
        // initialize current x,y with the start points. they will be changed as we add new blocks.
        int currentX = (int) this.start.getX();
        int currentY = (int) this.start.getY();
        // add the blocks to the shield, one by one.
        for (int i = 0; i < numOfLines; i++) {
            for (int j = 0; j < blocksInLine; j++) {
                Point point = new Point(currentX, currentY);
                Rectangle rectangle = new Rectangle(point, blockWidth, blockHeight);
                Block block = new Block(rectangle, fill);
                this.blocks.add(block);
                block.addToGame(gl);
                currentX = currentX + (int) blockWidth;
            }
            // reset the new line's start coordinates.
            currentX = (int) this.start.getX();
            currentY = currentY + (int) blockHeight;
        }
    }

    /**
     * runns on block list and draws each block.
     * @param surface the surface where we draw the block
     */
    public void drawOn(DrawSurface surface) {
        for (Block block : this.blocks) {
            block.drawOn(surface);
        }
    }

    /**
     * notifies the sprite that time has passed.
     * @param dt - the amount of seconds passed since the last call.
     */
    public void timePassed(double dt) {
    }

    /**
     * add a listener to the shields's listeners list.
     * @param hl - the listener we want to add.
     */
    public void addHitListener(HitListener hl) {
        for (Block block : this.blocks) {
            block.addHitListener(hl);
        }
    }

    /**
     * remove a listener from the shield's listeners list.
     * @param hl - the listener we want to remove.
     */
    public void removeHitListener(HitListener hl) {
        for (Block block : this.blocks) {
            block.removeHitListener(hl);
        }
    }

    /**
     * return shield's block list..
     * @return block list.
     */
    public List<Block> getBlocks() {
        return this.blocks;
    }

    /**
     * lowest y value actually belongs to the upper side of the top block of the shield.
     * @return min y of the shield.
     */
    public double findHighestY() {
        // set the lowest point to be the first block's y.
        double lowest = this.blocks.get(0).getCollisionRectangle().getLowerLeft().getY();
        // compare to find the true lowest block in shield.
        for (Block block : this.blocks) {
            if (block.getCollisionRectangle().getLowerLeft().getY() < lowest) {
                lowest = block.getCollisionRectangle().getLowerLeft().getY();
            }
        }
        return lowest;
    }

    /**
     * lowest y value actually belongs to the upper side of the top block of the shield.
     * @return min y of the shield.
     */
    public double findLowestY() {
        // set the lowest point to be the first block's y.
        double lowest = this.blocks.get(0).getCollisionRectangle().getLowerLeft().getY();
        // compare to find the true lowest block in shield.
        for (Block block : this.blocks) {
            if (block.getCollisionRectangle().getLowerLeft().getY() > lowest) {
                lowest = block.getCollisionRectangle().getLowerLeft().getY();
            }
        }
        return lowest;
    }

    /**
     * return the start point..
     * @return start point.
     */
    public Point getStart() {
        return this.start;
    }
}
