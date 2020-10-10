public class UpdateDisplay extends CreatureAction{

    protected String name;
    protected Creature owner;
    
    public UpdateDisplay(String name, Creature owner){
        super(owner);
        System.out.println("UpdateDisplay: constructor: name: " + name + ", Creature: " + owner);
    }
}
