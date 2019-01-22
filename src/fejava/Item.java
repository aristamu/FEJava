
package fejava;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * By Arista Mueller
 */
public class Item 
{
    //DOESN'T CURRENTLY WORK, THOUGH MOSTLY IMPLEMENTED
    
    //!!Add option to use items on self
    
    //Weapons used against enemies, items used on allies
    //Abilities will probably be specialised weapons/items
    private String name;
    private int [] stats;
    private int hp;
    private String type; //On self or on target
    //private int [] growths;
    
    /**
     * Item or ability being created
     * @param name name of item, used to find info from text file
     * @throws IOException if items.txt not found
     */
    public Item (String name) throws IOException
    {
        //!! Don't set name before testing if in list
        this.name = name;
        ArrayList tempData = new ArrayList ();
        BufferedReader file = new BufferedReader (new FileReader (FEJava.class.getResource ("/FEJava/resources/text/items.txt").getFile ()));
        
        String line = file.readLine ();
        while (line != null)
        {
            tempData.add (line);
            line = file.readLine ();
        }
        
        String tempStats;
        this.stats = new int [7];
        for (int i = 0; i < tempData.size(); i += 4)
        {
            if (this.name.equals(tempData.get(i)))
            {
                this.type = tempData.get (i + 1).toString ();
                String [] parts = ((String)tempData.get (i + 2)).split(":");
                for (int j = 0; j < parts.length; j ++)
                {
                    this.stats [j] = Integer.parseInt (parts [j]);
                }
                this.hp = Integer.parseInt (tempData.get (i + 3).toString ());
                break;
            }
        }
    }
    
    /**
     * Use item by adding values to unit stats
     * CURRENTLY ONLY WORKS ON SELF
     * @param unit 
     */
    public void useItem (Unit unit)
    {
        for (int i = 0; i < this.stats.length; i ++)
        {
            unit.setStats (i, this.stats [i] + unit.getStats (i));
        }
        unit.setHP (this.hp + unit.getHP ());
        
        //If new HP is greater than max, set HP to max
        if (unit.getHP () > unit.getStats (0))
        {
            unit.setHP (unit.getStats (0));
        }
    }

    /**
     * Gets name of item
     * @return name of item
     */
    public String getName ()
    {
        return name;
    }

    /**
     * Gets item type
     * @return type (S if on self, T if on target)
     */
    public String getType ()
    {
        return type;
    }
    
}
