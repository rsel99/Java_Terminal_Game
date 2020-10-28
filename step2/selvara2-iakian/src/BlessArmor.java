package src;

public class BlessArmor extends ItemAction {

    protected Item owner;
    protected String name, type;

    public BlessArmor(Item owner, String name, String type){
        super(owner, name, type);
        this.owner = owner;
        this.name = name;
        this.type = type;
        System.out.println("BlessArmor: constructor: " + owner + ", " + this);
    }
}
