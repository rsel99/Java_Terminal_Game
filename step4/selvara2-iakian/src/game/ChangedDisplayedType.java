package game;

public class ChangedDisplayedType extends CreatureAction {
    
    public ChangedDisplayedType(String name, String type, Creature owner){
        super(owner, name, type);
        System.out.println("ChangedDisplayedType: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
