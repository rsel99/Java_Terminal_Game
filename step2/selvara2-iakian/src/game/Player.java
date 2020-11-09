package game;

public class Player extends Creature {

    protected Item sword, armor;
    protected String name;
    protected int room, serial;
    protected boolean onDoor = false;

    public Player(String name, int room, int serial){
        this.name = name;
        this.room = room;
        this.serial = serial;
        this.setType('@');
        System.out.println("Player: constructor, name: " + name + ", room: " + room + ", serial: " + serial + ", " + this);
    }
    
    public void setWeapon(Item sword){
        this.sword = sword;
        System.out.println("Player: setWeapon to " + sword);
    }

    public void setArmor(Item armor){
        this.armor = armor;
        System.out.println("Player: setArmor to " + armor);
    }

    public void setOnDoor(boolean onDoor) {
        this.onDoor = onDoor;
    }

    public String getName() {
        return this.name;
    }

    public int getRoom() {
        return this.room;
    }

    public int getSerial() {
        return this.serial;
    }

    public boolean getOnDoor() {
        return this.onDoor;
    }

    // public void setID(int room, int serial){
    //     System.out.println("Player: setID: room: " + room + ", serial: " + serial);
    // }
}
