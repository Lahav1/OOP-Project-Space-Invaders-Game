package levels;
import java.awt.Color;
import levelsio.Fill;
import levelsio.FillColor;
import sprites.Background;
import sprites.Sprite;

/**
 * This class is a standart level's info.
 * @author Lahav Amsalem 204632566
 */
public class StandartLevel implements LevelInformation {
    private int levelNum;
    private int enemyDx;

    /**
     * Constructor creates a new standart level object.
     * @param levelNumber - round number.
     * @param enemyDx - dx of enemy.
     */
    public StandartLevel(int levelNumber, int enemyDx) {
        this.levelNum = levelNumber;
        this.enemyDx = enemyDx;
    }

    /**
     * function determines the name of the level.
     *
     * @return level's name in a string.
     */
    public String levelName() {
       return "Battle no. " + this.levelNum;
    }

    /**
     * function determines the speed of the paddle.
     *
     * @return speed of paddle.
     */
    public int paddleSpeed() {
        return 400;
    }

    /**
     * function determines the width of the paddle.
     *
     * @return width of paddle.
     */
    public int paddleWidth() {
        return 80;
    }


    /**
     * function determines the look of the background.
     *
     * @return level's name in a string.
     */
    public Sprite getBackground() {
        Fill fill = new FillColor(Color.black);
        return new Background(fill);
    }


    /**
     * function returns the initial speed of the enemies.
     *
     * @return level's initial enemy speed.
     */
    public double enemyDx() {
        return this.enemyDx;
    }


}


