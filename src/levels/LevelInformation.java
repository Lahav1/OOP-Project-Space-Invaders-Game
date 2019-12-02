package levels;
import sprites.Sprite;

/**
 * This interface includes all the function a "level information" object must implement.
 * @author Lahav Amsalem 204632566
 */
public interface LevelInformation {
    /**
     * function determines the name of the level.
     *
     * @return level's name in a string.
     */
    String levelName();

    /**
     * function determines the speed of the paddle.
     *
     * @return speed of paddle.
     */
    int paddleSpeed();

    /**
     * function determines the width of the paddle.
     *
     * @return width of paddle.
     */
    int paddleWidth();


    /**
     * function determines the look of the background.
     *
     * @return level's name in a string.
     */
    Sprite getBackground();

    /**
     * function returns the initial speed of the enemies.
     *
     * @return level's initial enemy speed.
     */
    double enemyDx();
}


