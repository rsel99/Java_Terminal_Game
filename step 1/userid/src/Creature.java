public class Creature extends Displayable {

    protected int h, hpm;
    protected CreatureAction da, ha;

    public Creature(){
        System.out.println("Creature: constructor, " + this);
    }

    public void setHp(int h){
        System.out.println("Creature: setHp to " + h);
    }

    public void setHpMoves(int hpm){
        System.out.println("Creature: setHpMoves to " + hpm);
    }

    public void setDeathAction(CreatureAction da){
        System.out.println("Creature: setDeathAction to " + da);
    }

    public void setHitAction(CreatureAction ha){
        //(type CreatureAction???)
        System.out.println("Creature: setHitAction to " + ha);
    }
}
