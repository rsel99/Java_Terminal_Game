package src;

public class Armor extends Item {
    
    protected String name;
    protected int room, serial;

    public Armor(String name){
        this.name = name;
        System.out.println("Armor: constructor: " + name + ", " + this);
    }

    public void setName(String name){
        this.name = name;
        System.out.println("Armor: setName to " + name);
    }

    public void setID(int room, int serial){
        this.room = room;
        this.serial = serial;
        System.out.println("Armor: setID: room: " + room + ", serial: " + serial);
    }

    public String getName(){
        return this.name;
    }

    public int getRoom(){
        return this.room;
    }

    public int getSerial(){
        return this.serial;
    }
}
