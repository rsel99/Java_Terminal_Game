public class BlessArmor extends ItemAction {

    protected Item owner;

    public BlessArmor(Item owner){
        super(owner);
        System.out.println("BlessArmor: constructor: " + owner);
    }
}
