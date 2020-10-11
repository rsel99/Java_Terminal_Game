public class CreatureAction extends Action {

    protected Creature owner;
    protected String type;
    
    public CreatureAction(Creature owner){ //should take Creature owner as input
        System.out.println("CreatureAction: constructor: " + owner);
    }
    
    public String getType() {
        return this.type;
    }
}
