public class DropPack extends CreatureAction{

    protected String name;
    protected String type;
    protected Creature owner;
    
    public DropPack(String name, String type, Creature owner){
        super(owner, name, type);
        this.name = name;
        this.owner = owner;
        this.type = type;
        System.out.println("DropPack: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
