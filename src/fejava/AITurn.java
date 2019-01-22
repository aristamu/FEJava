
package fejava;

import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

/**
 * By Arista Mueller
 */
public class AITurn 
{
    private Unit unit;
    
    /**
     * Creates object to determine enemy movements
     * @param unit enemy taking turn
     */
    public AITurn (Unit unit)
    {
        this.unit = unit;
        //turn (unit, tilesInRange, protag, antag, map, insets);
    }
    
    /**
     * One enemy turn
     * @param unit enemy unit moving
     * @param tilesInRange all tiles enemy can move to
     * @param protag all player units
     * @param antag all enemy units
     * @param map
     * @param insets
     * @return combat actions list if combat happens, otherwise null
     */
    public ArrayList turn (Unit unit, ArrayList tilesInRange, ArrayList protag, 
            ArrayList antag, TileMap map, Insets insets)
    {
        //!! Clean this up
        String behaviour = unit.getBehaviour ();
        
        //Don't move ever
        //Attack only if within range without moving
        if (behaviour.equals ("Defensive"))
        {
            ArrayList unitsInRange = checkAttackRange (unit, protag, unit.getWeapon ().getRange (), map.getTileSize ());
            if (!unitsInRange.isEmpty ())
            {
                //Combat against weakest enemy
                return initiateCombat (unit, weakestUnit (unit, unitsInRange), protag, antag);
            }
            return null;
        }
        //Don't move unless enemy is within move dist
        else if (behaviour.equals ("Normal"))
        {
            Unit target = weakestUnit (unit, getUnitsInMoveDist (tilesInRange, protag, unit.getWeapon ().getRange (), map.getTileSize ()));
            int range = unit.getWeapon ().getRange ();
            
            if (target != null)
            {
                Point location = moveTo (unit, target, tilesInRange, map.getTileSize ());
                unit.setX (location.x);
                unit.setY (location.y);
                return initiateCombat (unit, target, protag, antag);
            }
            return null;
        }
        //Seek out target and attack if within movement range
        else if (behaviour.equals ("Aggressive"))
        {
            if (weakestUnit (unit, getUnitsInMoveDist (tilesInRange, protag, unit.getWeapon ().getRange (), map.getTileSize ()))
                    == null)
            {
                int distance = 2166000;
                Unit target = null;
                for (int i = 0; i < protag.size (); i ++)
                {
                    if (separationDistance (unit.getX (), unit.getY (), ((Unit)protag.get (i)).getX (), ((Unit)protag.get (i)).getY ())
                            < distance)
                    {
                        target = (Unit)protag.get (i);
                        distance = separationDistance (unit.getX (), unit.getY (), ((Unit)protag.get (i)).getX (), ((Unit)protag.get (i)).getY ());
                    }
                }
                Point newLocation = moveTowards (unit, target.getX (), target.getY (), map, protag, tilesInRange, insets);
                unit.setX (newLocation.x);
                unit.setY (newLocation.y);
                return null;
            }
            else
            {
                Unit target = weakestUnit (unit, getUnitsInMoveDist (tilesInRange, protag, unit.getWeapon ().getRange (), map.getTileSize ()));
                int range = unit.getWeapon ().getRange ();
            
                Point location = moveTo (unit, target, tilesInRange, map.getTileSize ());
                unit.setX (location.x);
                unit.setY (location.y);
                return initiateCombat (unit, target, protag, antag);
            }
        }
        return null;
    }
    
    /**
     * Gets weakest unit within range
     * @param unit enemy unit
     * @param unitsInRange opposing team units that are available
     * @return 
     */
    private Unit weakestUnit (Unit unit, ArrayList unitsInRange)
    {
        //!! Add score for units with different ranges
        if (unitsInRange.isEmpty ())
        {
            return null;
        }
        //Target def or res depending on attacker's weapon type
        int targettedStat = unit.getWeapon ().getType ();
        if (targettedStat == 5)
        {
            Collections.sort (unitsInRange, Unit.defCompare);
        }
        else
        {
            Collections.sort (unitsInRange, Unit.resCompare);
        }
        return (Unit)unitsInRange.get (0);
    }
    
    /**
     * Move to a unit that is already within movement range
     * @param unit enemy unit moving
     * @param target player unit being targeted, determined by weakestUnit
     * @param tilesInRange all tiles unit can move to
     * @param tileSize size of tiles (needed for movement)
     * @return point unit should move to in order to be within attack range of target
     */
    private Point moveTo (Unit unit, Unit target, ArrayList tilesInRange, int tileSize)
    {
        int shortestDistance = 2000000;
        Point closestTile = null;
        int range = unit.getWeapon ().getRange ();
        for (int x = -(range); x < 2 * range + 1; x ++)
        {
            for (int y = -(range); y < 2 * range + 1; y ++)
            {
                if (Math.abs (x) + Math.abs (y) != range)
                {
                    continue;
                }
                Point newTile = new Point (target.getX () + (x * tileSize), target.getY () + (y * tileSize));
                if (tilesInRange.contains (newTile))
                {
                    if (separationDistance (unit.getX (), unit.getY (), newTile.x, newTile.y) < shortestDistance)
                    {
                        closestTile = newTile;
                    }
                }
            }
        }
        return closestTile;
    }
    
