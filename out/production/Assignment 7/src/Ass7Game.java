import biuoop.GUI;
import biuoop.KeyboardSensor;
import biuoop.Sleeper;
import game.AnimationRunner;
import game.GameFlow;
import game.KeyPressStoppableAnimation;
import highscore.HighScoresAnimation;
import highscore.HighScoresTable;
import menu.MenuAnimation;
import menu.StartGameTask;
import menu.HighScoresTask;
import menu.QuitGameTask;
import menu.Task;
import java.io.File;
import java.io.IOException;

/**
 * This class creates a new game, initializes it and runs it.
 * @author Lahav Amsalem 204632566
 */
public class Ass7Game {

    /**
     * main function of the game proccess.
     *
     * @param args for specific level order choice.
     */
    public static void main(String[] args) {
        // create new gui and keyboard sensor.
        GUI gui = new GUI("Space Invaders", 800, 600);
        // create a sleeper.
        Sleeper sleeper = new Sleeper();
        // use 60 frames ps.
        int framesPerSecond = 60;
        //create an animation runner.
        AnimationRunner ar = new AnimationRunner(gui, framesPerSecond, sleeper);
        // create a keyboard sensor using te gui.
        KeyboardSensor keyboard = gui.getKeyboardSensor();
        // create a new high score table.
        HighScoresTable hs = new HighScoresTable();
        // create a new file to manage the all-time high scores.
        File hsFile = new File("highscores");
        // if there is a file in the certain path, load it. else, create a new file.
        if (hsFile.exists()) {
            try {
                hs.load(hsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // create a gameflow.
        GameFlow flow = new GameFlow(ar, keyboard, hs, hsFile);
        // create a high score animation screen.
        HighScoresAnimation hsScreen = new HighScoresAnimation(keyboard, hs);
        KeyPressStoppableAnimation hsScreenStoppable = new KeyPressStoppableAnimation(keyboard,
                KeyboardSensor.SPACE_KEY, hsScreen);
        // endless animation loop.
        // will stop only if "q" is pressed.
        while (true) {
            // create a new main menu.
            MenuAnimation menu = new MenuAnimation(keyboard, "Main Menu");
            // create a new game task and add it to the menu.
            StartGameTask gameTask = new StartGameTask(ar, flow);
            menu.addSelection("s", "s - Start Game", gameTask);
            // create a new high score task and add it to the main menu.
            HighScoresTask hsTask = new HighScoresTask(ar, hsScreenStoppable);
            menu.addSelection("h", "h - High Scores Table", hsTask);
            // create a new quit game task and add it to the main menu.
            QuitGameTask quitTask = new QuitGameTask(keyboard, ar);
            menu.addSelection("q", "q - Quit Game", quitTask);
            // run the main menu
            ar.run(menu);
            // run the player's choice.
            Task<Void> task = menu.getStatus();
            task.run();
            // reset the task list.
            menu.resetTasks();
        }
    }



}

