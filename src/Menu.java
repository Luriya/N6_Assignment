import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Menu implements MouseListener // Main menu screen
{

    // Images to use in menu
    Image start_button;
    Image howToPlay_button;
    Image quit_button;

    public void render(Graphics g) // Method to draw items to the menu
    {
        Graphics2D g2 = (Graphics2D) g;// Graphics object to draw to

        if (Game.State == Game.STATE.MENU)
        {
            ImageIcon start_b = new ImageIcon("images/Icons/icon_start.png"); // create new ImageIcon
            start_button = start_b.getImage(); // fetch image

            ImageIcon help_b = new ImageIcon("images/Icons/icon_how_to_play.png");
            howToPlay_button = help_b.getImage();

            ImageIcon quit_b = new ImageIcon("images/Icons/icon_quit.png");
            quit_button = quit_b.getImage();

            g.drawImage(start_button, 160, 75, null); // draw images to screen
            g.drawImage(howToPlay_button, 160, 175, null);
            g.drawImage(quit_button, 160, 275, null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (Game.State == Game.STATE.MENU)
        {
            if (mx >= 160 && mx <= 340) // If within this horizontal area
            {
                if (my >= 75 && my<= 115) // If within this vertical area
                {
                    //Pressed Start
                    Game.State = Game.STATE.GAME;
                }
                if (my >= 175 && my<= 215)
                {
                    //Pressed How to Play
                    Game.State = Game.STATE.HELP; // Change to 'Help' state
                }
                if (my >= 275 && my<= 315)
                {
                    //Pressed Quit
                    System.exit(0); // Exit game
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
