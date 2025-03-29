import java.util.List;

/**
 * Represents a room in the game environment.
 * Each room has a name, room number, and a description.
 * Rooms contain elements suchs as puzzles, monsters, items, and  fixtures.
 * Each room also has directional properties (N, S,  E, W).
 * Rooms are connected via directional exits (North, South, East, West).
 */
public class Room {
    private String room_name; // room name
    private int room_number; // number
    private String description;
    private int N; // north
    private int S;//south
    private int E; //east
    private int W; //west
    private Puzzle puzzle; // puzzle present in room, if any
    private Monster monster; // monster present in room, if any
    private List<Item> item; // list of items in room
    private List<Fixture> fixures; // list of fixtures in room
    private String picture; // image representing the room

    /**
     * Constructs a new Room with the specified attributes.
     *
     * @param room_name the room name.
     * @param room_number the number of the room, the number is unique to the room.
     * @param description a description of the room.
     * @param n the North exit room number.
     * @param s the South exit room number.
     * @param e the East exit room number.
     * @param w the West exit room number.
     * @param puzzles the puzzle in room (can be null).
     * @param monster the monster in room (can be null).
     * @param item the list of items in room.
     * @param fixures the list of fixtures in room.
     * @param picture the image representing the room.
     */
    public Room(String room_name, int room_number, String description, int n, int s, int e, int w, Puzzle puzzles, Monster monster, List<Item> item, List<Fixture> fixures, String picture) {
        this.room_name = room_name;
        this.room_number = room_number;
        this.description = description;
        N = n;
        S = s;
        E = e;
        W = w;
        this.puzzle = puzzles;
        this.monster = monster;
        this.item = item;
        this.fixures = fixures;
        this.picture = picture;
    }

    // getter method

    /**
     * Returns the name of room.
     * @return the room name.
     */
    public String getRoom_name() {
        return room_name;
    }

    /**
     * Returns the number (unique) assigned to the room.
     * @return the room number.
     */
    public int getRoom_number() {
        return room_number;
    }


    /**
     * Returns the description of the room.
     * This method first checks the list of puzzles in the room. For each puzzle,
     * if it is active,
     * affects its target, and has a valid target  that matches
     * current room number and name
     *  then the puzzle's effects description is returned.
     * If no matching puzzle is found, the method then checks the list of monsters
     * in a similar manner.
     * For each monster, if it is active, affects its target, and has a valid target
     * string in the format
     * and have a target that match current room number and name
     * return monster's effect decription
     * If neither an active puzzle nor n active monster that affects the room is found,
     * the original room description is returned.
     * @return effect from an active puzzle or monster or the original room description.
     */
    public String getDescription() {
        Puzzle puzzle = this.puzzle;
        // Check that the puzzle is active, affects the target, and has a valid target string.
        if (puzzle != null && puzzle.isActive() && puzzle.isAffects_target() && puzzle.getTarget() != null) {
            String[] targetParts = puzzle.getTarget().split(":");
            if (targetParts.length == 2) {
                // Remove any extra whitespace
                String targetRoomNumber = targetParts[0].trim();
                String targetRoomName = targetParts[1].trim();
                if (targetRoomNumber.equals(this.room_number) && targetRoomName.equalsIgnoreCase(this.room_name)) {
                    return puzzle.getEffects();
                    // if the puzzle  is active and affect this room ,then return a puzzle's effect
                    // instead of returning the original description
                }
            }
        }

        Monster monster = this.monster;
        // Check that the monster is active, affects the target, and has a valid target string.
        if (monster != null && monster.isActive() && monster.isAffects_target() && monster.getTarget() != null) {
            String[] targetParts = monster.getTarget().split(":");
            if (targetParts.length == 2) {
                // Remove any extra whitespace
                String targetRoomNumber = targetParts[0].trim();
                String targetRoomName = targetParts[1].trim();
                if (targetRoomNumber.equals(this.room_number) && targetRoomName.equalsIgnoreCase(this.room_name)) {
                    return monster.getEffects();
                    // if the monster  is active and affect this room, then return a monster's effect
                    // instead of returning the original description
                }
            }
        }

        return description;
    }

    /**
     * Helper function that set Room's direction to passable by converting to absolute values.
     */
    public void setRoomToPassable() {
        this.N = Math.abs(this.N);
        this.W = Math.abs(this.W);
        this.E = Math.abs(this.E);
        this.S = Math.abs(this.S);
    }

    // Getters for room properties
    /**
     * Helper function that return the original descritption
     */
    public String getOriginalDescription() {
        return description;
    }

    public int getN() {
        return N;
    }

    public int getS() {
        return S;
    }

    public int getE() {
        return E;
    }

    public int getW() {
        return W;
    }

    public Puzzle getPuzzles() {
        return puzzle;
    }

    public Monster getMonsters() {
        return monster;
    }

    public List<Item> getItem() {
        return item;
    }

    public List<Fixture> getFixtures() {
        return fixures;
    }

    public String getPicture() {
        return picture;
    }


    // Setters for room properties


    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public void setRoom_number(int room_number) {
        this.room_number = room_number;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setN(int n) {
        N = n;
    }

    public void setS(int s) {
        S = s;
    }

    public void setE(int e) {
        E = e;
    }

    public void setW(int w) {
        W = w;
    }

    public void setPuzzles(Puzzle puzzles) {
        this.puzzle = puzzles;
    }

    public void setMonsters(Monster monsters) {
        this.monster = monsters;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }

    public void setFixures(List<Fixture> fixures) {
        this.fixures = fixures;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}