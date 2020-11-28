package game;

public class YouWin extends CreatureAction{
    
    public YouWin(String name, String type, Creature owner){
        super(owner, name, type);
        System.out.println("YouWin: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }  
}
