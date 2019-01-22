
package fejava;

/*
Arista Mueller
FEJava
2018/03/12
*/

//Possible titles: The Wilds of Ciracia

/*
TODO:
See https://docs.google.com/document/d/1y8_JIo5im9DH70Cn0dKcjD3NS0d0N6LqjWjuMTzjruw/edit?usp=sharing
Add reinforcements
*/

/*
!! Small edit for improvement
?? Non-essential suggestion
## Bug/Testing needed
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;


/**
 * Main class for FEJava
 * @author Arista
 */
public class FEJava extends JPanel implements KeyListener
{
    JFrame frame = new JFrame ();
    boolean isRunning = true;
    
    Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
    double windowWidth = screenSize.getWidth ();
    double windowHeight = screenSize.getHeight ();
    
    Insets insets;
    TileMap map;
    PathFinding pathFinder;
    Image buffer;
    Level currentLevel;
    
    ArrayList <Unit> unitsAlly;
    ArrayList <Unit> unitsEnemy;
    
    ArrayList <Point> inRange = new <Point> ArrayList();
    ArrayList <Unit> unitsInRange = new ArrayList();
    
    Unit unitSelected = null;
    String actionState = "";
    String currentGraphicState = "";
    Unit unitDisplayed = null;
    
    int cursorX;
    int cursorY;
    
    StartMenu start;
    Image [] imageList;
    
    ArrayList combatActions = null;
    
    /**
     * Creates game
     * @param args 
     */
    public static void main(String[] args) 
    {
        FEJava game = new FEJava ();
        game.run ();
        System.exit (0);
    }
    
    /**
     * Calls all required setup methods and runs program
     */
    public void run () 
    {
        setupFrame ();
        frame.addKeyListener(this);
        
        Graphics g = frame.getGraphics ();
        frame.setVisible (true);
        
        //Adjust sizes and positions by value of insets
        insets = frame.getInsets(); //On testing laptop, top = 25, left = 3, bottom = 3, right = 3
        windowWidth -= (insets.left + insets.right);
        windowHeight -= (insets.top + insets.bottom);
        cursorX = insets.left;
        cursorY = insets.top;
        
        startLevel ();
        
        buffer = new BufferedImage ((int)windowWidth + insets.right + insets.left, (int)windowHeight + insets.top + insets.bottom, BufferedImage.TYPE_INT_RGB);
                
        populateMap ();
        
        //!! Add win/lose conditions
        while (isRunning) 
        {
            paintComponent (g);
            if (unitsAlly.isEmpty () || unitsEnemy.isEmpty ())
            {
                isRunning = false;
                //!! Needs better result when win (probably use when multiple levels implemented
                System.out.println ("Level complete!");
            }
        }
    }
    
    /**
     * Generates all level information
     */
    public void startLevel ()
    {
        try
        {
            //!! Will eventually need to allow multiple levels
            Level levelTest = new Level ("/FEJava/resources/text/tutorial.txt", (int)windowWidth, (int)windowHeight, insets);
            currentLevel = levelTest;
            imageList = currentLevel.getTerrain ((int)windowWidth, (int)windowHeight);
        }
        catch (IOException e) { }
        map = currentLevel.getMap ();
        unitsAlly = currentLevel.getProtag ().getMembers ();
        unitsEnemy = currentLevel.getAntag ().getMembers ();
    }
    
    /**
     * Creates all starting classes and unitsAlly as part of setup
     */
    public void populateMap () 
    {
        Team protag = currentLevel.getProtag ();
        Team antag = currentLevel.getProtag ();
        pathFinder = new PathFinding (map);
    }
    
