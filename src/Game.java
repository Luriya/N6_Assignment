import game2D.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author David Cairns
 */
@SuppressWarnings("serial")

public class Game extends GameCore implements ActionListener {
    // Useful game constants
    private static int screenWidth = 512;
    private static int screenHeight = 384;

    private float lift = 0.005f;
    private float gravity = 0.0010f;

    // Game state flags
    private boolean jump = false;
    private boolean left = false;
    private boolean right = false;
    private boolean decelerate = false;
    private boolean canJump = true;
    private boolean touchingGround = false;
    private boolean facingRight = true;

    private int jumpsDone = 0;
    private int tileX = 0;
    private int tileY = 0;
    private int levelNumber = 1;
    private int gemsCollected = 0;
    private int totalGems = 50;
    private int lifeRemaining = 3;

    private String flagStatus = ("Red");

    // Game resources
    private Animation standing_right;
    private Animation standing_left;
    private Animation running_right;
    private Animation running_left;
    private Animation dying_right;
    private Animation dying_left;
    private Animation jumping_right;
    private Animation jumping_left;
    private Animation falling_right;
    private Animation falling_left;
    private Animation enemy_running_left;
    private Animation enemy_running_right;
    private Animation enemy_dying;
    private Animation flag_r;
    private Animation flag_g;

    private Sprite player = null;
    private Sprite enemy1 = null;
    private Sprite enemy2 = null;
    private Sprite enemy3 = null;
    private Sprite enemy4 = null;
    private Sprite flag_red = null;
    private Sprite flag_green = null;

    private TileMap tmap = new TileMap();    // Our tile map, note that we load it in init()

    private Image bg;
    private Image fg;
    private Image heart1;
    private Image heart2;
    private Image heart3;

    int yT;
    int xT;
    int xB;
    int yB;
    int xR;
    int yR;
    int xL;
    int yL;

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static STATE State = STATE.MENU;
    private Menu menu;
    private Dead dead;
    private Help help;
    private Complete complete;

