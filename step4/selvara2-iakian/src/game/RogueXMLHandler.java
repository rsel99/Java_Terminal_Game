package game;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Stack;

public class RogueXMLHandler extends DefaultHandler {
    private static final int DEBUG = 1;
    private static final String CLASSID = "RogueXMLHandler";

    private StringBuilder data = null;

    private Dungeon dungeon = null;
    private Stack<Displayable> disps = new Stack<Displayable>();
    private Stack<Action> acts = new Stack<Action>();

    public RogueXMLHandler() {
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    private boolean room = false;
    private boolean passage = false;

    private boolean player = false;
    private boolean monster = false;

    private boolean scroll = false;
    private boolean sword = false;
    private boolean armor = false;

    private boolean visible = false;
    private boolean maxhit = false;
    private boolean hpMoves = false;
    private boolean hp = false;
    private boolean type = false;
    private boolean itemintvalue = false;
    private boolean posX = false;
    private boolean posY = false;
    private boolean width = false;
    private boolean height = false; 

    private boolean actionMessage = false;
    private boolean actionIntValue = false;
    private boolean actionCharValue = false;

    private boolean itemaction = false;
    private boolean creatureaction = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (DEBUG > 1) {
            System.out.println(CLASSID + ".startElement qName: " + qName);
        }

        if (qName.equalsIgnoreCase("Dungeon")) {
            dungeon = new Dungeon(attributes.getValue("name"), Integer.parseInt(attributes.getValue("width")), 
                      Integer.parseInt(attributes.getValue("gameHeight")), Integer.parseInt(attributes.getValue("topHeight")), 
                      Integer.parseInt(attributes.getValue("bottomHeight")));
        } 

        else if (qName.equalsIgnoreCase("Room")) {
            Room room = new Room(Integer.parseInt(attributes.getValue("room")));
            disps.push(room);
            this.room = true;
        }
        else if (qName.equalsIgnoreCase("Passage")) {
            Passage passage = new Passage(null);
            passage.setID(Integer.parseInt(attributes.getValue("room1")), 
                          Integer.parseInt(attributes.getValue("room2")));
            disps.push(passage);
            this.passage = true;
        }

        else if (qName.equalsIgnoreCase("visible")) {
            this.visible = true;
        }
        else if (qName.equalsIgnoreCase("maxhit")) {
            this.maxhit = true;
        }
        else if (qName.equalsIgnoreCase("hpMoves")) {
            this.hpMoves = true;
        }
        else if (qName.equalsIgnoreCase("hp")) {
            this.hp = true;
        }
        else if (qName.equalsIgnoreCase("type")) {
            this.type = true;
        }
        else if (qName.equalsIgnoreCase("ItemIntValue")) {
            this.itemintvalue = true;
        }
        else if (qName.equalsIgnoreCase("posX")) {
            this.posX = true;
        }
        else if (qName.equalsIgnoreCase("posY")) {
            this.posY = true;
        }
        else if (qName.equalsIgnoreCase("width")) {
            this.width = true;
        }
        else if (qName.equalsIgnoreCase("height")) {
            this.height = true;
        }
        
        else if (qName.equalsIgnoreCase("Scroll")) {
            Scroll scroll = new Scroll(attributes.getValue("name"));
            scroll.setID(Integer.parseInt(attributes.getValue("room")), 
                         Integer.parseInt(attributes.getValue("serial")));
            disps.push(scroll);
            this.scroll = true;
        }
        else if (qName.equalsIgnoreCase("Sword")) {
            Sword sword = new Sword(attributes.getValue("name"));
            sword.setID(Integer.parseInt(attributes.getValue("room")),
                        Integer.parseInt(attributes.getValue("serial")));
            disps.push(sword);
            this.sword = true;
        }
        else if (qName.equalsIgnoreCase("Armor")) {
            Armor armor = new Armor(attributes.getValue("name"));
            armor.setID(Integer.parseInt(attributes.getValue("room")), Integer.parseInt(attributes.getValue("serial")));
            disps.push(armor);
            this.armor = true;
        }

        else if (qName.equalsIgnoreCase("Player")) {
            Player player = new Player(attributes.getValue("name"), Integer.parseInt(attributes.getValue("room")), 
                            Integer.parseInt(attributes.getValue("serial")));
            disps.push(player);
            this.player = true;
        }
        else if (qName.equalsIgnoreCase("Monster")) {
            Monster monster = new Monster(attributes.getValue("name"), Integer.parseInt(attributes.getValue("room")),
                    Integer.parseInt(attributes.getValue("serial")));
            disps.push(monster);
            this.monster = true;
        }

        else if (qName.equalsIgnoreCase("ItemAction")) {
            //ItemAction itemaction = new ItemAction(attributes.getValue("name"), attributes.getValue("type"));
            String name = attributes.getValue("name");
            String type = attributes.getValue("type");
            Item tmpItem = (Item) disps.pop();
            // if (tmpItem.getActions() == null) {
            //     tmpItem.initActions();
            // }
            switch(name) {
                case "Hallucinate":
                    Hallucinate hallucinate = new Hallucinate(tmpItem, name, type);
                    acts.push(hallucinate);
                    break;
                case "BlessCurseOwner":
                    BlessCurseOwner blesscurseowner = new BlessCurseOwner(tmpItem, name, type);
                    acts.push(blesscurseowner);
                    break;
                case "BlessArmor":
                    BlessArmor blessarmor = new BlessArmor(tmpItem, name, type);
                    acts.push(blessarmor);
                    break;
                default:
                    ItemAction itemaction = new ItemAction(tmpItem, name, type);
                    acts.push(itemaction);
                    break;
            }
            disps.push(tmpItem);
            this.itemaction = true;
        }
        // TODO: Needs work, not sure where to store it
        else if (qName.equalsIgnoreCase("CreatureAction")) {
            String name = attributes.getValue("name");
            String type = attributes.getValue("type");
            Creature tmpCreature = (Creature) disps.pop();
            // if (tmpCreature.getActions() == null) {
            //     tmpCreature.initActions();
            // }
            // CreatureAction creatureaction = new CreatureAction(attrattributes.getValue("name"), attributes.getValue("type"));
            switch(name) {
                case "YouWin":
                    YouWin youwin = new YouWin(name, type, tmpCreature);
                    acts.push(youwin);
                    break;
                case "Teleport":
                    Teleport teleport = new Teleport(name, type, tmpCreature);
                    acts.push(teleport);
                    break;
                case "DropPack":
                    DropPack droppack = new DropPack(name, type, tmpCreature);
                    acts.push(droppack);
                    break;
                case "Remove":
                    Remove remove = new Remove(name, type, tmpCreature);
                    acts.push(remove);
                    break;
                case "ChangeDisplayedType":
                    ChangedDisplayedType changeddisplayedtype = new ChangedDisplayedType(name, type, tmpCreature);
                    acts.push(changeddisplayedtype);
                    break;
                case "UpdateDisplay":
                    UpdateDisplay updatedisplay = new UpdateDisplay(name, type, tmpCreature);
                    acts.push(updatedisplay);
                    break;
                case "EndGame":
                    EndGame endgame = new EndGame(name, type, tmpCreature);
                    acts.push(endgame);
                    break;
                default:
                    CreatureAction creatureaction = new CreatureAction(tmpCreature, name, type);
                    acts.push(creatureaction);
                    break;
            }
            disps.push(tmpCreature);
            this.creatureaction = true;
        }

        else if (qName.equalsIgnoreCase("actionMessage")) {
            this.actionMessage = true;
        }
        else if (qName.equalsIgnoreCase("actionIntValue")) {
            this.actionIntValue = true;
        }
        else if (qName.equalsIgnoreCase("actionCharValue")) {
            this.actionCharValue = true;
        }
        // System.out.println(disps);
        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (visible) {
            Displayable disp = disps.pop();
            if (data.toString().equals("1")) {
                disp.setVisible();
            }
            else {
                disp.setInvisible();
            }
            disps.push(disp);
            visible = false;
        }
        else if (maxhit) {
            Displayable disp = disps.pop();
            disp.setMaxHit(Integer.parseInt(data.toString()));
            disps.push(disp);
            maxhit = false;
        }
        else if (hpMoves) {
            Displayable disp = disps.pop();
            disp.setHpMove(Integer.parseInt(data.toString()));
            disps.push(disp);
            hpMoves = false;
        }
        else if (hp) {
            Displayable disp = disps.pop();
            disp.setHp(Integer.parseInt(data.toString()));
            if (monster) {
                System.out.println("### DEBUG ###");
                System.out.println("### DEBUG ###");
                System.out.println("### DEBUG ###");
                System.out.println(disp);
                System.out.println(disp.getHp());
                System.out.println("### DEBUG ###");
                System.out.println("### DEBUG ###");
                System.out.println("### DEBUG ###");
            }
            disps.push(disp);
            hp = false;
        }
        else if (type) {
            Displayable disp = disps.pop();
            disp.setType(data.toString().charAt(0));
            disps.push(disp);
            type = false;
        }
        else if (itemintvalue) {
            Displayable disp = disps.pop();
            disp.setIntValue(Integer.parseInt(data.toString()));
            disps.push(disp);
            itemintvalue = false;
        }
        else if (posX) {
            Displayable disp = disps.pop();
            disp.setPosX(Integer.parseInt(data.toString()));
            disps.push(disp);
            posX = false;
        }
        else if (posY) {
            Displayable disp = disps.pop();
            disp.setPosY(Integer.parseInt(data.toString()));
            disps.push(disp);
            posY = false;
        }
        else if (width) {
            Displayable disp = disps.pop();
            disp.setWidth(Integer.parseInt(data.toString()));
            disps.push(disp);
            width = false;
        }
        else if (height) {
            Displayable disp = disps.pop();
            disp.setHeight(Integer.parseInt(data.toString()));
            disps.push(disp);
            height = false;
        }
        else if (actionMessage) {
            Action action = acts.pop();
            action.setMessage(data.toString());
            acts.push(action);
            actionMessage = false;
        }
        else if (actionIntValue) {
            Action action = acts.pop();
            action.setIntValue(Integer.parseInt(data.toString()));
            acts.push(action);
            actionIntValue = false;
        }
        else if (actionCharValue) {
            Action action = acts.pop();
            action.setCharValue(data.toString().charAt(0));
            acts.push(action);
            actionCharValue = false;
        }
        else if (itemaction) {
            Action action = acts.pop();
            Displayable disp = disps.pop();
            disp.addAction(action);
            disps.push(disp);
            itemaction = false;
        }
        else if (creatureaction) {
            CreatureAction action = (CreatureAction) acts.pop();
            Creature creature = (Creature) disps.pop();
            if (action.getType().equalsIgnoreCase("death")) {
                creature.setDeathAction(action);
            }
            else if (action.getType().equalsIgnoreCase("hit")) {
                creature.setHitAction(action);
            }
            creature.addAction(action);

            disps.push(creature);
            this.creatureaction = false;
        }
        else if (scroll) {
            Scroll scroll = (Scroll) disps.pop();
            Room room = (Room) disps.pop();
            room.addItem(scroll);
            disps.push(room);
            this.scroll = false;
        }
        else if (sword) {
            if(player){
                Sword sword = (Sword) disps.pop();
                Player player = (Player) disps.pop();
                player.setWeapon(sword);
                disps.push(player);
            }
            else{
                Sword sword = (Sword) disps.pop();
                Room room = (Room) disps.pop();
                room.addItem(sword);
                // disps.push(sword);
                disps.push(room);
            }
            this.sword = false;
        }
        else if (armor) {
            if(player) {
                Armor armor = (Armor) disps.pop();
                Player player = (Player) disps.pop();
                player.setArmor(armor);
                disps.push(player);
            }
            else {
                Armor armor = (Armor) disps.pop();
                Room room = (Room) disps.pop();
                room.addItem(armor);
                disps.push(room);
            }
            this.armor = false;
        }
        else if (player) {
            Player player = (Player) disps.pop();
            Room room = (Room) disps.pop();
            room.setPlayer(player);
            this.dungeon.setPlayerLoc((Structure)room);
            disps.push(room);
            this.player = false;
        }
        else if (monster) {
            Monster monster = (Monster) disps.pop();
            Room room = (Room) disps.pop();
            room.addMonster((Monster) monster);
            monster.setCreatLoc(room);
            disps.push(room);
            this.monster = false;
        }
        else if (room) {
            Room room = (Room) disps.pop();
            dungeon.addRoom(room);
            this.room = false;
        }
        else if (passage) {
            Passage passage = (Passage) disps.pop();
            dungeon.addPassage(passage);
            this.passage = false;
        }
        // System.out.println("## DEBUG ##");
        // System.out.println(disps.toString());
        // System.out.println("## DEBUG ##");
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
        if (DEBUG > 1) {
            System.out.println(CLASSID + ".characters: " + new String(ch, start, length));
            System.out.flush();
        }
    }

    @Override
    public String toString() {
        return null;
    }
}