    /**
     * Paints visuals to screen
     * @param g graphics of frame to draw images to
     */
    @Override
    protected void paintComponent (Graphics g)
    {
        super.paintComponent (g);
        Graphics bbg = buffer.getGraphics();
        
        if (currentGraphicState.equals ("TITLE"))
        {
            if (start == null)
            {
                start = new StartMenu ();
            }
            currentGraphicState = start.title (frame, (int)windowHeight, (int)windowWidth, insets, imageList [12]);
        }
        else
        {
            //start = null;
            if (combatActions != null)
            {
                attackingGraphics (bbg);
                g.drawImage (buffer, 0, 0, this);
                //##Not sure why I have to divide by 3
                //For some reason it adds more entries that don't make any sense to me
                //System.out.println (combatActions.size () / 3);
                for (int i = 0; i < combatActions.size () / 3; i ++)
                {
                    attackingGraphicsText (bbg, i);
                    g.drawImage (buffer, 0, 0, this);
                    //##Causes unit to temporarily disappear while being replaced by grey version
                    try
                    {
                        Thread.sleep (1750);
                    }
                    catch (InterruptedException e) { }
                }
                //System.out.println ("DONE");
                combatActions = null;
            }
            else
            {
                mainGraphics (bbg);
            }
        
            g.drawImage (buffer, 0, 0, this);
            setDoubleBuffered (true);
            this.setVisible(true);
        }
    }
    
    /**
     * Draws all main level graphics
     * @param g graphics of frame
     */
    public void mainGraphics (Graphics g)
    {
        //Covers blank area not filled by tiles
        g.setColor (Color.BLACK);
        g.fillRect (insets.left, insets.top, (int)windowWidth, (int)windowHeight);
        
        //Draw tiles
        for (int y = 0; y < (map.getHeight()); y ++)
        {
            for (int x = 0; x < (map.getWidth()); x ++)
            {
                g.drawImage(imageList [map.getTile (x, y)], x * map.getTileSize() + insets.left, y * map.getTileSize() + insets.top, this);
            }
        }
        //Draw player units
        for (int i = 0; i < unitsAlly.size (); i ++)
        {
            if (unitsAlly.get (i).getActionActive () == false && unitsAlly.get (i).getMoveActive () == false)
            {
                g.drawImage (unitsAlly.get (i).getSpriteInactive(), unitsAlly.get (i).getX(), ((Unit)unitsAlly.get (i)).getY(), this); 
            }
            else
            {
                g.drawImage (((Unit)unitsAlly.get (i)).getSprite(), ((Unit)unitsAlly.get (i)).getX(), ((Unit)unitsAlly.get (i)).getY(), this); 
            }
        }
        //Draw enemy units
        for (int i = 0; i < unitsEnemy.size (); i ++)
        {
            g.drawImage (((Unit)unitsEnemy.get (i)).getSprite(), ((Unit)unitsEnemy.get (i)).getX(), ((Unit)unitsEnemy.get (i)).getY(), this); 
        }
        //Cursor
        g.drawImage (imageList [10], cursorX, cursorY, this);
        g.drawImage (imageList [11], (map.getWidth() * map.getTileSize() + insets.left), insets.top, map.getPanelWidth(), (int)windowHeight, this);
        
        //Side panel
        if (unitSelected == null)
        {
            if (unitDisplayed != null)
            {
                sidePanelGraphics (g, unitDisplayed);
            }
        }
        else if (unitSelected != null)
        {
            try
            {
                //Translucent blue for tiles in range
                g.setColor (new Color (0, 0, 255, 100));
                for (int i = 0; i < inRange.size(); i ++)
                {
                    g.fillRect ((inRange.get (i)).x, (inRange.get (i)).y, map.getTileSize(), map.getTileSize());
                }
                //Show path
                for (int i = 0; i < inRange.size(); i ++)
                {
                    if (cursorX == inRange.get(i).x && cursorY == inRange.get(i).y)
                    {
                        showPath (g, unitSelected, i);
                    }
                }
            }
            catch (IndexOutOfBoundsException | NullPointerException e) { }
            if (actionState.equals ("Attacking"))
            {
                g.setColor (new Color (255, 0, 0, 125));
                for (int i = 0; i < unitsInRange.size(); i ++)
                {
                    g.fillRect(((Unit)unitsInRange.get(i)).getX(), ((Unit)unitsInRange.get(i)).getY(), 
                            map.getTileSize(), map.getTileSize());
                }
            }
            
            //## Sometimes gives NullPointerException
            try
            {
                sidePanelGraphics (g, unitDisplayed);
            }
            catch (NullPointerException e) { }
            
        }
    }
    
