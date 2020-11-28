package game;

public class BlessCurseOwner extends ItemAction {

    public BlessCurseOwner(Item owner, String name, String type){
        super(owner, name, type);
        System.out.println("BlessCurseOwner: constructor: " + owner + ", " + this);
    }
}
