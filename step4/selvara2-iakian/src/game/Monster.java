package game;

public class Monster extends Creature {

    protected int room, serial;
    
    public Monster(String name, int room, int serial){
        super(name);
        this.room = room;
        this.serial = serial;
        System.out.println("Monster: constructor, name: " + name + ", room: " + room + ", serial: " + serial + ", " + this);
    }

    public void setName(String name){
        this.name = name;
        System.out.println("Monster: setName to " + name);
    }

    public void setID(int room, int serial){
        this.room = room;
        this.serial = serial;
        System.out.println("Monster: setID: room: " + room + ", serial: " + serial);
    }

    public int getRoom(){
        return this.room;
    }
}
