public class ItemAction extends Action {

    protected Item owner;
    
    public ItemAction(Item owner){  //should take Item owner as input
        System.out.println("ItemAction: constructor: " + owner);
    }
}
