public class ItemAction extends Action {

    protected Item owner;
    
    public ItemAction(Item owner){  //should take Creature owner as input
        System.out.println("ItemAction: constructor: " + owner);
    }
}
