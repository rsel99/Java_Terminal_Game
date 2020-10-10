public class Room extends Structure {

    protected String string;
    protected int room;
    protected Creature Monster;
    
    public Room(String string){
        System.out.println("Room: constructor: " + string);
    }

    public void setId(int room){
        System.out.println("Room: setId to " + room);
    }

    public void setCreature(Creature Monster){
        System.out.println("Room: setCreature to " + Monster);
    }
}
