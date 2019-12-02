package game;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import geometry.Point;
import geometry.Rectangle;
import collidables.Collidable;
import collidables.GameEnvironment;
import listeners.ScoreTrackingListener;
import listeners.HitListener;
import listeners.AliensBulletRemover;
import listeners.PlayersBulletRemover;
import listeners.PaddleRemover;
import listeners.BlockRemover;
import listeners.AlienRemover;
import sprites.Block;
import sprites.Bullet;
import sprites.Alien;
import sprites.AlienGroup;
import sprites.Shield;
import sprites.LivesIndicator;
import sprites.ScoreIndicator;
import sprites.LevelsName;
import sprites.Paddle;
import sprites.Counter;
import sprites.Sprite;
import sprites.SpriteCollection;
import levels.LevelInformation;

/**
 * This class is used to create the sprite and collidable collections, and create the animation loop.
 * some process logics -
 * gameflow creates a new standart level, calls initialize and starts a loop of playoneturn,
 * as long as the player can keep playing.
 * playoneturn calls the runner which runs the game and calls dooneframe until the game does not
 * allow it.
 * @author Lahav Amsalem 204632566
 */
public class GameLevel implements Animation {
    // screen frame
    private int upper;
    private int lower;
    private int left;
    private int right;
    // collections of sprites and collidables.
    private SpriteCollection sprites;
    private GameEnvironment environment;
    // level-specific counters.
    private Counter enemyCounter;
    private Counter paddleCounter;
    // whole-game counters.
    private Counter scoreCounter;
    private Counter lifeCounter;
    // indicators we follow through game.
    private LivesIndicator lifeIndi;
    private ScoreIndicator scoreIndi;
    // removers
    private BlockRemover blockRemover;
    private AlienRemover alienRemover;
    private PlayersBulletRemover playersBulletRemover;
    private AliensBulletRemover aliensBulletRemover;
    private PaddleRemover paddleRemover;
    // listeners
    private ScoreTrackingListener scoreListener;
    // animation runner
    private AnimationRunner runner;
    // indicates if the game has stopped (caused by victory/loss)
    private boolean running;
    // keyboard sensor to allow player use keyboard.
    private KeyboardSensor keyboard;
    // level information
    private LevelInformation levelInfo;
    // enemies related
    private AlienGroup alienGroup;
    private double enemySpeed;
    private boolean enemiesReachShield;
    // shields
    private List<Shield> shieldList = new ArrayList<>();
    private Paddle paddle;
    private List<Bullet> bulletList;
    // shooting info
    private boolean canPlayerShoot;
    private long lastPlayerShootingTime;
    private boolean canEnemyShoot;
    private long lastEnemyShootingTime;


    /**
     * Constructor creates a gamelevel given a gamelevel info.
     * @param info - all the information we need to create the level.
     * @param kboard - keyboard sensor.
     * @param aRunner - animation runner.
     * @param scoreInd - score indicator.
     * @param lifeInd - life indicator.
     * @param scoreC - score counter.
     * @param livesC - lives counter.
     */
    public GameLevel(LevelInformation info, KeyboardSensor kboard, AnimationRunner aRunner,
                     ScoreIndicator scoreInd, LivesIndicator lifeInd, Counter scoreC, Counter livesC) {
        this.levelInfo = info;
        this.keyboard = kboard;
        this.runner = aRunner;
        this.scoreIndi = scoreInd;
        this.lifeIndi = lifeInd;
        this.scoreCounter = scoreC;
        this.lifeCounter = livesC;
        this.enemySpeed = info.enemyDx();
        this.enemiesReachShield = false;
    }

