package game;

import java.util.ArrayList;

public class Room extends Structure {

    protected int roomNum;
    protected ArrayList<Creature> monsters = new ArrayList<Creature>();
    protected ArrayList<Item> items = new ArrayList<Item>();
    
    public Room(int roomNum){
        super(null);
        this.roomNum = roomNum;
        System.out.println("Room: constructor: " + roomNum + ", " + this);
    }

    public void setId(int roomNum){
        this.roomNum = roomNum;
        System.out.println("Room: setId to " + roomNum);
    }

    public void addCreature(Creature Monster){
        this.monsters.add(Monster);
        System.out.println("Room: setCreature to " + Monster);
    }

    public void addItem(Item item) {
        this.items.add(item);
        System.out.println("Room: addItem " + item);
    }

    public ArrayList<Item> getItems() {
        return this.items;
    }

    public ArrayList<Creature> getMonsters() {
        return this.monsters;
    }
    public int getRoomNum() {
        return this.roomNum;
    }
}
