public class Remove extends CreatureAction{

    protected String name;
    protected Creature owner;
    
    public Remove(String name, Creature owner){
        super(owner);
        System.out.println("Remove: constructor: name: " + name + ", Creature: " + owner);
    }
}
