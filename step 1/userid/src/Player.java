public class Player extends Creature {

    protected Item sword, armor;
    protected String name;
    protected int room, serial;

    public Player(String name, int room, int serial){
        this.name = name;
        this.room = room;
        this.serial = serial;
        System.out.println("Player: constructor: " + name + ", " + this);
    }
    
    public void setWeapon(Item sword){
        this.sword = sword;
        System.out.println("Player: setWeapon to " + sword);
    }

    public void setArmor(Item armor){
        this.armor = armor;
        System.out.println("Player: setArmor to " + armor);
    }

    // public void setID(int room, int serial){
    //     System.out.println("Player: setID: room: " + room + ", serial: " + serial);
    // }
}
