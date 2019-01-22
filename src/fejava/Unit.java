package fejava;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;


/**
 * Unit/character object
 * @author Arista
 */
public class Unit //implements Comparable
{
    
    private int x, y;
    private UnitClass unitClass;
    private Image portraitImage;
    private Image spriteImage;
    private Image spriteInactive;
    private int [] stats;
    private int [] growth;
    private String name;
    private int currentHP;
    private boolean moveActive;
    private boolean actionActive;
    private ArrayList inventory;
    private Weapon equippedWeapon;
    private String behaviour;
    private int exp; //experience
    private int level;
    private ArrayList <String> statusEffects;
    private ArrayList <Integer> statusTimer;
    
    /**
     * Constructor
     * @param name name of character/unit
     * @param x x of unit
     * @param y y of unit
     * @param unitClass class for character
     * @param stats starting stat values, added to class stats
     * @param growth growth rates, added to class growths
     * @param tempInventory inventory of strings, before being filled with actual objects
     * @param imagePath path of image for side panel
     * @param behaviour behaviour of unit, used for enemies only
     * @param map map on which units placed
     * @param exp starting experience
     */
    public Unit (String name, int x, int y, UnitClass unitClass, int [] stats, 
            int [] growth, ArrayList tempInventory, String imagePath, String behaviour, int exp, TileMap map)
    {
        this.x = x;
        this.y = y;
        this.unitClass = unitClass;
        this.stats = stats;
        this.growth = growth;
        this.name = name;
        this.inventory = new ArrayList ();
        this.behaviour = behaviour;
        this.currentHP = stats [0];
        this.moveActive = true;
        this.actionActive = true;
        this.exp = exp;
        this.level = 1;
        //!! Make sure first inventory slot is weapon when creating
        for (int i = 0; i < tempInventory.size (); i ++)
        {
            try 
            {
                inventory.add (new Weapon (tempInventory.get (i).toString ()));
            }
            catch (IOException e)
            {
                try 
                {
                    inventory.add (new Item (tempInventory.get (i).toString ()));
                }
                catch (IOException f) { }
            }
        }
        this.equippedWeapon = (Weapon)inventory.get (0);
        this.statusEffects = new ArrayList ();
        this.statusTimer = new ArrayList ();
        
        String inactivePath = imagePath.substring (0, imagePath.length () - 4) + "Inactive.png";
        try
        {
            this.portraitImage = ((Toolkit.getDefaultToolkit().getImage(FEJava.class.getResource(imagePath))).getScaledInstance((map.getPanelWidth() - map.getTileSize() * 4 )/ 2, (map.getPanelWidth())/ 3, Image.SCALE_FAST));
            this.spriteImage = ((Toolkit.getDefaultToolkit().getImage(FEJava.class.getResource(imagePath))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
            this.spriteInactive = ((Toolkit.getDefaultToolkit().getImage(FEJava.class.getResource(inactivePath))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
        }
        
        catch (NullPointerException e)
        {
            //System.out.println ("No image found");
        } 
    }
    
    /**
     * Sets x
     * @param x x coordinate
     */
    public void setX (int x)
    {
        this.x = x;
    }
    
    /**
     * Sets y 
     * @param y y coordinate 
     */
    public void setY (int y)
    {
        this.y = y;
    }
    
    /**
     * Gets x
     * @return x of unit
     */
    public int getX ()
    {
        return x;
    }
    
    /**
     * Gets y
     * @return y of unit 
     */
    public int getY ()
    {
        return y;
    }
    
    /**
     * Gets unit class
     * @return unit class object
     */
    public UnitClass getUnitClass ()
    {
        return unitClass;
    }
    
    /**
     * Gets image
     * @return image
     */
    public Image getPortrait ()
    {
        return portraitImage;
    }
    
    /**
     * Gets image
     * @return image
     */
    public Image getSprite ()
    {
        return spriteImage;
    }
    
    /**
     * Gets stats
     * @return stats
     */
    public int [] getStats ()
    {
        return stats;
    }
    
    /**
     * Gets particular stat
     * @param pos index of desired stat
     * @return stat value
     */
    public int getStats (int pos)
    {
        return stats [pos];
    }
    
    /**
     * Gets name of unit
     * @return name
     */
    public String getName ()
    {
        return name;
    }
    
    /**
     * Sets stats
     * @param pos index of stat to change
     * @param newStat new value of stat
     */
    public void setStats (int pos, int newStat)
    {
        stats [pos] = newStat;
    }
    
    /**
     * Sets HP
     * @param HP new hit point value 
     */
    public void setHP (int HP)
    {
        this.currentHP = HP;
    }
    
    /**
     * Gets HP
     * @return current hit points 
     */
    public int getHP ()
    {
        return currentHP;
    }
    
    /**
     * Sets whether unit can move
     * @param active false if unit can no longer move
     */
    public void setMoveActive (boolean active)
    {
        this.moveActive = active;
    }
    
    /**
     * Gets whether unit can move
     * @return false if unit can no longer move
     */
    public boolean getMoveActive ()
    {
        return moveActive;
    }

    /**
     * Sets whether unit can perform action
     * @param actionActive false if unit can no longer perform actions
     */
    public void setActionActive (boolean actionActive)
    {
        this.actionActive = actionActive;
    }

    /**
     * Gets whether unit can perform action
     * @return false if unit can no longer perform actions
     */
    public boolean getActionActive ()
    {
        return actionActive;
    }
    
    /**
     * Adds item to inventory
     * @param item item to be added
     */
    public void addToInventory (Object item)
    {
        inventory.add (item);
    }
    
    /**
     * Gets item from inventory
     * @param element number of element to get
     * @return item at desired index
     */
    public Object getFromInventory (int element)
    {
        return inventory.get (element);
    }
    
    /**
     * Sets equipped weapon
     * @param equippedWeapon 
     */
    public void setWeapon (Weapon equippedWeapon)
    {
        this.equippedWeapon = equippedWeapon;
    }
    
    /**
     * Gets currently equipped weapon
     * @return weapon currently equipped
     */
    public Weapon getWeapon ()
    {
        return equippedWeapon;
    }
    
    /**
     * Set growth rates
     * @param pos index of stat to alter
     * @param newStat new value to change
     */
    public void setGrowths (int pos, int newStat)
    {
        growth [pos] = newStat;
    }
    
    /**
     * Gets behviour of unit (only applicable for enemy units)
     * @return behviour: "Normal", "Defensive", "Aggressive"
     */
    public String getBehaviour ()
    {
        return behaviour;
    }

//    @Override
//    public int compareTo (Object t)
//    {
//        //!!Only targets defense currently
//        return (stats [5]) - ((Unit)t).getStats (5);
//    }

    /**
     * Gets current experience value
     * @return experience
     */
    public int getExp ()
    {
        return exp;
    }

    /**
     * Adds to current experience
     * @param exp experience to be added
     */
    public void addExp (int exp)
    {
        this.exp += exp;
//        System.out.println ("NE " + this.exp);
        if (this.exp >= 100)
        {
//            System.out.println ("L");
            levelUp ();
        }
    }
    
    /**
     * Increases level and adds to stats using growth rates
     */
    public void levelUp ()
    {
        this.level ++;
        Random rn = new Random ();
        
        for (int i = 0; i < stats.length; i ++)
        {
            if (rn.nextInt (100) <= growth [i])
            {
                stats [i] += 1;
                //System.out.println (stats [i]);
            }
        }
        this.exp -= 100;
    }

    /**
     * Gets level of unit
     * @return level
     */
    public int getLevel ()
    {
        return level;
    }

    /**
     * Gets inactive sprite (after movement used up)
     * @return image of inactive sprite
     */
    public Image getSpriteInactive ()
    {
        return spriteInactive;
    }

    /**
     * Gets entire inventory
     * @return inventory ArrayList
     */
    public ArrayList getInventory ()
    {
        return inventory;
    }
    
    /**
     * Compares and sorts units based on def
     */
    public static Comparator <Unit> defCompare = new Comparator <Unit> ()
    {
        @Override
        public int compare (Unit t1, Unit t2)
        {
            return t1.getStats (5) - t2.getStats (5);
        }
    };
    
    public void turnEnd ()
    {
        this.actionActive = true;
        this.moveActive = true;
        
        statusEffectUpdate ();
    }
    
    public void addToStatus (String newStatus)
    {
        statusEffects.add (newStatus);
    }
    
    public void statusEffectUpdate ()
    {
        String sourceFile = "/FEJava/resources/text/statusEffects.txt";
        
    }
    
    /**
     * Compares and sorts units based on res
     */
    public static Comparator <Unit> resCompare = new Comparator <Unit> ()
    {
        @Override
        public int compare (Unit t1, Unit t2)
        {
            return t1.getStats (6) - t2.getStats (6);
        }
    };
}
