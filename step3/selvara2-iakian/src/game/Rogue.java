package game;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;
import java.lang.Math;
import java.time.*;
import java.util.LinkedList;

public class Rogue implements Runnable {
    private static final int DEBUG = 0;
    private boolean isRunning;
    public static final int FRAMESPERSECOND = 60;
    public static final int TIMEPERLOOP = 1000000000 / FRAMESPERSECOND;
    private static ObjectDisplayGrid displayGrid = null;
    private Thread keyStrokePrinter;
    private int WIDTH = 80;
    private int HEIGHT = 40;
    private Dungeon dungeon;
    private Stack[][] objectGrid;
    private static Stack[][] characters;

    public Rogue(int width, int height, Dungeon dungeon) {
        this.dungeon = dungeon;
        WIDTH = width;
        HEIGHT = height;

        displayGrid = new ObjectDisplayGrid(WIDTH, HEIGHT);

        objectGrid = new Stack[WIDTH][HEIGHT];
        characters = new Stack[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                objectGrid[i][j] = new Stack<Displayable>();
                characters[i][j] = new Stack<Character>();
            }
        }
    }

    @Override
    public void run() {

        ArrayList<Room> rooms = this.dungeon.getRooms();
        ArrayList<Passage> passages = this.dungeon.getPassages();
        
        for (Passage passage : passages) {
            LinkedList<Integer> X = new LinkedList<Integer>();
            LinkedList<Integer> Y = new LinkedList<Integer>();
            X = passage.getX();
            Y = passage.getY();

            Door door1 = new Door();
            Door door2 = new Door();

            door1.setPosX(X.getFirst());
            door1.setPosY(Y.getFirst());
            door2.setPosX(X.getLast());
            door2.setPosY(Y.getLast());

            passage.setDoor1(door1);
            passage.setDoor2(door2);

            int x = X.poll();
            int y = Y.poll();
            int sz = X.size();
            for (int i = 0; i < sz; i++) {  
                int x2 = X.poll();
                int y2 = Y.poll();

                if(x == x2) {
                    for(int j = Math.min(y, y2); j <= Math.max(y, y2); j++) {
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
            ArrayList<Monster> monsters = room.getMonsters();
            ArrayList<Item> items = room.getItems();
            Creature player = room.getPlayer();

            if (player != null) {
                objectGrid[player.getPosX() + room.getPosX()][player.getPosY() + this.dungeon.getTopHeight() + room.getPosY()].push(player);
            }

            for (Item item : items) {
                objectGrid[item.getPosX() + room.getPosX()][item.getPosY() + this.dungeon.getTopHeight() + room.getPosY()].push(item);
            }

            for (Monster monster : monsters) {
                if (room.getRoomNum() == 2) {
                    System.out.println("monsters: " + monster);
                }
                objectGrid[monster.getPosX() + room.getPosX()][monster.getPosY() + this.dungeon.getTopHeight() + room.getPosY()].push(monster);
            }

            for (int i = room.getPosX(); i < room.getWidth() + room.getPosX(); i++) {
                for (int j = this.dungeon.getTopHeight() + room.getPosY(); j < room.getHeight() + this.dungeon.getTopHeight() + room.getPosY(); j++) {
                    objectGrid[i][j].push(room);
                }
            }
        }

        // displayGrid.initializeDisplay();

        //displayGrid.fireUp();
        for (int i = 0; i < ("Pack: ").length(); i++) {
            displayGrid.addObjectToDisplay(("Pack: ").charAt(i), i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
        }
        for (int i = 0; i < ("Info: ").length(); i++) {
            displayGrid.addObjectToDisplay(("Info: ").charAt(i), i, 
                this.dungeon.getBottomHeight() / 2 + this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
        }
        for (int i = 0; i < ("HP: ").length(); i++) {
            displayGrid.addObjectToDisplay(("HP: ").charAt(i), i, 0);
        }
        for (int i = 0; i < ("Score: ").length(); i++) {
            displayGrid.addObjectToDisplay(("Score: ").charAt(i), i + ("HP: ").length() + 4, 0);
        }


        
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                int k = objectGrid[i][j].size();
                char temp;
                if(k == 1) {
                    temp = getDisplayChar((Displayable)objectGrid[i][j].pop(), i, j);
                    displayGrid.addObjectToDisplay(temp, i, j);
                    characters[i][j].push((Character) temp);
                    // System.out.println(characters[i][j].peek());
                }
                else if (k > 1) {
                    Displayable disp = (Displayable) objectGrid[i][j].pop();
                    if(disp.getClass() == Room.class) {
                        Displayable disp2 = (Displayable) objectGrid[i][j].pop();
                        if (disp2.getClass() == Passage.class) {
                            displayGrid.addObjectToDisplay('+', i, j);
                            characters[i][j].push((Character) '+');
                            // System.out.println(characters[i][j].peek());
                        }
                        else {
                            temp = getDisplayChar(disp, i, j);
                            characters[i][j].push((Character) temp);
                            displayGrid.addObjectToDisplay(temp, i, j);
                            temp = getDisplayChar(disp2, i, j);
                            displayGrid.addObjectToDisplay(temp, i, j);
                            characters[i][j].push((Character) temp);
                            // System.out.println(characters[i][j].peek());
                        }
                    }
                    else {
                        Displayable disp2 = (Displayable) objectGrid[i][j].pop();
                        temp = getDisplayChar(disp, i, j);
                        displayGrid.addObjectToDisplay(temp, i, j);
                        characters[i][j].push((Character) temp);
                        temp = getDisplayChar(disp2, i, j);
                        displayGrid.addObjectToDisplay(temp, i, j);
                        characters[i][j].push((Character) temp);
                        // System.out.println(characters[i][j].peek());
                        // displayGrid.addObjectToDisplay(new Char ((getDisplayChar((Displayable)objectGrid[i][j].pop(), i, j))), i, j);
                    }
                }
            }
        }
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    public char getDisplayChar(Displayable disp, int x, int y){
        if (disp.getClass() == Player.class) {
            return '@';
        }
        if(disp.getClass().getSuperclass() == Creature.class){
            return ((char)disp.getType());
        }
        if(disp.getClass().getSuperclass() == Item.class){
            return ((char)disp.getType());
        }
        else if(disp.getClass() == Room.class) {
            if(x == disp.getPosX() || x == disp.getPosX() + disp.getWidth() - 1 || y == disp.getPosY() + this.dungeon.getTopHeight() || y == disp.getPosY() + this.dungeon.getTopHeight() + disp.getHeight() - 1) {               
                return 'X';
            }
            else {
                return '.';
            }
        }
        else if(disp.getClass() == Passage.class){
            return '#';
        }
        System.out.println("BAD");
        return ' ';
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
        // try {
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
            // System.out.println(dungeon);
            // }
            /*
             * the above is a different form of for (int i = 0; i < students.length; i++) {
             * System.out.println(students[i]); }
             */
            // these lines should be copied exactly.
            int width = dungeon.getWidth();
            int height = dungeon.getGameHeight() + dungeon.getTopHeight() + dungeon.getBottomHeight();

            Rogue rogue = new Rogue(width, height, dungeon);
            // Thread.sleep(10000);
            Thread testThread = new Thread(rogue);
            testThread.start();
            
            rogue.keyStrokePrinter = new Thread(new KeyStrokePrinter(displayGrid, dungeon, characters));
            rogue.keyStrokePrinter.start();

            testThread.join();
            rogue.keyStrokePrinter.join();

        // } catch (ParserConfigurationException | SAXException | IOException e) {
        //     e.printStackTrace(System.out);
        // }
    }
}