    /**
     * Draws all graphics for side panel 
     * @param g graphics of frame
     * @param unit unit to show info for
     */
    public void sidePanelGraphics (Graphics g, Unit unit)
    {
        int padding = map.getTileSize();
        int panelSide = (int)windowWidth - map.getPanelWidth();
        
        //!!Have other options if not in system
        int fontSize = 16;
        Font font = new Font ("Cambria", 0, fontSize);
        Font smallFont = new Font ("Cambria", 0, fontSize - 4);
        
        //Set background
        g.drawImage (imageList [11], (map.getWidth() * map.getTileSize() + insets.left), insets.top, map.getPanelWidth(), (int)windowHeight, this);
        
        //Information, a lot of it
        g.setFont (font);
        g.setColor (Color.BLACK);
        g.drawString (unit.getName(), panelSide + padding + (int)(map.getPanelWidth() * 0.5), insets.top + (padding * 2));
        g.drawString (unit.getUnitClass().getName (), panelSide + padding + (int)(map.getPanelWidth() * 0.5), insets.top + (padding * 2) + (fontSize + 2));
        g.drawString ("Lvl: " + Integer.toString (unit.getLevel ()), panelSide + padding + (int)(map.getPanelWidth() * 0.5), insets.top + (padding * 2) + (fontSize + 2) * 2);
        g.drawString ("Exp: " + Integer.toString (unit.getExp ()), panelSide + padding + (int)(map.getPanelWidth() * 0.5), insets.top + (padding * 2) + (fontSize + 2) * 3);
        g.drawString ("Move: " + Integer.toString (unit.getUnitClass ().getMoveDist ()), panelSide + padding + (int)(map.getPanelWidth() * 0.5), insets.top + (padding * 2) + (fontSize + 2) * 4);
        g.drawImage ((unit.getPortrait()), panelSide + padding, insets.top + padding, this);
        
        g.drawString ("HP: " + unit.getHP () + "/" + unit.getStats(0), panelSide + padding, (int)windowHeight / 3);
        g.drawString ("Atk: " + unit.getStats(1), panelSide + padding, (int)windowHeight / 3 + fontSize + 2);
        g.drawString ("Spd: " + unit.getStats(2), panelSide + padding, (int)windowHeight / 3 + (fontSize + 2) * 2);
        g.drawString ("Hit: " + unit.getStats(3), panelSide + padding, (int)windowHeight / 3 + (fontSize + 2) * 3);
        g.drawString ("Crit: " + unit.getStats(4), panelSide + padding, (int)windowHeight / 3 + (fontSize + 2) * 4);
        g.drawString ("Def: " + unit.getStats(5), panelSide + padding, (int)windowHeight / 3 + (fontSize + 2) * 5);
        g.drawString ("Res: " + unit.getStats(6), panelSide + padding, (int)windowHeight / 3 + (fontSize + 2) * 6);
        
        //Weapon information
        g.drawString ("Weapon: " + unit.getWeapon ().getName (), panelSide + map.getPanelWidth () / 2, (int)windowHeight / 3);
        g.setFont (smallFont);
        g.drawString ("        Dam: " + unit.getWeapon ().getStats (0), panelSide + map.getPanelWidth () / 2, (int)windowHeight / 3 + (fontSize + 2));
        g.drawString ("        Hit: " + unit.getWeapon ().getStats (1), panelSide + map.getPanelWidth () / 2, (int)windowHeight / 3 + (fontSize + 2) * 2);
        g.drawString ("        Crit: " + unit.getWeapon ().getStats (2), panelSide + map.getPanelWidth () / 2, (int)windowHeight / 3 + (fontSize + 2) * 3);
        g.drawString ("        Range: " + unit.getWeapon ().getRange (), panelSide + map.getPanelWidth () / 2, (int)windowHeight / 3 + (fontSize + 2) * 4);
        
        //Inventory: item use is incomplete
        g.setFont (font);
        //!!Highlight inventory when using item (i + 1,2,3...)
        g.drawString ("Inventory", panelSide + padding, (int)(((int)windowHeight / 3) * 1.5) + padding);
        for (int i = 1; i < unit.getInventory ().size (); i ++)
        {
            g.drawString (i + ". " + ((Item)unit.getFromInventory (i)).getName (), panelSide + padding, (int)windowHeight / 3 * 2);
        }
//        //Help/commands
//        if (actionState  == "")
//        {
//            g.drawString ("Commands:", panelSide + padding, (int)windowHeight / 2 + (int)windowHeight / 4 + (fontSize + 2));
//            g.drawString ("Enter - select and move unit", panelSide + padding, (int)windowHeight / 2 + (int)windowHeight / 4 + (fontSize + 2) * 2);
//            g.drawString ("a - attack", panelSide + padding, (int)windowHeight / 2 + (int)windowHeight / 4 + (fontSize + 2) * 3);
//            g.drawString ("Escape - deselect or exit attacking mode", panelSide + padding, (int)windowHeight / 2 + (int)windowHeight / 4 + (fontSize + 2) * 5);
//            g.drawString ("e - end turn", panelSide + padding, (int)windowHeight / 2 + (int)windowHeight / 4 + (fontSize + 2) * 4);
//        }
        
    }
    
    
    /**
     * Handles drawing of background for combat
     * @param g 
     */
    public void attackingGraphics (Graphics g)
    {
        g.setColor (Color.LIGHT_GRAY);
        int mainWidth = (int)windowWidth - map.getPanelWidth ();
        g.fillRect (insets.left + mainWidth / 6, insets.top + (int)windowHeight / 4, 
                mainWidth - 2 * (mainWidth / 6), (int)windowHeight - 2 * ((int)windowHeight / 4));
    }
    
