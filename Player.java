import java.util.List;

/**
 * The class represents the Player Class.
 */
public class Player {
  private String name; // no exits in Player class in UML - need discussion
  private Integer score;
  private Integer health;
  private List<Item> inventory;
  private Integer capacity;
  // max capacity you can carry
  private Room currentRoom;

  private final int MINIMUM = 0;
  private final int ROOMNUMBERINVALID = 0;
  private final int MAXIMUMREMAININGUSES = 1;
  private final int MAXIMUMHEALTH = 100;
  private final int MAXIMUMCAPACITY = 13;

  /**
   * Player constructor.
   */
  public Player(String name, List<Item> inventory, Room currentRoom) {
    this.name = name;
    this.score = MINIMUM;
    this.health = MAXIMUMHEALTH;
    this.inventory = inventory;
    this.capacity = MAXIMUMCAPACITY;
    this.currentRoom = currentRoom;
  }

  /**
   * Pick up the item.
   * @param pickedItem item
   * @return return false means capacity exceed limit,0 succeed
   */
  public boolean pickUpItem(Item pickedItem) {
    int sum = MINIMUM;
    // sum the weight of all items in the inventory
    for (Item item : inventory) {
      sum += item.getWeight();
    }
    // add the weight of the picked item
    sum += pickedItem.getWeight();
    // check if adding the new item exceeds the capacity
    if (sum > capacity) {
      // exit the method if capacity is exceeded
      return false;
    } else {
      inventory.add(pickedItem);
      return true;
    }
  }

  public boolean pickUpItem(Fixture fixture) {
    return false;
  }



