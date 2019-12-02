package game;
import biuoop.KeyboardSensor;
import biuoop.DialogManager;
import java.io.IOException;
import java.io.File;
import highscore.HighScoresAnimation;
import highscore.ScoreInfo;
import levels.StandartLevel;
import sprites.Counter;
import sprites.LivesIndicator;
import sprites.ScoreIndicator;
import levels.LevelInformation;
import highscore.HighScoresTable;

/**
 * This class is in charge of managing the whole game and moving thorugh the different levels.
 * @author Lahav Amsalem 204632566
 */
public class GameFlow {
    // whole-game counters.
    private Counter scoreCounter;
    private Counter lifeCounter;
    // indicators we follow through game.
    private LivesIndicator lifeIndi;
    private ScoreIndicator scoreIndi;
    // animation runner
    private AnimationRunner runner;
    // keyboard sensor.
    private KeyboardSensor keyboard;
    // did the player win?
    private boolean victory;
    // high scores table
    private HighScoresTable hsTable;
    // file of highscores
    private File hsFile;


    /**
     * Constructor creates a new gameflow object.
     * @param ar - animation runner.
     * @param ks - keyboard sensor.
     * @param hs - highscore table.
     * @param f - file.
     */
    public GameFlow(AnimationRunner ar, KeyboardSensor ks, HighScoresTable hs, File f) {
        this.runner = ar;
        this.keyboard = ks;
        this.hsTable = hs;
        this.hsFile = f;
    }

    /**
     * the function runs the levels in a difficulty order until the player loses.
     */
    public void runLevels() {
        // create whole-game counters.
        this.scoreCounter = new Counter();
        this.scoreIndi = new ScoreIndicator(this.scoreCounter);
        this.lifeCounter = new Counter();
        this.lifeIndi = new LivesIndicator(this.lifeCounter, 3);
        // if the player hasn't lost, keep creating new levels.
        // only changes are the level's name, and the enemies speed is related to the level number,
        // while the game getts harder as the levels procceed.
        int levelNumber = 1;
        while (this.lifeCounter.getValue() > 0) {
            // create a standart level with increasing speed each time, and initialize it.
            LevelInformation standart = new StandartLevel(levelNumber, 20 * levelNumber);
            GameLevel level = new GameLevel(standart, this.keyboard, this.runner, this.scoreIndi,
                    this.lifeIndi, this.scoreCounter, this.lifeCounter);
            level.initialize();
            // give the player another turn as long as there are lives and enemies.
            while ((level.getAlienCount() > 0) && (this.lifeCounter.getValue() > 0)) {
                level.playOneTurn();
            }
            // no lives = breaks the loop because the player lost the game.
            if (this.lifeCounter.getValue() == 0) {
                break;
            }
            // if level was cleared and player has more lives, increase the level's number to continue.
            levelNumber++;
        }
        // create a game over screen and display his score.
        // space key will finish the animation.
        EndScreenLoss end = new EndScreenLoss(this.keyboard, this.scoreCounter);
        Animation endStopable = new KeyPressStoppableAnimation(this.keyboard, KeyboardSensor.SPACE_KEY, end);
        this.runner.run(endStopable);
        // open a dialog to allow player insert his name.
        DialogManager dialog = this.runner.getGUI().getDialogManager();
        String name = dialog.showQuestionDialog("Name", "What is your name?", "");
        // if the player's score was high enough, add him to the highscores table.
        this.hsTable.add(new ScoreInfo(name, this.scoreCounter.getValue()));
        // save the new table to the file.
        try {
            this.hsTable.save(this.hsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // create a high score animation screen.
        HighScoresAnimation hsScreen = new HighScoresAnimation(this.keyboard, this.hsTable);
        Animation hsScreenStopable = new KeyPressStoppableAnimation(this.keyboard, KeyboardSensor.SPACE_KEY, hsScreen);
        // open high scores animation screen.
        this.runner.run(hsScreenStopable);
    }

}
