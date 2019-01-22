
package fejava;

import java.awt.*;

/**
 * Creates all unit classes for units
 * @author Arista
 */
public class UnitClass {
    
    private int [] stats;
    private int [] growth;
    private String imagePath;
    //private Image image;
    private int moveDistance;
    private int [] moveRestrictions;
    private String name;
    /**
     * Constructor
     * @param name name of class
     * @param stats base levels, added to character levels
     * @param growth growth rates for level up
     * @param moveDistance distance allowed for pathfinding
     * @param map map used
     */
    public UnitClass (String name, int [] stats, int [] growth, int moveDistance, 
            TileMap map)
    {
        this.name = name;
        this.stats = stats;
        this.growth = growth;
        this.moveDistance = moveDistance;   
    }
    
    /**
     * gets allowed movement distance
     * @return distance
     */
    public int getMoveDist ()
    {
        return moveDistance;
    }
    
    /**
     * Gets name of class
     * @return class name
     */
    public String getName ()
    {
        return name;
    }
}
