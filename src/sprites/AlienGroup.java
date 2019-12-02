package sprites;
import biuoop.DrawSurface;
import game.GameLevel;
import geometry.Point;
import geometry.Rectangle;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;
import java.util.Random;

/**
 * This class is used to create an object of a group of enemies.
 * @author Lahav Amsalem 204632566
 */
public class AlienGroup implements Sprite {
    private List<Alien> alienList;
    private List<Point> startingPoints;
    private List<Block> startingBlocks;
    private double leftMost;
    private double rightMost;
    private double bottom;
    private int alienWidth;
    private int space;
    private double upperLeftX;

    /**
     * Constructor creates an alien group given an alien list.
     * @param speed - location change rate.
     * @param c - a counter of aliens.
     */
    public AlienGroup(int speed, Counter c) {
        this.alienList = alienList(speed, c);
    }

    /**
     * function returns a full list of the aliens.
     * @param speed - speed.
     * @param c - a counter of enemies.
     * @return level's initial enemy list.
     */
    public List<Alien> alienList(int speed, Counter c) {
        List<Alien> list = new ArrayList<>();
        this.startingPoints = new ArrayList<>();
        this.startingBlocks = new ArrayList<>();
        int startX = 50;
        int startY = 50;
        int enemiesInLine = 10;
        int numOfLines = 5;
        int currentX = 50;
        int currentY = 50;
        int width = 40;
        int height = 30;
        String path = "resources/block_images/enemy.png";
        // create the enemies.
        for (int i = 0; i < numOfLines; i++) {
            for (int j = 0; j < enemiesInLine; j++) {
                // create the upperleft point and add it to the initial points list.
                Point point = new Point(currentX, currentY);
                this.startingPoints.add(point);
                // create the rectangle in the point with the requested dimensions.
                Rectangle rectangle = new Rectangle(point, width, height);
                // use the rectangle to create the block and add the block to the initial blocks list.
                Block block = new Block(rectangle);
                this.startingBlocks.add(block);
                // create an alien and add it to the list + increase counter.
                Alien alien = new Alien(block, speed, path);
                // add the new alien to the list and increase counter by 1.
                list.add(alien);
                c.increase(1);
                // current x of next alien is in distance of 10 pixels from current's
                currentX = currentX + width + 10;
            }
            // go back to beginning of the line.
            currentX = startX;
            // move one line down, with extra space of 10 pixels.
            currentY = currentY + height + 10;
        }
        return list;
    }

    /**
     * calls the original drawon function of the object, depended on it's type.
     * @param d the draw surface where we want to draw the objects.
     */
    public void drawOn(DrawSurface d) {
    }

    /**
     * finds the rightmost enemy's x coordinate.
     */
    public void findRightMost() {
        // set the first enemy's right point as a temp right most.
        this.rightMost = this.alienList.get(0).getBlock().getCollisionRectangle().getUpperRight().getX();
        // compare it to the rest and change rightmost accordingly.
        for (Alien alien : this.alienList) {
            double currentRight = alien.getBlock().getCollisionRectangle().getUpperRight().getX();
            if (currentRight > this.rightMost) {
                this.rightMost = currentRight;
            }
        }
    }

    /**
     * finds the rightmost enemy's x coordinate.
     */
    public void findLeftMost() {
        // set the first enemy's left point as a temp right most.
        this.leftMost = this.alienList.get(0).getBlock().getCollisionRectangle().getUpperLeft().getX();
        // compare it to the rest and change leftmost accordingly.
        for (Alien alien : this.alienList) {
            double currentLeft = alien.getBlock().getCollisionRectangle().getUpperLeft().getX();
            if (currentLeft < this.leftMost) {
                this.leftMost = currentLeft;
            }
        }
    }

    /**
     * finds the rightmost enemy's x coordinate.
     */
    public void findBottom() {
        // set the first enemy's lower point as a temp bottom.
        this.bottom = this.alienList.get(0).getBlock().getCollisionRectangle().getLowerLeft().getY();
        // compare it to the rest and change bottom accordingly.
        for (Alien alien : this.alienList) {
            double currentBottom = alien.getBlock().getCollisionRectangle().getLowerLeft().getY();
            if (currentBottom > this.bottom) {
                this.bottom = currentBottom;
            }
        }
    }

