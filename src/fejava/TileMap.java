
package fejava;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Map for level
 * @author Arista
 */
public class TileMap {
    
    //Dimensions of tiles (square)
    private int tileSize;
    //in tiles
    private int height;
    private int width;
    //Arrays to hold terrain and unit locations
    private int [][] level;
    private int [][] unitTest;
    private double panelWidth;
    
    
    /**
     * Constructor
     * @param sourceFile image path of map text file
     * @param windowHeight height of window (insets removed)
     * @param windowWidth width of window (insets removed)
     */
    public TileMap (String sourceFile, double windowHeight, double windowWidth)
    {
        //!!Have check in case screen is square to reserve some side panel space
        // Could just force screen to be good dimensions
        //!!Get height and width using file input
        //Or something like that
        //First line of file contains height and width?
        height = 26;
        width = 32;
        
        level = readLevel (height, width, FEJava.class.getResource (sourceFile).getFile ());
    
        //System.out.println ("A" + levelTest[0][0]);
        
        //size of tiles based on height
        tileSize = (int)(windowHeight) / (height);
        
        panelWidth = windowWidth - width * tileSize;
        
        unitTest = new int [height][width];
    }
    
    
    /**
     * Reads file input and assigns to two dimensional array
     * @param height height in tiles
     * @param width width in tiles
     * @param file path of file to read from
     * @return 2d array of level input
     */
    public int[][] readLevel (int height, int width, String file)
    {
        try 
        {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader (fileReader);

            ArrayList lines = new ArrayList ();
            int [][] levelTest = new int [height][width];
            String line = null;

            while ((line = reader.readLine ()) != null)
            {
                lines.add (line);
            }

            reader.close();
            
            //!!Edit to read int rather than char to allow > 10 images
            //Or use other chars and later translate these to other numbers
            //In order to keep level files uniform in size
            for(int y = 0; y < height; y++)
            {
                line = (String) lines.get (y);
                String [] parts = line.split (" ");
                for(int x = 0; x < parts.length; x++)
                {
                    //!!Split strings instead?
                    
                    levelTest [y][x] = Integer.parseInt (parts [x]);
                    //levelTest [y][x] = (int) (parts [x]).charAt(0);
                    //System.out.println (levelTest [x][y]);
                }
            }
            System.out.println ((int) levelTest [0][0]);
            return levelTest;
        }
        
        catch (FileNotFoundException e) { } 
        catch (IOException ex) { }

        System.out.println ("Stuff messed up");
        return null;
    }
    
    /**
     * Gets tile size
     * @return dimensions of tiles
     */
    public int getTileSize ()
    {
        return tileSize;
    }
    
    /**
     * Gets height of map
     * @return height in tiles
     */
    public int getHeight ()
    {
        return height;
    }
    
    /**
     * Gets width of map
     * @return width in tiles
     */
    public int getWidth ()
    {
        return width;
    }
    
    /**
     * Gets specific tile value
     * @param x x of desired tile
     * @param y y of desired tile
     * @return integer value at desired location
     */
    public int getTile (int x, int y)
    {
        return level [y][x];
    }
    
    public int getCost (Unit unit, int x, int y)
    {
        int cost = 0;
        
        switch (level [y][x])
        {
            case (0):
                cost = 1;
                break;
            case (1):
                cost = 1;
                break;
            case (2):
                cost = 3;
                break;
            case (3):
                cost = 5;
                break;
            case (4):
                cost = 20;
        }
        return cost;
    }
    
    public int getPanelWidth ()
    {
        return (int)panelWidth;
    }
}
