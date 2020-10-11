package src;

public class Monster extends Creature {

    protected String name;
    protected int room, serial;
    
    public Monster(String name, int room, int serial){
        this.name = name;
        this.room = room;
        this.serial = serial;
        System.out.println("Monster: constructor, " + this);
    }

    public void setName(String name){
        System.out.println("Monster: setName to " + name);
    }

    public void setID(int room, int serial){
        System.out.println("Monster: setID: room: " + room + ", serial: " + serial);
    }
}
