package src;

public class BlessCurseOwner extends ItemAction {

    protected Item owner;

    public BlessCurseOwner(Item owner){
        super(owner);
        this.owner = owner;
        System.out.println("BlessCurseOwner: constructor: " + owner + ", " + this);
    }
}
