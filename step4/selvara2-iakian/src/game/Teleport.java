package game;

public class Teleport extends CreatureAction {
    
    public Teleport(String name, String type, Creature owner){
        super(owner, name, type);
        System.out.println("Teleport: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