    /**
     * moves the group one step (matters of collision with frame).
     * @param dt - the amount of seconds passed since the last call.
     */
    public void moveOneStep(double dt) {
        // find the borders of the group,
        this.findRightMost();
        this.findLeftMost();
        this.findBottom();
        // if the group has reached right/left frame, move the whole group by one line lower.
        if ((this.rightMost >= 800) || (this.leftMost <= 0)) {
            groupGoLower();
        }
        // call moveonestep for each enemy separately.
        for (Alien alien : this.alienList) {
            alien.moveOneStep(dt);
        }
    }

    /**
     * moves the group lower by one line (in case it has reached the screen frame).
     */
    public void groupGoLower() {
        // for each enemy, call "golower" and change it's dx direction.
        for (Alien alien : this.alienList) {
            alien.goLower();
            // change each alien's dx direction.
            alien.updateVelocity(alien.getVelocity().getDX() * -1, alien.getVelocity().getDY());
        }
    }

    /**
     * moves the group lower by one line (in case it has reached the screen frame).
     * @param g - the game level.
     */
    public void addToGame(GameLevel g) {
        // add each enemy to the game.
        for (Alien alien : this.alienList) {
            alien.addToGame(g);
        }
    }

    /**
     * returns the enemies who survived to their initial location and settings..
     */
    public void returnToInitialLayout() {
        for (Alien alien : this.alienList) {
            alien.updatePointAndBlock(alien.getInitialPoint(), alien.getInitialBlock());
            alien.updateVelocity(alien.getInitialDx(), 0);
        }
    }

    /**
     * return the leftmost point.
     * @return leftmost.
     */
    public double getLeftMost() {
        return this.leftMost;
    }

    /**
     * return the rightmost point.
     * @return rightMost.
     */
    public double getRightMost() {
        return this.rightMost;
    }

    /**
     * return the lowest point.
     * @return bottom.
     */
    public double getBottom() {
        return this.bottom;
    }

    /**
     * returns the enemy list.
     * @return enemy list.
     */
    public List<Alien> getAlienList() {
        return this.alienList;
        }

    /**
     * notifies the sprite that time has passed.
     * @param dt - the amount of seconds passed since the last call.
     */
    public void timePassed(double dt) {
        this.moveOneStep(dt);
    }

    /**
     * creates a list of the bottom enemies of each existing column.
     * @return one random enemy.
     */
    public Alien returnRandomBottomEnemy() {
        this.alienWidth = 40;
        this.space = 10;
        this.upperLeftX = this.alienList.get(0).getCollisionRectangle().getUpperLeft().getX();
        TreeMap<Integer, List<Alien>> columns = new TreeMap<>();
        List<List<Alien>> unemptyColumns = new ArrayList<>();
        List<Alien> columnBottoms = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            columns.put((int) this.upperLeftX + (this.alienWidth * (i - 1))
                    + (this.space * (i - 1)), new ArrayList<>());
        }
        Set<Integer> keySet = columns.keySet();
        for (int key : keySet) {
            for (Alien alien : this.getAlienList()) {
                int index = this.getAlienList().indexOf(alien);
                int enemyLeftX = (int) alien.getBlock().getCollisionRectangle().getUpperLeft().getX();
                if ((enemyLeftX > key - 5) && (enemyLeftX < key + 5)) {
                    columns.get(key).add(alien);
                }
            }
        }
        for (int key : keySet) {
            if (!columns.get(key).isEmpty()) {
                unemptyColumns.add(columns.get(key));
            }
        }
        for (List<Alien> unemptyColumn : unemptyColumns) {
            // set the first enemy's lower point as a temp bottom.
            Alien currentBottom = unemptyColumn.get(0);
            // compare it to the rest and change bottom accordingly.
            for (Alien alien : unemptyColumn) {
                if (alien.getCollisionRectangle().getLowerLeft().getY()
                        > currentBottom.getCollisionRectangle().getLowerLeft().getY()) {
                    currentBottom = alien;
                }
            }
            columnBottoms.add(currentBottom);
        }
        Random random = new Random();
        int randomIndex = random.nextInt(columnBottoms.size());
        return columnBottoms.get(randomIndex);
    }
}
