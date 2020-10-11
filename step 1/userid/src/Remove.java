public class Remove extends CreatureAction{

    protected String name;
    protected String type;
    protected Creature owner;
    
    public Remove(String name, String type, Creature owner){
        super(owner);
        this.name = name;
        this.type = type;
        System.out.println("Remove: constructor: name: " + name + ", Creature: " + owner);
    }
}
