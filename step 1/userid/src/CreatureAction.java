public class CreatureAction extends Action {

    protected Creature owner;
    
    public CreatureAction(Creature owner){ //should take Creature owner as input
        System.out.println("CreatureAction: constructor: " + owner);
    }
}
