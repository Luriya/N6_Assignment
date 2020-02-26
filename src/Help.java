import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Help implements MouseListener {

    Image return_button;
    Image game_instructions;

    public void render(Graphics g)
    {
        Graphics2D g4 = (Graphics2D) g;

        ImageIcon instructions_icon = new ImageIcon("images/Icons/icon_instructions.png");
        game_instructions = instructions_icon.getImage();
        g.drawImage(game_instructions, 140, 50, null);

        ImageIcon return_b = new ImageIcon("images/Icons/icon_return.png");
        return_button = return_b.getImage();
        g.drawImage(return_button, 160, 300, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (mx >= 160 && mx <= 340)
        {
            if (my >= 300 && my<= 340)
            {
                //Pressed Return
                Game.State = Game.STATE.MENU;
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
