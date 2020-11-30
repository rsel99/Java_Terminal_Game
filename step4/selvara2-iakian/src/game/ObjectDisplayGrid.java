package game;
import asciiPanel.AsciiPanel;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.LinkedList;
import java.io.File;

public class ObjectDisplayGrid extends JFrame implements KeyListener, InputSubject {

    private static final int DEBUG = 0;
    private static final String CLASSID = ".ObjectDisplayGrid";

    private static AsciiPanel terminal;
    private Stack[][] objectGrid;

    private List<InputObserver> inputObservers = null;

    private static int height;
    private static int width;

    private LinkedList<String> info;
    private boolean hallucinate = false;
    private Dungeon dungeon;
    private char playerHallucinateChar;
    private ArrayList<Character> chars = new ArrayList<Character>();

    public ObjectDisplayGrid(int _width, int _height, Dungeon dungeon) {
        width = _width;
        height = _height;
        this.dungeon = dungeon;
        terminal = new AsciiPanel(width, height);

        chars.add('.');
        chars.add('X');
        chars.add('#');
        chars.add('+');
        chars.add('T');
        chars.add('H');
        chars.add('S');
        chars.add('?');
        chars.add(']');
        chars.add(')');

        objectGrid = new Stack[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                objectGrid[i][j] = new Stack<Character>();
                // objectGrid[i][j].push((Character)'-');
            }
        }
        info = new LinkedList<String>();
        // initializeDisplay();

