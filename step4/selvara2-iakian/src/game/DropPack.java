package game;

public class DropPack extends CreatureAction{
    
    public DropPack(String name, String type, Creature owner){
        super(owner, name, type);
        System.out.println("DropPack: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
