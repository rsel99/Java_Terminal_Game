public class YouWin extends CreatureAction{

    protected String name;
    protected String type;
    protected Creature owner;
    
    public YouWin(String name, String type, Creature owner){
        super(owner);
        this.name = name;
        this.type = type;
        System.out.println("YouWin: constructor: name: " + name + ", Creature: " + owner);
    }  
}
