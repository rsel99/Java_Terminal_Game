package game;

public class EndGame extends CreatureAction{
    
    public EndGame(String name, String type, Creature owner) {
        super(owner, name, type);
        System.out.println("EndGame: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
