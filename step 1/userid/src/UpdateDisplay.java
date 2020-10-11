public class UpdateDisplay extends CreatureAction{

    protected String name;
    protected Creature owner;
    protected String type;
    
    public UpdateDisplay(String name, String type, Creature owner){
        super(owner);
        this.type = type;
        System.out.println("UpdateDisplay: constructor: name: " + name + ", Creature: " + owner);
    }
}
