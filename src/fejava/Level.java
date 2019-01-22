
package fejava;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.*;
import java.util.ArrayList;

/**
 * By Arista Mueller
 */
public class Level 
{
    private String levelName;
    private TileMap map;
    //!!Possibly use ArrayList instead
    private Team protag;
    private Team antag;
    private Team misc;
    private ArrayList unitClasses;
    
    /**
     * Creates each chapter and all necessary information
     * @param sourceFile file containing chapter data, titles "level..."
     * @param width windowWidth
     * @param height windowHeight
     * @param insets 
     * @throws IOException if file not found, should not happen
     */
    public Level (String sourceFile, int width, int height, Insets insets) throws IOException 
    {
        /*
        Source File:
        1. map file
        2. protagonist team name
        3. protagonist team member file
        4. antagonist team name
        5. antagonist team member file
        */
        
        BufferedReader file = new BufferedReader (new FileReader (FEJava.class.getResource (sourceFile).getFile ()));
        
        this.map = new TileMap (file.readLine (), height, width);
        
        //Create all information for classes
        initClasses ("/FEJava/resources/text/unitClasses.txt", map);
        
        this.protag = new Team (file.readLine (), Color.BLUE, true, file.readLine (), unitClasses, insets, map);
        this.antag = new Team (file.readLine (), Color.RED, true, file.readLine (), unitClasses, insets, map);
    }

    /**
     * Returns title of chapter
     * @return title
     */
    public String getLevelName ()
    {
        return levelName;
    }

    /**
     * Gives map level takes place on
     * @return map
     */
    public TileMap getMap ()
    {
        return map;
    }

    /**
     * Gets team controlled by player
     * @return player team
     */
    public Team getProtag ()
    {
        return protag;
    }

    /**
     * Gets team opposing the player
     * @return opposing team
     */
    public Team getAntag ()
    {
        return antag;
    }

    /**
     * Gets other misc teams
     * Currently unused
     * @return other team
     */
    public Team getMisc ()
    {
        return misc;
    }
    
    /**
     * Creates all classes for units
     * @param sourceFile file containing all unit information
     * @param map for image scaling
     * @throws IOException 
     */
    public void initClasses (String sourceFile, TileMap map) throws IOException
    {
        BufferedReader file = new BufferedReader (new FileReader (FEJava.class.getResource (sourceFile).getFile ()));
        
        ArrayList tempData = new ArrayList ();
        String line = file.readLine ();
        while (line != null)
        {
            tempData.add (line);
            line = file.readLine ();
        }
        
        unitClasses = new ArrayList ();
        
        for (int i = 0; i < tempData.size (); i += 5)
        {
            String [] statsTemp = tempData.get (i + 1).toString ().split (":");
            int [] stats = new int [statsTemp.length];
            String [] growthsTemp = tempData.get (i + 2).toString ().split (":");
            int [] growths = new int [statsTemp.length];
            for (int j = 0; j < stats.length; j ++)
            {
                stats [j] = Integer.parseInt (statsTemp [j]);
                growths [j] = Integer.parseInt (growthsTemp [j]);
            }
            unitClasses.add (new UnitClass ((String)tempData.get (i), stats, growths, 
                    Integer.parseInt ((String)tempData.get (i + 4)), map));
        }
    }
    
    /**
     * gets and scales all non-sprite images used
     * @param windowWidth
     * @param windowHeight
     * @return array containing all necessary images
     */
    public Image [] getTerrain (int windowWidth, int windowHeight)
    {
        Image [] imageList = new Image [100];
        imageList [0] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/terrain/Grass Vibrant.png"))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
        imageList [1] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/terrain/tempDirt.png"))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
        imageList [2] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/terrain/tempForest.png"))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
        imageList [3] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/terrain/tempMountain.png"))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
        imageList [4] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/terrain/tempWater.png"))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
        imageList [5] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/terrain/tempDirt2.png"))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
        imageList [10] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/misc/tempCursor.png"))).getScaledInstance(map.getTileSize(), map.getTileSize(), Image.SCALE_FAST));
        imageList [11] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/misc/tempBackground.png"))).getScaledInstance(map.getPanelWidth (), (int)windowHeight, Image.SCALE_FAST));
        imageList [12] = ((Toolkit.getDefaultToolkit ().getImage(FEJava.class.getResource("/FEJava/resources/misc/Title Screen Background.png"))).getScaledInstance((int)windowWidth, (int)windowHeight, Image.SCALE_FAST));
        
        return imageList;
    }
}
