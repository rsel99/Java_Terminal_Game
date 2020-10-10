public class Item extends Displayable {

    protected Creature owner;
    
    public void setOwner(Creature owner){
        System.out.println("Item: setOwner to " + owner);
    }
}
