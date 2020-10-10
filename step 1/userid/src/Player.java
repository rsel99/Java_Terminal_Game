public class Player extends Creature{

    protected Item sword, armor;
    
    public void setWeapon(Item sword){
        System.out.println("Player: setWeapon to " + sword);
    }

    public void setArmor(Item armor){
        System.out.println("Player: setArmor to " + armor);
    }
}
