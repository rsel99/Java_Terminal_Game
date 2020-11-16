package game;
import java.util.Queue;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.ClassCastException;

public class KeyStrokePrinter implements InputObserver, Runnable {

    private static int DEBUG = 1;
    private static String CLASSID = "KeyStrokePrinter";
    private static Queue<Character> inputQueue = null;
    private ObjectDisplayGrid displayGrid;
    private Dungeon dungeon;

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

                    switch (ch) {
                        case 'h':
                            System.out.println(loc);
                            System.out.println(player);

                            try {
                                Room room = (Room) loc;
                                if ((objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '.' )) {
                                    // Change location
                                    displayGrid.addObjectToDisplay(objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()], 
                                                                room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosX(player.getPosX() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else if (objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('.'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosX(player.getPosX() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor) {
                                    displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosX(player.getPosX() - 1);

                                    ArrayList<Passage> passages = dungeon.getPassages();
                                    Passage targPass = null;
                                    for (Passage passage : passages) {
                                        System.out.println(room.getRoomNum());
                                        if (passage.getRoom2() == room.getRoomNum()) {
                                            targPass = passage;
                                            break;
                                        }
                                    }
                                    System.out.println(targPass);

                                    player.setPosX(room.getPosX() + player.getPosX());
                                    player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());

                                    player.setOnDoor(false);
                                    targPass.setPlayer(player);
                                    dungeon.setPlayerLoc(targPass);
                                    loc = targPass;
                                }
                            }
                            catch(Exception e) {
                                Passage passage = (Passage) loc;
                                if ((objectGrid[player.getPosX() - 1][player.getPosY()].getChar() == '#' )) {
                                    // Change location
                                    displayGrid.addObjectToDisplay(objectGrid[player.getPosX() - 1][player.getPosY()], player.getPosX(), + player.getPosY());
                                    player.setPosX(player.getPosX() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                }
                                else if (objectGrid[player.getPosX() - 1][player.getPosY()].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('#'), player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor) {
                                    displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() - 1);

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (passage.getRoom1() == room.getRoomNum()) {
                                            targRoom = room;
                                            break;
                                        }
                                    }

                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX() + targRoom.getPosX(), player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

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
                        case 'j':
                            try {
                                Room room = (Room) loc;
                                if ((objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1].getChar() == '.' )) {
                                    // Change location
                                    displayGrid.addObjectToDisplay(objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1], 
                                                                room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosY(player.getPosY() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else if (objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('.'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosY(player.getPosY() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor) {
                                    displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosY(player.getPosY() + 1);

                                    ArrayList<Passage> passages = dungeon.getPassages();
                                    Passage targPass = null;
                                    for (Passage passage : passages) {
                                        System.out.println(room.getRoomNum());
                                        if (passage.getRoom1() == room.getRoomNum()) {
                                            targPass = passage;
                                            break;
                                        }
                                    }
                                    System.out.println(targPass);

                                    player.setPosX(room.getPosX() + player.getPosX());
                                    player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());

                                    player.setOnDoor(false);
                                    targPass.setPlayer(player);
                                    dungeon.setPlayerLoc(targPass);
                                    loc = targPass;
                                }
                            }
                            catch(Exception e) {
                                Passage passage = (Passage) loc;
                                if ((objectGrid[player.getPosX()][player.getPosY() + 1].getChar() == '#')) {
                                    // Change location
                                    displayGrid.addObjectToDisplay(objectGrid[player.getPosX()][player.getPosY() + 1], player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                }
                                else if (objectGrid[player.getPosX()][player.getPosY() + 1].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('#'), player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor) {
                                    displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() + 1);

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (passage.getRoom2() == room.getRoomNum()) {
                                            targRoom = room;
                                            break;
                                        }
                                    }

                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX() + targRoom.getPosX(), player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

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
                                if ((objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1].getChar() == '.' )) {
                                    // Change location
                                    displayGrid.addObjectToDisplay(objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1], 
                                                                room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosY(player.getPosY() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else if (objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('.'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosY(player.getPosY() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor) {
                                    displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosY(player.getPosY() - 1);

                                    ArrayList<Passage> passages = dungeon.getPassages();
                                    Passage targPass = null;
                                    for (Passage passage : passages) {
                                        System.out.println(room.getRoomNum());
                                        if (passage.getRoom2() == room.getRoomNum()) {
                                            targPass = passage;
                                            break;
                                        }
                                    }
                                    System.out.println(targPass);

                                    player.setPosX(room.getPosX() + player.getPosX());
                                    player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());

                                    player.setOnDoor(false);
                                    targPass.setPlayer(player);
                                    dungeon.setPlayerLoc(targPass);
                                    loc = targPass;
                                }
                            }
                            catch(Exception e) {
                                Passage passage = (Passage) loc;
                                if ((objectGrid[player.getPosX()][player.getPosY() - 1].getChar() == '#')) {
                                    // Change location
                                    displayGrid.addObjectToDisplay(objectGrid[player.getPosX()][player.getPosY() - 1], player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                }
                                else if (objectGrid[player.getPosX()][player.getPosY() - 1].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('#'), player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() - 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor) {
                                    displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());
                                    player.setPosY(player.getPosY() - 1);

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (passage.getRoom1() == room.getRoomNum()) {
                                            targRoom = room;
                                            break;
                                        }
                                    }

                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX() + targRoom.getPosX(), player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

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
                        case 'l':
                            try {
                                Room room = (Room) loc;
                                if ((objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '.' )) {
                                    // Change location
                                    displayGrid.addObjectToDisplay(objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()], 
                                                                room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosX(player.getPosX() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else if (objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('.'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosX(player.getPosX() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor) {
                                    displayGrid.addObjectToDisplay(new Char('+'), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    player.setPosX(player.getPosX() + 1);

                                    ArrayList<Passage> passages = dungeon.getPassages();
                                    Passage targPass = null;
                                    for (Passage passage : passages) {
                                        if (passage.getRoom1() == room.getRoomNum()) {
                                            targPass = passage;
                                            break;
                                        }
                                    }

                                    player.setPosX(room.getPosX() + player.getPosX());
                                    player.setPosY(dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());

                                    player.setOnDoor(false);
                                    targPass.setPlayer(player);
                                    dungeon.setPlayerLoc(targPass);
                                    loc = targPass;
                                }
                            }
                            catch(Exception e) {
                                Passage passage = (Passage) loc;
                                if ((objectGrid[player.getPosX() + 1][player.getPosY()].getChar() == '#' )) {
                                    // Change location
                                    displayGrid.addObjectToDisplay(objectGrid[player.getPosX() + 1][player.getPosY()], player.getPosX(), + player.getPosY());
                                    player.setPosX(player.getPosX() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                }
                                else if (objectGrid[player.getPosX() + 1][player.getPosY()].getChar() == '+') {
                                    // Change location and structure type
                                    displayGrid.addObjectToDisplay(new Char('#'), player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() + 1);
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX(), player.getPosY());
                                    
                                    player.setOnDoor(true);
                                }
                                else if (onDoor) {
                                    displayGrid.addObjectToDisplay(new Char('+'), player.getPosX(), player.getPosY());
                                    player.setPosX(player.getPosX() + 1);

                                    ArrayList<Room> rooms = dungeon.getRooms();
                                    Room targRoom = null;
                                    for (Room room : rooms) {
                                        if (passage.getRoom2() == room.getRoomNum()) {
                                            targRoom = room;
                                            break;
                                        }
                                    }

                                    player.setPosX(player.getPosX() - targRoom.getPosX());
                                    player.setPosY(player.getPosY() - (dungeon.getTopHeight() + targRoom.getPosY()));
                                    displayGrid.addObjectToDisplay(new Char(player.getType()), player.getPosX() + targRoom.getPosX(), player.getPosY() + dungeon.getTopHeight() + targRoom.getPosY());

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
                        case 'e':
                            System.exit(0);
                            break;
                        default:
                            break;
                    }
                    System.out.println(objectGrid[loc.getPosX() + player.getPosX()][dungeon.getTopHeight()+ loc.getPosY() + player.getPosY()].getChar());
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
