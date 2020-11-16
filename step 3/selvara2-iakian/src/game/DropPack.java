package game;

public class DropPack extends CreatureAction{

    protected String name;
    protected String type;
    protected Creature owner;
    protected ArrayList<Char> pack;
    
    public DropPack(String name, String type, Creature owner){
        super(owner, name, type);
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.pack = new ArrayList<Char>();
        System.out.println("DropPack: constructor: name: " + name + ", Creature: " + owner + ", " + this);
    }

    public void addToPack(char ch){
        pack.add(ch);
    }

    public char dropPack(int i){
        char temp = pack[i];
        pack.remove(i);
        return temp;
    }

}
