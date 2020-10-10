public class YouWin extends CreatureAction{

    protected String name;
    protected Creature owner;
    
    public YouWin(String name, Creature owner){
        super(owner);
        System.out.println("YouWin: constructor: name: " + name + ", Creature: " + owner);
    }  
}
