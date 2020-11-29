package game;

import java.util.ArrayList;

public class Room extends Structure {

    protected int roomNum;
    protected ArrayList<Monster> monsters = new ArrayList<Monster>();
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

    public void addMonster(Monster monster){
        this.monsters.add(monster);
        if (this.roomNum == 2) {
            System.out.println("## DEBUG ##");
            System.out.println("## DEBUG ##");
            System.out.println("## DEBUG ##");
            System.out.println("## DEBUG ##");
            System.out.println("Room: setCreature to " + monster);
            System.out.println("## DEBUG ##");
            System.out.println("## DEBUG ##");
            System.out.println("## DEBUG ##");
            System.out.println("## DEBUG ##");
        }
    }

    public void addItem(Item item) {
        this.items.add(item);
        System.out.println("Room: addItem " + item);
    }

    public void removeItem(int i){
        this.items.remove(i);
    }

    public ArrayList<Item> getItems() {
        return this.items;
    }

    public ArrayList<Monster> getMonsters() {
        return this.monsters;
    }
    public int getRoomNum() {
        return this.roomNum;
    }
}
