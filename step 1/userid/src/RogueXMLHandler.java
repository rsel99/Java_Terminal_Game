
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RogueXMLHandler extends DefaultHandler {
    private static final int DEBUG = 1;
    private static final String CLASSID = "RogueXMLHandler";

    private StringBuilder data = null;
    
    private ArrayList<Dungeon> dungeons = new ArrayList<Dungeon>();
    private int width = 0;
    private int gameHeight = 0;

    private Dungeon dungeonParsed         = null;
    private Displayable displayableParsed = null;



    public RogueXMLHandler() {
    }
    public ArrayList<Dungeon> getDungeons() {
        return dungeons;
    }
    public void addDungeon(Dungeon dungeon) {
        dungeons.add(dungeon);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (DEBUG > 1) {
            System.out.println(CLASSID + ".startElement qName: " + qName);
        }

        if (qName.equalsIgnoreCase("Dungeon")) {
            maxStudents = Integer.parseInt(attributes.getValue("count"));
            students = new Student[maxStudents];
        }
    }
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

    }
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

    }
    @Override
    public String toString() {
        return null;
    }
}