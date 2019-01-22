
package fejava;

import java.io.*;
import java.util.ArrayList;

/**
 * By Arista Mueller
 */
public class Weapon 
{
    private String name;
    private int type;
    private int [] stats;
    private int range;
    
    //!!Weapon effectiveness?
    //private String effective;
    //??Durability?
    
    /**
     * Constructor for weapons
     * @param name name of weapon, used to find stats from text file
     * @throws IOException only happens if files missing
     */
    public Weapon (String name) throws IOException
    {
        this.name = name;
        ArrayList tempData = new ArrayList ();
        BufferedReader file = new BufferedReader (new FileReader (FEJava.class.getResource ("/FEJava/resources/text/weapons.txt").getFile ()));
        
        String line = file.readLine ();
        while (line != null)
        {
            tempData.add (line);
            line = file.readLine ();
        }
        
        this.stats = new int [3];
        for (int i = 0; i < tempData.size(); i += 4)
        {
            //System.out.println (name + "test");
            //System.out.println (i);
            //System.out.println (this.name.equals (tempData.get (i)));
            if (this.name.equals(tempData.get(i)))
            {
                this.type = Integer.parseInt (tempData.get (i + 1).toString ())
                        ;
                String [] parts = ((String)tempData.get (i + 2)).split(":");
                for (int j = 0; j < parts.length; j ++)
                {
                    this.stats [j] = Integer.parseInt (parts [j]);
                }
                this.range = Integer.parseInt (tempData.get (i + 3).toString ());
                //System.out.println (name + "created");
                break;
            }
            if (i == tempData.size () - 4 && !this.name.equals (tempData.get (i)))
            {
                //System.out.println (name + "not created");
                throw new IOException ();
            }
        }
    }

    /**
     * Gets attack range of weapon
     * @return attack range, either 1 or 2
     */
    public int getRange ()
    {
        return range;
    }

    /**
     * Gets name of weapon
     * @return name
     */
    public String getName ()
    {
        return name;
    }

    /**
     * Gets type of weapon
     * @return 5 if targets def, 6 if targets res
     */
    public int getType ()
    {
        return type;
    }

    /**
     * Gets stats of weapon
     * @return stats (damage, hit, crit)
     */
    public int[] getStats ()
    {
        return stats;
    }
    
    /**
     * Gets stat at specific position
     * @param pos index of desired stat
     * @return value at desired index
     */
    public int getStats (int pos)
    {
        return stats [pos];
    }

}
