import java.util.ArrayList;

public class Dungeon {
    
    protected int width, gameHeight;
    protected String name;
    protected Room room;
    protected Creature creature;
    protected Passage passage;
    protected Item item;
    public ArrayList<Room> rooms = new ArrayList<Room>();

    public void getDungeon(String name, int width, int gameHeight){
        System.out.println("Dungeon: getDungeon: name: " + name + ", width: " + width + ", gameHeight: " + gameHeight);
    }

    public void addRoom(Room room){
        rooms.add(room);
        System.out.println("Dungeon: addRoom: " + room);
    }

    public void addCreature(Creature creature){
        System.out.println("Dungeon: addCreature: " + creature);
    }

    public void addPassage(Passage passage){
        System.out.println("Dungeon: addPassage: " + passage);
    }

    public void addItem(Item item){
        System.out.println("Dungeon: addItem: " + item);
    }
}
