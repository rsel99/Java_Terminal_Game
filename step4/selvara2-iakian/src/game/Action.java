package game;

public class Action {

    protected String msg;
    protected int v;
    protected char c;
    protected String name;
    
    public Action(String name) {
        this.name = name;
    }

    public void setMessage(String msg){
        this.msg = msg;
        System.out.println("Action: setMessage to " + msg);
    }

    public void setIntValue(int v){
        this.v = v;
        System.out.println("Action: setIntValue to "+v);
    }

    public void setCharValue(char c){
        this.c = c;
        System.out.println("Action: setCharValue to "+c);
    }

    public String getName() {
        return this.name;
    }

    public String getMessage(){
        return this.msg;
    }

    public int getIntValue(){
        return this.v;
    }

    public int getCharValue(){
        return this.c;
    }
}
