public class BlessCurseOwner extends ItemAction {

    protected Item owner;

    public BlessCurseOwner(Item owner){
        super(owner);
        System.out.println("BlessCurseOwner: constructor: " + owner);
    }
}
