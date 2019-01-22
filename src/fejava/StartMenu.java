
package fejava;

/**
 * Start menu to begin game
 * @author Arista Mueller
 */

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Displays intro screen
 * @author Arista Mueller
 */
public class StartMenu 
{
    private String state;
    /**
     * Creates start menu object
     */
    public StartMenu ()
    { 
        this.state = "TITLE";
        //UNFINISHED
        //!!Buttons flicker and are probably an epilepsy hazard
        //  Combining graphics and swing seems to work very badly
    }
    
    /**
     * Displays all start menu graphics
     * @param frame 
     * @param g
     * @param windowHeight
     * @param windowWidth
     * @param insets
     * @param background background image
     */
    public String title (JFrame frame, int windowHeight, int windowWidth, Insets insets, Image background)
    {
        JPanel panel = new JPanel ();
        
        //g.drawImage (background, insets.left, insets.top, frame);
        //g.setColor (Color.WHITE);
        //g.fillRect (0, 0, windowWidth, windowHeight);
        
        Graphics g = frame.getGraphics ();
        //g.setColor (new Color (92, 119, 192));
        //g.fillRect (insets.left + 10, insets.top + 10, windowWidth - 20, windowHeight - 20);
        
        GridLayout layout = new GridLayout (4, 1);
        panel.setLayout (layout);
        Font titleFont = new Font ("Cambria", Font.PLAIN, 100);
        Font smallFont = new Font ("Cambria", Font.PLAIN, 42);
        
        JLabel titleLabel = (new JLabel ("The Wilds of Ciracia"));
        titleLabel.setFont (titleFont);
        panel.add (titleLabel);
        
        JLabel underlineLabel = new JLabel ("_______________________");
        underlineLabel.setFont (titleFont);
        panel.add (underlineLabel);
        
        JLabel authourLabel = new JLabel ("By Arista Mueller");
        authourLabel.setFont (smallFont);
        panel.add (authourLabel);
        
        JButton startButton = new JButton ("Play");
        startButton.setFont (smallFont);
        panel.add (startButton);
        //g.setFont (titleFont);
        //g.drawString ("Currently Untitled", insets.left + 50, insets.top + 50);
        
//        JButton startButton = new JButton ("Play");
        //I just realised I'm using a frame without a panel
        //I'm sorry
        //I don't want to change everything now
//        panel.add (startButton);
//        
//        Dimension size = startButton.getPreferredSize ();
//        startButton.setBounds (400, 400, 60, 30);
//        
        //!!Flickers temporarily when ended
        startButton.addActionListener((ActionEvent ae) -> {
            //System.out.println ("Start");
            endTitle ("");
        });
        frame.add (panel);
        frame.setVisible (true);
        return this.state;
    }
    
    public void endTitle (String state)
    {
        this.state = state;
    }
}
