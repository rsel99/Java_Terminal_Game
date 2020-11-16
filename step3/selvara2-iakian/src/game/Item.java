package game;

public class Item extends Displayable {

    protected Creature owner;
    
    public void setOwner(Creature owner){
        this.owner = owner;
        System.out.println("Item: setOwner to " + owner + ", " + this);
    }
}
