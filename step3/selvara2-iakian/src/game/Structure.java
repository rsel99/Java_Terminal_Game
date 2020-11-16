package game;

public class Structure extends Displayable{
    Player player;

    public Structure(Player player) {
        this.player = player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
}