    /**
     * Displays text results of combat
     * @param g 
     * @param i action number, determines which action to display
     */
    public void attackingGraphicsText (Graphics g, int i)
    {
        //!! Needs level up screen
        int mainWidth = (int)windowWidth - map.getPanelWidth ();
            /*
            0 - atkr, 1 - defr, 2 - start hp, 3 - hit, 4 - end hp
            */
            Font font = new Font ("Cambria", 0, 16);
            g.setFont (font);
            String [] parts = ((String)combatActions.get (i)).split (":");
            g.setColor (Color.BLACK);
            //System.out.println (combatActions.get (i));
            //!! element 4 sometimes doesn't get added
            //   I honestly don't know what's going on with that
            //Happens for 1 range attacking 2 range
            try
            {
                g.drawString (parts [0] + " " + parts [3] + " " + parts [1] + ", doing " + (Integer.parseInt (parts [2]) - Integer.parseInt (parts [4])) + " damage.",
                        //(insets.left + mainWidth / 8 + 20), ((int)windowHeight / 2 + 20));
                        insets.left + mainWidth / 6 + 20, ((int)windowHeight / 3 + (30 * (2 * i + 1))));
                //Displays 0 if less than 0 hp
                g.drawString (parts [1] + "'s HP: " + (Integer.parseInt (parts [4]) < 0 ? 0 : parts [4]), 
                        insets.left + mainWidth / 6 + 20, ((int)windowHeight / 3 + (30 * (2 * i + 2))));
            }
            catch (ArrayIndexOutOfBoundsException e) { }
    }
    
    /**
     * Displays path found to destination
     * @param g graphics of frame
     * @param unit unit being moved
     * @param index index of tile moving to
     */
    public void showPath (Graphics g, Unit unit, int index)
    {
        for (int i = 0; i < inRange.size (); i ++)
        {
            try
            {
                Path path = pathFinder.findPath((inRange.get (index)).x / map.getTileSize(), 
                        (inRange.get (index)).y / map.getTileSize(), unit.getX() / map.getTileSize(), 
                        unit.getY() / map.getTileSize(), unit, unitsEnemy, unit.getUnitClass ().getMoveDist ());

                if (path != null)
                {
                    g.setColor (new Color (255, 255, 255));
                    for (int j = 0; j < path.getLength(); j ++)
                    {
                        g.fillRect (path.getX (j) * map.getTileSize() + insets.left + map.getTileSize () / 2 - 3, 
                                path.getY (j) * map.getTileSize() + insets.top + map.getTileSize () / 2 - 3, 
                                6, 6);
                    }
                }
            }
            catch (NullPointerException | IndexOutOfBoundsException e) { }
        }
    }
        