    /**
     * function initializes the game, creating the gui, balls, collidables.
     */
    public void initialize() {
        // SCREEN BORDER SETTING + KEYBOARD SENSOR //
        this.createScreenBorders();
        this.keyboard = this.runner.getGUI().getKeyboardSensor();
        //ENVIRONMENTS //
        this.createEnvironment();
        // BACKGROUND CREATION //
        this.createBackground();
        // COUNTERS CREATION //
        this.enemyCounter = new Counter();
        this.paddleCounter = new Counter();
        // REMOVERS CREATION //.
        this.alienRemover = new AlienRemover(this.enemyCounter, this);
        this.blockRemover = new BlockRemover(this);
        this.playersBulletRemover = new PlayersBulletRemover(this);
        this.aliensBulletRemover = new AliensBulletRemover(this);
        // LISTENERS CREATION //
        this.scoreListener = new ScoreTrackingListener(this.scoreCounter);
        //create a new score counter listener.
        // SHIELDS CREATION //
        this.createShields();
        // ENEMIES CREATION //
        this.createEnemies(this.scoreListener);
        // DEATH BLOCKS CREATION //
        this.createDeathBlock(playersBulletRemover);
        // LEVEL'S NAME //
        LevelsName name = new LevelsName(this.levelInfo.levelName());
        // add the whole-game indicators to the sprites list.
        this.sprites.addSprite(scoreIndi);
        this.sprites.addSprite(lifeIndi);
        this.sprites.addSprite(name);
    }

    /**
     * function starts a single turn of the player.
     */
    public void playOneTurn() {
        // set running to be true
        this.running = true;
        this.createPaddle();
        this.paddleRemover = new PaddleRemover(this.paddleCounter, this);
        this.paddle.addHitListener(this.paddleRemover);
        this.paddleCounter.increase(1);
        // create bullet list.
        this.bulletList = new ArrayList<>();
        // countdown before turn starts
        this.runner.run(new CountdownAnimation(2, 3, this.sprites));
        // let the player shoot when game begins.
        this.canPlayerShoot = true;
        this.canEnemyShoot = true;
        // use our runner to run the current animation -- which is one turn of the game
        this.runner.run(this);
        this.enemiesReachShield = false;
        // Notify all listeners about a hit event:
        this.alienGroup.returnToInitialLayout();
        this.removeBullets();
    }

    /**
     * specific level logic and drawings.
     * @param d - drawsurface
     * @param dt - the amount of seconds passed since the last call.
     */
    public void doOneFrame(DrawSurface d, double dt) {
        // operate timepassed function on each sprite
        this.sprites.notifyAllTimePassed(dt);
        // draw all objects on drawsurface
        this.sprites.drawAllOn(d);
        // if the player has no lives left or he cleared the level, make the loop stop running.
        if (this.lifeCounter.getValue() == 0) {
            this.running = false;
        }
        if (this.enemyCounter.getValue() == 0) {
            this.running = false;
            //finally remove pad before the next turn.
            this.paddle.removeFromGame(this);
        }
        // if the enemies crossed the line, make the loop stop running.
        checkEnemiesReachedShield();
        if (didEnemiesPassShield()) {
            this.lifeCounter.decrease(1);
            this.running = false;
        }
        //if the user pressed "p", pause the game and show the pausescreen animation.
        //when user presses space key, we go back to the game animation.
        if (this.keyboard.isPressed("p")) {
            PauseScreen pause = new PauseScreen(this.keyboard);
            Animation pauseStoppable = new KeyPressStoppableAnimation(this.keyboard,
                    KeyboardSensor.SPACE_KEY, pause);
            this.runner.run(pauseStoppable);
        }
        if (this.paddleCounter.getValue() <= 0) {
            this.lifeCounter.decrease(1);
            this.running = false;
        }
        if (this.keyboard.isPressed(KeyboardSensor.SPACE_KEY)) {
            // check if the player can shoot and update canPlayerShoot member accordingly.
            this.checkIfPlayerAllowedToShoot();
            // if player can shoot, create a new bullet.
            if (this.canPlayerShoot) {
                Bullet bullet = this.paddle.shootOneBullet();
                bullet.announcePlayerIsShooter();
                bullet.addToGame(this);
                bullet.addHitListener(this.playersBulletRemover);
                this.lastPlayerShootingTime = System.currentTimeMillis();
            }
        }
        this.checkIfEnemyIsAllowedToShoot();
        if (this.canEnemyShoot) {
            Alien shooter = this.alienGroup.returnRandomBottomEnemy();
            Bullet bullet = shooter.shootOneBullet();
            bullet.announceAlienIsShooter();
            bullet.addToGame(this);
            bullet.addHitListener(this.aliensBulletRemover);
            this.lastEnemyShootingTime = System.currentTimeMillis();
        }
    }

