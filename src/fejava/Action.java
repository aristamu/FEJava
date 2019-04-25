
package fejava;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author ginkg
 */
public class Action
{
    private String name;
    private int priority;
    
    private int [] multipliers;
    
    public Action (String name) throws IOException
    {
        BufferedReader file = new BufferedReader (new FileReader (FEJava.class.getResource 
        ("/FEJava/resources/text/actions.txt").getFile ()));
        
        String line = file.readLine ();
        while (line != null)
        {
            // !! Check comparison
            if (line.equals (name + "\n"))
            {
                this.name = name;
                this.priority = Integer.parseInt (file.readLine ());
                String [] parts = file.readLine ().split (":");
                if (parts.length != 7)
                {
                    // ?? Pick better exception
                    // Input file written improperly
                    throw new IOException ();
                }
                this.multipliers = new int [7];
                for (int i = 0; i < 7; ++ i)
                {
                    multipliers [i] = Integer.parseInt (parts [i]);
                }
            }
            // !! Skip non-name lines
            line = file.readLine ();
        }
        if (!this.name.equals (name))
        {
            // Input file does not contain name
            throw new IOException ();
        }
    }

    public String getName ()
    {
        return name;
    }

    public int getPriority ()
    {
        return priority;
    }

    public int [] getMultipliers ()
    {
        return multipliers;
    }
    
}
