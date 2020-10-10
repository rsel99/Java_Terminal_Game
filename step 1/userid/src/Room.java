public class Room extends Structure {
    
    public Room(String str){
        System.out.println("Room: constructor");
    }

    public void setId(int room){
        System.out.println("Room: setId");
    }

    public void setCreature(Creature Monster){
        System.out.println("Room: setCreature");
    }
}
