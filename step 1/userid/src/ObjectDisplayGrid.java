package src;

public class ObjectDisplayGrid{

    protected int gameHeight, width, topHeight, bottomHeight;

    public void getObjectDisplayGrid(int gameHeight, int width, int topHeight){
        System.out.println("ObjectDisplayGrid: getObjectDisplayGrid: gameHeight: " + gameHeight + ", width: " + width + ", topHeight: " + topHeight);
    }

    public void setTopMessageHeight(int topHeight){
        this.topHeight = topHeight;
        System.out.println("ObjectDisplayGrid: setTopMessageHeight to " + topHeight);
    }
}