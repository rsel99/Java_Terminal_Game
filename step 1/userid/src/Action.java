public class Action {

    protected String msg;
    protected int v;
    protected char c;
    
    public void setMessage(String msg){
        System.out.println("Action: setMessage to " + msg);
    }

    public void setIntValue(int v){
        System.out.println("Action: setIntValue to "+v);
    }

    public void setCharValue(char c){
        System.out.println("Action: setCharValue to "+c);
    }
}