  /**
   * Drop item to the room.
   * @param droppedItem drop item
   *@return return true means success ,false otherwise
   */
  public boolean dropItem(Item droppedItem) {
    // Check if the inventory contains the dropped item
    if (inventory.contains(droppedItem)) {
      // add item to Room
      this.currentRoom.getItem().add(droppedItem);
      // Remove the item from the inventory
      inventory.remove(droppedItem);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Getter function - get the name of the player.
   * @return name of the player
   */
  public String getName() {
    return name;
  }

  /**
   * Setter function - set the name of the player.
   * @param name name of the player
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter function - get the score of the player.
   * @return the score of the function
   */
  public Integer getScore() {
    return score;
  }

  /**
   * Setter function - set the score of the player
   * @param score of the player
   */
  private boolean setScore(Integer score) {
    if (score >= MINIMUM) {
      this.score = score;
      return true;
    }
    return false;
  }

  /**
   * Getter function - get the health of the player.
   * @return the health of the player
   */
  public Integer getHealth() {
    return health;
  }

  /**
   * get the health status of the player.
   *
   * @return health status
   */
  public HealthStatus getHealthStatus() {
    if (this.health <= 0) return HealthStatus.SLEEP;
    if (this.health <= 20) return HealthStatus.WOOZY;
    if (this.health <= 70) return HealthStatus.FATIGUED;
    return HealthStatus.AWAKE;
  }


  /**
   * Set the health of the player.
   * @param health health should range from 0 to 100
   * @return boolean - status of the setting
   */
  public boolean setHealth(Integer health) {
    if (health >= MINIMUM && health <= MAXIMUMHEALTH) {
      this.health = health;
      return true;
    } else {
      // If health of the player is out of range it's false
      return false;
    }
  }

  /**
   * Checks if the player runs out of health.
   * @param health Integer
   * @return boolean depending on whether the player is asleep
   */
  public boolean isAsleep(Integer health) {
    return health == MINIMUM;
  }

  /**
   * Getter function - get the capacity
   * @return the items that the player can pick
   */
  public Integer getCapacity() {
    return capacity;
  }

  /**
   * Getter function - get the capacity
   * @return the items that the player can pick
   */
  public boolean setCapacity(Integer capacity) {
    if (capacity >= MINIMUM && capacity <= MAXIMUMCAPACITY) {
      this.capacity = capacity;
      return true;
    }
    return false;
  }

  /**
   * Getter function - get the room player located
   * @return the room player currently at
   */
  public Room getCurrentRoom() {
    return currentRoom;
  }

  /**
   * Getter function - get the room player located
   * @return the room player currently at
   */
  public boolean setCurrentRoom(Room currentRoom) {
    if (currentRoom != null) {
      this.currentRoom = currentRoom;
      return true;
    }
    return false;
  }

  /**
   * Getter function - get the inventory of the player.
   * @return the inventory of the player
   */
  public List<Item> getInventory() {
    return inventory;
  }

  /**
   * Setter function - set the inventory of the player.
   * @param inventory of the player
   */
  public void setInventory(List<Item> inventory) {
    this.inventory = inventory;
  }

  /**
   * Solve the puzzle with item
   * @param item item the player uses
   * @param puzzle puzzle that player facing
   * @return common status code
   */
  public Integer solvePuzzle(Item item, Puzzle puzzle) {
    if (puzzle == null || item == null) {
      return Challenge.SOLVE_ERROR;
    }
    if (item.getUses_remaining() < MAXIMUMREMAININGUSES) {
      return Challenge.SOLVE_VERY_FAIL;
      //when item remaining less than 1 return -3
    }
    Integer result = puzzle.solve(item);
    if (result == Challenge.SOLVE_SUCCESS && !puzzle.isActive()) {
      this.score += puzzle.getValue();
      item.setUses_remaining(item.getUses_remaining() - MAXIMUMREMAININGUSES);
      this.currentRoom.setRoomToPassable();
      if (item.getUses_remaining() < MAXIMUMREMAININGUSES) {
        inventory.remove(item);
      }
    }
    return result;
  }

  /**
   * Solve the puzzle with item
   * @param magicWords magic words that player input
   * @param puzzle puzzle that player facing
   * @return common status code
   */
  public Integer solvePuzzle(String magicWords, Puzzle puzzle) {
    if (puzzle == null || magicWords == null || magicWords.isEmpty()) {
      // "not a valid puzzle or magic words"
      return Challenge.SOLVE_ERROR;
    }
    Integer result = puzzle.solve(magicWords);
    if (result == Challenge.SOLVE_SUCCESS && !puzzle.isActive()) {
      this.score += puzzle.getValue();
      // set room to passable for all direction
      // once the puzzle or monster being solved
      this.currentRoom.setRoomToPassable();
    }
    return result;
  }

  /**
   * Fight with the monster using item.
   * @param item used to fight the monster
   * @param monster monster player facing right now
   * @return integer code shows that whether it works for the monster
   */
  public Integer solveMonster(Item item, Monster monster) {
    if (monster == null || item == null) {
      return Challenge.SOLVE_ERROR;
    }
    if (item.getUses_remaining() < MAXIMUMREMAININGUSES) {
      return Challenge.SOLVE_VERY_FAIL;
      //when item remaining less than 1 return -3
    }
    Integer result = monster.solve(item);
    if (result == Challenge.SOLVE_SUCCESS && !monster.isActive()) {
      this.score += monster.getValue();
      item.setUses_remaining(item.getUses_remaining() - MAXIMUMREMAININGUSES);
      this.currentRoom.setRoomToPassable();
      if (item.getUses_remaining() < MAXIMUMREMAININGUSES) {
        inventory.remove(item);
      }
    }
    return result;
  }

  /**
   * Fight with the monster using magic words.
   * @param magicWords used to fight the monster
   * @param monster monster player facing right now
   * @return integer code shows that whether it works for the monster
   */
  public Integer solveMonster(String magicWords, Monster monster) {
    if (monster == null || magicWords == null || magicWords.isEmpty()) {
      // "not a valid puzzle or magic words"
      return Challenge.SOLVE_ERROR;
    }
    int result = monster.solve(magicWords);
    if (result == Challenge.SOLVE_SUCCESS && !monster.isActive()) {
      this.score += monster.getValue();
      // set room to passable for all direction
      // once the puzzle or monster being solved
      this.currentRoom.setRoomToPassable();
    }
    return result;
  }

  public Integer move(String direction, Map map) {
    int nextRoomNumber = getNextRoomNumber(direction);

    if (nextRoomNumber > ROOMNUMBERINVALID) {
      Room nextRoom = findRoomByNumber(nextRoomNumber, map);
      if (nextRoom != null) {
        this.currentRoom = nextRoom;
        return 1; // move successfully
      }
      return -2; // unexpected error (valid room number but no room)
    } else if (nextRoomNumber == ROOMNUMBERINVALID) {
      return 0; // permanently blocked
    } else {
      return -1; // puzzle/monster in the room
    }
  }



  public int getNextRoomNumber(String direction) {
    return switch (direction) {
      case "n", "north" -> this.currentRoom.getN();
      case "e", "east"  -> this.currentRoom.getE();
      case "s", "south" -> this.currentRoom.getS();
      case "w", "west"  -> this.currentRoom.getW();
      default           -> -2; // unexpected error
    };
  }

  public Room findRoomByNumber(int roomNumber, Map map) {
    for (Room room : map.getRooms()) {
      if (room.getRoom_number() == roomNumber) {
        return room;
      }
    }
    return null; // unexpected error
  }
}