    /**
     * The obligatory main method that creates
     * an instance of our class and starts it running
     *
     * @param args The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        Game gct = new Game();
        gct.init("map1.txt");
//        Sound theme = new Sound("sounds/theme.wav");//play jumping noise
//        theme.playTheme();
        // Start in windowed mode with the given screen height and width
        gct.run(false, screenWidth, screenHeight);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame() {

        gemsCollected = 0;
        lifeRemaining = 3;

        if (levelNumber == 1) {
            // Load the tile map and print it out so we can check it is valid
            tmap.loadMap("maps", "map1.txt");

            totalGems = 57;

            player.setX(tmap.getTileXC(3, 11));
            player.setY(tmap.getTileYC(3, 11));
            player.setVelocityX(0);
            player.setVelocityY(0);

            enemy1.setSpawnX(tmap.getTileXC(5, 5));
            enemy2.setSpawnX(tmap.getTileXC(9, 8));
            enemy3.setSpawnX(tmap.getTileXC(12, 10));
            enemy4.setSpawnX(tmap.getTileXC(38, 4));

            enemy1.setMaxPatrol(enemy1.getSpawnX() + 10);
            enemy2.setMaxPatrol(enemy2.getSpawnX() + 40);
            enemy3.setMaxPatrol(enemy3.getSpawnX() + 10);
            enemy4.setMaxPatrol(enemy4.getSpawnX() + 10);

            enemy1.setMinPatrol(enemy1.getSpawnX() - 30);
            enemy2.setMinPatrol(enemy2.getSpawnX() - 30);
            enemy3.setMinPatrol(enemy3.getSpawnX() - 30);
            enemy4.setMinPatrol(enemy4.getSpawnX() - 30);

            enemy1.setSpawnY(tmap.getTileYC(5, 5));
            enemy2.setSpawnY(tmap.getTileYC(9, 8));
            enemy3.setSpawnY(tmap.getTileYC(12, 10));
            enemy4.setSpawnY(tmap.getTileYC(38, 4));

            flag_red.setX(tmap.getTileXC(3, 10));
            flag_red.setY(tmap.getTileYC(3, 10));

            flag_green.setX(tmap.getTileXC(62, 6));
            flag_green.setY(tmap.getTileYC(62, 6));
        } else if (levelNumber == 2) {
            // Load the tile map and print it out so we can check it is valid
            tmap.loadMap("maps", "map2.txt");

            totalGems = 69;

            player.setX(tmap.getTileXC(2, 3));
            player.setY(tmap.getTileYC(2, 3));
            player.setVelocityX(0);
            player.setVelocityY(0);

            enemy1.setSpawnX(tmap.getTileXC(34, 12));
            enemy2.setSpawnX(tmap.getTileXC(11, 15));
            enemy3.setSpawnX(tmap.getTileXC(35, 19));
            enemy4.setSpawnX(tmap.getTileXC(58, 14));

            enemy1.setMaxPatrol(enemy1.getSpawnX() + 60);
            enemy2.setMaxPatrol(enemy2.getSpawnX() + 40);
            enemy3.setMaxPatrol(enemy3.getSpawnX() + 60);
            enemy4.setMaxPatrol(enemy4.getSpawnX() + 50);

            enemy1.setMinPatrol(enemy1.getSpawnX() - 100);
            enemy2.setMinPatrol(enemy2.getSpawnX() - 40);
            enemy3.setMinPatrol(enemy3.getSpawnX() - 100);
            enemy4.setMinPatrol(enemy4.getSpawnX() - 40);

            enemy1.setSpawnY(tmap.getTileYC(34, 12));
            enemy2.setSpawnY(tmap.getTileYC(11, 15));
            enemy3.setSpawnY(tmap.getTileYC(35, 19));
            enemy4.setSpawnY(tmap.getTileYC(58, 14));

            flag_red.setX(tmap.getTileXC(2, 3));
            flag_red.setY(tmap.getTileYC(2, 3));

            flag_green.setX(tmap.getTileXC(61, 3));
            flag_green.setY(tmap.getTileYC(61, 3));
        } else if (levelNumber == 3) {
            // Load the tile map and print it out so we can check it is valid
            tmap.loadMap("maps", "map3.txt");

            totalGems = 46;

            player.setX(tmap.getTileXC(2, 9));
            player.setY(tmap.getTileYC(2, 9));
            player.setVelocityX(0);
            player.setVelocityY(0);

            enemy1.setSpawnX(tmap.getTileXC(5, 5));
            enemy2.setSpawnX(tmap.getTileXC(16, 20));
            enemy3.setSpawnX(tmap.getTileXC(12, 5));
            enemy4.setSpawnX(tmap.getTileXC(54, 20));

            enemy1.setMaxPatrol(enemy1.getSpawnX() + 50);
            enemy2.setMaxPatrol(enemy2.getSpawnX() + 120);
            enemy3.setMaxPatrol(enemy3.getSpawnX() + 40);
            enemy4.setMaxPatrol(enemy4.getSpawnX() + 50);

            enemy1.setMinPatrol(enemy1.getSpawnX() - 30);
            enemy2.setMinPatrol(enemy2.getSpawnX() - 200);
            enemy3.setMinPatrol(enemy3.getSpawnX() - 50);
            enemy4.setMinPatrol(enemy4.getSpawnX() - 80);

            enemy1.setSpawnY(tmap.getTileYC(5, 5));
            enemy2.setSpawnY(tmap.getTileYC(16, 20));
            enemy3.setSpawnY(tmap.getTileYC(12, 10));
            enemy4.setSpawnY(tmap.getTileYC(54, 20));

            flag_red.setX(tmap.getTileXC(2, 8));
            flag_red.setY(tmap.getTileYC(2, 8));

            flag_green.setX(tmap.getTileXC(61, 3));
            flag_green.setY(tmap.getTileYC(61, 3));
        }
    }

    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers
     */
    public void init(String map) {
        Sprite s;    // Temporary reference to a sprite

        //bg = loadImage("images/bg.png");
        bg = loadImage("images/bg.png").getScaledInstance(728, 455, Image.SCALE_DEFAULT);

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", map);

        // Create a set of background sprites that we can
        // rearrange to give the illusion of motion

        running_right = new Animation();
        running_right.loadAnimationFromSheet("images/Animations/anim_running_right.png", 4, 1, 120);
        running_left = new Animation();
        running_left.loadAnimationFromSheet("images/Animations/anim_running_left.png", 4, 1, 120);
        standing_right = new Animation();
        standing_right.loadAnimationFromSheet("images/Animations/anim_standing_right.png", 4, 1, 600);
        standing_left = new Animation();
        standing_left.loadAnimationFromSheet("images/Animations/anim_standing_left.png", 4, 1, 600);
        dying_right = new Animation();
        dying_right.loadAnimationFromSheet("images/Animations/anim_dying_right.png", 1, 1, 6000);
        dying_left = new Animation();
        dying_left.loadAnimationFromSheet("images/Animations/anim_dying_left.png", 1, 1, 6000);
        jumping_right = new Animation();
        jumping_right.loadAnimationFromSheet("images/Animations/anim_jumping_right.png", 1, 1, 6000);
        jumping_left = new Animation();
        jumping_left.loadAnimationFromSheet("images/Animations/anim_jumping_left.png", 1, 1, 6000);
        falling_right = new Animation();
        falling_right.loadAnimationFromSheet("images/Animations/anim_falling_right.png", 1, 1, 6000);
        falling_left = new Animation();
        falling_left.loadAnimationFromSheet("images/Animations/anim_falling_left.png", 1, 1, 6000);

        enemy_running_left = new Animation();
        enemy_running_left.loadAnimationFromSheet("images/Animations/anim_enemy_running_left.png", 6, 1, 600);
        enemy_running_right = new Animation();
        enemy_running_right.loadAnimationFromSheet("images/Animations/anim_enemy_running_right.png", 6, 1, 600);
        enemy_dying = new Animation();
        enemy_dying.loadAnimationFromSheet("images/Animations/anim_enemy_dying.png", 2, 1, 60);

        flag_r = new Animation();
        flag_r.loadAnimationFromSheet("images/finish_red.png", 1, 1, 60);

        flag_g = new Animation();
        flag_g.loadAnimationFromSheet("images/finish_green.png", 1, 1, 60);

        // Initialise the player with an animation
        player = new Sprite(standing_right);

        enemy1 = new Sprite(enemy_running_right);
        enemy2 = new Sprite(enemy_running_right);
        enemy3 = new Sprite(enemy_running_right);
        enemy4 = new Sprite(enemy_running_right);

        flag_red = new Sprite(flag_r);
        flag_green = new Sprite(flag_g);

        menu = new Menu();
        dead = new Dead();
        help = new Help();
        complete = new Complete();

        initialiseGame();

        System.out.println(tmap);
        fg = loadImage("images/clouds.png").getScaledInstance(1920, 1080, Image.SCALE_DEFAULT);

        heart1 = loadImage("images/Heart.png").getScaledInstance(25, 22, Image.SCALE_DEFAULT);
        heart2 = heart1;
        heart3 = heart1;
    }

