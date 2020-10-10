public class Hallucinate extends ItemAction {

    protected Creature owner;
    
    public Hallucinate(Creature owner){
        super(owner);
        System.out.println("Hallucinate: constructor: " + owner);
    }
}
