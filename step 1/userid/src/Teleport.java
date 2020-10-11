public class Teleport extends CreatureAction {

    protected String name;
    protected String type;
    protected Creature owner;
    
    public Teleport(String name, String type, Creature owner){
        super(owner, name, type);
        this.name = name;
        this.type = type;
        this.owner = owner;
        System.out.println("Teleport: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