    /**
     * Handles controls
     * @param ke 
     */
    @Override
    public void keyPressed(KeyEvent ke)
    {
        int keyCode = ke.getKeyCode ();
        
        switch (keyCode)
        { 
            //Move cursor
            case KeyEvent.VK_UP:
                updateCursor (0, -1);
                break;
            case KeyEvent.VK_DOWN:
                updateCursor (0, 1);
                break;
            case KeyEvent.VK_LEFT:
                updateCursor (-1, 0);
                break;
            case KeyEvent.VK_RIGHT :
                updateCursor (1, 0);
                break;
            case KeyEvent.VK_ENTER: //Select, does many different tasks depending on state
                //## Sometimes freezes when selected (Only on Avanni?)
                if (unitSelected == null)
                {
                    unitSelected = compareLocation (unitsAlly);
                    unitDisplayed = unitSelected;
                    inRange.clear ();
                    try 
                    {
                        if (unitSelected.getMoveActive () == true)
                        {
                            getTilesInRange (unitSelected);
                        }
                    }
                    catch (NullPointerException e) { }
                    if (unitSelected == null)
                    {
                        unitDisplayed = compareLocation (unitsEnemy);
                        //System.out.println (unitDisplayed);
                    }
                }
                else if (unitSelected != null)
                {
                    if (!actionState.equals ("Attacking"))
                    {
                        unitSelected = updateUnitPos ();
                    }
                    else
                    {
                        for (int i = 0; i < unitsInRange.size(); i ++)
                        {
                            if (((Unit)unitsInRange.get (i)).getX() == cursorX 
                                    && ((Unit)unitsInRange.get (i)).getY() == cursorY)
                            {
                                initiateCombat ((Unit)unitsInRange.get (i));
                                unitsInRange.clear();
                            }
                            //!! Make it clear when in attacking mode
                        }
                    }
                }
                break;
            case KeyEvent.VK_A: //Enters attacking mode
                if (unitSelected != null)
                {
                    if (unitSelected.getActionActive () == true)
                    {
                        actionState = "Attacking";
                        checkAttackRange(unitsEnemy, unitSelected.getWeapon ().getRange ());
                    }
                }
                break;
            case KeyEvent.VK_E: //Ends turn
                //System.out.println ("End turn");
                resetTurn ();
                break;
            
//            case KeyEvent.VK_I: //Enters inventory selection
//                if (unitSelected != null)
//                {
//                    //System.out.println ("Currently unsupported");
//                    if (unitSelected.getActionActive () == true)
//                    {
//                        actionState = "Inventory";
//                        //System.out.println (actionState);
//                    }
//                }
//                break;
            
            case KeyEvent.VK_ESCAPE: //Exits modes
                if (!actionState.equals (""))
                {
                    actionState = "";
                }
                else if (unitSelected != null)
                {
                    unitSelected = null;
                    unitDisplayed = null;
                }
                break;
                
            case KeyEvent.VK_1: //Uses item from inventory when in inventory mode
                itemFromInventory (1);
                break;
            case KeyEvent.VK_2:
                itemFromInventory (2);
                break;
            case KeyEvent.VK_3:
                itemFromInventory (3);
                break;
            case KeyEvent.VK_4:
                itemFromInventory (4);
                break;
            case KeyEvent.VK_5:
                itemFromInventory (5);
                break;
        }  
    }
    
    /**
     * Gets and uses item from inventory
     * @param index index of item in inventory
     */
    public void itemFromInventory (int index)
    {
        if (unitSelected != null)
        {
            try 
            {
                Item item = (Item)unitSelected.getFromInventory (index);
                if (item.getType ().equals ("S"))
                {
                    item.useItem (unitSelected);
                }
                if (item.getType ().equals ("T"))
                {
                    unitsInRange.clear ();
                    checkAttackRange (unitsAlly, 2);
                    actionState = "Inventory";
                }
                actionState = "";
                unitSelected.setActionActive (false);
                unitSelected.setMoveActive (false);
                unitSelected = null;
                unitDisplayed = null;
            }
            catch (IndexOutOfBoundsException e) { }
        }
    }
    
