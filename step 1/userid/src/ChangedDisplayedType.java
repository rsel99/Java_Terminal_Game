public class ChangedDisplayedType extends CreatureAction {

    protected String name;
    protected Creature owner;
    protected String type;
    
    public ChangedDisplayedType(String name, String type, Creature owner){
        super(owner);
        this.name = name;
        this.type = type;
        System.out.println("ChangedDisplayedType: constructor: name: " + name + ", Creature: " + owner);
    }
}
