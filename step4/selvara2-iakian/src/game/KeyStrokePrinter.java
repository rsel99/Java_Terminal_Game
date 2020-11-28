package game;
import java.util.Queue;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.ClassCastException;
import java.util.Random;
import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

// import org.graalvm.compiler.nodes.cfg.HIRLoop;

public class KeyStrokePrinter implements InputObserver, Runnable {

    private static int DEBUG = 1;
    private static String CLASSID = "KeyStrokePrinter";
    private static Queue<Character> inputQueue = null;
    private ObjectDisplayGrid displayGrid;
    private Dungeon dungeon;
    private boolean endInvoked = false;
    private Stack[][] objectGrid;
    private ArrayList<Character> pack = new ArrayList<>();
    private char input = ' ';

    public KeyStrokePrinter(ObjectDisplayGrid grid, Dungeon dungeon, Stack[][] characters) {
        inputQueue = new ConcurrentLinkedQueue<>();
        displayGrid = grid;
        // objectGrid  = displayGrid.getObjectGrid();
        // System.out.println(objectGrid[0][0].peek());
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
            Thread.sleep(2000);
        } catch (Exception e) {
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
        displayGrid.addObjectToDisplay(' ', 2 + ("HP: ").length(), 0);
        displayGrid.addObjectToDisplay(' ', 4 + ("HP: ").length() + ("Score: ").length(), 0);
        displayGrid.addObjectToDisplay(' ', 5 + ("HP: ").length() + ("Score: ").length(), 0);
        displayGrid.addObjectToDisplay(' ', 6 + ("HP: ").length() + ("Score: ").length(), 0);


        for (int i = 0; i < (String.valueOf(playerHp)).length(); i++) {
            displayGrid.addObjectToDisplay((String.valueOf(playerHp).charAt(i)), i + ("HP: ").length(), 0);
        }
        for (int i = 0; i < (String.valueOf(playerScore)).length(); i++) {
            displayGrid.addObjectToDisplay((String.valueOf(playerScore).charAt(i)), 4 + i + ("HP: ").length() + ("Score: ").length(), 0);
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
        int playerX = player.getPosX();
        int playerY = player.getPosY();

        for (Monster monster : monsters) {
            if(player.getRoom() != monster.getRoom()){
                // Structure location = dungeon.getPlayerLoc();
                // Room ogRoom = (Room) location;
                // int x = ogRoom.getPosX();
                // int y = ogRoom.getPosY();
                // Room newRoom = monster.getRoom();
    
                //Do something here
            }
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
                    
                    // System.out.println(objectGrid[0][0].peek());
                    
                    updatePlayerDisp(player);
                    switch(ch){
                        case 'h':
                            try{
                                Room room = (Room) loc;
                                Monster monster = monsterNext(player, room, (char) ch);
                                // System.out.println(player);
                                // System.out.println(room);
                                // System.out.println(monster);
                                // System.out.println((char) ch);
                                char next = (char) objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')) {

                                    if (onDoor) {
                                        displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.addObjectToDisplay(('+'), room.getPosX() + player.getPosX(), 
                                                                       dungeon.getTopHeight() + room.getPosY() + player.getPosY());

                                        ArrayList<Passage> passages = dungeon.getPassages();
                                        Passage targPass = null;
                                        for (Passage passage : passages) {
                                            if (passage.getDoor2().getPosX() == room.getPosX() + player.getPosX() && 
                                                passage.getDoor2().getPosY() == player.getPosY() + room.getPosY()) {

                                                targPass = passage;
                                                break;
                                            }
                                        }
                                        if (targPass == null) {
                                            for (Passage passage : passages) {
                                                if (passage.getDoor1().getPosX() == room.getPosX() + player.getPosX() && 
                                                    passage.getDoor1().getPosY() == player.getPosY() + room.getPosY()) {

                                                    targPass = passage;
                                                    break;
                                                }
                                            }
                                        }

                                        player.setPosX(player.getPosX() - 1);
                                        player.setPosX(room.getPosX() + player.getPosX());
                                        player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.addObjectToDisplay((player.getType()), player.getPosX(), player.getPosY());

                                        player.setOnDoor(false);
                                        targPass.setPlayer(player);
                                        dungeon.setPlayerLoc(targPass);
                                        loc = targPass;
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX() - 1, dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosX(player.getPosX() - 1);

                                        if (next == '+') {
                                            player.setOnDoor(true);
                                        }
                                    }
                                }
                                else if((next == 'S') || (next == 'T') || (next == 'H')){
                                    processMonsterHit(player, monster);
                                    Thread.sleep(1000);
                                    processMonsterHit(monster, player);
                                    updatePlayerDisp(player);
                                    if (player.getHp() == 0) {
                                        System.exit(0);
                                    }
                                }
                            }
                            catch(ClassCastException e){
                                Passage passage = (Passage) loc;
                                char next = (char) objectGrid[player.getPosX() - 1][player.getPosY()].peek();
                                if (onDoor) {
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    displayGrid.addObjectToDisplay('+', player.getPosX(), player.getPosY());

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (player.getPosX() == room.getPosX() + room.getWidth() - 1 &&
                                            player.getPosY() < room.getPosY() + room.getHeight() + 1 &&
                                            player.getPosY() > room.getPosY()) {
                                            targRoom = room;
                                            break;
                                        }
                                    }
                                    if (targRoom == null) {
                                        for (Room room : rooms) {
                                            if (player.getPosX() == room.getPosX()
                                                    && player.getPosY() < room.getPosY() + room.getHeight() + 1
                                                    && player.getPosY() > room.getPosY()) {
                                                targRoom = room;
                                                break;
                                            }
                                        }
                                    }

                                    player.setPosX(player.getPosX() - 1);
                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(player.getType(), player.getPosX() + targRoom.getPosX(), player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

                                    player.setOnDoor(false);
                                    targRoom.setPlayer(player);
                                    dungeon.setPlayerLoc(targRoom);
                                    loc = targRoom;
                                }
                                else if (next == '#') {
                                    displayGrid.addObjectToDisplay('@', player.getPosX() - 1, player.getPosY());
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() - 1);
                                }
                                else if (next == '+') {
                                    displayGrid.addObjectToDisplay('@', player.getPosX() - 1, player.getPosY());
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() - 1);

                                    player.setOnDoor(true);
                                }
                            }
                            catch (Exception e) {
                                System.out.println("Major implementation issue");
                                e.printStackTrace(System.out);
                            }
                            break;
                        case 'j':
                            try{
                                Room room = (Room) loc;
                                Monster monster = monsterNext(player, room, (char) ch);
                                // System.out.println("HELLO");
                                char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1].peek();
                                if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')){
                                    if (onDoor) {
                                        displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(),
                                                dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.addObjectToDisplay(('+'), room.getPosX() + player.getPosX(),
                                                dungeon.getTopHeight() + room.getPosY() + player.getPosY());

                                        ArrayList<Passage> passages = dungeon.getPassages();
                                        Passage targPass = null;
                                        for (Passage passage : passages) {
                                            if (passage.getDoor2().getPosX() == room.getPosX() + player.getPosX()
                                                    && passage.getDoor2().getPosY() == player.getPosY()
                                                            + room.getPosY()) {

                                                targPass = passage;
                                                break;
                                            }
                                        }
                                        if (targPass == null) {
                                            for (Passage passage : passages) {
                                                if (passage.getDoor1().getPosX() == room.getPosX() + player.getPosX() && 
                                                    passage.getDoor1().getPosY() == player.getPosY() + room.getPosY()) {

                                                    targPass = passage;
                                                    break;
                                                }
                                            }
                                        }

                                        player.setPosY(player.getPosY() + 1);
                                        player.setPosX(room.getPosX() + player.getPosX());
                                        player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.addObjectToDisplay((player.getType()), player.getPosX(), player.getPosY());

                                        player.setOnDoor(false);
                                        targPass.setPlayer(player);
                                        dungeon.setPlayerLoc(targPass);
                                        loc = targPass;
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1);
                                        displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosY(player.getPosY() + 1);

                                        if (next == '+') {
                                            player.setOnDoor(true);
                                        }
                                    }
                                }
                                else if((next == 'S') || (next == 'T') || (next == 'H')){
                                    processMonsterHit(player, monster);
                                    Thread.sleep(1000);
                                    processMonsterHit(monster, player);
                                    updatePlayerDisp(player);
                                    if (player.getHp() == 0) {
                                        System.exit(0);
                                    }
                                }
                            }
                            catch (ClassCastException e) {
                                Passage passage = (Passage) loc;
                                char next = (char) objectGrid[player.getPosX()][player.getPosY() + 1].peek();
                                if (onDoor) {
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    displayGrid.addObjectToDisplay('+', player.getPosX(), player.getPosY());

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (player.getPosX() > room.getPosX()
                                                && player.getPosX() < room.getPosX() + room.getWidth()
                                                && player.getPosY() == room.getPosY() + room.getHeight() - 1) {
                                            targRoom = room;
                                            break;
                                        }
                                    }
                                    if (targRoom == null) {
                                        for (Room room : rooms) {
                                            if (room.getRoomNum() == 3) {
                                                System.out.println("playerX: " + player.getPosX());
                                                System.out.println("playerY: " + player.getPosY());
                                                System.out.println("");
                                            }
                                            if (player.getPosX() > room.getPosX()
                                                    && player.getPosX() < room.getPosX() + room.getWidth()
                                                    && player.getPosY() == room.getPosY()) {
                                                targRoom = room;
                                                break;
                                            }
                                        }
                                    }

                                    player.setPosY(player.getPosY() + 1);
                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(player.getType(),
                                            player.getPosX() + targRoom.getPosX(),
                                            player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

                                    player.setOnDoor(false);
                                    targRoom.setPlayer(player);
                                    dungeon.setPlayerLoc(targRoom);
                                    loc = targRoom;
                                } 
                                else if (next == '#') {
                                    displayGrid.addObjectToDisplay('@', player.getPosX(), player.getPosY() + 1);
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() + 1);
                                } 
                                else if (next == '+') {
                                    displayGrid.addObjectToDisplay('@', player.getPosX(), player.getPosY() + 1);
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() + 1);

                                    player.setOnDoor(true);
                                }
                            }
                            catch(Exception e){
                                System.out.println("Major implementation issue");
                                e.printStackTrace(System.out);
                            }
                            break;
                        case 'k':
                            try{
                                Room room = (Room) loc;
                                Monster monster = monsterNext(player, room, (char) ch);
                                // System.out.println("HELLO");
                                char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1].peek();
                                if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')){
                                    if (onDoor) {
                                        displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(),
                                                dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.addObjectToDisplay(('+'), room.getPosX() + player.getPosX(),
                                                dungeon.getTopHeight() + room.getPosY() + player.getPosY());

                                        ArrayList<Passage> passages = dungeon.getPassages();
                                        Passage targPass = null;
                                        for (Passage passage : passages) {
                                            if (passage.getDoor2().getPosX() == room.getPosX() + player.getPosX()
                                                    && passage.getDoor2().getPosY() == player.getPosY()
                                                            + room.getPosY()) {

                                                targPass = passage;
                                                break;
                                            }
                                        }
                                        if (targPass == null) {
                                            for (Passage passage : passages) {
                                                if (passage.getDoor1().getPosX() == room.getPosX() + player.getPosX() && 
                                                    passage.getDoor1().getPosY() == player.getPosY() + room.getPosY()) {

                                                    targPass = passage;
                                                    break;
                                                }
                                            }
                                        }

                                        player.setPosY(player.getPosY() - 1);
                                        player.setPosX(room.getPosX() + player.getPosX());
                                        player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.addObjectToDisplay((player.getType()), player.getPosX(), player.getPosY());

                                        player.setOnDoor(false);
                                        targPass.setPlayer(player);
                                        dungeon.setPlayerLoc(targPass);
                                        loc = targPass;
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1);
                                        displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosY(player.getPosY() - 1);

                                        if (next == '+') {
                                            player.setOnDoor(true);
                                        }
                                    }
                                }
                                else if((next == 'S') || (next == 'T') || (next == 'H')){
                                    processMonsterHit(player, monster);
                                    Thread.sleep(1000);
                                    processMonsterHit(monster, player);
                                    updatePlayerDisp(player);
                                    if (player.getHp() == 0) {
                                        System.exit(0);
                                    }
                                }
                            }
                            catch (ClassCastException e) {
                                Passage passage = (Passage) loc;
                                char next = (char) objectGrid[player.getPosX()][player.getPosY() - 1].peek();
                                if (onDoor) {
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    displayGrid.addObjectToDisplay('+', player.getPosX(), player.getPosY());

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (player.getPosX() > room.getPosX()
                                                && player.getPosX() < room.getPosX() + room.getWidth()
                                                && player.getPosY() == room.getPosY() + room.getHeight() + 1) {
                                            targRoom = room;
                                            break;
                                        }
                                    }
                                    if (targRoom == null) {
                                        for (Room room : rooms) {
                                            if (player.getPosX() > room.getPosX()
                                                    && player.getPosX() < room.getPosX() + room.getWidth()
                                                    && player.getPosY() == room.getPosY()) {
                                                targRoom = room;
                                                break;
                                            }
                                        }
                                    }

                                    player.setPosY(player.getPosY() - 1);
                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(player.getType(),
                                            player.getPosX() + targRoom.getPosX(),
                                            player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

                                    player.setOnDoor(false);
                                    targRoom.setPlayer(player);
                                    dungeon.setPlayerLoc(targRoom);
                                    loc = targRoom;
                                } 
                                else if (next == '#') {
                                    displayGrid.addObjectToDisplay('@', player.getPosX(), player.getPosY() - 1);
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() - 1);
                                } 
                                else if (next == '+') {
                                    displayGrid.addObjectToDisplay('@', player.getPosX(), player.getPosY() - 1);
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() - 1);

                                    player.setOnDoor(true);
                                }
                            }
                            catch(Exception e){
                                System.out.println("Major implementation issue");
                                e.printStackTrace(System.out);
                            }
                            break;
                        case 'l':
                            try{
                                Room room = (Room) loc;
                                Monster monster = monsterNext(player, room, (char) ch);
                                // System.out.println(player);
                                // System.out.println(room);
                                // System.out.println(monster);
                                // System.out.println((char) ch);
                                char next = (char) objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')){
                                    if (onDoor) {
                                        displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.addObjectToDisplay(('+'), room.getPosX() + player.getPosX(), 
                                                                       dungeon.getTopHeight() + room.getPosY() + player.getPosY());

                                        ArrayList<Passage> passages = dungeon.getPassages();
                                        Passage targPass = null;
                                        for (Passage passage : passages) {
                                            if (passage.getDoor2().getPosX() == room.getPosX() + player.getPosX() && 
                                                passage.getDoor2().getPosY() == player.getPosY() + room.getPosY()) {

                                                targPass = passage;
                                                break;
                                            }
                                        }
                                        if (targPass == null) {
                                            for (Passage passage : passages) {
                                                if (passage.getDoor1().getPosX() == room.getPosX() + player.getPosX() && 
                                                    passage.getDoor1().getPosY() == player.getPosY() + room.getPosY()) {

                                                    targPass = passage;
                                                    break;
                                                }
                                            }
                                        }
                                        player.setPosX(player.getPosX() + 1);
                                        player.setPosX(room.getPosX() + player.getPosX());
                                        player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.addObjectToDisplay((player.getType()), player.getPosX(), player.getPosY());

                                        player.setOnDoor(false);
                                        targPass.setPlayer(player);
                                        dungeon.setPlayerLoc(targPass);
                                        loc = targPass;
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX() + 1, dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosX(player.getPosX() + 1);

                                        if (next == '+') {
                                            player.setOnDoor(true);
                                        }
                                    }
                                }
                                else if((next == 'S') || (next == 'T') || (next == 'H')){
                                    processMonsterHit(player, monster);
                                    Thread.sleep(1000);
                                    processMonsterHit(monster, player);
                                    updatePlayerDisp(player);
                                    if (player.getHp() == 0) {
                                        System.exit(0);
                                    }
                                
                                }
                            }
                            catch(ClassCastException e){
                                Passage passage = (Passage) loc;
                                char next = (char) objectGrid[player.getPosX() + 1][player.getPosY()].peek();
                                if (onDoor) {
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    displayGrid.addObjectToDisplay('+', player.getPosX(), player.getPosY());

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (player.getPosX() == room.getPosX() + room.getWidth() - 1 &&
                                            player.getPosY() < room.getPosY() + room.getHeight() + 1 &&
                                            player.getPosY() > room.getPosY()) {
                                            targRoom = room;
                                            break;
                                        }
                                    }
                                    if (targRoom == null) {
                                        for (Room room : rooms) {
                                            if (player.getPosX() == room.getPosX()
                                                    && player.getPosY() < room.getPosY() + room.getHeight() + 1
                                                    && player.getPosY() > room.getPosY()) {
                                                targRoom = room;
                                                break;
                                            }
                                        }
                                    }

                                    player.setPosX(player.getPosX() + 1);
                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(player.getType(), player.getPosX() + targRoom.getPosX(), player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

                                    player.setOnDoor(false);
                                    targRoom.setPlayer(player);
                                    dungeon.setPlayerLoc(targRoom);
                                    loc = targRoom;
                                }
                                else if (next == '#') {
                                    displayGrid.addObjectToDisplay('@', player.getPosX() + 1, player.getPosY());
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() + 1);
                                }
                                else if (next == '+') {
                                    displayGrid.addObjectToDisplay('@', player.getPosX() + 1, player.getPosY());
                                    displayGrid.removeObjectFromDisplay(player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() + 1);

                                    player.setOnDoor(true);
                                }
                            }
                            catch(Exception e) {
                                System.out.println("Major implementation issue");
                                e.printStackTrace(System.out);
                            }
                            break;
                        case 'p':
                            Room room = (Room) loc;
                            for(int i = 0; i < (this.dungeon.getWidth() - 10); i++){
                                displayGrid.addObjectToDisplay(' ', 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                            }
                            displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                            char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                            if((next == ']') || (next == ')') || (next == '?')){  
                                pack.add(next);
                                // System.out.println(pack.get(0));
                                System.out.println("ADDING TO PACK");
                                // displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                            }
                            displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                            break;
                        case 'd':
                            input = 'd';
                            break;
                        case '0':
                            // System.out.println(input);
                            if(input == 'd'){
                                // System.out.println(pack.get(0));
                                for(int i = 0; i < (this.dungeon.getWidth() - 10); i++){
                                    displayGrid.addObjectToDisplay(' ', 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                }
                                room = (Room) loc;
                                displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                if(((next == ']') || (next == ')') || (next == '?') || (next == '.')) && (pack.size() > 0)){
                                    displayGrid.addObjectToDisplay(pack.get(0), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    pack.remove(0);
                                    
                                }
                                displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                            }
                            break;
                        case '1':
                            // System.out.println(input);
                            if(input == 'd'){
                                // System.out.println(pack.get(0));
                                for(int i = 0; i < (this.dungeon.getWidth() - 10); i++){
                                    displayGrid.addObjectToDisplay(' ', 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                }
                                room = (Room) loc;
                                displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                if(((next == ']') || (next == ')') || (next == '?') || (next == '.')) && (pack.size() > 1)){
                                    displayGrid.addObjectToDisplay(pack.get(1), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    pack.remove(1);
                                    
                                }
                                displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                            }
                            break;
                        case '2':
                            // System.out.println(input);
                            if(input == 'd'){
                                // System.out.println(pack.get(0));
                                for(int i = 0; i < (this.dungeon.getWidth() - 10); i++){
                                    displayGrid.addObjectToDisplay(' ', 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                }
                                room = (Room) loc;
                                displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                if(((next == ']') || (next == ')') || (next == '?') || (next == '.')) && (pack.size() > 2)){
                                    displayGrid.addObjectToDisplay(pack.get(2), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    pack.remove(2);
                                    
                                }
                                displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                            }
                            break;
                        case '3':
                            // System.out.println(input);
                            if(input == 'd'){
                                // System.out.println(pack.get(0));
                                for(int i = 0; i < (this.dungeon.getWidth() - 10); i++){
                                    displayGrid.addObjectToDisplay(' ', 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                }
                                room = (Room) loc;
                                displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                if(((next == ']') || (next == ')') || (next == '?') || (next == '.')) && (pack.size() > 3)){
                                    displayGrid.addObjectToDisplay(pack.get(3), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    pack.remove(3);
                                    
                                }
                                displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                            }
                            break;
                        case '4':
                            // System.out.println(input);
                            if(input == 'd'){
                                // System.out.println(pack.get(0));
                                for(int i = 0; i < (this.dungeon.getWidth() - 10); i++){
                                    displayGrid.addObjectToDisplay(' ', 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                }
                                room = (Room) loc;
                                displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                if(((next == ']') || (next == ')') || (next == '?') || (next == '.')) && (pack.size() > 4)){
                                    displayGrid.addObjectToDisplay(pack.get(4), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    pack.remove(4);
                                    
                                }
                                displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                            }
                            break;
                        case 'I':
                        case 'i':
                            if(pack.size() > 0){
                                for(int i = 0; i < pack.size(); i++){
                                    displayGrid.addObjectToDisplay((char) (i + 48), 7 + 6*i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                    displayGrid.addObjectToDisplay(':', 8+ 6*i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                    displayGrid.addObjectToDisplay(' ', 9+ 6*i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                    displayGrid.addObjectToDisplay(pack.get(i), 10+ 6*i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                    if(i < (pack.size() - 1)){
                                        displayGrid.addObjectToDisplay(',', 11+ 6*i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                    }
                                }
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
                    // System.out.println(objectGrid[loc.getPosX() + player.getPosX()][dungeon.getTopHeight()+ loc.getPosY() + player.getPosY()].peek());
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