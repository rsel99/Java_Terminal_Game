package game;

public class UpdateDisplay extends CreatureAction{
    
    public UpdateDisplay(String name, String type, Creature owner){
        super(owner, name, type);
        System.out.println("UpdateDisplay: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
