public class Teleport extends CreatureAction {

    protected String name;
    protected Creature owner;
    
    public Teleport(String name, Creature owner){
        super(owner);
        System.out.println("Teleport: constructor: name: " + name + ", Creature: " + owner);
    }
}