    /**
     * function creates the animation runner and its' constructor's materials.
     */
    public void createScreenBorders() {
        // set the screen's borders.
        this.upper = 0;
        this.lower = 600;
        this.left = 0;
        this.right = 800;
    }

    /**
     * function creates the environemnts of the game.
     */
    public void createEnvironment() {
        //create a list of collidable objects and a list of sprite objects
        List<Collidable> collidables = new ArrayList<Collidable>();
        List<Sprite> spriteList = new ArrayList<Sprite>();
        //update the game's environment to be the collidables list.
        this.environment = new GameEnvironment(collidables);
        //update the game's sprites collection to be the list of sprites.
        this.sprites = new SpriteCollection(spriteList);
    }

    /**
     * function uses levelinfo to create blocks.
     * @param scorelstn - score tracking listener.
     */
    public void createEnemies(HitListener scorelstn) {
        this.alienGroup = new AlienGroup((int) this.enemySpeed, this.enemyCounter);
        this.alienGroup.addToGame(this);
        this.sprites.addSprite(this.alienGroup);
        for (Alien alien : this.alienGroup.getAlienList()) {
            alien.addHitListener(this.alienRemover);
            alien.addHitListener(this.playersBulletRemover);
            alien.addHitListener(this.scoreListener);
        }
    }

    /**
     * function uses levelinfo to create the paddle.
     */
    public void createPaddle() {
        int width = this.levelInfo.paddleWidth();
        int speed = this.levelInfo.paddleSpeed();
        // optimize the paddle location to the middle of the screen.
        // it should be placed a half of its' width from the center of the screen (which its' x coordinate is 400)
        double paddleX = 400 - (width / 2);
        double paddleY = 585;
        // create the paddle using the details we received from level info.
        this.paddle = new Paddle(new Rectangle(new Point(paddleX, paddleY), width, 12),
                Color.ORANGE, speed, this);
        // update the keyboard sensor to pad.
        this.paddle.updateKeyboardSensor(this.keyboard);
        // update the screen frame to pad.
        this.paddle.updateFrame(this.upper, this.lower, this.right, this.left);
        // create a paddle remover listener so we can remove the paddle when it getts hit.
        this.paddle.addToGame(this);
    }

    /**
     * function creates the death block in the bottom of the screen, so when a ball touches it, the ball is removed.
     * @param ballrmv - the ball remover listener.
     */
    public void createDeathBlock(HitListener ballrmv) {
        Block topDeath = new Block(new Rectangle(new Point(0, 0), 800, 10), Color.DARK_GRAY);
        Block bottomDeath = new Block(new Rectangle(new Point(0, 600), 800, 10), Color.DARK_GRAY);
        topDeath.addHitListener(ballrmv);
        topDeath.addToGame(this);
        bottomDeath.addHitListener(ballrmv);
        bottomDeath.addToGame(this);
    }

    /**
     * function creates the indicators of the game (score and life).
     */
    public void createBackground() {
        Sprite bg = this.levelInfo.getBackground();
        this.sprites.addSprite(bg);
    }

    /**
     * function activates addcollidable function from gameenvironment class and adds collidable 'c' to the list.
     *
     * @param c the collidable we want to add.
     */
    public void addCollidable(Collidable c) {
        environment.addCollidable(c);
    }

    /**
     * function activates addsprite function from spritescollection class and adds sprite 's' to the list.
     *
     * @param s the sprite we want to add.
     */
    public void addSprite(Sprite s) {
        sprites.addSprite(s);
    }

    /**
     * remove a collidable from game when it is no longer relevant.
     *
     * @param c - the collidable we want to remove.
     */
    public void removeCollidable(Collidable c) {
        this.environment.removeCollidable(c);
    }

    /**
     * remove a sprite from game when it is no longer relevant.
     *
     * @param s - the sprite we want to remove.
     */
    public void removeSprite(Sprite s) {
        this.sprites.removeSprite(s);
    }

