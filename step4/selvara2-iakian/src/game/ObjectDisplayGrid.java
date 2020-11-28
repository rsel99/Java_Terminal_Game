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

    public ObjectDisplayGrid(int _width, int _height) {
        width = _width;
        height = _height;
        terminal = new AsciiPanel(width, height);

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
                // System.out.println(objectGrid[x][y]);
                writeToTerminal(x, y);
            }
        }
    }

    public void removeObjectFromDisplay(int x, int y) {
        if ((0 <= x) && (x < width)) {
            if ((0 <= y) && (y < height)) {
                System.out.println(objectGrid[x][y]);
                objectGrid[x][y].pop();
                writeToTerminal(x, y);
            }
        }
    }

    private void writeToTerminal(int x, int y) {
        char ch = (char) objectGrid[x][y].peek();
        terminal.write(ch, x, y);
        terminal.repaint();
    }

    public Stack[][] getObjectGrid() {
        return this.objectGrid;
    }
}
