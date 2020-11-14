package game;

public class Remove extends CreatureAction{

    protected String name;
    protected String type;
    protected Creature owner;
    
    public Remove(String name, String type, Creature owner){
        super(owner, name, type);
        this.name = name;
        this.type = type;
        this.owner = owner;
        System.out.println("Remove: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
