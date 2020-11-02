// package src.game;        we should figure out the package stuff after, for now I'm compiling locally and it works
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
        displayGrid = new ObjectDisplayGrid(width, height, dungeon);
    }

    @Override
    public void run() {
        // displayGrid.fireUp();
        // for (int i = 0; i < WIDTH; i += 1) {
        //     for (int j = 0; j < HEIGHT; j += 1) {
        //         // TODO: implement printing creatures, items, player to screen
        //         // displayGrid.addObjectToDisplay(new Char('X'), i, j);

        //     }
        // }

        ArrayList<Room> rooms = this.dungeon.getRooms();
        ArrayList<Passage> passages = this.dungeon.getPassages();

        for 

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        displayGrid.initializeDisplay();
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