package menu;
import game.AnimationRunner;
import game.GameFlow;

/**
 * This class is used for creation of the start game task.
 * @author Lahav Amsalem 204632566
 */
public class StartGameTask implements Task<Void> {
    // animation runner
    private AnimationRunner runner;
    // keyboard sensor.
    private GameFlow game;

    /**
     * Constructor creates a start game task.
     * @param r - animation runner.
     * @param g - gameflow.
     */
    public StartGameTask(AnimationRunner r, GameFlow g) {
        this.runner = r;
        this.game = g;
    }

    /**
     * used for running the specific task.
     * @return null
     */
    public Void run() {
        this.game.runLevels();
        return null;
    }
}
