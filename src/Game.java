import game2D.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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

public class Game extends GameCore implements ActionListener
{
    private float lift = 0.005f; // lift (counteracts gravity)

    // Game state flags
    private boolean jump = false;
    private boolean left = false;
    private boolean right = false;
    private boolean decelerate = false;
    private boolean canJump = true;
    private boolean touchingGround = false;

    // Integer values
    private int jumpsDone = 0; // no. jumps performed this jump
    private int levelNumber = 1; // the current level the player is on
    private int gemsCollected = 0; // the number of gems the player has collected
    private int totalGems = 0; // the total number of gems in the level
    private int lifeRemaining = 3; // the amount of life the player has remaining

    private String flagStatus = ("Red"); // the current status of the flag, i.e. can the player finish the level

    // Animations (typically here you would use setScale for the left versions of animations, was unable to get it working)
    private Animation standing_right;
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

    // Sprites
    private Sprite player = null;
    private Sprite enemy1 = null;
    private Sprite enemy2 = null;
    private Sprite enemy3 = null;
    private Sprite enemy4 = null;
    private Sprite flag_red = null;
    private Sprite flag_green = null;

    // The game TileMap
    private final TileMap tmap = new TileMap();    // Our tile map, note that we load it in init()

    // Images
    private Image bg; // The background image
    private Image fg; // The foreground image
    private Image heart1; // 3 individual heart images for the player's remaining life
    private Image heart2;
    private Image heart3;

    // Ints representing the positions (in tiles) of the tiles surrounding the player
    int yT; // Tile above the player
    int xT;
    int xB; // Tile below the player
    int yB;
    int xR; // Tile to the right of the player
    int yR;
    int xL; // Tile to the left of the player
    int yL;

    // Unused, just here to stop IntelliJ complaining!
    @Override
    public void actionPerformed(ActionEvent e)
    {

    }

