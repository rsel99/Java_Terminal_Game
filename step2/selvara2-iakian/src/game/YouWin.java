package game;

public class YouWin extends CreatureAction{

    protected String name;
    protected String type;
    protected Creature owner;
    
    public YouWin(String name, String type, Creature owner){
        super(owner, name, type);
        this.name = name;
        this.type = type;
        this.owner = owner;
        System.out.println("YouWin: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }  
}
