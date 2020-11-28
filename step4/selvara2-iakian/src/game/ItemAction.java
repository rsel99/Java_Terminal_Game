package game;

public class ItemAction extends Action {

    protected Item owner;
    protected String type;
    
    public ItemAction(Item owner, String name, String type){  //should take Item owner as input
        super(name);
        this.owner = owner;
        this.type = type;
        System.out.println("ItemAction: constructor: " + owner + ", " + this);
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}
