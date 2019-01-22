
package fejava;

import java.awt.Color;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * By Arista Mueller
 */
public class Team 
{
    private String name;
    private Color colour;
    private boolean allied;
    private ArrayList members;
    
    /**
     * Team object holding all units allied to each other
     * @param name name of team
     * @param colour colour, currently unused
     * @param allied true if on player team
     * @param sourceFile file containing all units on team
     * @param unitClasses all unit classes that are valid
     * @param insets
     * @param map
     * @throws IOException shouldn't throw unless you mess with the text files
     */
    public Team (String name, Color colour, boolean allied, String sourceFile, ArrayList unitClasses, Insets insets, TileMap map) throws IOException
    {
        this.name = name;
        this.colour = colour;
        this.allied = allied;
        this.members = new ArrayList ();
        populateTeam (sourceFile, unitClasses, insets, map);
    }

    /**
     * Gets name of team
     * @return name
     */
    public String getName ()
    {
        return name;
    }

    /**
     * Gets colour
     * @return coulour
     */
    public Color getColour ()
    {
        return colour;
    }

    /**
     * Allied or not
     * @return true if on player team
     */
    public boolean getAllied ()
    {
        return allied;
    }
    
    /**
     * Gets all members on the team
     * @return ArrayList of all members of team
     */
    public ArrayList getMembers ()
    {
        return members;
    }
    
    /**
     * Adds member to team
     * @param unit unit to be added to team
     */
    public void addMember (Unit unit)
    {
        members.add (unit);
    }
    
    /**
     * Add all units to team that are within source file
     * @param sourceFile image path of text file with units
     * @param unitClasses all valid unit classes
     * @param insets
     * @param map
     * @throws IOException shouldn't unless file missing
     */
    public void populateTeam (String sourceFile, ArrayList unitClasses, Insets insets, TileMap map) throws IOException
    {
        try 
        {
            BufferedReader file = new BufferedReader (new FileReader (FEJava.class.getResource (sourceFile).getFile ()));

            ArrayList tempData = new ArrayList ();
            String line = file.readLine ();
            while (line != null)
            {
                tempData.add (line);
                line = file.readLine ();
            }

            /*
            name
            x
            y
            class
            stats
            growth
            inventory
            image path
            */
            //System.out.println ("TD: " + tempData.size());
            for (int i = 0; i < tempData.size (); i += 10)
            {
                String [] inventory = tempData.get (i + 6).toString ().split (":");
                String [] statsTemp = tempData.get (i + 4).toString ().split (":");
                int [] stats = new int [statsTemp.length];
                String [] growthsTemp = tempData.get (i + 5).toString ().split (":");
                int [] growths = new int [statsTemp.length];

                UnitClass unitClass = null;
                for (int j = 0; j < unitClasses.size (); j ++)
                {
                    if (tempData.get (i + 3).equals (((UnitClass)unitClasses.get (j)).getName ()))
                    {
                        unitClass = (UnitClass)unitClasses.get (j);
                        break;
                    }
                }

                for (int j = 0; j < stats.length; j ++)
                {
                    stats [j] = Integer.parseInt (statsTemp [j]);
                    growths [j] = Integer.parseInt (growthsTemp [j]);
                }
                //!! Really ugly implementation
                members.add (new Unit (tempData.get (i).toString (), 
                        Integer.parseInt (tempData.get (i + 1).toString ()) * map.getTileSize () + insets.left, 
                        Integer.parseInt (tempData.get (i + 2).toString ()) * map.getTileSize () + insets.top, 
                        unitClass, stats, growths, 
                        new ArrayList (Arrays.asList (inventory)), (String)tempData.get (i + 7), 
                        tempData.get (i + 8).toString (), Integer.parseInt (tempData.get (i + 9).toString ()), map));
            }
        }
        catch (IOException e)
        {
            System.out.println ("No unit file");
        }
        catch (NullPointerException e)
        {
            System.out.println ("Improper info in unit file");
        }
        
    }
    
    /**
     * Determine if unit is on this team
     * @param unit unit to check
     * @return true if unit is on team
     */
    public boolean onTeam (Unit unit)
    {
        for (int i = 0; i < this.members.size (); i ++)
        {
            if (unit == members.get (i))
            {
                return true;
            }
        }
        return false;
    }
    
}