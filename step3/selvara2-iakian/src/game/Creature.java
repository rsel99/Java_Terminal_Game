package game;

public class Creature extends Displayable {

    protected int h, hpm;
    protected String name;
    protected CreatureAction da, ha;

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

    public void setDeathAction(CreatureAction da){
        this.da = da;
        System.out.println("Creature: setDeathAction to " + da);
    }

    public void setHitAction(CreatureAction ha){
        //(type CreatureAction???)
        this.ha = ha;
        System.out.println("Creature: setHitAction to " + ha);
    }
    
    public String getName() {
        return this.name;
    }
}