    // Various menu-type screens to improve UX
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
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException
    {
        Game gct = new Game(); // Create a new instance of Game
        gct.init("map1.txt"); // load the first map
        Sound theme = new Sound("sounds/theme.wav"); // load theme
        theme.playTheme(); // play theme
        // Start in windowed mode with the given screen height and width
        // Useful game constants
        // width of the screen
        int screenWidth = 512;
        // height of the screen
        int screenHeight = 384;
        gct.run(false, screenWidth, screenHeight);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame()
    {

        gemsCollected = 0; // reset variables
        lifeRemaining = 3;

        if (levelNumber == 1) // if on level 1
        {
            // Load the tile map and print it out so we can check it is valid
            tmap.loadMap("maps", "map1.txt");

            totalGems = 57; // assign number of gems in this level

            player.setX(tmap.getTileXC(3, 11)); // get x & y coordinates of this tile
            player.setY(tmap.getTileYC(3, 11));
            player.setVelocityX(0); // set velocities to 0
            player.setVelocityY(0);

            // set up spawn positions for the enemies
            enemy1.setSpawnX(tmap.getTileXC(5, 5));
            enemy2.setSpawnX(tmap.getTileXC(9, 8));
            enemy3.setSpawnX(tmap.getTileXC(12, 10));
            enemy4.setSpawnX(tmap.getTileXC(38, 4));

            enemy1.setSpawnY(tmap.getTileYC(5, 5));
            enemy2.setSpawnY(tmap.getTileYC(9, 8));
            enemy3.setSpawnY(tmap.getTileYC(12, 10));
            enemy4.setSpawnY(tmap.getTileYC(38, 4));

            // set min and max patrol ranges for enemies - so they can move back and forth
            enemy1.setMaxPatrol(enemy1.getSpawnX() + 10);
            enemy2.setMaxPatrol(enemy2.getSpawnX() + 40);
            enemy3.setMaxPatrol(enemy3.getSpawnX() + 10);
            enemy4.setMaxPatrol(enemy4.getSpawnX() + 10);

            enemy1.setMinPatrol(enemy1.getSpawnX() - 30);
            enemy2.setMinPatrol(enemy2.getSpawnX() - 30);
            enemy3.setMinPatrol(enemy3.getSpawnX() - 30);
            enemy4.setMinPatrol(enemy4.getSpawnX() - 30);

            // set the spawn points of the red and green flag (start and finish)
            flag_red.setX(tmap.getTileXC(3, 10));
            flag_red.setY(tmap.getTileYC(3, 10));

            flag_green.setX(tmap.getTileXC(62, 6));
            flag_green.setY(tmap.getTileYC(62, 6));
        }
        else if (levelNumber == 2) // if on level 2
        {
            // similar setup as above

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
        }
        else if (levelNumber == 3) // if on level 3
        {
            // similar setup as above

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
     * @param map The map to be loaded
     */
    public void init(String map)
    {
        Sprite s;    // Temporary reference to a sprite

        bg = loadImage("images/bg.png").getScaledInstance(728, 455, Image.SCALE_DEFAULT); // load background image

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", map); // load the correct map

        // Loading player animations
        running_right = new Animation();
        running_right.loadAnimationFromSheet("images/Animations/anim_running_right.png", 4, 1, 120);
        running_left = new Animation();
        running_left.loadAnimationFromSheet("images/Animations/anim_running_left.png", 4, 1, 120);
        standing_right = new Animation();
        standing_right.loadAnimationFromSheet("images/Animations/anim_standing_right.png", 4, 1, 600);
        Animation standing_left = new Animation();
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

        // Loading enemy animations
        enemy_running_left = new Animation();
        enemy_running_left.loadAnimationFromSheet("images/Animations/anim_enemy_running_left.png", 6, 1, 400);
        enemy_running_right = new Animation();
        enemy_running_right.loadAnimationFromSheet("images/Animations/anim_enemy_running_right.png", 6, 1, 400);
        Animation enemy_dying = new Animation();
        enemy_dying.loadAnimationFromSheet("images/Animations/anim_enemy_dying.png", 2, 1, 60);

        // Flag animations
        Animation flag_r = new Animation();
        flag_r.loadAnimationFromSheet("images/finish_red.png", 1, 1, 60);

        Animation flag_g = new Animation();
        flag_g.loadAnimationFromSheet("images/finish_green.png", 1, 1, 60);

        // Initialise sprites with animations
        player = new Sprite(standing_right);

        enemy1 = new Sprite(enemy_running_right);
        enemy2 = new Sprite(enemy_running_right);
        enemy3 = new Sprite(enemy_running_right);
        enemy4 = new Sprite(enemy_running_right);

        flag_red = new Sprite(flag_r);
        flag_green = new Sprite(flag_g);

        // Initialise the various menu screens
        menu = new Menu();
        dead = new Dead();
        help = new Help();
        complete = new Complete();

        // Initialise game
        initialiseGame();

        // Print the tilemap to the console
        System.out.println(tmap);

        fg = loadImage("images/clouds.png").getScaledInstance(1920, 1080, Image.SCALE_DEFAULT); // Load foreground image

        heart1 = loadImage("images/Heart.png").getScaledInstance(25, 22, Image.SCALE_DEFAULT); // Load health images
        heart2 = heart1;
        heart3 = heart1;
    }

    /**
     * Update any sprites and check for collisions
     *
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */
    public void update(long elapsed)
    {
        if (State == STATE.GAME)  // If in the game state, i.e. in a level..
        {

            // Add sprites to an array list for easier processing
            ArrayList<Sprite> sprites = new ArrayList<>();
            sprites.add(player);
            sprites.add(enemy1);
            sprites.add(enemy2);
            sprites.add(enemy3);
            sprites.add(enemy4);

            for (Sprite s : sprites)
            {
                // to control gravity/falling
                float gravity = 0.0010f;
                s.setVelocityY(s.getVelocityY() + (gravity * elapsed)); // readjust velocity due to gravity
            }

            player.setAnimationSpeed(1.0f); // set animation speed for the player

            checkTileCollision(player); // check for further tile collisions


            if (!touchingGround && player.getVelocityY() > 0)  // if not touching the ground and moving down
            {
                if (player.getPlayerDirection()) // if moving right
                {
                    player.setAnimation(falling_right);
                }
                else // if moving left
                {
                    player.setAnimation(falling_left);
                }
            }
            else if (!touchingGround && player.getVelocityY() < 0) // if not touching the ground and moving up
            {
                if (player.getPlayerDirection()) // if moving right
                {
                    player.setAnimation(jumping_right);
                }
                else // if moving left
                {
                    player.setAnimation(jumping_left);
                }
            }
            else if (touchingGround) // if touching the ground
            {
                player.setAnimation(standing_right); // use standing animation by default, could be overridden by code below
            }

            if (left) // if moving left
            {
                player.setVelocityX(-0.2f); // move player to the left
                if (touchingGround)
                {
                    player.setAnimation(running_left);
                }

            }
            if (right) // if moving right
            {
                player.setVelocityX(0.2f); // move player to the right
                if (touchingGround)
                {
                    player.setAnimation(running_right);
                }

            }

            if (jump && canJump && touchingGround) // if the player is in a position to be able to jump..
            {
                touchingGround = false; // player is no longer touching the ground
                if (jumpsDone < 1) // prevents player from jumping more than once
                {
                    if (player.getVelocityY() >= 0) // player must be moving down in order to jump
                    {
                        player.setVelocityY(-0.50f); // move the player up
                        player.shiftY(-0.01f); // adjust y coordinate slightly
                        jump = false; // reset jump flag
                        Sound jump = new Sound("sounds/jump.wav"); // play jump SE
                        jump.start(); // start thread
                        jumpsDone++; // increment the number of jumps done
                    }
                }
                else
                {
                    canJump = false; // prevent the player from jumping more until the flag is reset
                }
            }

            if (decelerate) // if the player lets go of a movement key, we want them to slow down gradually rather than stop dead..
            {
                player.setVelocityX(player.getVelocityX() * 0.9f); // set velocity to 90% of the current value
                if (player.getVelocityX() <= 0.01f && player.getVelocityX() >= -0.01f) // using this method the player could be moving at 0.00000001 pixels per frame and the deceleration would never stop, so need this condition to stop eventually
                {
                    player.setVelocityX(0); // reset velocity to 0
                    decelerate = false; // set flag to false to break loop
                }
            }

            // Add sprites to an array list for easier processing
            ArrayList<Sprite> enemies = new ArrayList<>();
            enemies.add(enemy1);
            enemies.add(enemy2);
            enemies.add(enemy3);
            enemies.add(enemy4);

            for (Sprite enemy : enemies)
            {
                if ((enemy.getX() > enemy.getMaxPatrol() && enemy.getDirection()) || (enemy.getX() < enemy.getMinPatrol() && !enemy.getDirection()))  // if the enemy hits the patrol area edge
                {
                    enemy.setDirection(!enemy.getDirection()); // Change the direction boolean
                }
                if (enemy.getDirection()) // if moving right
                {
                    enemy.setVelocityX(0.02f); // move sprite to the right
                    enemy.setAnimation(enemy_running_right); // change animation accordingly
                }
                else // if moving left
                {
                    enemy.setVelocityX(-0.02f); // move sprite to the left
                    enemy.setAnimation(enemy_running_left); // change animation accordingly
                }
                enemy.update(elapsed); // call update method on the enemies //TODO: Maybe unnecessary code?
            }

            // Update animations and positions
            for (Sprite s : sprites)
            {
                s.update(elapsed);
            }

            // Check for tile map collisions
            for (Sprite s : sprites)
            {
                handleTileMapCollisions(s, elapsed);
            }

            // Check for sprite collisions
            handleSpriteCollisions();

            if (lifeRemaining == 0)
            {
                Sound sound = new Sound("fail.wav"); // load fail/death sound
                sound.echo("fail.wav"); // call the echo method on the sound
                System.out.println("You died!"); // inform user that they died
                levelNumber = 1; // reset level number to 1
                resetVariables(); // reset all variables
                Game.State = Game.STATE.DEAD; // Change game state to the 'Dead' state
            }
        }
    }

    /**
     * Draw the current state of the game
     *
     * @param g the graphics object to draw to
     */
    public void draw(Graphics2D g) {

        int xo = (int) -player.getX() + 100; // offsets for the 'camera' view of the game based on the player's position
        int yo = (int) -player.getY() + 250;

        // Draw the background image within the bounds of the map size
        for (int y = 0; y < tmap.getMapHeight(); y += bg.getHeight(null))
        {
            for (int x = 0; x < tmap.getMapWidth(); x += bg.getWidth(null))
            {
                g.drawImage(bg, xo / 10, yo / 10, null); // draw image
            }
        }

        if (State == STATE.GAME) // If in the 'Game' state
        {

            // Add sprites to an array list for easier processing
            ArrayList<Sprite> sprites = new ArrayList<>();
            sprites.add(player);
            sprites.add(enemy1);
            sprites.add(enemy2);
            sprites.add(enemy3);
            sprites.add(enemy4);

            for (Sprite s : sprites)
            {
                s.setOffsets(xo, yo); // set the offsets for each sprite
                checkOnScreen(g, s, xo, yo); // check if they are on screen, drawing them only if they are (saves resources)
            }

            flag_red.setOffsets(xo, yo); // set flag offsets
            flag_green.setOffsets(xo, yo);
            flag_red.draw(g); // draw the green and red flags
            flag_green.draw(g);
            flag_green.hide(); // hide the green flag for now
            flag_red.show(); // show the red flag

            tmap.draw(g, xo, yo); // Draw the tilemap, adding in the offset values

            // Draw the foreground image in two different tiled positions
            g.drawImage(fg, xo * 2, yo * 2 - 320, null);
            g.drawImage(fg, xo * 2 + fg.getWidth(null), yo * 2 - 320, null);

            // Show score information
            String msg = String.format("Score: %d / %d", gemsCollected, totalGems); // string format
            g.setColor(Color.darkGray); // set the font colour
            g.drawString(msg, getWidth() - 80, 50); // draw the string

            // Show the status of the flag
            String msg2 = "Flag Status: " + flagStatus;
            g.setColor(Color.darkGray);
            g.drawString(msg2, (getWidth() / 2) - 50, 50);

            // Get and print the framerate
            int frames = (int) getFPS(); // call getFPS method
            String framerate = frames + "fps";
            g.setColor(Color.black);
            g.drawString(framerate, (getWidth() / 2) - 20, 70);

            // Add hearts to ArrayList
            ArrayList<Image> life = new ArrayList<>();
            life.add(heart1);
            life.add(heart2);
            life.add(heart3);

            //Variables for controlling where to print the hearts on screen
            int x = 20;
            int y = 40;

            // Draw the number of hearts relative to remaining life
            for (int i = 0; i < lifeRemaining; i++)
            {
                g.drawImage(life.get(i), x, y, null);
                x = x + 30; // Some space between each heart
            }

        }
        else if (State == STATE.MENU) // If in the 'Menu' state
        {
            this.addMouseListener(new Menu()); // Add a mouse listener for this menu
            menu.render(g); // call the render method for this menu
        }
        else if (State == STATE.DEAD) // If in the 'Dead' state
        {
            this.addMouseListener(new Dead());
            dead.render(g);
        }
        else if (State == STATE.HELP) // If in the 'Help' state
        {
            this.addMouseListener(new Help());
            help.render(g);
        }
        else if (State == STATE.COMPLETE) // If in the 'Complete' state
        {
            this.addMouseListener(new Complete());
            complete.render(g);
        }
    }

    // Method to check if a sprite is on screen

    /**
     *
     * @param g the graphics object to draw to
     * @param s the current sprite
     * @param xo the x offset value
     * @param yo the y offset value
     */
    public void checkOnScreen(Graphics2D g, Sprite s, int xo, int yo)
    {
        Rectangle rect = (Rectangle) g.getClip(); // Create a rectangle around the edges of the screen
        int xc, yc; // variables to register the position of the

        // get the x and y position of the sprite
        xc = (int) (xo + s.getX());
        yc = (int) (yo + s.getY());

        if (rect.contains(xc, yc)) // if the sprite's coordinates are within the rectangle border
        {
            s.show(); // show the sprite
            s.draw(g); // draw them to the screen
        }
        else
        {
            s.hide(); // hide the sprite
        }
    }

    // Method to reset the variables at the end of the game
    public void resetVariables()
    {
        // reset and adjust variables as above for level 1..
        gemsCollected = 0;
        lifeRemaining = 3;
        init("map1.txt"); // load the first level again

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
     * @param elapsed How much time has gone by
     */
    public void handleTileMapCollisions(Sprite s, long elapsed)
    {
        // get the x and y position (in tiles)
        // the x position of the tile (in tiles)
        int tileX = (int) (s.getX() / tmap.getTileWidth());
        //the y position of the tile (in tiles)
        int tileY = (int) ((s.getY() + s.getHeight()) / tmap.getTileHeight());

        // Variables for the control of movement on slopes (doesn't really work)
        int xc = tmap.getTileXC(tileX, tileY);
        int xcc = (int) (player.getX() - xc);
        int yc = tmap.getTileYC(tileX, tileY) - tmap.getTileHeight();

        if (tmap.getTileChar(tileX, tileY) == 'g' || tmap.getTileChar(tileX, tileY) == 's' || tmap.getTileChar(tileX, tileY) == 'k' || tmap.getTileChar(tileX, tileY) == 'q' || tmap.getTileChar(tileX, tileY) == 'p' || tmap.getTileChar(tileX, tileY) == 'u' || tmap.getTileChar(tileX, tileY) == 'o' || tmap.getTileChar(tileX, tileY) == 'k') // If touching ground tile
        {
            if (s.getVelocityY() > 0) // resets Y velocity to prevent falling through the ground
            {
                s.setVelocityY(0);
            }
            s.setY((float) (tileY * tmap.getTileHeight()) - s.getHeight()); // Set the player's Y position to just above the bottom of the tile they're on
            if (s.equals(player)) // reset variables to allow the player to jump again
            {
                jumpsDone = 0;
                touchingGround = true;
            }
        }

        //TODO: Rotation on slopes & additional work on making it not look terrible, lots of jittering in and out of slopes right now
        if (tmap.getTileChar(tileX, tileY - 1) == '/') // If on a bottom left slope
        {
            s.setVelocityY(0); // try to stop player from clipping through the tile
            s.setY(yc - (xcc / 2)); // set the player y coordinate to a point on the slope relative to the x position
            s.setVelocityY(0); // try again to stop clipping?
        }

        if (tmap.getTileChar(tileX, tileY - 1) == '?') // If on a top left slope
        {
            s.setVelocityY(0);
            s.setY(yc - 16 - (xcc / 2));
            s.setVelocityY(0);
        }

        if (tmap.getTileChar(tileX, tileY - 1) == '\\') // If on a top right slope
        {
            s.setVelocityY(0);
            s.setY(yc - 16 - (16 - (xcc / 2)));
            s.setVelocityY(0);
        }

        if (tmap.getTileChar(tileX, tileY - 1) == '|') // If on a bottom right slope
        {
            s.setVelocityY(0);
            s.setY(yc - (16 - (xcc / 2)));
            s.setVelocityY(0);
        }

        if (tmap.getTileChar(tileX, tileY) == 'l') // If player touches lava //TODO: Player can be forced into a wall after being knocked back - same for enemy damage
        {
            if (s.equals(player))
            {
                Sound damage = new Sound("sounds/bwah.wav"); // Load damage sound
                damage.start(); // Run thread
                // decrement remaining life
                if (player.getPlayerDirection()) // if player is moving right
                {
                    player.setAnimation(dying_right);
                    player.setVelocityY(-0.2f); // send them flying to the left (recoil damage)
                    player.setVelocityX(-0.2f);
                    player.setX(player.getX() - 10); // set the x position back a little to prevent 3 collisions happening at once
                }
                else  // since player is moving left
                {
                    player.setAnimation(dying_left);
                    player.setVelocityY(0.2f); // send them flying to the right
                    player.setVelocityX(0.2f);
                    player.setX(player.getX() + 10);
                }
                player.setY(player.getY() - 10); // set the y position back a little to prevent 3 collisions happening at once
                lifeRemaining--; // decrement remaining life

            } else {
                s.stop();
                s.hide();
            }
        }

        // Code for the collecting of gems
        if ((tmap.getTileChar(tileX, tileY - 1) == '1') && s.equals(player))  // If green gem touched
        {
            Sound collect = new Sound("sounds/collect1.wav"); // Load collect sound
            collect.start(); // Run thread
            gemsCollected++; // increment gems collected by the appropriate value
            tmap.setTileChar('.', tileX, tileY - 1); // Replace the gem with an empty space
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == '2') && s.equals(player)) // If red gem touched
        {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected = gemsCollected + 2;
            tmap.setTileChar('.', tileX, tileY - 1);
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == '3') && s.equals(player)) // If blue gem touched
        {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected = gemsCollected + 3;
            tmap.setTileChar('.', tileX, tileY - 1);
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == '4') && s.equals(player)) // If purple gem touched
        {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected = gemsCollected + 4;
            tmap.setTileChar('.', tileX, tileY - 1);
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == '5') && s.equals(player)) // If white gem touched
        {
            Sound collect = new Sound("sounds/collect1.wav");
            collect.start();
            gemsCollected = gemsCollected + 5;
            tmap.setTileChar('.', tileX, tileY - 1);
        }

        if (gemsCollected >= totalGems / 2) // Once the player has collected half of the gems
        {
            flagStatus = ("Green"); // Set flag status to green
            flag_red.hide(); // Hide the red flag
            flag_green.show(); // Show the green flag
        }
        if ((tmap.getTileChar(tileX, tileY - 1) == 'h') && s.equals(player)) // If the player touches a heart
        {
            if (lifeRemaining < 3) // If not at full health
            {
                tmap.setTileChar('.', tileX, tileY - 1); // Set the tile the heart was on to a blank space
                lifeRemaining++; // Increment life remaining
            }
        }

    }

