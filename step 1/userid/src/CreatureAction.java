public class CreatureAction extends Action {

    protected Creature owner;
    protected String type, name;
    
    public CreatureAction(Creature owner, String name, String type){ //should take Creature owner as input
        this.owner = owner;
        this.name = name;
        this.type = type;
        System.out.println("CreatureAction: constructor: " + owner + ", " + this);
    }

    public String getName() {
        return this.name;
    }
    
    public String getType() {
        return this.type;
    }
}

