public class ItemAction extends Action {

    protected Creature owner;
    
    public ItemAction(Creature owner){  //should take Creature owner as input
        System.out.println("ItemAction: constructor: " + owner);
    }
}