    // Method to check tile collision surrounding the player
    /**
     *
     * @param sprite the sprite to check collisions on
     */
    public void checkTileCollision(Sprite sprite)
    {

        // Get position of tile above the player
        xT = (int) ((sprite.getX() / tmap.getTileWidth()) + 0.5);
        yT = (int) (sprite.getY() / tmap.getTileHeight());

        // Get position of tile below the player
        xB = (int) (sprite.getX() / tmap.getTileWidth() + 0.5);
        yB = (int) ((sprite.getY() + sprite.getHeight()) / tmap.getTileHeight() - 1.0);

        // Get position of tile to the right of the player
        xR = (int) (sprite.getX() / tmap.getTileWidth() + 0.5);
        yR = (int) ((sprite.getY() + sprite.getHeight()) / tmap.getTileHeight() - 0.75);

        // Get position of tile to the left of the player
        xL = (int) (sprite.getX() / tmap.getTileWidth());
        yL = (int) ((sprite.getY() + sprite.getHeight()) / tmap.getTileHeight() - 0.75);

        // if the tile above is a 'ground' tile
        if ((tmap.getTileChar(xT, yT) == 'g' || tmap.getTileChar(xT, yT) == 'u' || tmap.getTileChar(xT, yT) == 'w' || tmap.getTileChar(xT, yT) == 's' || tmap.getTileChar(xT, yT) == 'o' || tmap.getTileChar(xT, yT) == 'k' || tmap.getTileChar(xT, yT) == 'q' || tmap.getTileChar(xT, yT) == 'p') && sprite.getVelocityY() > 0) {
            sprite.setVelocityY(0); // stop the player
        }
        // if the tile to the right is a wall tile
        if ((tmap.getTileChar(xR, yR) == 'g' || tmap.getTileChar(xR, yR) == 'u' || tmap.getTileChar(xR, yR) == 'w' || tmap.getTileChar(xR, yR) == 's' || tmap.getTileChar(xR, yR) == 'o' || tmap.getTileChar(xR, yR) == 'k') && sprite.getVelocityX() > 0) {
            sprite.setVelocityX(0); // stop player
            sprite.setX(xR * tmap.getTileWidth() - sprite.getImage().getWidth(null));
        }
        // if the tile to the left is a wall tile
        if ((tmap.getTileChar(xL, yL) == 'g' || tmap.getTileChar(xL, yL) == 'u' || tmap.getTileChar(xL, yL) == 'w' || tmap.getTileChar(xL, yL) == 's' || tmap.getTileChar(xL, yL) == 'o' || tmap.getTileChar(xL, yL) == 'k') && sprite.getVelocityX() < 0) {
            sprite.setVelocityX(0); // stop player
            sprite.setX(xL * tmap.getTileWidth() + tmap.getTileWidth());
        }
        // if the tile below is a 'ground' tile
        if (tmap.getTileChar(xB, yB) == 'g' || tmap.getTileChar(xB, yB) == 'u' || tmap.getTileChar(xB, yB) == 'w' || tmap.getTileChar(xB, yB) == 's' || tmap.getTileChar(xB, yB) == 'o' || tmap.getTileChar(xB, yB) == 'k' || tmap.getTileChar(xB, yB) == 'q' || tmap.getTileChar(xB, yB) == 'p') {
            sprite.setVelocityY(0); // stop player
            sprite.shiftY(2); // move them up a little bit
        }
    }

