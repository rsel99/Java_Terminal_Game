public class Monster extends Creature {
    
    public Monster(){
        System.out.println("Monster: constructor");
    }

    public void setName(String name){
        System.out.println("Monster: setName");
    }

    public void setID(int room, int serial){
        System.out.println("Monster: setID");
    }
}
