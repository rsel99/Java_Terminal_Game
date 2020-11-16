package game;

import java.util.ArrayList;

public class Dungeon {
    
    protected int width, gameHeight, topHeight, bottomHeight;
    protected String name;
    protected Creature creature;
    protected ArrayList<Passage> passages;
    protected Item item;
    protected ArrayList<Room> rooms;
    protected Structure playersLoc;
    protected String info;

    public Dungeon(String name, int width, int gameHeight, int topHeight, int bottomHeight) {
        this.name = name;
        this.width = width;
        this.gameHeight = gameHeight;
        this.topHeight = topHeight;
        this.bottomHeight = bottomHeight;
        this.rooms = new ArrayList<Room>();
        this.passages = new ArrayList<Passage>();
        this.playersLoc = new Structure(null);
        this.info = "";

        System.out.println("Dungeon: getDungeon: name: " + name + ", width: " + width + ", gameHeight: " + gameHeight + ", topHeight: "+ topHeight + ", bottomHeight: "+bottomHeight+", " + this);
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
        this.passages.add(passage);
        System.out.println("Dungeon: addPassage: " + passage);
    }

    public void addItem(Item item){
        this.item = item;
        System.out.println("Dungeon: addItem: " + item);
    }

    public void setPlayerLoc(Structure structure) {
        this.playersLoc = structure;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ArrayList<Room> getRooms() {
        return this.rooms;
    }

    public ArrayList<Passage> getPassages() {
        return this.passages;
    }

    public int getWidth() {
        return this.width;
    }
    
    public int getGameHeight() {
        return this.gameHeight;
    }
    
    public int getTopHeight() {
        return this.topHeight;
    }
    
    public int getBottomHeight() {
        return this.bottomHeight;
    }
    
    public Structure getPlayerLoc() {
        return this.playersLoc;
    }

    public String getInfo() {
        return this.info;
    }
}