    // Method for handling sprite collisions using a bounding circle
    private void handleSpriteCollisions()
    {

        if (State == STATE.GAME) // Only run if in the 'Game' state
        {
            // Add enemies to ArrayList for easier processing
            ArrayList<Sprite> enemies = new ArrayList<>();
            enemies.add(enemy1);
            enemies.add(enemy2);
            enemies.add(enemy3);
            enemies.add(enemy4);

            // (Re)set the collided flag to false
            boolean collided = false;

            for (Sprite enemy : enemies)
            {
                if (BoundingCircleCollision(player, enemy)) // if the player and enemy bounding circles collide..
                {
                    if (player.getVelocityY() > 0.2f) // if the player is "landing" on the enemy
                    {
                        Sound enemyDeath = new Sound("sounds/whimper.wav"); // play the enemy death sound
                        enemyDeath.start();

                        enemy.stop(); // stop their movement
                        enemy.hide(); // hide them
                        enemy.setX(0); // move them "off-screen"
                        enemy.setY(0);
                    }
                    else // if the enemy walked into the player, or they jumped into it
                    {
                        collided = true; // set collided to true
                    }
                }
            }

            if (collided)
            {
                Sound damage = new Sound("sounds/bwah.wav"); // load damage sound effect
                damage.start(); // start thread

                // if this collision would kill the player
                if (lifeRemaining == 1)
                {
                    lifeRemaining--;
                    player.setAnimation(dying_left);
                    player.stop();
                }
                else
                {
                    if (player.getPlayerDirection()) // if player is moving right
                    {
                        player.setAnimation(dying_right);
                        player.setVelocityY(-0.2f); // send them flying to the left (recoil damage)
                        player.setVelocityX(-0.2f);
                        player.setX(player.getX() - 10); // set the x and y position back a little to prevent 3 collisions happening at once
                        player.setY(player.getY() - 10);
                    }
                    else  // since player is moving left
                    {
                        player.setAnimation(dying_left);
                        player.setVelocityY(0.2f); // send them flying to the right
                        player.setVelocityX(0.2f);
                        player.setX(player.getX() + 10);
                        player.setY(player.getY() - 10);
                    }
                    lifeRemaining--; // decrement remaining life
                }

            }

            if ((BoundingCircleCollision(player, flag_green)) && flagStatus.equals("Green"))  // if the player has touched the green flag and enough gems are collected
            {
                Sound levelComplete = new Sound("sounds/levelComplete.wav"); // Load level complete sound
                levelComplete.start(); // start thread
                finishLevel(); // call the finishLevel() method
            }
        }
    }

    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     *
     * @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode(); // fetch the key press input

