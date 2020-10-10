public class EndGame extends CreatureAction{

    protected String name;
    protected Creature owner;
    
    public EndGame(String name, Creature owner){
        super(owner);
        System.out.println("EndGame: constructor: name: " + name + ", Creature: " + owner);
    }
}
