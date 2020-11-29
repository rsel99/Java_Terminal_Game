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
    // private ArrayList<Item> pack = new ArrayList<>();
    private char input = ' ';
    private int moveCount = 0;

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

    private void clearLine(int j){
        for(int i = 6; i < this.dungeon.getWidth(); i++){
            while(displayGrid.getStack(i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + j) != null && displayGrid.getStack(i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + j).size() > 1){
                displayGrid.removeObjectFromDisplay(i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + j);
            }
        }
    }

    private void clearHp(){
        for(int i = 4; i < 8; i++){
            while(displayGrid.getStack(i, 0).size() > 1){
                displayGrid.removeObjectFromDisplay(i, 0);
            }
        }
    }

    private void clearScore(){
        for(int i = 19; i < 22; i++){
            while(displayGrid.getStack(i, 0).size() > 1){
                displayGrid.removeObjectFromDisplay(i, 0);
            }
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
        clearHp();
        clearScore();

        for (int i = 0; i < (String.valueOf(playerHp)).length(); i++) {
            displayGrid.addObjectToDisplay((String.valueOf(playerHp).charAt(i)), i + ("HP: ").length(), 0);
        }
        for (int i = 0; i < (String.valueOf(playerScore)).length(); i++) {
            displayGrid.addObjectToDisplay((String.valueOf(playerScore).charAt(i)), 4 + i + ("HP:     ").length() + ("Score: ").length(), 0);
        }
    }

    private void updateInfo(String info) {
        if (dungeon.getInfo().length() > 0) {
            clearLine(2);
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
        if(attacker.getName() == "Player"){
            Player player = (Player) attacker;
            if(player.getWeapon() != null){
                damage += player.getWeapon().getIntValue();
            }
        }
        else if(defender.getName() == "Player"){
            Player player = (Player) defender;
            if(player.getArmor() != null){
                damage -= player.getArmor().getIntValue();
            }
        }
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

    private void processAddPack(Player player, Room room, char currItem){
        ArrayList<Item> items = room.getItems();
        int i = 0;
        for(Item item : items){
            if(player.getPosX() == item.getPosX() && player.getPosY() == item.getPosY() && item.getType() == currItem){
                player.addToPack(item);
                System.out.println("entering for loop");
                room.removeItem(i);
                return;
            }
            else{
                i = i + 1;
            }
        }
    }

    private void processDropPack(Player player, Room room, int index, char input){
        Item item = player.getItemFromPack(index);
        item.setPosX(player.getPosX());
        item.setPosY(player.getPosY());
        if(input == 'd'){
            room.addItem(item);
        }
        player.removeItemFromPack(index);
    }

    private void processScrollAction(ItemAction action, Player player){
        // System.out.println(action.getName());
        if(action.getName().equals("BlessArmor")){
            if(action.getCharValue() == 'a'){
                if(player.getArmor() != null){
                    player.getArmor().setIntValue(player.getArmor().getIntValue() + action.getIntValue());
                }
            }
            else if(action.getCharValue() == 'w'){
                if(player.getWeapon() != null){
                    player.getWeapon().setIntValue(player.getWeapon().getIntValue() + action.getIntValue());
                }
            }
        }
        else if(action.getName().equals("Hallucinate")){
            System.out.println("hallucinate");
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
                    // input = ' ';
                    
                    // System.out.println(objectGrid[0][0].peek());
                    if(player.getHpMove() == moveCount){
                        player.setHp(player.getHp() + 1);
                        moveCount = 0;
                    }
                    
                    updatePlayerDisp(player);
                    switch(ch){
                        case 'h':
                            clearLine(2);
                            if(input == 'H'){
                                // clearLine(2);
                                String message = "command 'h': move player 1 space left";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                try{
                                    Room room = (Room) loc;
                                    Monster monster = monsterNext(player, room, (char) ch);
                                    
                                    // System.out.println(player);
                                    // System.out.println(room);
                                    // System.out.println(monster);
                                    // System.out.println((char) ch);
                                    // moveCount = moveCount + 1;
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX() - 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                    if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')) {
                                        moveCount = moveCount + 1;

                                        if (onDoor) {
                                            displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                            // displayGrid.addObjectToDisplay(('+'), room.getPosX() + player.getPosX(), 
                                            //                             dungeon.getTopHeight() + room.getPosY() + player.getPosY());

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
                                        // displayGrid.addObjectToDisplay('+', player.getPosX(), player.getPosY());

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
                            }
                            break;
                        case 'j':
                            clearLine(2);
                            if(input == 'H'){
                                // clearLine(2);
                                String message = "command 'j': move player 1 space down";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                try{
                                    Room room = (Room) loc;
                                    Monster monster = monsterNext(player, room, (char) ch);
                                    // System.out.println("HELLO");
                                    
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() + 1].peek();
                                    if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')){
                                        moveCount = moveCount + 1;
                                        if (onDoor) {
                                            displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(),
                                                    dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                            // displayGrid.addObjectToDisplay(('+'), room.getPosX() + player.getPosX(),
                                            //         dungeon.getTopHeight() + room.getPosY() + player.getPosY());

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
                                        // displayGrid.addObjectToDisplay('+', player.getPosX(), player.getPosY());

                                        ArrayList<Room> rooms = dungeon.getRooms();
                                        Room targRoom = null;
                                        for (Room room : rooms) {
                                            if (player.getPosX() > room.getPosX()
                                                    && player.getPosX() < room.getPosX() + room.getWidth()
                                                    && player.getPosY() == dungeon.getTopHeight() + room.getPosY() + room.getHeight()) {
                                                targRoom = room;
                                                break;
                                            }
                                        }
                                        if (targRoom == null) {
                                            for (Room room : rooms) {
                                                if (player.getPosX() > room.getPosX()
                                                        && player.getPosX() < room.getPosX() + room.getWidth()
                                                        && player.getPosY() == dungeon.getTopHeight() + room.getPosY()) {
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
                            }
                            break;
                        case 'k':
                            clearLine(2);
                            if(input == 'H'){
                                // clearLine(2);
                                String message = "command 'k': move player 1 space up";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                try{
                                    Room room = (Room) loc;
                                    Monster monster = monsterNext(player, room, (char) ch);
                                    // System.out.println("HELLO");
                                    
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY() - 1].peek();
                                    if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')){
                                        moveCount = moveCount + 1;
                                        if (onDoor) {
                                            displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(),
                                                    dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                            // displayGrid.addObjectToDisplay(('+'), room.getPosX() + player.getPosX(),
                                            //         dungeon.getTopHeight() + room.getPosY() + player.getPosY());

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
                                        // displayGrid.addObjectToDisplay('+', player.getPosX(), player.getPosY());

                                        ArrayList<Room> rooms = dungeon.getRooms();
                                        Room targRoom = null;
                                        for (Room room : rooms) {
                                            if (player.getPosX() > room.getPosX()
                                                    && player.getPosX() < room.getPosX() + room.getWidth()
                                                    && player.getPosY() == dungeon.getTopHeight() + room.getPosY() + room.getHeight() - 1) {
                                                targRoom = room;
                                                break;
                                            }
                                        }
                                        if (targRoom == null) {
                                            for (Room room : rooms) {
                                                if (player.getPosX() > room.getPosX()
                                                        && player.getPosX() < room.getPosX() + room.getWidth()
                                                        && player.getPosY() == dungeon.getTopHeight() + room.getPosY()) {
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
                            }
                            break;
                        case 'l':
                            clearLine(2);
                            if(input == 'H'){
                                // clearLine(2);
                                String message = "command 'l': move player 1 space right";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                try{
                                    Room room = (Room) loc;
                                    Monster monster = monsterNext(player, room, (char) ch);
                                    // System.out.println(player);
                                    // System.out.println(room);
                                    // System.out.println(monster);
                                    // System.out.println((char) ch);
                                    
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX() + 1][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                    if((next == '+') || (next == '.') || (next == '#') || (next == ']') || (next == '?') || (next == ')')){
                                        moveCount = moveCount + 1;
                                        if (onDoor) {
                                            displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                            // displayGrid.addObjectToDisplay(('+'), room.getPosX() + player.getPosX(), 
                                            //                             dungeon.getTopHeight() + room.getPosY() + player.getPosY());

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
                                        // displayGrid.addObjectToDisplay('+', player.getPosX(), player.getPosY());

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
                            }
                            break;
                        case 'p':
                            if(input == 'H'){
                                clearLine(2);
                                String message = "command 'p': add item to pack";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                Room room = (Room) loc;
                                clearLine(0);
                                clearLine(2);
                                displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                if((next == ']') || (next == ')') || (next == '?')){
                                    processAddPack(player, room, next);
                                    // pack.add(next);
                                    // System.out.println(pack.get(0));
                                    String message = "Adding item to pack";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                    // displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                    displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else{
                                    String message = "Invalid pick up request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                            }
                            break;
                        case 'd':
                            if(input == 'H'){
                                clearLine(2);
                                String message = "command 'd': drop item <integer> from pack";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                input = 'd';
                            }
                            break;
                        case 'w':
                            if(input == 'H'){
                                clearLine(2);
                                String message = "command 'w': take out/wear armor <integer>";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                input = 'w';
                                //fill in
                            }
                            break;
                        case 'H':
                            if(input == 'H'){
                                clearLine(2);
                                String message = "command 'H': see more info on command <character>";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                input = 'H';
                            }
                            break;
                        case '0':
                            clearLine(2);
                            if(input == 'd'){
                                // clearLine(2);
                                String message = "Invalid drop request";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else if(input == 'w'){
                                // clearLine(2);
                                String message = "Invalid wear armor request";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else if(input == 't'){
                                // clearLine(2);
                                String message = "Invalid use weapon request";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else if(input == 'r'){
                                // clearLine(2);
                                String message = "Invalid read scroll request";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            break;
                        case '1':
                            // System.out.println(input);
                            if(input == 'd'){
                                clearLine(2);
                                // System.out.println(pack.get(0));
                                if(player.getPackSize() > 0){
                                    clearLine(0);
                                    // clearLine(2);
                                    Room room = (Room) loc;
                                    displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                    if((next == ']') || (next == ')') || (next == '?') || (next == '.')){
                                        if(player.getArmor() != null && player.getItemFromPack(0) == player.getArmor()){
                                            player.changeArmor();
                                            String message = "Player has removed armor";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        else if(player.getWeapon() != null && player.getItemFromPack(0) == player.getWeapon()){
                                            player.dropWeapon();
                                            String message = "Player has dropped their weapon";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        displayGrid.addObjectToDisplay(player.getItemFromPack(0).getType(), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        processDropPack(player, room, 0, input);
                                    }
                                    displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else{
                                    // clearLine(2);
                                    String message = "Invalid drop request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'w'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(0).getType() == ']'){
                                        player.setArmor(player.getItemFromPack(0));
                                        clearLine(0);
                                        // processDropPack(player, room, 0, input);
                                        System.out.println("wearing armor");
                                    }
                                    else{
                                        String message = "Invalid wear armor request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    // clearLine(2);
                                    String message = "Invalid wear armor request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 't'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(0).getType() == ')'){
                                        player.setWeapon(player.getItemFromPack(0));
                                        clearLine(0);
                                        System.out.println("wielding sword");
                                    }
                                    else{
                                        String message = "Invalid use weapon request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid use weapon request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'r'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(0).getType() == '?'){
                                        if(((Displayable) player.getItemFromPack(0)).getActions() != null){
                                            ItemAction action = (ItemAction) (((Displayable) player.getItemFromPack(0)).getActions()).get(0);
                                            processScrollAction(action, player);
                                            String message = action.getMessage();
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                            player.removeItemFromPack(0);
                                            clearLine(0);
                                            System.out.println("reading scroll");
                                        }
                                        else{
                                            System.out.println("reading ERROR");
                                        }
                                    }
                                    else{
                                        String message = "Invalid read scroll request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid read scroll request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            break;
                        case '2':
                            // System.out.println(input);
                            if(input == 'd'){
                                clearLine(2);
                                // System.out.println(pack.get(0));
                                if(player.getPackSize() > 1){
                                    clearLine(0);
                                    Room room = (Room) loc;
                                    displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                    if((next == ']') || (next == ')') || (next == '?') || (next == '.')){
                                        if(player.getArmor() != null && player.getItemFromPack(1) == player.getArmor()){
                                            player.changeArmor();
                                            String message = "Player has removed armor";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        else if(player.getWeapon() != null && player.getItemFromPack(1) == player.getWeapon()){
                                            player.dropWeapon();
                                            String message = "Player has dropped their weapon";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        displayGrid.addObjectToDisplay(player.getItemFromPack(1).getType(), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        processDropPack(player, room, 1, input);
                                        
                                    }
                                    displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else{
                                    String message = "Invalid drop request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'w'){
                                clearLine(2);
                                if(player.getPackSize() > 1){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(1).getType() == ']'){
                                        player.setArmor(player.getItemFromPack(1));
                                        clearLine(0);
                                        // processDropPack(player, room, 1, input);
                                        System.out.println("wearing armor");
                                    }
                                    else{
                                        String message = "Invalid wear armor request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    // clearLine(2);
                                    String message = "Invalid wear armor request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 't'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(1).getType() == ')'){
                                        player.setWeapon(player.getItemFromPack(1));
                                        System.out.println("wielding sword");
                                        clearLine(0);
                                    }
                                    else{
                                        String message = "Invalid use weapon request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid use weapon request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'r'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(1).getType() == '?'){
                                        if(((Displayable) player.getItemFromPack(1)).getActions() != null){
                                            ItemAction action = (ItemAction) (((Displayable) player.getItemFromPack(1)).getActions()).get(0);
                                            processScrollAction(action, player);
                                            String message = action.getMessage();
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                            player.removeItemFromPack(1);
                                            clearLine(0);
                                            System.out.println("reading scroll");
                                        }
                                        else{
                                            System.out.println("reading ERROR");
                                        }
                                    }
                                    else{
                                        String message = "Invalid read scroll request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid read scroll request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            break;
                        case '3':
                            // System.out.println(input);
                            if(input == 'd'){
                                clearLine(2);
                                // System.out.println(pack.get(0));
                                if(player.getPackSize() > 2){
                                    clearLine(0);
                        
                                    Room room = (Room) loc;
                                    displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                    if((next == ']') || (next == ')') || (next == '?') || (next == '.')){
                                        if(player.getArmor() != null && player.getItemFromPack(2) == player.getArmor()){
                                            player.changeArmor();
                                            String message = "Player has removed armor";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        else if(player.getWeapon() != null && player.getItemFromPack(2) == player.getWeapon()){
                                            player.dropWeapon();
                                            String message = "Player has dropped their weapon";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        displayGrid.addObjectToDisplay(player.getItemFromPack(2).getType(), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        processDropPack(player, room, 2, input);
                                        
                                    }
                                    displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else{
                                    String message = "Invalid drop request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'w'){
                                clearLine(2);
                                if(player.getPackSize() > 2){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(2).getType() == ']'){
                                        player.setArmor(player.getItemFromPack(2));
                                        clearLine(0);
                                        // processDropPack(player, room, 2, input);
                                        System.out.println("wearing armor");
                                    }
                                    else{
                                        String message = "Invalid wear armor request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    // clearLine(2);
                                    String message = "Invalid wear armor request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 't'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(2).getType() == ')'){
                                        player.setWeapon(player.getItemFromPack(2));
                                        clearLine(0);
                                        System.out.println("wielding sword");
                                    }
                                    else{
                                        String message = "Invalid use weapon request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid use weapon request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'r'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    // clearLine(0);
                                    if(player.getItemFromPack(2).getType() == '?'){
                                        if(((Displayable) player.getItemFromPack(2)).getActions() != null){
                                            ItemAction action = (ItemAction) (((Displayable) player.getItemFromPack(2)).getActions()).get(0);
                                            processScrollAction(action, player);
                                            String message = action.getMessage();
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                            player.removeItemFromPack(2);
                                            clearLine(0);
                                            System.out.println("reading scroll");
                                        }
                                        else{
                                            System.out.println("reading ERROR");
                                        }
                                    }
                                    else{
                                        String message = "Invalid read scroll request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid read scroll request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            break;
                        case '4':
                            // System.out.println(input);
                            if(input == 'd'){
                                clearLine(2);
                                // System.out.println(pack.get(0));
                                if(player.getPackSize() > 3){
                                    clearLine(0);
                                    // clearLine(2);
                                    Room room = (Room) loc;
                                    displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                    if((next == ']') || (next == ')') || (next == '?') || (next == '.')){
                                        if(player.getArmor() != null && player.getItemFromPack(3) == player.getArmor()){
                                            player.changeArmor();
                                            String message = "Player has removed armor";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        else if(player.getWeapon() != null && player.getItemFromPack(3) == player.getWeapon()){
                                            player.dropWeapon();
                                            String message = "Player has dropped their weapon";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        displayGrid.addObjectToDisplay(player.getItemFromPack(3).getType(), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        processDropPack(player, room, 3, input);
                                        
                                    }
                                    displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else{
                                    String message = "Invalid drop request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'w'){
                                clearLine(2);
                                if(player.getPackSize() > 3){
                                    Room room = (Room) loc;
                                    if(player.getItemFromPack(3).getType() == ']'){
                                        player.setArmor(player.getItemFromPack(3));
                                        clearLine(0);
                                        // processDropPack(player, room, 3, input);
                                        System.out.println("wearing armor");
                                    }
                                    else{
                                        String message = "Invalid wear armor request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    // clearLine(2);
                                    String message = "Invalid wear armor request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 't'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    
                                    if(player.getItemFromPack(3).getType() == ')'){
                                        player.setWeapon(player.getItemFromPack(3));
                                        System.out.println("wielding sword");
                                        clearLine(0);
                                    }
                                    else{
                                        String message = "Invalid use weapon request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid use weapon request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'r'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    
                                    if(player.getItemFromPack(3).getType() == '?'){
                                        if(((Displayable) player.getItemFromPack(3)).getActions() != null){
                                            ItemAction action = (ItemAction) (((Displayable) player.getItemFromPack(3)).getActions()).get(0);
                                            processScrollAction(action, player);
                                            String message = action.getMessage();
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                            player.removeItemFromPack(3);
                                            clearLine(0);
                                            System.out.println("reading scroll");
                                        }
                                        else{
                                            System.out.println("reading ERROR");
                                        }
                                    }
                                    else{
                                        String message = "Invalid read scroll request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid read scroll request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            break;
                        case '5':
                            // System.out.println(input);
                            if(input == 'd'){
                                clearLine(2);
                                if(player.getPackSize() > 4){
                                    clearLine(0);
                                    // clearLine(2);
                                    Room room = (Room) loc;
                                    displayGrid.removeObjectFromDisplay(room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());                            
                                    char next = (char) objectGrid[room.getPosX() + player.getPosX()][dungeon.getTopHeight() + room.getPosY() + player.getPosY()].peek();
                                    if((next == ']') || (next == ')') || (next == '?') || (next == '.')){
                                        if(player.getArmor() != null && player.getItemFromPack(4) == player.getArmor()){
                                            player.changeArmor();
                                            String message = "Player has removed armor";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        else if(player.getWeapon() != null && player.getItemFromPack(4) == player.getWeapon()){
                                            player.dropWeapon();
                                            String message = "Player has dropped their weapon";
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                        }
                                        displayGrid.addObjectToDisplay(player.getItemFromPack(4).getType(), room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                        processDropPack(player, room, 4, input);
                                        
                                    }
                                    displayGrid.addObjectToDisplay('@', room.getPosX() + player.getPosX(), dungeon.getTopHeight() + room.getPosY() + player.getPosY());
                                }
                                else{
                                    String message = "Invalid drop request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'w'){
                                clearLine(2);
                                if(player.getPackSize() > 4){
                                    Room room = (Room) loc;
                                    
                                    if(player.getItemFromPack(4).getType() == ']'){
                                        player.setArmor(player.getItemFromPack(4));
                                        clearLine(0);
                                        // processDropPack(player, room, 4, input);
                                        // System.out.println("wearing armor");
                                        String message = "Player is wearing armor";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                    else{
                                        String message = "Invalid wear armor request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    // clearLine(2);
                                    String message = "Invalid wear armor request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 't'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    
                                    if(player.getItemFromPack(4).getType() == ')'){
                                        player.setWeapon(player.getItemFromPack(4));
                                        System.out.println("wielding sword");
                                        clearLine(0);
                                    }
                                    else{
                                        String message = "Invalid use weapon request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid use weapon request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            else if (input == 'r'){
                                clearLine(2);
                                if(player.getPackSize() > 0){
                                    Room room = (Room) loc;
                                    
                                    if(player.getItemFromPack(4).getType() == '?'){
                                        if(((Displayable) player.getItemFromPack(4)).getActions() != null){
                                            ItemAction action = (ItemAction) (((Displayable) player.getItemFromPack(4)).getActions()).get(0);
                                            processScrollAction(action, player);
                                            String message = action.getMessage();
                                            for(int i = 0; i < message.length(); i++){
                                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                            }
                                            player.removeItemFromPack(4);
                                            clearLine(0);
                                            System.out.println("reading scroll");
                                        }
                                        else{
                                            System.out.println("reading ERROR");
                                        }
                                    }
                                    else{
                                        String message = "Invalid read scroll request";
                                        for(int i = 0; i < message.length(); i++){
                                            displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                        }
                                    }
                                }
                                else{
                                    String message = "Invalid read scroll request";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                input = ' ';
                            }
                            break;
                        case 'I':
                        case 'i':
                            clearLine(2);
                            if(input == 'H'){
                                // clearLine(2);
                                String message = "command 'i': see inventory";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                clearLine(0);
                                // clearLine(2);
                                if(player.getPackSize() > 0){
                                    int index = 6;
                                    for(int i = 0; i < player.getPackSize(); i++){
                                        displayGrid.addObjectToDisplay((char) (i + 49), index, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                        index += 1;
                                        displayGrid.addObjectToDisplay(':', index, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                        index += 1;
                                        displayGrid.addObjectToDisplay(' ', index, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                        index += 1;
                                        String message = " ";
                                        if(player.getItemFromPack(i).getType() == ']'){
                                            message = String.valueOf((player.getItemFromPack(i)).getIntValue()) + " Armor";
                                            if(player.getArmor() != null && player.getItemFromPack(i) == player.getArmor()){
                                                message = message + " (a)";
                                            }
                                        }
                                        else if(player.getItemFromPack(i).getType() == ')'){
                                            message = String.valueOf((player.getItemFromPack(i)).getIntValue()) + " Sword";
                                            if(player.getWeapon() != null && player.getItemFromPack(i) == player.getWeapon()){
                                                message = message + " (w)";
                                            }
                                        }
                                        else if(player.getItemFromPack(i).getType() == '?'){
                                            message = ((Scroll) player.getItemFromPack(i)).getName();
                                        }
                                        for(int j = 0; j < message.length(); j++){
                                            displayGrid.addObjectToDisplay(message.charAt(j), index, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                            index += 1;
                                        }
                                        // displayGrid.addObjectToDisplay(, 10+ 6*i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                        if(i < (player.getPackSize() - 1)){
                                            displayGrid.addObjectToDisplay(',', index, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                            index += 1;
                                            displayGrid.addObjectToDisplay(' ', index, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                            index += 1;
                                        }
                                    }
                                }
                                else{
                                    String message = "Pack is empty";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight());
                                    }
                                }
                            }
                            break;
                        case '?':
                            clearLine(2);
                            if(input == 'H'){
                                String message = "command '?': show list of commands";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                String message = "h, j, k, l, i, c, d, p, r, t|T, w, ?, E, H <cmd> for more info";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                            }
                            break;
                        case 'c':
                            clearLine(2);
                            clearLine(0);
                            if(input == 'H'){
                                
                                String message = "command 'c': take off/change armor";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                if(player.getArmor() != null){
                                    // Room room = (Room) loc;
                                    // player.addToPack(player.getArmor());
                                    player.changeArmor();
                                    // System.out.println("c");
                                    String message = "Player has removed armor";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                }
                                else{
                                    String message = "Player is not wearing armor";
                                    for(int i = 0; i < message.length(); i++){
                                        displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                    }
                                    
                                }
                                input = ' ';
                            }
                            break;
                        case 't':
                        case 'T':
                            clearLine(2);
                            clearLine(0);
                            if(input == 'H'){
                                // clearLine(2);
                                String message = "command 't|T': take out weapon from pack";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                input = 't';
                            }
                            break;
                        case 'r':
                            if(input == 'H'){
                                clearLine(2);
                                String message = "command 'r': read scroll <integer> in pack";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                input = 'r';
                                // System.out.println("r");
                            }
                            break;
                        case 'E':
                        case 'e':
                            clearLine(2);
                            if(input == 'H'){
                                String message = "command 'E': end game";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                input = ' ';
                            }
                            else{
                                this.endInvoked = true;
                                String message = "Are you sure (Y/N)";
                                for(int i = 0; i < message.length(); i++){
                                    displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                            }
                            break;
                        case 'N':
                        case 'n':
                            clearLine(2);
                            this.endInvoked = false;
                            String message = "Continuing game";
                            for(int i = 0; i < message.length(); i++){
                                displayGrid.addObjectToDisplay(message.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                            }
                            break;
                        case 'Y':
                        case 'y':
                            if (this.endInvoked) {
                                clearLine(2);
                                String m = "Game is over";
                                for(int i = 0; i < m.length(); i++){
                                    displayGrid.addObjectToDisplay(m.charAt(i), 7 + i, this.dungeon.getTopHeight() + this.dungeon.getGameHeight() + 2);
                                }
                                return false;
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