    /**
     * Update any sprites and check for collisions
     *
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */
    public void update(long elapsed) {
        if (State == STATE.GAME) {

            ArrayList<Sprite> sprites = new ArrayList<Sprite>();
            sprites.add(player);
            sprites.add(enemy1);
            sprites.add(enemy2);
            sprites.add(enemy3);
            sprites.add(enemy4);

            for (Sprite s : sprites) {
                tileX = (int) (s.getX() / tmap.getTileWidth()); //TODO: Fix this - something wrong here that prevents me from doing collision properly - drop off left side of tile0 when entering tile -2..
                tileY = (int) ((s.getY() + s.getHeight()) / tmap.getTileHeight());

                if (tmap.getTileChar(tileX, tileY - 1) == '/' || tmap.getTileChar(tileX, tileY - 1) == '\\' || tmap.getTileChar(tileX, tileY - 1) == '?' || tmap.getTileChar(tileX, tileY - 1) == '|') {
                    s.setVelocityY(0);
                    touchingGround = true;
                } else {
                    s.setVelocityY(s.getVelocityY() + (gravity * elapsed));
                }
            }

            player.setAnimationSpeed(1.0f);

            checkTileCollision(player);


            if (!touchingGround && player.getVelocityY() > 0) {
                if (player.getPlayerDirection()) {
                    player.setAnimation(falling_right);
                } else {
                    player.setAnimation(falling_left);
                }
            }
            else if (!touchingGround && player.getVelocityY() < 0) {
                if (player.getPlayerDirection()) {
                    player.setAnimation(jumping_right);
                } else {
                    player.setAnimation(jumping_left);
                }
            }
            else if (touchingGround) {
                player.setAnimation(standing_right);
            }

            if (left) {
                player.setVelocityX(-0.2f);
                if (touchingGround) {
                    player.setAnimation(running_left);
                }

            }
            if (right) {
                player.setVelocityX(0.2f);
                if (touchingGround) {
                    player.setAnimation(running_right);
                }

            }

            if (jump && canJump && touchingGround) {
                player.setAnimation(jumping_right);
                touchingGround = false;
                if (jumpsDone < 1) {//only allows for 2 jumps per landing i.e the player must land before they jump again
                    if (player.getVelocityY() >= 0) {//only allow jump when trajectory is downwards
                        player.setVelocityY(-0.50f);
                        player.shiftY(-0.01f);
                        jump = false;//reset up flag
                        Sound jump = new Sound("sounds/jump.wav");//play jumping noise
                        jump.start();
                        jumpsDone++;//increment jumps done this jump
                    }
                } else {
                    canJump = false;
                }
            }

            if (decelerate) {
                player.setVelocityX(player.getVelocityX() * 0.9f);
                if (player.getVelocityX() <= 0.01f && player.getVelocityX() >= -0.01f) {
                    player.setVelocityX(0);
                    decelerate = false;
                }
            }

            //collect the enemies into an ArrayList for simple processing
            ArrayList<Sprite> enemies = new ArrayList<Sprite>();
            enemies.add(enemy1);
            enemies.add(enemy2);
            enemies.add(enemy3);
            enemies.add(enemy4);

            for (Sprite enemy : enemies) {//for each enemy
                if ((enemy.getX() > enemy.getMaxPatrol() && enemy.getDirection()) || (enemy.getX() < enemy.getMinPatrol() && !enemy.getDirection())) {//if the enemy is at the edge of the patrol area
                    enemy.setDirection(!enemy.getDirection());//invert the direction boolean
                }
                if (enemy.getDirection()) {//if the enemy is moving right
                    enemy.setVelocityX(0.02f);
                    enemy.setAnimation(enemy_running_right);
                } else {//if the enemy is moving left
                    enemy.setVelocityX(-0.02f);
                    enemy.setAnimation(enemy_running_left);
                }
                enemy.update(elapsed);
            }

            // Now update the sprites animation and position
            for (Sprite s : sprites) {
                s.update(elapsed);
            }

            for (Sprite s : sprites) {
                handleTileMapCollisions(s, elapsed);
            }

            //check for sprite collisions
            handleSpriteCollisions();

            if (lifeRemaining == 0) {
                Sound sound = new Sound("fail.wav");
                sound.echo("fail.wav");
                System.out.println("You died!");
                levelNumber = 1;
                resetVariables();
                Game.State = Game.STATE.DEAD;
            }
        }
    }

