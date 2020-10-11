public class Scroll extends Item {

    protected String name;
    protected int room, serial;
    
    public Scroll(String name){
        this.name = name;
        System.out.println("Scroll: constructor: " + name + ", " + this);
    }

    public void setID(int room, int serial){
        System.out.println("Scroll: setID: room: " + room + ", serial: " + serial);
    }
}
