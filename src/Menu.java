import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Menu implements MouseListener {

    Image start_button;
    Image howToPlay_button;
    Image quit_button;

    public void render(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        if (Game.State == Game.STATE.MENU)
        {
            ImageIcon start_b = new ImageIcon("images/Icons/icon_start.png");
            start_button = start_b.getImage();

            ImageIcon help_b = new ImageIcon("images/Icons/icon_how_to_play.png");
            howToPlay_button = help_b.getImage();

            ImageIcon quit_b = new ImageIcon("images/Icons/icon_quit.png");
            quit_button = quit_b.getImage();

            g.drawImage(start_button, 160, 75, null);
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
            if (mx >= 160 && mx <= 340)
            {
                if (my >= 75 && my<= 115)
                {
                    //Pressed Start
                    Game.State = Game.STATE.GAME;
                }
                if (my >= 175 && my<= 215)
                {
                    //Pressed How to Play
                    Game.State = Game.STATE.HELP;
                }
                if (my >= 275 && my<= 315)
                {
                    //Pressed Quit
                    System.exit(0);
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
