public class Monster extends Creature {

    protected String name;
    protected int room, serial;
    
    public Monster(){
        System.out.println("Monster: constructor");
    }

    public void setName(String name){
        System.out.println("Monster: setName to " + name);
    }

    public void setID(int room, int serial){
        System.out.println("Monster: setID: room: " + room + ", serial: " + serial);
    }
}
