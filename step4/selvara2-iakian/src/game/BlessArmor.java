package game;

public class BlessArmor extends ItemAction {

    public BlessArmor(Item owner, String name, String type){
        super(owner, name, type);
        System.out.println("BlessArmor: constructor: " + owner + ", " + this);
    }
}
