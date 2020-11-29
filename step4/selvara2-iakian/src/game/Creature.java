package game;
import java.util.*;

public class Creature extends Displayable {

    protected int h, hpm;
    protected String name;
    protected ArrayList<CreatureAction> da = new ArrayList<CreatureAction>();
    protected ArrayList<CreatureAction> ha = new ArrayList<CreatureAction>();

    public Creature(String name) {
        this.name = name;
        System.out.println("Creature: constructor, " + this);
    }

    // public void setHp(int h){
    //     this.h = h;
    //     System.out.println("Creature^2: setHp to " + h);
    // }

    // public void setHpMoves(int hpm){
    //     this.hpm = hpm;
    //     System.out.println("Creature: setHpMoves to " + hpm);
    // }

    public void setDeathAction(CreatureAction a){
        this.da.add(a);
        System.out.println("Creature: setDeathAction to " + a);
    }

    public void setHitAction(CreatureAction a){
        //(type CreatureAction???)
        this.ha.add(a);
        System.out.println("Creature: setHitAction to " + a);
    }

    public ArrayList<CreatureAction> getHitActions(){
        return this.ha;
    }

    public ArrayList<CreatureAction> getDeathActions(){
        return this.da;
    }
    
    public String getName() {
        return this.name;
    }
}
