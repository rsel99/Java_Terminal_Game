package game;

public class BlessCurseOwner extends ItemAction {

    protected Item owner;
    protected String name, type;

    public BlessCurseOwner(Item owner, String name, String type){
        super(owner, name, type);
        this.owner = owner;
        this.name = name;
        this.type = type;
        System.out.println("BlessCurseOwner: constructor: " + owner + ", " + this);
    }
}
