package src;
import java.util.Queue;

public class Passage extends Structure {

    protected String name;
    protected int room1, room2;
    protected Queue<Integer> X;
    protected Queue<Integer> Y;

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

    @Override
    public void setPosX(int x){
        X.add(x);
    }

    @Override
    public void setPosX(int x){
        Y.add(y);
    }

    public Queue<Integer> getX(){
        return X;
    }

    public Queue<Integer> getY(){
        return Y;
    }
}
