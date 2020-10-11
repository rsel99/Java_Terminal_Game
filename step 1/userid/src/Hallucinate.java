package src;

public class Hallucinate extends ItemAction {

    protected Item owner;
    protected String name, type;
    
    public Hallucinate(Item owner, String name, String type){
        super(owner, name, type);
        this.owner = owner;
        this.name = name;
        this.type = type;
        System.out.println("Hallucinate: constructor: " + owner + ", " + this);
    }
}