        super.add(terminal);
        super.setSize(width * 9, height * 17);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // super.repaint();
        // terminal.repaint( );
        super.setVisible(true);
        terminal.setVisible(true);
        super.addKeyListener(this);
        inputObservers = new ArrayList<>();
        super.repaint();
    }

    @Override
    public void registerInputObserver(InputObserver observer) {
        if (DEBUG > 0) {
            System.out.println(CLASSID + ".registerInputObserver " + observer.toString());
        }
        inputObservers.add(observer);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (DEBUG > 0) {
            System.out.println(CLASSID + ".keyTyped entered" + e.toString());
        }
        KeyEvent keypress = (KeyEvent) e;
        notifyInputObservers(keypress.getKeyChar());
    }

    private void notifyInputObservers(char ch) {
        for (InputObserver observer : inputObservers) {
            observer.observerUpdate(ch);
            if (DEBUG > 0) {
                System.out.println(CLASSID + ".notifyInputObserver " + ch);
            }
        }
    }

    // we have to override, but we don't use this
    @Override
    public void keyPressed(KeyEvent even) {
    }

    // we have to override, but we don't use this
    @Override
    public void keyReleased(KeyEvent e) {
    }

    public final void initializeDisplay() {
        //char ch = ' ';
        for (int i = 0; i <= width; i++) {
            for (int j = (height - 1); j >= (height - 4); j-=2) {
                addObjectToDisplay(' ', i, j);
            }
        }
        terminal.repaint();
    }

    public void setHallucinate(){
        this.hallucinate = true;
        for(int i = 0; i < width; i++){
            for(int j = this.dungeon.getTopHeight(); j < (this.dungeon.getTopHeight() + this.dungeon.getGameHeight()); j++){
                if(objectGrid[i][j] != null && objectGrid[i][j].size() > 0){
                    Stack<Character> temp = new Stack<Character>();
                    while(objectGrid[i][j].size() > 0){
                        // System.out.println(objectGrid[i][j]);
                        Random rand = new Random();
                        int val = rand.nextInt(chars.size());
                        temp.push(Character.valueOf((char) chars.get(val)));
                        temp.push(Character.valueOf((char)objectGrid[i][j].peek()));
                        if((char)objectGrid[i][j].peek() == '@'){
                            playerHallucinateChar = (char) val;
                        }
                        objectGrid[i][j].pop();
                        // System.out.println(temp);
                    }
                    while(temp.size() > 0){
                        objectGrid[i][j].push(temp.pop());
                    }
                    writeToTerminal(i, j);
                    System.out.println(objectGrid[i][j]);
                }
                
            }
            
        }
    }

    public void refreshHallucinate(){
        for(int i = 0; i < width; i++){
            for(int j = this.dungeon.getTopHeight(); j < (this.dungeon.getTopHeight() + this.dungeon.getGameHeight()); j++){
                if(objectGrid[i][j] != null && objectGrid[i][j].size() > 0){
                    Stack<Character> temp = new Stack<Character>();
                    while(objectGrid[i][j].size() > 0){
                        // System.out.println(objectGrid[i][j]);
                        // Random rand = new Random();
                        // int val = rand.nextInt(127-33) + 33;
                        // temp.push(Character.valueOf((char) val));
                        objectGrid[i][j].pop();
                        temp.push(Character.valueOf((char)objectGrid[i][j].peek()));
                        objectGrid[i][j].pop();
                        
                        // System.out.println(temp);
                    }
                    while(temp.size() > 0){
                        objectGrid[i][j].push(temp.pop());
                    }
                    // writeToTerminal(i, j);
                    // System.out.println(objectGrid[i][j]);
                }
                
            }
            
        }

        for(int i = 0; i < width; i++){
            for(int j = this.dungeon.getTopHeight(); j < (this.dungeon.getTopHeight() + this.dungeon.getGameHeight()); j++){
                if(objectGrid[i][j] != null && objectGrid[i][j].size() > 0){
                    Stack<Character> temp = new Stack<Character>();
                    while(objectGrid[i][j].size() > 0){
                        // System.out.println(objectGrid[i][j]);
                        Random rand = new Random();
                        int val = rand.nextInt(10);
                        temp.push(Character.valueOf((char) chars.get(val)));
                        temp.push(Character.valueOf((char)objectGrid[i][j].peek()));
                        // if((char)objectGrid[i][j].peek() == '@'){
                        //     playerHallucinateChar = (char) val;
                        // }
                        objectGrid[i][j].pop();
                        // System.out.println(temp);
                    }
                    while(temp.size() > 0){
                        objectGrid[i][j].push(temp.pop());
                    }
                    writeToTerminal(i, j);
                    System.out.println(objectGrid[i][j]);
                }
                
            }
            
        }

    }

    public void endHallucinate(){
        this.hallucinate = false;
        for(int i = 0; i < width; i++){
            for(int j = this.dungeon.getTopHeight(); j < (this.dungeon.getTopHeight() + this.dungeon.getGameHeight()); j++){
                if(objectGrid[i][j] != null && objectGrid[i][j].size() > 0){
                    Stack<Character> temp = new Stack<Character>();
                    while(objectGrid[i][j].size() > 0){
                        // System.out.println(objectGrid[i][j]);
                        // Random rand = new Random();
                        // int val = rand.nextInt(127-33) + 33;
                        // temp.push(Character.valueOf((char) val));
                        objectGrid[i][j].pop();
                        temp.push(Character.valueOf((char)objectGrid[i][j].peek()));
                        objectGrid[i][j].pop();
                        
                        // System.out.println(temp);
                    }
                    while(temp.size() > 0){
                        objectGrid[i][j].push(temp.pop());
                    }
                    writeToTerminal(i, j);
                    System.out.println(objectGrid[i][j]);
                }
                
            }
            
        }
    }

    public void fireUp() {
        if (terminal.requestFocusInWindow()) {
            System.out.println(CLASSID + ".ObjectDisplayGrid(...) requestFocusInWindow Succeeded");
        } else {
            System.out.println(CLASSID + ".ObjectDisplayGrid(...) requestFocusInWindow FAILED");
        }
    }

    public Stack getStack(int i, int j){
        return objectGrid[i][j];
    }

    public void addObjectToDisplay(char ch, int x, int y) {
        if ((0 <= x) && (x < width)) {
            if ((0 <= y) && (y < height)) {
                objectGrid[x][y].push((Character) ch);
                if(ch == '@' && hallucinate == true && y > this.dungeon.getTopHeight() && y < (this.dungeon.getTopHeight() + this.dungeon.getGameHeight())){
                    // Random rand = new Random();
                    // int val = rand.nextInt(127-33) + 33;
                    // while(val < 33){
                    //     val = rand.nextInt(128);
                    // }
                    objectGrid[x][y].push(Character.valueOf((char) playerHallucinateChar));
                    
                }
                // System.out.println(objectGrid[x][y]);
                writeToTerminal(x, y);
            }
        }
    }

    public char getObject(int x, int y){
        if(hallucinate == true){
            // && objectGrid[x][y].size() > 1
            Character temp = (Character) (objectGrid[x][y]).pop();
            System.out.println(temp);
            System.out.println(objectGrid[x][y]);
            char val = ((Character) (objectGrid[x][y]).peek()).charValue();
            System.out.println(val);
            objectGrid[x][y].push(temp);
            return val;
        }
        return (char) ((Character) (objectGrid[x][y]).peek()).charValue();
    }

    public void removeObjectFromDisplay(int x, int y) {
        if ((0 <= x) && (x < width)) {
            if ((0 <= y) && (y < height)) {
                // System.out.println(objectGrid[x][y]);
                if(hallucinate == true && y > this.dungeon.getTopHeight() && y < (this.dungeon.getTopHeight() + this.dungeon.getGameHeight())){
                    objectGrid[x][y].pop();
                }
                objectGrid[x][y].pop();
                writeToTerminal(x, y);
            }
        }
    }

    private void writeToTerminal(int x, int y) {
        char ch = (char) objectGrid[x][y].peek();
        // System.out.println(objectGrid[x][y]);
        terminal.write(ch, x, y);
        terminal.repaint();
    }

    public Stack[][] getObjectGrid() {
        return this.objectGrid;
    }
}
