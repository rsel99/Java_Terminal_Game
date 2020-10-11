package src;

public class Action {

    protected String msg;
    protected int v;
    protected char c;
    
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
}