    /**
     * function returns the initial shields of the level.
     */
    public void createShields() {
        // create three shields.
        Shield s1 = new Shield(Color.cyan, new Point(50, 520), this, 5, 3);
        Shield s2 = new Shield(Color.cyan, new Point(315, 520), this, 5, 3);
        Shield s3 = new Shield(Color.cyan, new Point(580, 520), this, 5, 3);
        // add block remover listener to all shields.
        s1.addHitListener(this.blockRemover);
        s2.addHitListener(this.blockRemover);
        s3.addHitListener(this.blockRemover);
        // add bullet remover listener to all shields.
        s1.addHitListener(this.playersBulletRemover);
        s2.addHitListener(this.playersBulletRemover);
        s3.addHitListener(this.playersBulletRemover);
        // add bullet remover listener to all shields.
        s1.addHitListener(this.aliensBulletRemover);
        s2.addHitListener(this.aliensBulletRemover);
        s3.addHitListener(this.aliensBulletRemover);
        // add the new shield to the shield list.
        this.shieldList.add(s1);
        this.shieldList.add(s2);
        this.shieldList.add(s3);
    }

    /**
     * @return find the limit the enemies can get close to the paddle.
     */
    public double findShieldDefenceLine() {
        List<Double> shieldY = new ArrayList<>();
        if ((this.shieldList.get(0).getBlocks() == null) && (this.shieldList.get(1).getBlocks() == null)
                && (this.shieldList.get(2).getBlocks() == null)) {
            return this.shieldList.get(0).getStart().getY();
        }
        for (Shield s : this.shieldList) {
            double topY = s.findHighestY();
            shieldY.add(topY);
        }
        return Collections.min(shieldY);
    }

    /**
     * check if the enemies crossed the shield line.
     */
    public void checkEnemiesReachedShield() {
        if ((alienGroup.getBottom()) > (findShieldDefenceLine())) {
            this.enemiesReachShield = true;
        }
    }

    /**
     * when running = false, it means the player lost all lives or finished the level.
     * we want to stop at this point.
     * not running -> should stop.
     * @return true if running, false if not.
     */
    public boolean shouldStop() {
        return !this.running;
    }

    /**
     * @return number of blocks left in this level.
     */
    public int getAlienCount() {
        return this.enemyCounter.getValue();
    }

    /**
     * @return number of lives left in this level.
     */
    public int getLevelLivesCount() {
        return this.lifeCounter.getValue();
    }

    /**
     * @return number of lives left in this level.
     */
    public int getLevelPaddleCount() {
        return this.paddleCounter.getValue();
    }

    /**
     * @return true if the enemies crossed the line.
     */
    public boolean didEnemiesPassShield() {
        return this.enemiesReachShield;
    }

    /**
     * @return game environment.
     */
    public GameEnvironment getGE() {
        return this.environment;
    }

    /**
     * @return bullet list.
     */
    public List<Bullet> getBulletList() {
        return this.bulletList;
    }

    /**
     * @return enemy list.
     */
    public List<Alien> getEnemyList() {
        return this.alienGroup.getAlienList();
    }

    /** check if the player is allowed to shoot.
     * a new bullet can be shot every 0.35 seconds.
     */
    public void checkIfPlayerAllowedToShoot() {
        // if at least 350 milliseconds passed since last shooting, allow player to shoot.
        if (this.lastPlayerShootingTime + 350 <= System.currentTimeMillis()) {
            this.canPlayerShoot = true;
        } else {
            this.canPlayerShoot = false;
        }
    }

    /** check if the player is allowed to shoot.
     * a new bullet can be shot every 0.35 seconds.
     */
    public void checkIfEnemyIsAllowedToShoot() {
        // if at least 500 milliseconds passed since last shooting, allow player to shoot.
        if (this.lastEnemyShootingTime + 500 <= System.currentTimeMillis()) {
            this.canEnemyShoot = true;
        } else {
            this.canEnemyShoot = false;
        }
    }

    /** returns the paddle counter.
     * @return counter of left paddles.
     */
    public Counter getPaddleCounter() {
        return this.paddleCounter;
        }

    /**
     * remove the bullets from the game.
     */
    public void removeBullets() {
        List<Bullet> bullets = new ArrayList<>();
        bullets.addAll(this.bulletList);
        for (Bullet bullet : bullets) {
            bullet.removeFromGame(this);
        }
        this.bulletList.clear();
    }



    }