        if (State == STATE.GAME) // only run when in the 'Game' state
        {
            if (key == KeyEvent.VK_ESCAPE) stop(); // stop the game

            if (key == KeyEvent.VK_SPACE) jump = true; // jump

            if (key == KeyEvent.VK_LEFT)
            {
                left = true; // move left
            }

            if (key == KeyEvent.VK_RIGHT)
            {
                right = true; // move right
            }

            if (key == KeyEvent.VK_1)
            {
                levelNumber = 1; // set the level number to 1
                init("map1.txt"); // load level 1
                initialiseGame(); // re-initialise the game
            }

            if (key == KeyEvent.VK_2)
            {
                levelNumber = 2;
                init("map2.txt");
                initialiseGame();
            }

            if (key == KeyEvent.VK_3)
            {
                levelNumber = 3;
                init("map3.txt");
                initialiseGame();
            }

            if (key == KeyEvent.VK_Q) Game.State = Game.STATE.MENU; // return to the title screen
        }
    }

    public void finishLevel()
    {
        if (levelNumber == 1)  // if on level 1
        {
            levelNumber++; // increment level number
            System.out.println("Level 1 Complete!"); // inform user they successfully finished the level
            gemsCollected = 0; // reset the collected gems variable (otherwise the next level will instantly spawn the green flag)
            flagStatus = "Red"; // reset flag status
            init("map2.txt"); // load level 2
            initialiseGame(); // re-initialise the game
        }
        else if (levelNumber == 2)
        {
            levelNumber++;
            System.out.println("Level 2 Complete!");
            gemsCollected = 0;
            flagStatus = "Red";
            init("map3.txt");
            initialiseGame();
        }
        else if (levelNumber == 3)
        {
            levelNumber = 1; // reset to 1
            System.out.println("The End"); // Player has finished the game
            gemsCollected = 0;
            flagStatus = "Red";
            Game.State = Game.STATE.COMPLETE; // Return to the main menu
        }
    }

    // method to calculate collision between two sprites

    /**
     *
     * @param one the first sprite to check
     * @param two the second sprite to check
     * @return whether there has been a collision or not
     */
    public boolean BoundingCircleCollision(Sprite one, Sprite two)
    {
        int dx, dy, minimum; // variables to calculate collision between 2 sprites
        dx = ((int) one.getX() + one.getWidth() / 2) - ((int) two.getX() + two.getWidth() / 2); // get the x distance between the centre point of sprite one and sprite two
        dy = ((int) one.getY() + one.getHeight() / 2) - ((int) two.getY() + two.getHeight() / 2); // get the y distance between the centre point of sprite one and sprite two
        minimum = one.getWidth() / 2 + two.getWidth() / 2; // take the width of both sprites and divide them by two
        return (((dx * dx) + (dy * dy)) < (minimum * minimum)); // return true if the bounding circles overlap
    }

    /**
     *
     * @param e the key press event
     */
    public void keyReleased(KeyEvent e)
    {

        int key = e.getKeyCode(); // fetch the key input from the user

        // Using breaks ana switch cases makes the code neater
        switch (key)
        {
            case KeyEvent.VK_ESCAPE:
                stop(); // end the game
                break;
            case KeyEvent.VK_SPACE:
                jump = false; // set jump flag to false - player no longer currently trying to jump
                canJump = true; // set canJump to true, as the player has taken their finger off the key
                break;
            case KeyEvent.VK_LEFT:
                left = false; // stop travelling left
                decelerate = true; // start decelerating
                break;
            case KeyEvent.VK_RIGHT:
                right = false; // stop travelling right
                decelerate = true; // start decelerating
                break;
            default: // by default (if any other key pressed)
                break; // break out of statement
        }
    }

    public enum STATE // an enumerated list of states for the game to use (as above)
    {
        MENU,
        GAME,
        HELP,
        DEAD,
        COMPLETE
    }
}
