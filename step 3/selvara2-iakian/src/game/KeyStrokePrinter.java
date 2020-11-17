package game;
import java.util.Queue;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.ClassCastException;
import java.util.Random;
import java.lang.*;
import java.util.*;

public class KeyStrokePrinter implements InputObserver, Runnable {

    private static int DEBUG = 1;
    private static String CLASSID = "KeyStrokePrinter";
    private static Queue<Character> inputQueue = null;
    private ObjectDisplayGrid displayGrid;
    private Dungeon dungeon;
    private boolean endInvoked = false;
    private Stack[][] objectGrid;

    public KeyStrokePrinter(ObjectDisplayGrid grid, Dungeon dungeon) {
        inputQueue = new ConcurrentLinkedQueue<>();
        displayGrid = grid;
        this.dungeon = dungeon;
    }

    @Override
    public void observerUpdate(char ch) {
        if (DEBUG > 0) {
            System.out.println(CLASSID + ".observerUpdate receiving character " + ch);
        }
        inputQueue.add(ch);
    }

    private void rest() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updatePlayerDisp(Player player) {
        int playerHp = player.getHp();
        int playerScore = player.getScore();
        int temp = playerHp;
        int hpDigs = 0;
        while (temp != 0) {
            hpDigs++;
            temp /= 10;
        }
        displayGrid.addObjectToDisplay(' ', ("HP: ").length(), 0);
        displayGrid.addObjectToDisplay(' ', 1 + ("HP: ").length(), 0);
        displayGrid.addObjectToDisplay(' ', 4 + ("HP: ").length() + ("Score: ").length(), 0);
        displayGrid.addObjectToDisplay(' ', 5 + ("HP: ").length() + ("Score: ").length(), 0);

        for (int i = 0; i < (String.valueOf(playerHp)).length(); i++) {
            displayGrid.addObjectToDisplay((String.valueOf(playerHp)).charAt(i), i + ("HP: ").length(), 0);
        }
        for (int i = 0; i < (String.valueOf(playerScore)).length(); i++) {
            displayGrid.addObjectToDisplay((String.valueOf(playerScore)).charAt(i), 4 + i + ("HP: ").length() + ("Score: ").length(), 0);
        }
    }

    private void updateInfo(String info) {
        if (dungeon.getInfo().length() > 0) {
            for (int i = ("Info: ").length() - 1 ; i < 1 + dungeon.getWidth(); i++) {
                displayGrid.addObjectToDisplay(' ', i, this.dungeon.getBottomHeight() / 2 + this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
            }
        }
        dungeon.setInfo(info);
        for (int i = 0 ; i < dungeon.getInfo().length(); i++) {
            displayGrid.addObjectToDisplay(dungeon.getInfo().charAt(i), i + ("Info: ").length(), 
                                           this.dungeon.getBottomHeight() / 2 + this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
        }
    }

    private void processMonsterHit(Creature attacker, Creature defender) {
        Random random = new Random();
        int damage = random.nextInt(attacker.getMaxHit() + 1);
        if (attacker.getHp() == 0) {
            updateInfo(String.format("%s died", attacker.getName()));
        }
        else if (defender.getHp() - damage <= 0) {
            defender.setHp(0);
            defender.setMaxHit(0);
            updateInfo(String.format("%s died", defender.getName()));
        }
        else if (defender.getHp() - damage > 0) {
            defender.setHp(defender.getHp() - damage);
            updateInfo(String.format("%s --> %s: -%d hp for %s", attacker.getName(), defender.getName(), damage, defender.getName()));
        }
    }

    private Monster monsterNext(Player player, Room loc, char dir) {
        ArrayList<Monster> monsters = loc.getMonsters();

        for (Monster monster : monsters) {
            switch (dir) {
                case 'h':
                    if (monster.getPosX() == player.getPosX() - 1 && monster.getPosY() == player.getPosY()) {
                        return monster;
                    }
                    break;
                case 'l':
                    if (monster.getPosX() == player.getPosX() + 1 && monster.getPosY() == player.getPosY()) {
                        return monster;
                    }
                    break;
                case 'k':
                    if (monster.getPosX() == player.getPosX() && monster.getPosY() == player.getPosY() - 1) {
                        return monster;
                    }
                    break;
                case 'j':
                    if (monster.getPosX() == player.getPosX() && monster.getPosY() == player.getPosY() + 1) {
                        return monster;
                    }
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    private boolean processInput() {

        char ch;
        boolean processing = true;

        while (processing) {
            if (inputQueue.peek() == null) {
                processing = false;
            } else {
                ch = inputQueue.poll();
                if (DEBUG > 1) {
                    System.out.println(CLASSID + ".processInput peek is " + ch);
                }
                if (ch == 'X') {
                    System.out.println("got an X, ending input checking");
                    return false;
                } 
                else {
                    System.out.println("character " + ch + " entered on the keyboard");
                    Structure loc = dungeon.getPlayerLoc();
                    Player player = loc.getPlayer();
                    boolean onDoor = player.getOnDoor();
                    objectGrid = displayGrid.getObjectGrid();
                    
                    updatePlayerDisp(player);
                    switch (ch) {
                        // char next = ' ';
                        // Room room = (Room) loc;
                        // Monster monster = monsterNext(player, room, ch);
                        case 'h':
                            next = (objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek()).charValue();
                            if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')){
                                displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX() - 1, dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                player.setPosX(player.getPosX() - 1);
                            }
                            break;
                        case 'E':
                        case 'e':
                            this.endInvoked = true;
                            System.out.println("Are you sure (Y/N)");
                            break;
                        case 'Y':
                        case 'y':
                            if (this.endInvoked) {
                                System.exit(0);
                            }
                            break;
                        default:
                            if (this.endInvoked) {
                                this.endInvoked = false;
                            }
                            break;
                    }
                    // System.out.println(objectGrid[loc.getPosX() + player.getPosX()][dungeon.getTopHeight()+ loc.getPosY() + player.getPosY()].getChar());
                }
            }
        }
        return true;
    }

    @Override
    public void run() {
        displayGrid.registerInputObserver(this);
        boolean working = true;
        while (working) {
            rest();
            working = (processInput( ));
        }
    }

    public Queue<Character> getInputQueue() {
        return this.inputQueue;
    }
}