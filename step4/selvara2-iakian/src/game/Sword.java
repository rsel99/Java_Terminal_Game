package game;

public class Sword extends Item{

    protected String name;
    protected int room, serial;
    
    public Sword(String name){
        this.name = name;
        this.setType(')');
        System.out.println("Sword: constructor: " + name + ", " + this);
    }

    public void setID(int room, int serial){
        this.room = room;
        this.serial = serial;
        System.out.println("Sword: setID: room: " + room + ", serial: " + serial);
    }

    public String getName(){
        return this.name;
    }
}
