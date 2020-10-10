public class ChangedDisplayedType extends CreatureAction {

    protected String name;
    protected Creature owner;
    
    public ChangedDisplayedType(String name, Creature owner){
        super(owner);
        System.out.println("ChangedDisplayedType: constructor: name: " + name + ", Creature: " + owner);
    }
}
