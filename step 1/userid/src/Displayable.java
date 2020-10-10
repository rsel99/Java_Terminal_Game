public class Displayable {

    protected int maxHit, hpMoves, Hp, v, x, y;
    protected char t;

    public Displayable(){
        System.out.println("Displayable: constructor");
    }

    public void setInvisible(){
        System.out.println("Displayable: setInvisible");
    }

    public void setVisible(){
        System.out.println("Displayable: setVisible");
    }

    public void setMaxHit(int maxHit){
        System.out.println("Displayable: setMaxHit to " + maxHit);
    }

    public void setHpMove(int hpMoves){
        System.out.println("Displayable: setHpMove to " + hpMoves);
    }

    public void setHp(int Hp){
        System.out.println("Displayable: setHp to " + Hp);
    }

    public void setType(char t){
        System.out.println("Displayable: setType to " + t);
    }

    public void setIntValue(int v){
        System.out.println("Displayable: setIntValue to " + v);
    }

    public void setPosX(int x){
        System.out.println("Displayable: stePosX to " + x);
    }

    public void setPosY(int y){
        System.out.println("Displayable: setPosY to " + y);
    }

    public void setWidth(int x){
        System.out.println("Displayable: setWidth to " + x);
    }

    public void setHeight(int y){
        System.out.println("Displayable: setHeight to " + y);
    }
}
