package src;

import java.util.ArrayList;

public class Dungeon {
    
    protected int width, gameHeight, topHeight, bottomHeight;
    protected String name;
    protected Creature creature;
    protected ArrayList<Passage> passage = new ArrayList<Passage>();
    protected Item item;
    public ArrayList<Room> rooms = new ArrayList<Room>();

    public Dungeon(String name, int width, int gameHeight, int topHeight, int bottomHeight) {
        this.name = name;
        this.width = width;
        this.gameHeight = gameHeight;
        this.topHeight = topHeight;
        this.bottomHeight = bottomHeight;

        System.out.println("Dungeon: getDungeon: name: " + name + ", width: " + width + ", gameHeight: " + gameHeight + ", " + this);
    }

    public void addRoom(Room room){
        rooms.add(room);
        System.out.println("Dungeon: addRoom: " + room);
    }

    public void addCreature(Creature creature){
        this.creature = creature;
        System.out.println("Dungeon: addCreature: " + creature);
    }

    public void addPassage(Passage passage){
        this.passage.add(passage);
        System.out.println("Dungeon: addPassage: " + passage);
    }

    public void addItem(Item item){
        this.item = item;
        System.out.println("Dungeon: addItem: " + item);
    }
}
