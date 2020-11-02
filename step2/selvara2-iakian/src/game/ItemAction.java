package game;

public class ItemAction extends Action {

    protected Item owner;
    protected String name, type;
    
    public ItemAction(Item owner, String name, String type){  //should take Item owner as input
        this.owner = owner;
        this.name = name;
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
