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
  private Room currentRoom;


  private void pickUpItem(Item pickedItem) {
    int sum = 0;

    // sum the weight of all items in the inventory
    for (int i = 0; i < inventory.size(); i++) {
      sum += inventory.get(i).getWeight();
    }

    // add the weight of the picked item
    sum += pickedItem.getWeight();

    // check if adding the new item exceeds the capacity
    if (sum > capacity) {
      System.out.println("Weight exceeds capacity");
      return;
      // exit the method if capacity is exceeded
    } else {
      inventory.add(pickedItem);
      System.out.println("Item added successfully");
    }
  }

  private void dropItem(Item droppedItem) {
    // Check if the inventory contains the dropped item
    if (inventory.contains(droppedItem)) {

      // add item to Room
      this.currentRoom.getItem().add(droppedItem);
      // Remove the item from the inventory
      inventory.remove(droppedItem);

      System.out.println("Item dropped successfully");
    } else {
      System.out.println("Item not found in inventory");
    }
  }



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getScore() {
    return score;
  }

  private void setScore(Integer score) {
    this.score = score;
  }

  public Integer getHealth() {
    return health;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public Room getCurrentRoom() {
    return currentRoom;
  }


  // vaild method
  public String solvePuzzle(Item item, Puzzle puzzle) {
    if (puzzle == null ||item == null) {
      return("not a vaild puzzle or item");
    }
    if(item.getUses_remaining()>1){
      String result = puzzle.solve(item);
      if (!puzzle.isActive()) {
        this.score += puzzle.getValue();
        item.setUses_remaining(item.getUses_remaining()-1);
        //set use_remaining -=1
        if(item.getUses_remaining()< 1){
          currentRoom.getItem().remove(item);
          // if getUsesRemaining <1)
          // remove item
        }
      }
      return result;
    }else{
      return("item are out of use,no chance to use it");
    }
  }

  public String solvePuzzle(String magicWords, Puzzle puzzle) {
    if (puzzle == null || magicWords == null || magicWords.equals("")) {
      return("not a vaild puzzle or magic words");
    }
    String result = puzzle.solve(magicWords);
    if (!puzzle.isActive()) {
      this.score += puzzle.getValue();
    }
    return result;
  }

  // vaild method
  public String solveMonster(Item item, Monster monster) {
    if (monster == null || item == null) {
      return("not a vaild puzzle or item");
    }
    if(item.getUses_remaining() > 1){
      String result = monster.solve(item);
      if (!monster.isActive()) {
        this.score += monster.getValue();
        item.setUses_remaining(item.getUses_remaining() - 1);
        //set use_remaining -=1
        if(item.getUses_remaining() < 1){
          currentRoom.getItem().remove(item);
          // if getUsesRemaining <1)
          // remove item
        }
      }
      return result;
    } else {
      return("item are out of use,no chance to use it");
    }
  }

  public String solveMonster(String magicWords, Monster monster) {
    if (monster == null || magicWords == null || magicWords.equals("")) {
      return("not a vaild puzzle or magic words");
    }
    String result = monster.solve(magicWords);
    if (!monster.isActive()) {
      this.score += monster.getValue();
    }
    return result;
  }



  // move method
  public String move(String Direction, Map map) {
    if (!(Direction.equals("N") || Direction.equals("E") || Direction.equals("S") || Direction.equals("W"))) {
      return ("Input must be N, E, S, or W");
    }

    int nextRoomNumber = -1;
    String blockedMessage = "";

    // using switch case to try to catach direction
    switch (Direction) {
      case "N":
        nextRoomNumber = this.currentRoom.getN();
        blockedMessage = "North is being permantly blocked ";
        break;
      case "E":
        nextRoomNumber = this.currentRoom.getE();
        blockedMessage = "East is being permantly blocked ";
        break;
      case "S":
        nextRoomNumber = this.currentRoom.getS();
        blockedMessage = "South is being permantly blocked ";
        break;
      case "W":
        nextRoomNumber = this.currentRoom.getW();
        blockedMessage = "West is being permantly blocked ";
        break;
    }

    // check
    if (nextRoomNumber > 0) {
      // if nextRoom number is greater than >. it is a vaild way
      for (int i = 0; i < map.getRooms().size(); i++) {
        Room room = map.getRooms().get(i);
        if (nextRoomNumber == room.getRoom_number()) {
          this.currentRoom = room;
          return ("move successfully");
          // if map do have this room , then player move to this Room
        }
      }
    } else if (nextRoomNumber == 0) {
      // if nextRoomNumber ==0, it is permantly blocked
      return (blockedMessage);
    } else {
      // if it is negative, then there is puzzle or monster currently blocking the access
      return ("there is a puzzle or monster currently blocking access to the room in that direction");
    }
    return ("there is a puzzle or monster currently blocking access to the room in that direction");
  }

}