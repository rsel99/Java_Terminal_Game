public class EndGame extends CreatureAction{

    protected String name;
    protected String type;
    protected Creature owner;
    
    public EndGame(String name, String type, Creature owner) {
        super(owner);
        this.type = type;
        System.out.println("EndGame: constructor: name: " + name + ", Creature: " + owner);
    }
}
