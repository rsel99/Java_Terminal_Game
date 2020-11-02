package game;

import java.util.ArrayList;

public class Room extends Structure {

    protected String string;
    protected int room;
    protected ArrayList<Creature> monsters = new ArrayList<Creature>();
    protected Creature Player;
    protected ArrayList<Item> items = new ArrayList<Item>();
    
    public Room(String string){
        this.string = string;
        System.out.println("Room: constructor: " + string + ", " + this);
    }

    public void setId(int room){
        this.room = room;
        System.out.println("Room: setId to " + room);
    }

    public void addCreature(Creature Monster){
        this.monsters.add(Monster);
        System.out.println("Room: setCreature to " + Monster);
    }
    public void setPlayer(Creature Player) {
        this.Player = Player;
        System.out.println("Room: setPlayer to " + Player);
    }

    public void addItem(Item item) {
        this.items.add(item);
        System.out.println("Room: addItem " + item);
    }

    public ArrayList<Item> getItems() {
        return this.items;
    }

    public Creature getPlayer() {
        return this.Player;
    }

    public ArrayList<Creature> getMonsters() {
        return this.monsters;
    }
}
