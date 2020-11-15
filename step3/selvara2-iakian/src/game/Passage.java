package game;
import java.util.LinkedList;
import java.util.Queue;

public class Passage extends Structure {

    protected String name;
    protected int room1, room2;
    protected LinkedList<Integer> X;
    protected LinkedList<Integer> Y;
    protected Door door1;
    protected Door door2;

    public Passage (Player player){
        super(player);
        X = new LinkedList<Integer>();
        Y = new LinkedList<Integer>();
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

    public void setDoor1(Door door) {
        this.door1 = door;
    }

    public void setDoor2(Door door) {
        this.door2 = door;
    }

    @Override
    public void setPosX(int x){
        X.add(x);
    }

    @Override
    public void setPosY(int y){
        Y.add(y);
    }

    public LinkedList<Integer> getX(){
        return X;
    }

    public LinkedList<Integer> getY(){
        return Y;
    }

    public int getRoom1() {
        return this.room1;
    }

    public int getRoom2() {
        return this.room2;
    }

    public Door getDoor1() {
        return this.door1;
    }

    public Door getDoor2() {
        return this.door2;
    }
}
