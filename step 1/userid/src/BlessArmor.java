public class BlessArmor extends ItemAction {

    protected Item owner;

    public BlessArmor(Item owner){
        super(owner);
        this.owner = owner;
        System.out.println("BlessArmor: constructor: " + owner + ", " + this);
    }
}
