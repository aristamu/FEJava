/*
 * 
 */
package fejava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;

/**
 * Finds best pathway to location
 * Edited by Arista
 * @author Arista
 */
public class PathFinding 
{
    //Lists for nodes visited and nodes to check
    private ArrayList closed = new ArrayList ();
    private ArrayList open = new ArrayList();
    
    private TileMap map;
    
    private Node [][] nodes;
    
    
    /**
     * Constructor
     * @param map map on which to find path
     */
    public PathFinding (TileMap map)
    {
        this.map = map;
        //Uses height, width in tiles
        nodes = new Node [map.getWidth()][map.getHeight()];
        
        for (int x = 0; x < map.getWidth(); x ++)
        {
            for (int y = 0; y < map.getHeight(); y ++)
            {
                nodes [x][y] = new Node (x, y);
            }
        }
    }
    
    /**
     * Finds best pathway to location
     * @param tx x of target location
     * @param ty y of target location
     * @param sx x of unit moving
     * @param sy y of unit moving
     * @param unit unit moving
     * @param enemyUnits list of all enemy units to prevent pathing through enemies
     * @param moveDist maximum distance to be checked, usually unit's moveDist
     * @return Path or null if no valid pathway
     */
    public Path findPath (int tx, int ty, int sx, int sy, Unit unit, ArrayList enemyUnits, int moveDist)
    {
        try
        {
            //nodes[sx][sy].cost = 0;
        nodes[sx][sy].depth = 0;
        
        closed.clear ();
        open.clear ();
        open.add (nodes [sx][sy]);
        
        //## ArrayIndexOutOfBoundsException: -1
        try
        {
            nodes[tx][ty].parent = null;
        }
        catch (ArrayIndexOutOfBoundsException e) { }
        
        
        while (!open.isEmpty())
        {
            try 
            {
                //Sorts by distance scores
                Collections.sort (open);
            }
            catch (NullPointerException e) { }
            catch (ConcurrentModificationException e)
            {
                System.out.println ("Concurrent");
            }
            
            Node current = (Node)open.get (0);
            //Ends when reaches target node
            if (current == nodes [tx][ty])
            {
                break;
            }
            
            open.remove (current);
            closed.add (current);
            //Stops if over moveDist and tries other paths
            if (current.depth >= moveDist)
            {
                continue;
            }
            //Adds adjacent nodes to list to be checked
            for (int x = -1; x <= 1; x ++)
            {
                for (int y = -1; y <= 1; y ++)
                {
                    //Removes diagonal moves
                    if (x != 0 && y != 0 || (x == 0 && y == 0))
                    {
                        continue;
                    }
                    
                    int xp = x + current.x;
                    int yp = y + current.y;
                    
                    if (validLocation (unit, sx, sy, xp, yp, enemyUnits))
                    {
                        //Doesn't matter which moveCost method is used
                        float nextCost = map.getCost(unit, xp, yp);//getMovementCost (unit, xp, yp); //current.cost + ...
                        //System.out.printf ("\tCost: %f", nextCost);
                        Node neighbour = nodes [xp][yp];
                        
                        neighbour.heuristic = approximateDistance (sx, sy, tx, ty);
                        
                        if (open.contains (neighbour))
                        {
                            //Change path to tile if cheaper way to get there found
                            if (neighbour.fScore > neighbour.depth + neighbour.heuristic)
                            {
                                neighbour.parent = current;
                            }
                        }
                        
                        //if adjacent not checked yet, add it to possible next steps
                        if (!open.contains (neighbour) && !closed.contains (neighbour))
                        {
                            neighbour.setDepth(current, nextCost);
                            //System.out.printf ("\nDist: %d", searchDistance);
                            if (neighbour.depth <= moveDist)
                            {
                                neighbour.setParent(current, nextCost);
                                neighbour.fScore = neighbour.depth + neighbour.heuristic;
                                open.add (neighbour);
                            }
                        }
                    }
                }
            }
            //System.out.printf ("\tDist: %d", searchDistance);
        }
        
        //if no valid path, return null
        if (nodes [tx][ty].parent == null)
        {
            return null;
        }
        
        //if valid path, returns this path
        Path path = new Path ();
        Node target = nodes [tx][ty];
        while (target != nodes [sx][sy])
        {
            path.prependStep (target.x, target.y, map, unit);
            //System.out.println ("Target x: " + target.x + " Target y: " + target.y);
            target = target.parent;
        }
        //Adds unit start place to steps
        //path.prependStep(sx, sy);
                
        return path;
        }
        catch (ArrayIndexOutOfBoundsException e) { }
        return null;
    }
    
    /**
     * Gets cost of node
     * @param unit unit moving
     * @param x x coordinate of desired tile
     * @param y y coordinate of desired tile
     * @return cost 
     */
    public float getMovementCost (Unit unit, int x, int y)
    {
        switch (map.getTile (x, y))
        {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 30;
        }
        return 0;
    }
    
    /**
     * Estimates approximate distance between points
     * @param sx unit x
     * @param sy unit y
     * @param tx target x
     * @param ty target y
     * @return approximate distance
     */
    //?? Possibly unnecessary
    public int approximateDistance (int sx, int sy, int tx, int ty)
    {
        return ((int)(Math.abs (sx - tx)) / map.getTileSize() + (Math.abs (sy - ty)) / map.getTileSize());
    }
    
    /**
     * Checks if location is not blocked
     * @param unit unit type
     * @param sx unit x
     * @param sy unit y
     * @param x tile x
     * @param y tile y
     * @return true if location is valid
     */
    public boolean validLocation (Unit unit, int sx, int sy, int x, int y, ArrayList enemyUnits)
    {
        boolean invalid;
        invalid = (x < 0) || (y < 0) || (x >= map.getWidth()) || (y >= map.getHeight());
                
        if ((!invalid) && (sx != x) || (sy != y))
        {
            //invalid = map.blocked ();
        }
        for (int i = 0; i < enemyUnits.size (); i ++)
        {
            //System.out.printf ("X %d Y %d X %d Y %d\n", x, y, ((Unit)enemyUnits.get (i)).getX (), ((Unit)enemyUnits.get (i)).getY ());
            if (x == ((Unit)enemyUnits.get (i)).getX () / map.getTileSize ()
                    && y == ((Unit)enemyUnits.get (i)).getY () / map.getTileSize ())
            {
                //System.out.println ("CONTAINS");
                invalid = true;
            }
        }
        return !invalid;
    }
    
    /**
     * Each node/tile on map
     */
    private class Node implements Comparable
    {
        private int x;
        private int y;
        private float fScore;
        private Node parent;
        private float heuristic;
        private int depth;
        
        /**
         * Constructor
         * @param x x of node
         * @param y y of node
         */
        public Node (int x, int y)
        {
            this.x = x;
            this.y = y;
        }
        
        /**
         * Sets where node originated from
         * @param parent parent of node/where it came from
         * @return depth
         */
        public int setParent (Node parent, float cost)
        {
            this.parent = parent;
            return depth;
        }
        
        /**
         * Sets depth (total cost)
         * @param parent previous node
         * @param cost cost to move to next node
         */
        public void setDepth (Node parent, float cost)
        {
            depth = parent.depth + (int)cost;
        }

        /**
         * Compare node scores to sort by lowest cost
         * @param t other node being compared
         * @return difference in scores
         */
        @Override
        public int compareTo(Object t) 
        {
            return (int)fScore - (int)((Node)t).fScore;
        }
    }
}