    /**
     * Draw the current state of the game
     */
    public void draw(Graphics2D g) {
        // Be careful about the order in which you draw objects - you
        // should draw the background first, then work your way 'forward'

        // First work out how much we need to shift the view
        // in order to see where the player is.
        int xo = (int) -player.getX() + 100;
        int yo = (int) -player.getY() + 250;

        // If relative, adjust the offset so that
        // it is relative to the player

        for (int y = 0; y < tmap.getMapHeight(); y += bg.getHeight(null)) {
            for (int x = 0; x < tmap.getMapWidth(); x += bg.getWidth(null)) {
                g.drawImage(bg, xo / 10, yo / 10, null);
            }
        }

        if (State == STATE.GAME) {

            ArrayList<Sprite> sprites = new ArrayList<Sprite>();
            sprites.add(player);
            sprites.add(enemy1);
            sprites.add(enemy2);
            sprites.add(enemy3);
            sprites.add(enemy4);

            for (Sprite s : sprites) {
                s.setOffsets(xo, yo);
                checkOnScreen(g, s, xo, yo);
            }

            flag_red.setOffsets(xo, yo);
            flag_green.setOffsets(xo, yo);
            flag_red.draw(g);
            flag_green.draw(g);
            flag_green.hide();
            flag_red.show();

            // Apply offsets to tile map and draw  it
            tmap.draw(g, xo, yo);

            g.drawImage(fg, xo * 2, yo * 2 - 320, null);
            g.drawImage(fg, xo * 2 + fg.getWidth(null), yo * 2 - 320, null);
            //redrawImage(g, fg, xo, yo, fg.getWidth(null), fg.getHeight(null));

            // Show score and status information
            String msg = String.format("Score: %d / %d", gemsCollected, totalGems);
            g.setColor(Color.darkGray);
            g.drawString(msg, getWidth() - 80, 50);

            String msg2 = String.format("Flag Status: " + flagStatus);
            g.setColor(Color.darkGray);
            g.drawString(msg2, (getWidth() / 2) - 50, 50);

            int frames = (int) getFPS();
            String framerate = String.format(frames + "fps");
            g.setColor(Color.black);
            g.drawString(framerate, (getWidth() / 2) - 20, 70);

            ArrayList<Image> life = new ArrayList<>();
            life.add(heart1);
            life.add(heart2);
            life.add(heart3);

            int x = 20;
            int y = 40;

            for (int i = 0; i < lifeRemaining; i++) {
                g.drawImage(life.get(i), x, y, null);
                x = x + 30;
            }
            g.setColor(Color.yellow);
            Rectangle2D bounds = new Rectangle2D.Float(player.getX() + xo, player.getY() + yo, player.getWidth(), player.getHeight());
            g.draw(bounds);
        } else if (State == STATE.MENU) {
            this.addMouseListener(new Menu());
            menu.render(g);
        } else if (State == STATE.DEAD) {
            this.addMouseListener(new Dead());
            dead.render(g);
        } else if (State == STATE.HELP) {
            this.addMouseListener(new Help());
            help.render(g);
        } else if (State == STATE.COMPLETE) {
            this.addMouseListener(new Complete());
            complete.render(g);
        }
    }