    /**
     * Ends turn and resets all units as active
     */
    public void resetTurn ()
    {
        for (int i = 0; i < unitsAlly.size(); i++)
        {
            ((Unit)unitsAlly.get (i)).setMoveActive(true);
            ((Unit)unitsAlly.get (i)).setActionActive(true);
        }
        for (int i = 0; i < unitsEnemy.size (); i ++)
        {
            while (combatActions != null)
            {
                //For some strange reason this print statement is necessary
                //Without it, everything freezes
                //I have no idea why
                System.out.print ("");
            }
            //!!Graphics don't keep up with combat actions from enemies
            unitSelected = (Unit)unitsEnemy.get (i);
            unitsInRange.clear ();
            checkAttackRange (unitsAlly, unitSelected.getWeapon ().getRange ());
            inRange.clear ();
            getTilesInRange (unitSelected);
            AITurn enemyTurn = new AITurn (unitSelected);
            combatActions = enemyTurn.turn (unitSelected, inRange, unitsAlly, unitsEnemy, map, insets);
//            try
//            {
//                Thread.sleep (50);
//            }
//            catch (InterruptedException e)
//            {
//                
//            }
        }
        unitSelected = null;
    }
    
    /**
     * Checks for units that are within attack range and adds to list
     * @param enemies list of units to check range
     * @param range range of attacker's weapon
     */
    public void checkAttackRange (ArrayList enemies, int range)
    {
        Unit unit;
        for (int i = 0; i < enemies.size(); i ++)
        {
            unit = (Unit)enemies.get (i);
            if (range == 1)
            {
               if ((Math.abs (unit.getX() - unitSelected.getX()) == map.getTileSize()
                    && unit.getY() == unitSelected.getY())
                    || (Math.abs (unit.getY() - unitSelected.getY()) == map.getTileSize()
                    && unit.getX() == unitSelected.getX()))
                {
                    unitsInRange.add (unit);
                } 
            }
            else if (range == 2)
            {
                if ((Math.abs (unit.getX () - unitSelected.getX ()) + 
                        Math.abs (unit.getY () - unitSelected.getY ())) 
                        == map.getTileSize () * 2)
                {
                    unitsInRange.add (unit);
                } 
            }
        }
    }
    
    /**
     * Creates instance of combat and ends turn
     * @param unit unit being attacked
     */
    public void initiateCombat (Unit unit)
    {
        unitSelected.setActionActive(false);
        unitSelected.setMoveActive (false);
        actionState = "";
        Combat combat = new Combat (unitSelected, unit);
        combatActions = combat.round (unitSelected, unit);
        //!! Set to null to dispose of dead units
        if (unitSelected.getHP () <=0)
        {
            unitsAlly.remove (unitSelected);
        }
        if (unit.getHP () <= 0)
        {
            unitsEnemy.remove (unit);
        }
        unitSelected = null;
        unitDisplayed = null;
    }
    
    /**
     * Moves cursor
     * @param dx distance in the x to move
     * @param dy distances in the y to move
     */
    public void updateCursor (int dx, int dy)
    {
        int newCursorX = cursorX + map.getTileSize () * dx;
        int newCursorY = cursorY + map.getTileSize () * dy;
        
        if (newCursorX > 0 && newCursorX < map.getWidth() * map.getTileSize() 
                && newCursorY > 0 && newCursorY < map.getHeight() * map.getTileSize())
        {
            cursorX = newCursorX;
            cursorY = newCursorY;
        }
    }
    
    /**
     * Compare unit and cursor position to select unit
     * @param units gives all units on desired team
     * @return selected unit
     */
    public Unit compareLocation (ArrayList units)
    {
        for (int i = 0; i < units.size (); i ++)
        {
            if (cursorX == ((Unit)units.get (i)).getX() 
                    && cursorY == ((Unit)units.get (i)).getY())
            {
                //System.out.println ("Selected");
                if (((Unit)units.get (i)).getMoveActive() == true
                        || ((Unit)units.get (i)).getActionActive() == true)
                {
                    return (Unit)units.get (i);
                }
                break;
            }
        }
        return null;
    }
    
    /**
     * moves unit to selected locations
     * @return null to reset unitSelected, or unit if unmoved
     */
    public Unit updateUnitPos ()
    {
        if (inRange.contains(new Point (cursorX, cursorY)))
        {
            unitSelected.setX(cursorX);
            unitSelected.setY(cursorY);
            unitSelected.setMoveActive(false);
            unitDisplayed = null;
            return null;
        }
        return unitSelected;
    }
    
