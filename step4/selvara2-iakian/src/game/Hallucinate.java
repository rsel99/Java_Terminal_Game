package game;

public class Hallucinate extends ItemAction {
    
    public Hallucinate(Item owner, String name, String type){
        super(owner, name, type);
        System.out.println("Hallucinate: constructor: " + owner + ", " + this);
    }
}
