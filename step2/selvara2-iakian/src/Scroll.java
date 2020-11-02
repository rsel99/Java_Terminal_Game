package src;

public class Scroll extends Item {

    protected String name;
    protected int room, serial;
    
    public Scroll(String name){
        this.name = name;
        this.setType('?');
        System.out.println("Scroll: constructor: " + name + ", " + this);
    }

    public void setID(int room, int serial){
        this.room = room;
        this.serial = serial;
        System.out.println("Scroll: setID: room: " + room + ", serial: " + serial);
    }
}