    public void checkOnScreen(Graphics2D g, Sprite s, int xo, int yo) //TODO: Ask Dr Cairns about this or fix tomorrow - patrolling sprites don't render on screen until their whole patrol is in range - pop-in
    {
        g.setColor(Color.pink);
        Rectangle rect = (Rectangle) g.getClip();
        int xc, yc;

        xc = (int) (xo + s.getX());
        yc = (int) (yo + s.getY());

        if (rect.contains(xc, yc)) {
            s.show();
            s.draw(g);
        } else {
            s.hide();
        }
    }

    public void resetVariables() {
        gemsCollected = 0;
        lifeRemaining = 3;
        init("map1.txt");

        totalGems = 57;

        player.setX(tmap.getTileXC(3, 11));
        player.setY(tmap.getTileYC(3, 11));
        player.setVelocityX(0);
        player.setVelocityY(0);

        enemy1.setSpawnX(tmap.getTileXC(5, 5));
        enemy2.setSpawnX(tmap.getTileXC(9, 8));
        enemy3.setSpawnX(tmap.getTileXC(12, 10));
        enemy4.setSpawnX(tmap.getTileXC(38, 4));

        enemy1.setMaxPatrol(enemy1.getSpawnX() + 10);
        enemy2.setMaxPatrol(enemy2.getSpawnX() + 40);
        enemy3.setMaxPatrol(enemy3.getSpawnX() + 10);
        enemy4.setMaxPatrol(enemy4.getSpawnX() + 10);

        enemy1.setMinPatrol(enemy1.getSpawnX() - 30);
        enemy2.setMinPatrol(enemy2.getSpawnX() - 30);
        enemy3.setMinPatrol(enemy3.getSpawnX() - 30);
        enemy4.setMinPatrol(enemy4.getSpawnX() - 30);

        enemy1.setSpawnY(tmap.getTileYC(5, 5));
        enemy2.setSpawnY(tmap.getTileYC(9, 8));
        enemy3.setSpawnY(tmap.getTileYC(12, 10));
        enemy4.setSpawnY(tmap.getTileYC(38, 4));

        flag_red.setX(tmap.getTileXC(3, 10));
        flag_red.setY(tmap.getTileYC(3, 10));

        flag_green.setX(tmap.getTileXC(62, 6));
        flag_green.setY(tmap.getTileYC(62, 6));
    }

