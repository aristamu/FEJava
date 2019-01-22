
package fejava;

import java.util.ArrayList;

/**
 * Path taken by pathfinding
 * Edited by Arista
 * @author Arista
 */
public class Path 
{
    private ArrayList steps = new ArrayList();
    private float cost;
    
    /**
     * Constructor
     */
    public Path() 
    { 
        this.cost = 0;
    }

    /**
     * Gets number of steps in path
     * @return length of path
     */
    public int getLength() 
    {
        return steps.size();
    }
    
    public ArrayList getSteps ()
    {
        return steps;
    }
    
    /**
     * Gets step at index of path
     * @param index index of desired step
     * @return step at index
     */
    public Step getStep(int index) 
    {
        return (Step) steps.get(index);
    }
    
    
    /**
     * Gets x of step
     * @param index index of desired step
     * @return x of selected step
     */
    public int getX(int index)
    {
        return getStep(index).x;
    }
    
    
    /**
     * Gets y of step
     * @param index index of desired step
     * @return y of selected step
     */
    public int getY(int index)
    {
        return getStep(index).y;
    }
    
    
    /**
     * Adds step to end of path
     * @param x x of step
     * @param y y of step
     */
    public void appendStep(int x, int y) 
    {
        steps.add(new Step(x,y));
    }
    
    
    /**
     * Adds step to start of path
     * @param x x of step
     * @param y y of step
     * @param map map to get cost from
     * @param unit unit to get costs of step
     */
    public void prependStep(int x, int y, TileMap map, Unit unit) 
    {
        steps.add(0, new Step(x, y));
        this.cost += map.getCost(unit, x, y);
    }
    
    
    /**
     * Check if path contains a step
     * @param x x of step
     * @param y y of step
     * @return true if path contains step
     */
    public boolean contains(int x, int y) 
    {
        return steps.contains(new Step(x,y));
    }
    

    /**
     * gets total cost of path for testing purposes
     * @return total cost of path
     */
    public float getCost ()
    {
        return cost;
    }
    
    
    /**
     * Each movement along path
     */
    public class Step 
    {
        
        private int x;
        private int y;
        
        
        /**
         * Constructor
         * @param x x of step
         * @param y y of step
         */
        public Step(int x, int y) 
        {
            this.x = x;
            this.y = y;
        }
        
        
        /**
         * Gets x of step
         * @return x
         */
        public int getX() 
        {
            return x;
        }
        
        
        /**
         * Gets y of step
         * @return y
         */
        public int getY() 
        {
            return y;
        }
    }
}
