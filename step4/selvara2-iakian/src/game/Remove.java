package game;

public class Remove extends CreatureAction{
    
    public Remove(String name, String type, Creature owner){
        super(owner, name, type);
        System.out.println("Remove: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
