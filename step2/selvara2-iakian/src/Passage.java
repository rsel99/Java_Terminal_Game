package src;

public class Passage extends Structure {

    protected String name;
    protected int room1, room2;
    
    public Passage(){
        System.out.println("Passage: constructor, " + this);
    }

    public void setName(String name){
        this.name = name;
        System.out.println("Passage: setName to " + name);
    }

    public void setID(int room1, int room2){
        this.room1 = room1;
        this.room2 = room2;
        System.out.println("Passage: setID: room1: " + room1 + ", room2: " + room2);
    }
}
