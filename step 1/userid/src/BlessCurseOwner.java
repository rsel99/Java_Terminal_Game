public class BlessCurseOwner extends ItemAction {

    protected Creature owner;

    public BlessCurseOwner(Creature owner){
        super(owner);
        System.out.println("BlessCurseOwner: constructor: " + owner);
    }
}
