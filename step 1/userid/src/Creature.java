public class Creature extends Displayable {
    public Creature(){
        System.out.println("Creature: constructor");
    }

    public void setHp(int h){
        System.out.println("Creature: setHp");
    }

    public void setHpMoves(int hpm){
        System.out.println("Creature: setHpMoves");
    }

    public void setDeathAction(CreatureAction da){
        System.out.println("Creature: setDeathAction");
    }

    public void setHitAction(CreatureAction ha){
        //(type CreatureAction???)
        System.out.println("Creature: setHitAction");
    }
}
