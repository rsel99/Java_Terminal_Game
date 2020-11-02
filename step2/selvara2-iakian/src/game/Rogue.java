package src.game;        // we should figure out the package stuff after, for now I'm compiling locally and it works
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;
import java.lang.Math;

public class Rogue implements Runnable {
    private static final int DEBUG = 0;
    private boolean isRunning;
    public static final int FRAMESPERSECOND = 60;
    public static final int TIMEPERLOOP = 1000000000 / FRAMESPERSECOND;
    private static ObjectDisplayGrid displayGrid = null;
    private Thread keyStrokePrinter;
    private int WIDTH;
    private int HEIGHT;
    private Dungeon dungeon;
    private Stack<Displayable>[][] objectGrid = null;

    public Rogue(int width, int height, Dungeon dungeon) {
        this.dungeon = dungeon;
        WIDTH = width;
        HEIGHT = height;
        displayGrid = new ObjectDisplayGrid(width, height);
    }

    @Override
    public void run() {

        ArrayList<Room> rooms = this.dungeon.getRooms();
        ArrayList<Passage> passages = this.dungeon.getPassages();
        
        for (Passage passage : passages) {
            Queue<Integer> X = passage.getX();
            Queue<Integer> Y = passage.getY();
            int x = X.poll();
            int y = Y.poll();
            for (int i = 1; i < X.size(); i++) {  
                int x2 = X.poll();
                int y2 = Y.poll();
                if(x == x2){
                    for(int j = Math.min(y, y2); j <= Math.max(y, y2); j++){
                        objectGrid[x][j + this.dungeon.getTopHeight()].push(passage);
                    }
                }
                else{
                    for(int j = Math.min(x, x2); j <= Math.max(x, x2); j++){
                        objectGrid[j][y + this.dungeon.getTopHeight()].push(passage);
                    }
                }
                x = x2;
                y = y2;
            }
        }

        for (Room room : rooms) {
            ArrayList<Creature> monsters = room.getMonsters();
            ArrayList<Item> items = room.getItems();
            Creature player = room.getPlayer();

            for (int i = this.dungeon.getTopHeight() + room.getPosY(); i <= room.getHeight(); i++) {
                for (int j = room.getPosX(); j < room.getWidth(); j++) {
                    objectGrid[i][j].push(room);
                }
            }

            for (Item item : items){
                objectGrid[item.getPosX() + room.getPosX()][item.getPosY() + this.dungeon.getTopHeight() + room.getPosY()].push(item);
            }

            for (Creature monster : monsters){
                objectGrid[monster.getPosX() + room.getPosX()][monster.getPosY() + this.dungeon.getTopHeight() + room.getPosY()].push(monster);
            }

            objectGrid[player.getPosX() + room.getPosX()][player.getPosY() + this.dungeon.getTopHeight() + room.getPosY()].push(player);
     
        }

        displayGrid.fireUp();
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                int k = objectGrid[i][j].length();
                if(k == 1){
                    displayGrid.addObjectToDisplay(new Char (getDisplayChar(objectGrid[i][j].pop(), i, j)), i, j);
                }
                else if (k > 1){
                    Displayable disp = objectGrid[i][j].pop();
                    if(disp.getClass() == Room.class){
                        displayGrid.addObjectToDisplay(new Char ('+'), i, j);
                    }
                    else{
                        displayGrid.addObjectToDisplay(new Char (getDisplayChar(objectGrid[i][j].pop(), i, j)), i, j);
                    }
                }

            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        displayGrid.initializeDisplay();
    }

    public char getDisplayChar(Displayable disp, int x, int y){
        if(disp.getClass() == Creature.class){
            return disp.getType();
        }
        if(disp.getClass() == Item.class){
            return disp.getType();
        }
        else if(disp.getClass() == Room.class){
            if(x == disp.getPosX() || x == disp.getPosX() + disp.getWidth() || y == disp.getPosY() + disp.getTopHeight() || y == disp.getPosY() + disp.getTopHeight() + disp.getHeight()){
                return 'X';
            }
            else{
                return '.';
            }
        }
        else if(disp.getClass() == Passage.class){
            return '#';
        }
    }

    public static void main(String[] args) throws Exception {
        String fileName = null;
        switch (args.length) {
            case 1:
                // note that the relative file path may depend on what IDE you are
            // using.  This worked for NetBeans.
                fileName = "xmlfiles/" + args[0];
                break;
            default:
                System.out.println("java Test <xmlfilename>");
                return;
        }
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        // We haven't covered exceptions, so just copy the try { } catch {...}
        // exactly, // filling in what needs to be changed between the open and
        // closed braces.
        try {
            // just copy this
            SAXParser saxParser = saxParserFactory.newSAXParser();
            // just copy this
            RogueXMLHandler handler = new RogueXMLHandler();
            // just copy this. This will parse the xml file given by fileName
            saxParser.parse(new File(fileName), handler);
            // This will change depending on what kind of XML we are parsing
            Dungeon dungeon = handler.getDungeon();
            // print out all of the students. This will change depending on
            // what kind of XML we are parsing
            // for (Dungeon dungeon : dungeons) {
            System.out.println(dungeon);
            // }
            /*
             * the above is a different form of for (int i = 0; i < students.length; i++) {
             * System.out.println(students[i]); }
             */
            // these lines should be copied exactly.
            int width = dungeon.getWidth();
            int height = dungeon.getGameHeight() + dungeon.getTopHeight() + dungeon.getBottomHeight();
            Rogue rogue = new Rogue(width, height, dungeon);
            Thread testThread = new Thread(rogue);
            testThread.start();

            rogue.keyStrokePrinter = new Thread(new KeyStrokePrinter(displayGrid));
            rogue.keyStrokePrinter.start();

            testThread.join();
            rogue.keyStrokePrinter.join();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace(System.out);
        }
    }
}