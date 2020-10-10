public class Armor extends Item {
    
    public Armor(String name){
        System.out.println("Armor: constructor");
    }

    public void setName(String name){
        System.out.println("Armor: setName");
    }

    public void setID(int room, int serial){
        System.out.println("Armor: setID");
    }
}
