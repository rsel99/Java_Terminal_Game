package src;

public class UpdateDisplay extends CreatureAction{

    protected String name;
    protected Creature owner;
    protected String type;
    
    public UpdateDisplay(String name, String type, Creature owner){
        super(owner, name, type);
        this.type = type;
        this.name = name;
        this.owner = owner;
        System.out.println("UpdateDisplay: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
