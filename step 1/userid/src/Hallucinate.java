public class Hallucinate extends ItemAction {

    protected Item owner;
    
    public Hallucinate(Item owner){
        super(owner);
        this.owner = owner;
        System.out.println("Hallucinate: constructor: " + owner + ", " + this);
    }
}
