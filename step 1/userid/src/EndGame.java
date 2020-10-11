public class EndGame extends CreatureAction{

    protected String name;
    protected String type;
    protected Creature owner;
    
    public EndGame(String name, String type, Creature owner) {
        super(owner, name, type);
        this.type = type;
        this.name = name;
        this.owner = owner;
        System.out.println("EndGame: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }
}
