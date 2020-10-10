public class DropPack extends CreatureAction{

    protected String name;
    protected Creature owner;
    
    public DropPack(String name, Creature owner){
        super(owner);
        System.out.println("DropPack: constructor: name: " + name + ", Creature: " + owner);
    }
}
