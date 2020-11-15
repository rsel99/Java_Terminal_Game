package game;
import java.util.Queue;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.ClassCastException;
import java.util.Random;

public class KeyStrokePrinter implements InputObserver, Runnable {

    private static int DEBUG = 1;
    private static String CLASSID = "KeyStrokePrinter";
    private static Queue<Character> inputQueue = null;
    private ObjectDisplayGrid displayGrid;
    private Dungeon dungeon;
    private boolean endInvoked = false;

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

    private void updateMessageDisp(Player player) {
        int playerHp = player.getHp();
        int playerScore = player.getScore();
        int temp = playerHp;
        int hpDigs = 0;
        while (temp != 0) {
            hpDigs++;
            temp /= 10;
        }
        displayGrid.addObjectToDisplay(new Char(' '), ("HP: ").length(), 0);
        displayGrid.addObjectToDisplay(new Char(' '), 1 + ("HP: ").length(), 0);
        displayGrid.addObjectToDisplay(new Char(' '), 4 + ("HP: ").length() + ("Score: ").length(), 0);
        displayGrid.addObjectToDisplay(new Char(' '), 5 + ("HP: ").length() + ("Score: ").length(), 0);

        for (int i = 0; i < (String.valueOf(playerHp)).length(); i++) {
            displayGrid.addObjectToDisplay(new Char((String.valueOf(playerHp)).charAt(i)), i + ("HP: ").length(), 0);
        }
        for (int i = 0; i < (String.valueOf(playerScore)).length(); i++) {
            displayGrid.addObjectToDisplay(new Char((String.valueOf(playerScore)).charAt(i)), 4 + i + ("HP: ").length() + ("Score: ").length(), 0);
        }
    }