    /**
     * Distance between 2 points
     * @param startX x of start
     * @param startY y of start
     * @param endX x of end
     * @param endY y of end
     * @return total distance (does not use diagonal movement)
     */
    private int separationDistance (int startX, int startY, int endX, int endY)
    {
        return Math.abs (startX - endX) + Math.abs (startY - endY);
    }
    
    /**
     * Move towards a target that is not within move distance
     * @param unit enemy unit moving
     * @param targetX x of target unit
     * @param targetY y of target unit
     * @param map 
     * @param opposingUnits all enemies on the other team
     * @param tilesInRange all tiles unit can move to 
     * @param insets
     * @return point that brings unit closest to target
     */
    private Point moveTowards (Unit unit, int targetX, int targetY, TileMap map, ArrayList opposingUnits, ArrayList tilesInRange, Insets insets)
    {
        PathFinding pathFind = new PathFinding (map);
        
        //!! division by tileSize not exact
        //!! Make sure using invalid not high costs for terrain
        Path path = pathFind.findPath ((targetX - insets.left) / map.getTileSize (), (targetY - insets.top) / map.getTileSize (), unit.getX () / map.getTileSize (),
                unit.getY () / map.getTileSize (), unit, new ArrayList (), 20000000);
        
        //Find path with unlimited move distance, and pick the furthest along step in that path
        if (path != null)
        {
            ArrayList steps = path.getSteps ();
            for (int i = steps.size () - 1; i > 0; i --)
            {
                Point stepPoint = new Point (path.getX (i) * map.getTileSize () + insets.left, path.getY (i) * map.getTileSize () + insets.top);
                if (tilesInRange.contains (stepPoint))
                {
                    return stepPoint;
                }
            }
        }
        //Don't move if stuck somehow
        return new Point (unit.getX (), unit.getY ());
    }
    
    /**
     * Checks if any player unit is within range
     * @param unit enemy unit attacking
     * @param enemies list of player units
     * @param range range enemy unit can attack from
     * @param tileSize
     * @return list of player units that are within attacking range
     */
    private ArrayList checkAttackRange (Unit unit, ArrayList enemies, int range, int tileSize)
    {
        ArrayList unitsInRange = new ArrayList ();
        for (int i = 0; i < enemies.size (); i ++)
        {
            for (int x = -(range); x < 2 * range + 1; x ++)
            {
                for (int y = -(range); y < 2 * range + 1; y ++)
                {
                    if (Math.abs (x) + Math.abs (y) != range)
                    {
                        continue;
                    }
                    
                    if (unit.getX () + (x * tileSize) == ((Unit)enemies.get (i)).getX ()
                            && unit.getY () + (y * tileSize) == ((Unit)enemies.get (i)).getY ())
                    {
                        unitsInRange.add (enemies.get (i));
                    }
                }
            }
        }
        return unitsInRange;
    }
    
    /**
     * Combat between two units
     * @param atkr enemy unit attacking
     * @param defr player unit defending
     * @param protag list of player units
     * @param antag list of enemy units
     * @return list of combat actions to display in main class graphics
     */
    private ArrayList initiateCombat (Unit atkr, Unit defr, ArrayList protag, ArrayList antag)
    {
        ArrayList combatActions;
        Combat combat = new Combat (atkr, defr);
        combatActions = combat.round (atkr, defr);
        if (atkr.getHP () <=0)
        {
            antag.remove (atkr);
        }
        if (defr.getHP () <= 0)
        {
            protag.remove (defr);
        }
        return combatActions;
    }
    
    /**
     * Shows all player units within enemy move distance
     * @param tilesInRange tiles enemy can move to
     * @param enemies all player units that could be targeted
     * @param range weapon range, added to tilesInRange to determine attacking range
     * @param tileSize
     * @return list of all units that are within attacking range
     */
    private ArrayList getUnitsInMoveDist (ArrayList tilesInRange, ArrayList enemies, int range, int tileSize)
    {
        ArrayList possibleEnemies = new ArrayList ();
        for (int i = 0; i < tilesInRange.size (); i ++)
        {
            for (int j = 0; j < enemies.size (); j ++)
            {
                for (int x = -(range); x < 2 * range + 1; x ++)
                {
                    for (int y = -(range); y < 2 * range + 1; y ++)
                    {
                        if (Math.abs (x) + Math.abs (y) != range)
                        {
                            continue;
                        }
                        if (((Point)tilesInRange.get (i)).x + (x * tileSize) == ((Unit)enemies.get (j)).getX ()
                                && ((Point)tilesInRange.get (i)).y + (y * tileSize) == ((Unit)enemies.get (j)).getY ())
                        {
                            possibleEnemies.add (enemies.get (j));
                        }
                    }
                }
            }
        }
        return possibleEnemies;
    }
}