    /**
     * Adds all tiles that are valid to available movements
     * @param unit unit attempting to move
     * adds values to inRange
     */
    public void getTilesInRange (Unit unit)
    {
        //?? Could be streamlined/replaced to avoid looking at tiles we know don't work
        //?? Integrate better with pathfinding to remove redundant searching
        inRange.clear();
        PathFinding pathFinder2 = new PathFinding (map);
        
        //## Sometimes gives NullPointerException (if too fast?)
        // Maybe when out of range?
        int dist = (unit.getUnitClass()).getMoveDist() * map.getTileSize();
        ArrayList enemyTeam;
        if (unitsAlly.contains (unit))
        {
            enemyTeam = unitsEnemy;
        }
        else
        {
            enemyTeam = unitsAlly;
        }
        
        for (int y = unit.getY() - dist; y <= unit.getY() + dist; y += map.getTileSize())
        {
            for (int x = unit.getX() - dist; x <= unit.getX() + dist; x += map.getTileSize())
            {
                if (Math.abs (unit.getX() - x) + Math.abs (unit.getY() - y) <= dist)
                {
                    inRange.add (new Point (x, y));
                }
            }
        }
        //Iterate through ArrayList to remove points dynamically
        Iterator iter = inRange.iterator ();
        int size = inRange.size();
        Point nextPoint;
        while (iter.hasNext ())
        {
            nextPoint = (Point)iter.next ();
            if (nextPoint.x < 0 || nextPoint.y < 0 ||
                    nextPoint.x > windowWidth - map.getPanelWidth () 
                    || nextPoint.y > windowHeight - map.getTileSize ())
            {
                iter.remove ();
            }
            
            else if (pathFinder2.findPath ((nextPoint).x / map.getTileSize(), 
                    (nextPoint).y / map.getTileSize(),unit.getX() / map.getTileSize(), 
                    unit.getY() / map.getTileSize(), unit, enemyTeam, unit.getUnitClass ().getMoveDist ()) == null)
            {
                iter.remove ();
            }
            else
            {
                for (int j = 0; j < unitsAlly.size (); j ++)
                {
                    if (nextPoint.x == ((Unit)unitsAlly.get (j)).getX ()
                        && nextPoint.y == ((Unit)unitsAlly.get (j)).getY ())
                    {
                        iter.remove ();
                    }
                }
                for (int j = 0; j < unitsEnemy.size (); j ++)
                {
                    if (nextPoint.x == ((Unit)unitsEnemy.get (j)).getX ()
                        && nextPoint.y == ((Unit)unitsEnemy.get (j)).getY ())
                    {
                        iter.remove ();
                    }
                }
            }
        }
    }
    
    /**
     * Sets up all frame information
     */
    public void setupFrame () 
    {
        frame.setTitle ("The Wilds of Ciracia");
        if (Math.abs (screenSize.width - screenSize.height) < 300)
        {
            screenSize.height -= 300;
            windowHeight -= 300;
        }
        frame.setBounds (0, 0, screenSize.width, screenSize.height);
        frame.setResizable (false);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.setVisible (true);
    }
    
    /**
     * Tests path to get length and steps
     * @param unit unit being moved
     * @param index index of inRange to find path to
     */
    public void testPath (Unit unit, int index)
    {
        System.out.println ("Unit x: " + unit.getX() / map.getTileSize() +
                "\tUnit y: " + unit.getY() / map.getTileSize());
        System.out.println ("Target x: " + (inRange.get (index)).x / map.getTileSize() +
                "\tTarget y: " + inRange.get (index).y / map.getTileSize());
        
        Path path = pathFinder.findPath ((inRange.get (index)).x / map.getTileSize(), 
                    (inRange.get (index)).y / map.getTileSize(), unit.getX() / map.getTileSize(), 
                    unit.getY() / map.getTileSize(), unit, unitsEnemy, unit.getUnitClass ().getMoveDist ());
        
        if (path == null)
        {
            System.out.println ("No path");
        }
        else
        {
            System.out.println ("Length: " + path.getLength());
            System.out.println ("Cost: " + path.getCost());
            for (int j = 0; j < path.getLength(); j ++)
            {
                System.out.print ("| X: " + path.getStep (j).getX ());
                System.out.print (" Y: " + path.getStep (j).getY ());
                System.out.print (" C: " + map.getCost(unit, path.getStep (j).getX (), path.getStep (j).getY ()));
            }
        }
    }
    
    /**
     * Unused
     */
    @Override
    public void keyTyped(KeyEvent ke) { }
    @Override
    public void keyReleased(KeyEvent ke) { }
}

