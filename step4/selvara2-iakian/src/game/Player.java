package game;
import java.util.*;

public class Player extends Creature {

    protected Item sword, armor;
    protected String name;
    protected int room, serial;
    protected boolean onDoor = false;
    protected int score = 0;
    protected ArrayList<Item> pack = new ArrayList<Item>();

    public Player(String name, int room, int serial){
        super(name);
        this.room = room;
        this.serial = serial;
        this.setType('@');
        System.out.println("Player: constructor, name: " + name + ", room: " + room + ", serial: " + serial + ", " + this);
    }
    
    public void setWeapon(Item sword){
        this.sword = sword;
        checkPack(sword);
        System.out.println("Player: setWeapon to " + sword);
    }

    public void setArmor(Item armor){
        this.armor = armor;
        checkPack(armor);
        System.out.println("Player: setArmor to " + armor);
    }

    public void checkPack(Item currItem){
        if(pack.size() > 0){
            for(Item item : pack){
                if(currItem == item){
                    return;
                }
            }
        }
        addToPack(currItem);
    }

    public void setOnDoor(boolean onDoor) {
        this.onDoor = onDoor;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addToPack(Item item){
        this.pack.add(item);
    }

    public int getPackSize(){
        return this.pack.size();
    }

    public Item getItemFromPack(int i){
        return pack.get(i);
    }

    public void removeItemFromPack(int i){
        // (pack.get(i)).setPosX(x);
        // (pack.get(i)).setPosY(y);
        this.pack.remove(i);
    }

    public ArrayList<Item> getPack(){
        return this.pack;
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

    public int getScore() {
        return this.score;
    }

    public Item getArmor(){
        return this.armor;
    }

    public Item getWeapon(){
        return this.sword;
    }

    public void changeArmor(){
        this.armor = null;
    }

    public void dropWeapon(){
        this.sword = null;
    }

    // public void setID(int room, int serial){
    //     System.out.println("Player: setID: room: " + room + ", serial: " + serial);
    // }
}