    private void processMonsterHit(Creature attacker, Creature defender) {
        Random random = new Random();
        int damage = random.nextInt(attacker.getMaxHit() + 1);
        if (defender.getHp() - damage > 0) {
            defender.setHp(defender.getHp() - damage);
        }
        else {
            defender.setHp(0);
            defender.setMaxHit(0);
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
                    Char[][] objectGrid = displayGrid.getObjectGrid();
                    
                    updateMessageDisp(player);

                    switch (ch) {
                        case 'h':
                            try {
                                Room room = (Room) loc;
                                Monster monster = monsterNext(player, room, ch);
                                if ((objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '.' )) {
                                    // Change location
                                    if (onDoor) {
                                        displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosX(player.getPosX() - 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());        
                                        player.setOnDoor(false);
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay(objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()], 
                                                                    room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosX(player.getPosX() - 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    }
                                }
                                else if (objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('.'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosX(player.getPosX() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor && (objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '#') ||
                                                    objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '.') {
                                    displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    

                                    ArrayList<Passage> passages = dungeon.getPassages();
                                    Passage targPass = null;
                                    for (Passage passage : passages) {
                                        if (passage.getDoor2().getPosX() == room.getPosX() + player.getPosX() && 
                                            passage.getDoor2().getPosY() == player.getPosY() + room.getPosY()) {
                                            targPass = passage;
                                            break;
                                        }
                                    }
                                    player.setPosX(player.getPosX() - 1);
                                    player.setPosX(room.getPosX() + player.getPosX());
                                    player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());

                                    player.setOnDoor(false);
                                    targPass.setPlayer(player);
                                    dungeon.setPlayerLoc(targPass);
                                    loc = targPass;
                                }
                                else if (monster != null) {
                                    processMonsterHit(player, monster);
                                    processMonsterHit(monster, player);
                                    updateMessageDisp(player);
                                    if (player.getHp() == 0) {
                                        System.exit(0);
                                    }
                                }
                            }
                            catch(Exception e) {
                                Passage passage = (Passage) loc;
                                if ((objectGrid[player.getPosX() - 1][player.getPosY()].getChar() == '#' )) {
                                    // Change location
                                    if (onDoor) {
                                        displayGrid.addObjectToDisplay(new Char('+'), 
                                                                       player.getPosX(), 
                                                                       player.getPosY());
                                        player.setPosX(player.getPosX() - 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()),
                                                                       player.getPosX(),
                                                                       player.getPosY());
                                        player.setOnDoor(false);
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay(objectGrid[player.getPosX() - 1][player.getPosY()], player.getPosX(), + player.getPosY());
                                        player.setPosX(player.getPosX() - 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    }
                                }
                                else if (objectGrid[player.getPosX() - 1][player.getPosY()].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('#'), player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor && (objectGrid[player.getPosX() - 1][player.getPosY()].getChar() == '#') ||
                                                    objectGrid[player.getPosX() - 1][player.getPosY()].getChar() == '.') {
                                    displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());

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

                                    player.setPosX(player.getPosX() - 1);
                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX() + targRoom.getPosX(), player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

                                    player.setOnDoor(false);
                                    targRoom.setPlayer(player);
                                    dungeon.setPlayerLoc(targRoom);
                                    loc = targRoom;
                                }
                                else if ((objectGrid[player.getPosX() - 1][player.getPosY()].getChar() == 'X') || (objectGrid[player.getPosX() - 1][player.getPosY()].getChar() == ' ')) {
                                    System.out.println("Out of Bounds");
                                }
                                else {
                                    System.out.println("Major implementation issue");
                                    e.printStackTrace(System.out);
                                }
                            }
                            break;
                        case 'j':
                            try {
                                Room room = (Room) loc;
                                Monster monster = monsterNext(player, room, ch);
                                if ((objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1].getChar() == '.' )) {
                                    // Change location
                                    if (onDoor) {
                                        displayGrid.addObjectToDisplay(new Char('+'), 
                                                                       room.getPosX() + player.getPosX(),
                                                                       dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosY(player.getPosY() + 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()),
                                                                       room.getPosX() + player.getPosX(),
                                                                       dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setOnDoor(false);
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay(objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1], 
                                                                    room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosY(player.getPosY() + 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    }
                                }
                                else if (objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('.'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosY(player.getPosY() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor && (objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1].getChar() == '#') ||
                                                    objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1].getChar() == '.') {
                                    displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());

                                    ArrayList<Passage> passages = dungeon.getPassages();
                                    Passage targPass = null;
                                    for (Passage passage : passages) {
                                        if (passage.getDoor2().getPosX() == room.getPosX() + player.getPosX() && 
                                            passage.getDoor2().getPosY() == player.getPosY() + room.getPosY()) {
                                            targPass = passage;
                                            break;
                                        }
                                        else if (passage.getDoor1().getPosX() == room.getPosX() + player.getPosX() && 
                                                 passage.getDoor1().getPosY() == player.getPosY() + room.getPosY()) {
                                            targPass = passage;
                                            break;
                                        }
                                    }

                                    player.setPosY(player.getPosY() + 1);
                                    player.setPosX(room.getPosX() + player.getPosX());
                                    player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());

                                    player.setOnDoor(false);
                                    targPass.setPlayer(player);
                                    dungeon.setPlayerLoc(targPass);
                                    loc = targPass;
                                }
                                else if (monster != null) {
                                    processMonsterHit(player, monster);
                                    processMonsterHit(monster, player);
                                    updateMessageDisp(player);
                                    if (player.getHp() == 0) {
                                        System.exit(0);
                                    }
                                }
                            }
                            catch(Exception e) {
                                Passage passage = (Passage) loc;
                                if ((objectGrid[player.getPosX()][player.getPosY() + 1].getChar() == '#')) {
                                    // Change location
                                    if (onDoor) {
                                        displayGrid.addObjectToDisplay(new Char('+'), 
                                                                       player.getPosX(),
                                                                       player.getPosY());
                                        player.setPosY(player.getPosY() + 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()),
                                                                       player.getPosX(),
                                                                       player.getPosY());
                                        player.setOnDoor(false);
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay(objectGrid[player.getPosX()][player.getPosY() + 1], player.getPosX(), player.getPosY());
                                        player.setPosY(player.getPosY() + 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    }
                                }
                                else if (objectGrid[player.getPosX()][player.getPosY() + 1].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('#'), player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor && (objectGrid[player.getPosX()][player.getPosY() + 1].getChar() == '#') ||
                                                    objectGrid[player.getPosX()][player.getPosY() + 1].getChar() == '.') {
                                    displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    System.out.println("player x: " + player.getPosX());
                                    System.out.println("player y: " + player.getPosY());
                                    for (Room room : rooms) {
                                        System.out.println("room: " + room.getRoomNum());
                                        System.out.println(room.getPosX());
                                        System.out.println(room.getPosX() + room.getWidth());
                                        System.out.println(room.getPosY());
                                        if (player.getPosX() > room.getPosX() && 
                                            player.getPosX() < room.getPosX() + room.getWidth() &&
                                            player.getPosY() == room.getPosY() + dungeon.getTopHeight()) {
                                            targRoom = room;
                                            break;
                                        }
                                    }

                                    player.setPosY(player.getPosY() + 1);
                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX() + targRoom.getPosX(), 
                                                                   player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());
                                    player.setOnDoor(false);
                                    targRoom.setPlayer(player);
                                    dungeon.setPlayerLoc(targRoom);
                                    loc = targRoom;
                                }
                                else if ((objectGrid[player.getPosX()][player.getPosY() + 1].getChar() == 'X') || (objectGrid[player.getPosX()][player.getPosY() + 1].getChar() == ' ')) {
                                    System.out.println("Out of Bounds");
                                }
                                else {
                                    System.out.println("Major implementation issue");
                                    e.printStackTrace(System.out);
                                }
                            }
                            break;
                        case 'k':
                            try {
                                Room room = (Room) loc;
                                Monster monster = monsterNext(player, room, ch);
                                if ((objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1].getChar() == '.' )) {
                                    // Change location
                                    if (onDoor) {
                                        displayGrid.addObjectToDisplay(new Char('+'), 
                                                                       room.getPosX() + player.getPosX(),
                                                                       dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosY(player.getPosY() - 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()),
                                                                       room.getPosX() + player.getPosX(),
                                                                       dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setOnDoor(false);
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay(objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1], 
                                                                    room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosY(player.getPosY() - 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    }
                                }
                                else if (objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('.'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosY(player.getPosY() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor && (objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1].getChar() == '#') ||
                                                    objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1].getChar() == '.') {
                                    displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    

                                    ArrayList<Passage> passages = dungeon.getPassages();
                                    Passage targPass = null;
                                    for (Passage passage : passages) {
                                        if (passage.getDoor2().getPosX() == room.getPosX() + player.getPosX() && 
                                            passage.getDoor2().getPosY() == player.getPosY() + room.getPosY()) {
                                            targPass = passage;
                                            break;
                                        }
                                        else if (passage.getDoor1().getPosX() == room.getPosX() + player.getPosX() && 
                                                 passage.getDoor1().getPosY() == player.getPosY() + room.getPosY()) {
                                            targPass = passage;
                                            break;
                                        }
                                    }

                                    player.setPosY(player.getPosY() - 1);
                                    player.setPosX(room.getPosX() + player.getPosX());
                                    player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());

                                    player.setOnDoor(false);
                                    targPass.setPlayer(player);
                                    dungeon.setPlayerLoc(targPass);
                                    loc = targPass;
                                }
                                else if (monster != null) {
                                    processMonsterHit(player, monster);
                                    processMonsterHit(monster, player);
                                    updateMessageDisp(player);
                                    if (player.getHp() == 0) {
                                        System.exit(0);
                                    }
                                }
                            }
                            catch(Exception e) {
                                Passage passage = (Passage) loc;
                                if ((objectGrid[player.getPosX()][player.getPosY() - 1].getChar() == '#')) {
                                    // Change location
                                    if (onDoor) {
                                        displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(),
                                                player.getPosY());
                                        player.setPosY(player.getPosY() - 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(),
                                                player.getPosY());
                                        player.setOnDoor(false);
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay(objectGrid[player.getPosX()][player.getPosY() - 1], player.getPosX(), player.getPosY());
                                        player.setPosY(player.getPosY() - 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    }
                                }
                                else if (objectGrid[player.getPosX()][player.getPosY() - 1].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('#'), player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor && (objectGrid[player.getPosX()][player.getPosY() - 1].getChar() == '#') ||
                                                    objectGrid[player.getPosX()][player.getPosY() - 1].getChar() == '.') {
                                    displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    System.out.println("player x: " + player.getPosX());
                                    System.out.println("player y; " + player.getPosY());
                                    for (Room room : rooms) {
                                        System.out.println(room.getPosX());
                                        System.out.println(room.getPosX() + room.getWidth());
                                        System.out.println(room.getPosY() + room.getHeight() + 1);
                                        if (player.getPosX() > room.getPosX() && 
                                            player.getPosX() < room.getPosX() + room.getWidth() &&
                                            player.getPosY() == room.getPosY() + room.getHeight() + 1) {
                                            targRoom = room;
                                            break;
                                        }
                                    }
                                    player.setPosY(player.getPosY() - 1);
                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), 
                                                                   player.getPosX() + targRoom.getPosX(), 
                                                                   player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

                                    player.setOnDoor(false);
                                    targRoom.setPlayer(player);
                                    dungeon.setPlayerLoc(targRoom);
                                    loc = targRoom;
                                }
                                else if ((objectGrid[player.getPosX()][player.getPosY() - 1].getChar() == 'X') || (objectGrid[player.getPosX()][player.getPosY() - 1].getChar() == ' ')) {
                                    System.out.println("Out of Bounds");
                                }
                                else {
                                    System.out.println("Major implementation issue");
                                    e.printStackTrace(System.out);
                                }
                            }
                            break;
                        case 'l':
                            try {
                                Room room = (Room) loc;
                                Monster monster = monsterNext(player, room, ch);
                                if ((objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '.' )) {
                                    // Change location
                                    if (onDoor) {
                                        displayGrid.addObjectToDisplay(new Char('+'), 
                                                                       room.getPosX() + player.getPosX(),
                                                                       dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosX(player.getPosX() + 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()),
                                                                       room.getPosX() + player.getPosX(),
                                                                       dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setOnDoor(false);
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay(objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()], 
                                                                    room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        player.setPosX(player.getPosX() + 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    }
                                }
                                else if (objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('.'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosX(player.getPosX() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor && (objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '#') ||
                                                    objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '.') {
                                    displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());

                                    ArrayList<Passage> passages = dungeon.getPassages();
                                    Passage targPass = null;
                                    for (Passage passage : passages) {
                                        if (passage.getDoor1().getPosX() == room.getPosX() + player.getPosX() && 
                                            passage.getDoor1().getPosY() == player.getPosY() + room.getPosY()) {
                                            targPass = passage;
                                            break;
                                        }
                                        else if (passage.getDoor2().getPosX() == room.getPosX() + player.getPosX() && 
                                                 passage.getDoor2().getPosY() == player.getPosY() + room.getPosY()) {
                                            targPass = passage;
                                            break;
                                        }
                                    }

                                    player.setPosX(player.getPosX() + 1);
                                    player.setPosX(room.getPosX() + player.getPosX());
                                    player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());

                                    player.setOnDoor(false);
                                    targPass.setPlayer(player);
                                    dungeon.setPlayerLoc(targPass);
                                    loc = targPass;
                                }
                                else if (monster != null) {
                                    processMonsterHit(player, monster);
                                    processMonsterHit(monster, player);
                                    updateMessageDisp(player);
                                    if (player.getHp() == 0) {
                                        System.exit(0);
                                    }
                                }
                            }
                            catch(Exception e) {
                                Passage passage = (Passage) loc;
                                if ((objectGrid[player.getPosX() + 1][player.getPosY()].getChar() == '#' )) {
                                    // Change location
                                    if (onDoor) {
                                        displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());
                                        player.setPosX(player.getPosX() + 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                        player.setOnDoor(false);
                                    }
                                    else {
                                        displayGrid.addObjectToDisplay(objectGrid[player.getPosX() + 1][player.getPosY()], player.getPosX(), + player.getPosY());
                                        player.setPosX(player.getPosX() + 1);
                                        displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    }
                                }
                                else if (objectGrid[player.getPosX() + 1][player.getPosY()].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('#'), player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor && (objectGrid[player.getPosX() + 1][player.getPosY()].getChar() == '#') ||
                                                    objectGrid[player.getPosX() + 1][player.getPosY()].getChar() == '.') {
                                    displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (player.getPosX() == room.getPosX() &&
                                            player.getPosY() < room.getPosY() + room.getHeight() + dungeon.getTopHeight() &&
                                            player.getPosY() > room.getPosY() + dungeon.getTopHeight()) {
                                            targRoom = room;
                                            break;
                                        }
                                    }
                                    player.setPosX(player.getPosX() + 1);
                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX() + targRoom.getPosX(), player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

                                    player.setOnDoor(false);
                                    targRoom.setPlayer(player);
                                    dungeon.setPlayerLoc(targRoom);
                                    loc = targRoom;
                                }
                                else if ((objectGrid[player.getPosX() + 1][player.getPosY()].getChar() == 'X') || 
                                         (objectGrid[player.getPosX() + 1][player.getPosY()].getChar() == ' ')) {
                                    System.out.println("Out of Bounds");
                                }
                                else {
                                    System.out.println("Major implementation issue");
                                    e.printStackTrace(System.out);
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
