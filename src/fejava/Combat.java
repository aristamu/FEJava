
package fejava;

/**
 * Combat between two units
 * @author Arista
 */

import java.util.ArrayList;
import java.util.Random;

public class Combat
{
    
    /**
     * Constructor
     * @param atkr attacking unit
     * @param defr defending unit
     */
    public Combat (Unit atkr, Unit defr)
    {
        //round (atkr, defr);
    }
    
    
    /**
     * One round of combat
     * @param atkr attacking unit
     * @param defr defending unit
     * @return list of combat actions that occurred to display in main class graphics
     */
    public ArrayList round (Unit atkr, Unit defr)
    {
        /*
        Element      0    1   2   3   4    5   6
        Stat       MaxHP Atk Spd Hit Crit Def Res
        
        Dodge based on spd
        Hit based on hit
        Crit based on crit - enemy crit
        */
        
        ArrayList actions = new ArrayList ();
        
        Random rn = new Random ();
        
        int [] atkrStats = atkr.getStats();
        int [] defrStats = defr.getStats();
        
        int damTypeAtkr = atkr.getWeapon ().getType ();
        int damTypeDefr = defr.getWeapon ().getType ();
        
        int action = 0;
        int dam;
        boolean canCounter = true;
        
        //I'm so sorry
        //This is such a horrific way to implement this
        //But I had to do it
        //There was no other way

        //Well, there probably was. but I'm too lazy for that
        //Still, I'm sorry
        atkrStats [1] += atkr.getWeapon ().getStats (0);
        atkrStats [3] += atkr.getWeapon ().getStats (1);
        atkrStats [4] += atkr.getWeapon ().getStats (2);

        defrStats [1] += defr.getWeapon ().getStats (0);
        defrStats [3] += defr.getWeapon ().getStats (1);
        defrStats [4] += defr.getWeapon ().getStats (2);
        if (atkr.getWeapon ().getRange () != defr.getWeapon ().getRange ())
        {
            canCounter = false;
        }
        //Checks if alive after every round
        while (isAlive (atkr, defr) == true && isAlive (defr, atkr) == true && action < 3)
        {
            if (action == 0)
            {
                //System.out.println ("A: " + action);
                actions.add (atkr.getName () + ":" + defr.getName () + ":" + defr.getHP ());
                dam = hitChance (rn, atkrStats, defrStats, action, actions);
                //if negative number, leave HP unchanged
                defr.setHP (defr.getHP() - (dam - defrStats [damTypeDefr] < 0 ? 0 : dam - defrStats [damTypeDefr]));
                actions.add (action, ((String)actions.get (action)).concat (":" + defr.getHP ()));
            }
            else if (action == 1 && canCounter)
            {
                //System.out.println ("A: " + action);
                actions.add (1, defr.getName () + ":" + atkr.getName () + ":" + atkr.getHP ());
                dam = hitChance (rn, defrStats, atkrStats, action, actions);
                atkr.setHP (atkr.getHP() - (dam - atkrStats [damTypeAtkr] < 0 ? 0 : dam - atkrStats [damTypeAtkr]));
                actions.add (action, ((String)actions.get (action)).concat (":" + atkr.getHP ()));
            }
            //Units may double if speed is high enough
            else if (action == 2 && atkrStats [2] - defrStats [2] >= 4)
            {
                //System.out.println ("A: " + action);
                actions.add (2, atkr.getName () + ":" + defr.getName ()+ ":" + defr.getHP ());
                dam = hitChance (rn, atkrStats, defrStats, action, actions);
                defr.setHP (defr.getHP() - (dam - defrStats [damTypeDefr] < 0 ? 0 : dam - defrStats [damTypeDefr]));
                actions.add (action, ((String)actions.get (action)).concat (":" + defr.getHP ()));
            }
            else if (action == 2 && defrStats [2] - atkrStats [2] >= 4 && canCounter)
            {
                //System.out.println ("A: " + action);
                actions.add (2, defr.getName () + ":" + atkr.getName () + ":" + atkr.getHP ());
                dam = hitChance (rn, defrStats, atkrStats, action, actions);
                atkr.setHP (atkr.getHP() - (dam - atkrStats [damTypeAtkr] < 0 ? 0 : dam - atkrStats [damTypeAtkr]));
                actions.add (action, ((String)actions.get (action)).concat (":" + atkr.getHP ()));
            }
            //System.out.println ("S @ C: " + actions.size ());
            action ++;
        }
        atkrStats [1] -= atkr.getWeapon ().getStats (0);
        atkrStats [3] -= atkr.getWeapon ().getStats (1);
        atkrStats [4] -= atkr.getWeapon ().getStats (2);

        defrStats [1] -= defr.getWeapon ().getStats (0);
        defrStats [3] -= defr.getWeapon ().getStats (1);
        defrStats [4] -= defr.getWeapon ().getStats (2);
        //System.out.println (atkr.getHP());
//        System.out.println (defr.getHP());
//        for (int i = 0; i < actions.size (); i ++)
//        {
//            System.out.println (i + " " + actions.get (i));
//        }
        
        return actions;
    }
    
    
    /**
     * Checks if unit is still alive to end combat early if necessary
     * @param unit unit being checked
     * @return true if alive, otherwise false
     */
    public boolean isAlive (Unit unit, Unit murderer)
    {
        if (unit.getHP() <= 0)
        {
            murderer.addExp (unit.getExp () / murderer.getLevel ());
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * Determine hit, miss, or crit using RNG and stats
     * @param rn random object
     * @param atkrStats stats of unit hitting
     * @param defrStats stats of unit being hit
     * @param index index showing where to assign combat action info
     * @param actions list of combat actions 
     * @return damage before subtracting def/res
     */
    public int hitChance (Random rn, int [] atkrStats, int [] defrStats, int index, ArrayList actions)
    {
        //Inlcuding 1, including 100
        int num = rn.nextInt(100) + 1;
        if (num <= atkrStats [4] - defrStats [4])
        {
            actions.add (index, (((String)actions.get (index)).concat (":CRITICAL hits")));
            //System.out.println ("Crit");
            return atkrStats [1] * 3;
        }
        else if (num <= atkrStats [3] * 8 - defrStats [2] * 2)
        {
            //System.out.println (atkrStats [3]);
            actions.add (index, (((String)actions.get (index)).concat (":HITS")));
            //System.out.println ("Hit");
            return atkrStats [1];
        }
        else
        {
            actions.add (index, (((String)actions.get (index)).concat (":MISSES")));
            //System.out.println ("Miss");
            return 0;
        }
    }
    
    // REWRITE BELOW
    // ?? Possibly create a separate class for actions, allowing for skills, etc
    
    public ArrayList roundOrder (Unit atkr, Unit defr)
    {
        /*
        Each action has a priority, which determines its position in the list combined with
            unit's initiative
        So attack = 1, counter = 5, double = 10 (for either side)
        Then vantage changes counter to 0, desperation changes double to 2, etc
        Each unit or weapon has a "standard action" built in, so everyone's default
            standard defense is counter, but a weapon might replace that with vantage
        Then actions are generated from the weapons wielded
        Same priority then just go with default sorted order
        Upon initiation, add all actions to the order, then sort, then execute
        So weapon is brave then add two standard attacks, let it sort, then go
        Standard actions are lists, extract from list and then add to round order
        */
        return null;
    }
    
}