    /**
     * Checks and handles collisions with the tile map for the
     * given sprite 's'. Initial functionality is limited...
     *
     * @param s       The Sprite to check collisions for
     * @param elapsed How time has gone by
     */
    public void handleTileMapCollisions(Sprite s, long elapsed) {

        tileX = (int) (s.getX() / tmap.getTileWidth()); //TODO: Fix this - something wrong here that prevents me from doing collision properly - drop off left side of tile0 when entering tile -2..
        tileY = (int) ((s.getY() + s.getHeight()) / tmap.getTileHeight());

        int xc = tmap.getTileXC(tileX, tileY);
        int xcc = (int) (player.getX() - xc);
        int yc = tmap.getTileYC(tileX, tileY) - tmap.getTileHeight();
        int offset = 0;

        if (tmap.getTileChar(tileX, tileY) == 'g' || tmap.getTileChar(tileX, tileY) == 's' || tmap.getTileChar(tileX, tileY) == 'k' || tmap.getTileChar(tileX, tileY) == 'q' || tmap.getTileChar(tileX, tileY) == 'p' || tmap.getTileChar(tileX, tileY) == 'u' || tmap.getTileChar(tileX, tileY) == 'o' || tmap.getTileChar(tileX, tileY) == 'k') {
            if (s.getVelocityY() > 0) {
                s.setVelocityY(0);
            }
            s.setY((float) (tileY * tmap.getTileHeight()) - s.getHeight());
            if (s.equals(player)) {
                jumpsDone = 0;
                touchingGround = true;
            }
            if (tmap.getTileChar(tileX, tileY - 1) == '/' || tmap.getTileChar(tileX, tileY - 1) == '\\' || tmap.getTileChar(tileX, tileY - 1) == '?' || tmap.getTileChar(tileX, tileY - 1) == '|') {
                s.setY((float) (tileY * tmap.getTileHeight()) - s.getHeight() - offset);
            }
        }

        //TODO: Rotation on slopes
        if (tmap.getTileChar(tileX, tileY - 1) == '/') {
            s.setVelocityY(0);
            s.setY(yc - (xcc / 2));
            s.setVelocityY(0);
            offset = xcc / 2;
        }

        if (tmap.getTileChar(tileX, tileY - 1) == '?') {
            s.setVelocityY(0);
            s.setY(yc - 16 - (xcc / 2));
            s.setVelocityY(0);
            offset = 16 - (xcc / 2);
        }

        if (tmap.getTileChar(tileX, tileY - 1) == '\\') {
            s.setVelocityY(0);
            s.setY(yc - 16 - (16 - (xcc / 2)));
            s.setVelocityY(0);
            offset = 16 - (16 - (xcc / 2));
        }

        if (tmap.getTileChar(tileX, tileY - 1) == '|') {
            s.setVelocityY(0);
            s.setY(yc - (16 - (xcc / 2)));
            s.setVelocityY(0);
            offset = 16 - (xcc / 2);
        }

        if (tmap.getTileChar(tileX, tileY) == 'l') { //TODO: If you don't touch anything before and after you get knocked back, you will slide off the map as long as you continue to not touch anything - fix?
            if (s.equals(player)) {
                Sound damage = new Sound("sounds/bwah.wav");
                damage.start();
                if (player.getPlayerDirection()) {
                    player.setAnimation(dying_right);
                    player.setVelocityY(-0.2f);
                    player.setVelocityX(-0.2f);
                    player.setX(player.getX() - 40);
                    player.setY(player.getY() - 40);
                    lifeRemaining--;
                } else {
                    player.setAnimation(dying_left);
                    player.setVelocityY(0.2f);
                    player.setVelocityX(0.2f);
                    player.setX(player.getX() + 40);
                    player.setY(player.getY() - 40);
                    lifeRemaining--;
                }
            } else {
                s.stop();
                s.hide();
            }
        }
        //bottom left, bottom right, top left, top right
        if ((tmap.getTileChar(tileX, tileY - 1) == '1') && s.equals(player)) {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected++;
            tmap.setTileChar('.', tileX, tileY - 1);
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == '2') && s.equals(player)) {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected = gemsCollected + 2;
            tmap.setTileChar('.', tileX, tileY - 1);
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == '3') && s.equals(player)) {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected = gemsCollected + 3;
            tmap.setTileChar('.', tileX, tileY - 1);
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == '4') && s.equals(player)) {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected = gemsCollected + 4;
            tmap.setTileChar('.', tileX, tileY - 1);
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == '5') && s.equals(player)) {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected = gemsCollected + 5;
            tmap.setTileChar('.', tileX, tileY - 1);
        }

        if (gemsCollected >= totalGems / 2) {
            flagStatus = ("Green");
            flag_red.hide();
            flag_green.show();
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == 'h') && s.equals(player)) {
            if (lifeRemaining < 3) {
                tmap.setTileChar('.', tileX, tileY - 1);
                lifeRemaining++;
            }
        }

    }

    public void checkTileCollision(Sprite sprite) {

        xT = (int) ((sprite.getX() / tmap.getTileWidth()) + 0.5);
        yT = (int) (sprite.getY() / tmap.getTileHeight());

        xB = (int) (sprite.getX() / tmap.getTileWidth() + 0.5);
        yB = (int) ((sprite.getY() + sprite.getHeight()) / tmap.getTileHeight() - 1.0);

        xR = (int) (sprite.getX() / tmap.getTileWidth() + 0.5);
        yR = (int) ((sprite.getY() + sprite.getHeight()) / tmap.getTileHeight() - 0.75);

        xL = (int) (sprite.getX() / tmap.getTileWidth());
        yL = (int) ((sprite.getY() + sprite.getHeight()) / tmap.getTileHeight() - 0.75);

        if ((tmap.getTileChar(xT, yT) == 'g' || tmap.getTileChar(xT, yT) == 'u' || tmap.getTileChar(xT, yT) == 'w' || tmap.getTileChar(xT, yT) == 's' || tmap.getTileChar(xT, yT) == 'o' || tmap.getTileChar(xT, yT) == 'k' || tmap.getTileChar(xT, yT) == 'q' || tmap.getTileChar(xT, yT) == 'p') && sprite.getVelocityY() > 0) {
            sprite.setVelocityY(0);
        }
        if ((tmap.getTileChar(xR, yR) == 'g' || tmap.getTileChar(xR, yR) == 'u' || tmap.getTileChar(xR, yR) == 'w' || tmap.getTileChar(xR, yR) == 's' || tmap.getTileChar(xR, yR) == 'o' || tmap.getTileChar(xR, yR) == 'k') && sprite.getVelocityX() > 0) {
            sprite.setVelocityX(0);
            sprite.setX(xR * tmap.getTileWidth() - sprite.getImage().getWidth(null));
        }
        if ((tmap.getTileChar(xL, yL) == 'g' || tmap.getTileChar(xL, yL) == 'u' || tmap.getTileChar(xL, yL) == 'w' || tmap.getTileChar(xL, yL) == 's' || tmap.getTileChar(xL, yL) == 'o' || tmap.getTileChar(xL, yL) == 'k') && sprite.getVelocityX() < 0) {
            sprite.setVelocityX(0);
            sprite.setX(xL * tmap.getTileWidth() + tmap.getTileWidth());
        }
        if (tmap.getTileChar(xB, yB) == 'g' || tmap.getTileChar(xB, yB) == 'u' || tmap.getTileChar(xB, yB) == 'w' || tmap.getTileChar(xB, yB) == 's' || tmap.getTileChar(xB, yB) == 'o' || tmap.getTileChar(xB, yB) == 'k' || tmap.getTileChar(xB, yB) == 'q' || tmap.getTileChar(xB, yB) == 'p') {
            sprite.setVelocityY(0);
            sprite.shiftY(2);
        }
    }

    private void handleSpriteCollisions() {

        if (State == STATE.GAME) {
            ArrayList<Sprite> enemies = new ArrayList<Sprite>();
            enemies.add(enemy1);
            enemies.add(enemy2);
            enemies.add(enemy3);
            enemies.add(enemy4);

            boolean collided = false;

            for (Sprite enemy : enemies) {
                if (BoundingCircleCollision(player, enemy)) {
                    if (player.getVelocityY() > 0.2f) {
                        Sound enemyDeath = new Sound("sounds/whimper.wav");
                        enemyDeath.start();

                        enemy.stop();
                        enemy.hide();
                        enemy.setX(0);
                        enemy.setY(0);
                    } else {
                        collided = true;
                    }
                }
            }

            if (collided) {

                Sound damage = new Sound("sounds/bwah.wav");
                damage.start();

                if (lifeRemaining == 1) {
                    lifeRemaining--;
                    player.setAnimation(dying_left);
                    player.stop();
                } else {
                    if (player.getPlayerDirection()) {
                        player.setAnimation(dying_right);
                        player.setVelocityY(-0.2f);
                        player.setVelocityX(-0.2f);
                        player.setX(player.getX() - 10);
                        player.setY(player.getY() - 10);
                    } else {
                        player.setAnimation(dying_left);
                        player.setVelocityY(-0.2f);
                        player.setVelocityX(-0.2f);
                        player.setX(player.getX() + 40);
                        player.setY(player.getY() - 40);
                    }
                    lifeRemaining--;
                }

            }

            if ((BoundingCircleCollision(player, flag_green)) && flagStatus.equals("Green")) {
                Sound levelComplete = new Sound("sounds/levelComplete.wav"); //play level complete sound
                levelComplete.start();
                finishLevel();
            }
        }
    }

    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     *
     * @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (State == STATE.GAME) {
            if (key == KeyEvent.VK_ESCAPE) stop();

            if (key == KeyEvent.VK_SPACE) jump = true;

            if (key == KeyEvent.VK_LEFT) {
                left = true;
            }

            if (key == KeyEvent.VK_RIGHT) {
                right = true;
            }

            if (key == KeyEvent.VK_1) {
                levelNumber = 1;
                init("map1.txt");
                initialiseGame();
            }

            if (key == KeyEvent.VK_2) {
                levelNumber = 2;
                init("map2.txt");
                initialiseGame();
            }

            if (key == KeyEvent.VK_3) {
                levelNumber = 3;
                init("map3.txt");
                initialiseGame();
            }

            if (key == KeyEvent.VK_Q) Game.State = Game.STATE.MENU;
        }

    }

    public void finishLevel() {
        if (levelNumber == 1) {
            levelNumber++;
            System.out.println("Level 1 Complete!");
            gemsCollected = 0;
            flagStatus = "Red";
            init("map2.txt");
            initialiseGame();
        } else if (levelNumber == 2) {
            levelNumber++;
            System.out.println("Level 2 Complete!");
            gemsCollected = 0;
            flagStatus = "Red";
            init("map3.txt");
            initialiseGame();
        } else if (levelNumber == 3) {
            levelNumber = 1;
            System.out.println("The End");
            gemsCollected = 0;
            flagStatus = "Red";
            Game.State = Game.STATE.MENU;
        }
    }

    public boolean BoundingCircleCollision(Sprite one, Sprite two) {
        int dx, dy, minimum;
        dx = ((int) one.getX() + one.getWidth() / 2) - ((int) two.getX() + two.getWidth() / 2);
        dy = ((int) one.getY() + one.getHeight() / 2) - ((int) two.getY() + two.getHeight() / 2);
        minimum = one.getWidth() / 2 + two.getWidth() / 2;
        return (((dx * dx) + (dy * dy)) < (minimum * minimum));
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        // Switch statement instead of lots of ifs...
        // Need to use break to prevent fall through.
        switch (key) {
            case KeyEvent.VK_ESCAPE:
                stop();
                break;
            case KeyEvent.VK_SPACE:
                jump = false;
                canJump = true;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                facingRight = false;
                decelerate = true;
                break;
            case KeyEvent.VK_RIGHT:
                right = false;
                facingRight = true;
                decelerate = true;
                break;
            default:
                break;
        }
    }

    public enum STATE {
        MENU,
        GAME,
        HELP,
        DEAD,
        COMPLETE
    }
}
