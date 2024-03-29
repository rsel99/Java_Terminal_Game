package src;

public class Displayable {

    protected int maxhit, hpMoves, Hp, v, x, y, w, h;
    protected char t;

    public Displayable(){
        System.out.println("Displayable: constructor, " + this);
    }

    public void setInvisible(){
        System.out.println("Displayable: setInvisible");
    }

    public void setVisible(){
        System.out.println("Displayable: setVisible");
    }

    public void setMaxHit(int maxhit){
        this.maxhit = maxhit;
        System.out.println("Displayable: setMaxHit to " + maxhit);
    }

    public void setHpMove(int hpMoves){
        this.hpMoves = hpMoves;
        System.out.println("Displayable: setHpMove to " + hpMoves);
    }

    public void setHp(int Hp){
        this.Hp = Hp;
        System.out.println("Displayable: setHp to " + Hp);
    }

    public void setType(char t){
        this.t = t;
        System.out.println("Displayable: setType to " + t);
    }

    public void setIntValue(int v){
        this.v = v;
        System.out.println("Displayable: setIntValue to " + v);
    }

    public void setPosX(int x){
        this.x = x;
        System.out.println("Displayable: stePosX to " + x);
    }

    public void setPosY(int y){
        this.y = y;
        System.out.println("Displayable: setPosY to " + y);
    }

    public void setWidth(int w){
        this.w = w;
        System.out.println("Displayable: setWidth to " + w);
    }

    public void setHeight(int h){
        this.h = h;
        System.out.println("Displayable: setHeight to " + h);
    }
}
