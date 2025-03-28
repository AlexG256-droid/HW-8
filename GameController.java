import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import javax.print.DocFlavor;

public class GameController {
  private Map map;
  private Player player;
  private View view;

  public GameController(String pathname) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    GameData gameData = objectMapper.readValue(new File(pathname), GameData.class);

    JsonNode rootNode = objectMapper.readTree(Files.readString(new File(pathname).toPath()));
    JsonNode roomsNode = rootNode.get("rooms");

    List<Room> roomList = parseRooms(roomsNode, gameData);
    this.map = new Map(roomList, gameData.getName(), gameData.getVersion());
    this.view = new View();
  }

  private List<Room> parseRooms(JsonNode roomsNode, GameData gameData) {
    List<Room> roomList = new ArrayList<>();
    for (JsonNode roomNode : roomsNode) {
      Room room = createRoom(roomNode, gameData);
      roomList.add(room);
    }
    return roomList;
  }

  private Room createRoom(JsonNode roomNode, GameData gameData) {
    String name = roomNode.get("room_name").asText();
    int number = roomNode.get("room_number").asInt();
    String description = roomNode.get("description").asText();
    int N = roomNode.get("N").asInt();
    int S = roomNode.get("S").asInt();
    int E = roomNode.get("E").asInt();
    int W = roomNode.get("W").asInt();

    Puzzle puzzle = findPuzzle(roomNode.get("puzzle").asText(null), gameData);
    Monster monster = findMonster(roomNode.get("monster").asText(null), gameData);
    List<Item> roomItems = findItems(roomNode.get("items").asText(null), gameData);
    List<Fixture> roomFixtures = findFixtures(roomNode.get("fixtures").asText(null), gameData);
    String picture = roomNode.get("picture").asText();

    return new Room(name, number, description, N, S, E, W, puzzle, monster, roomItems, roomFixtures, picture);
  }

  private Puzzle findPuzzle(String puzzleName, GameData gameData) {
    if (puzzleName == null) return null;
    return gameData.puzzles.stream()
            .filter(p -> p.getName().equalsIgnoreCase(puzzleName))
            .findFirst()
            .orElse(null);
  }

  private Monster findMonster(String monsterName, GameData gameData) {
    if (monsterName == null) return null;
    return gameData.monsters.stream()
            .filter(m -> m.getName().equalsIgnoreCase(monsterName))
            .findFirst()
            .orElse(null);
  }

  private List<Item> findItems(String itemNames, GameData gameData) {
    List<Item> roomItems = new ArrayList<>();
    if (itemNames == null) return roomItems;

    for (String itemName : itemNames.split(",")) {
      gameData.items.stream()
              .filter(item -> item.getName().equalsIgnoreCase(itemName.trim()))
              .findFirst()
              .ifPresent(roomItems::add);
    }
    return roomItems;
  }

  private List<Fixture> findFixtures(String fixtureNames, GameData gameData) {
    List<Fixture> roomFixtures = new ArrayList<>();
    if (fixtureNames == null) return roomFixtures;

    for (String fixtureName : fixtureNames.split(",")) {
      gameData.fixtures.stream()
              .filter(fixture -> fixture.getName().equalsIgnoreCase(fixtureName.trim()))
              .findFirst()
              .ifPresent(roomFixtures::add);
    }
    return roomFixtures;
  }

  /**
   public GameController(String pathname) throws IOException {

   // use jackson package to automatically read the data
   ObjectMapper objectMapper = new ObjectMapper();
   GameData gameData = objectMapper.readValue(new File(pathname), GameData.class);

   // manually read  using jsonnode the Room data and assign ,object such as monster,puzzle,
   // fixure and item to them
   String jsonContent = Files.readString(new File(pathname).toPath());
   JsonNode rootNode = objectMapper.readTree(jsonContent);
   JsonNode roomsNode = rootNode.get("rooms");

   List<Room> roomList = new ArrayList<>();

   for (int i = 0; i < roomsNode.size(); i++) {
   JsonNode roomNode = roomsNode.get(i);
   //get current node

   // add INT and String to Room
   String name = roomNode.get("room_name").asText();
   int number = Integer.parseInt(roomNode.get("room_number").asText());
   String description = roomNode.get("description").asText();
   int N = Integer.parseInt(roomNode.get("N").asText());
   int S = Integer.parseInt(roomNode.get("S").asText());
   int E = Integer.parseInt(roomNode.get("E").asText());
   int W = Integer.parseInt(roomNode.get("W").asText());

   // add puzzle object to ROOM
   Puzzle puzzle = null;
   String puzzleName = roomNode.get("puzzle").asText(null);
   if (puzzleName != null) {
   for (int j = 0; j < gameData.puzzles.size(); j++) {
   Puzzle p = gameData.puzzles.get(j);
   if (p.getName().equalsIgnoreCase(puzzleName)) {
   puzzle = p;
   break;
   }
   }
   }

   // add monster object to the room
   Monster monster = null;
   String monsterName = roomNode.get("monster").asText(null);
   if (monsterName != null) {
   for (int j = 0; j < gameData.monsters.size(); j++) {
   Monster m = gameData.monsters.get(j);
   if (m.getName().equalsIgnoreCase(monsterName)) {
   monster = m;
   break;
   }
   }
   }

   // add a list f item to
   List<Item> roomItems = new ArrayList<>();
   String itemNames = roomNode.get("items").asText(null);
   if (itemNames != null) {
   String[] itemSplit = itemNames.split(",");
   for (int j = 0; j < itemSplit.length; j++) {
   String itemName = itemSplit[j].trim();
   for (int k = 0; k < gameData.items.size(); k++) {
   Item item = gameData.items.get(k);
   if (item.getName().equalsIgnoreCase(itemName)) {
   roomItems.add(item);
   break;
   }
   }
   }
   }

   // add a list of fixure project
   List<Fixture> roomFixtures = new ArrayList<>();
   String fixtureNames = roomNode.get("fixtures").asText(null);
   if (fixtureNames != null) {
   String[] fixtureSplit = fixtureNames.split(",");
   for (int j = 0; j < fixtureSplit.length; j++) {
   String fixtureName = fixtureSplit[j].trim();
   for (int k = 0; k < gameData.fixtures.size(); k++) {
   Fixture fixture = gameData.fixtures.get(k);
   if (fixture.getName().equalsIgnoreCase(fixtureName)) {
   roomFixtures.add(fixture);
   break;
   }
   }
   }
   }

   String picture = roomNode.get("picture").asText();

   //Create new ROOM
   Room room = new Room(name, number, description, N, S, E, W, puzzle, monster, roomItems, roomFixtures, picture);
   roomList.add(room);
   }

   this.map = new Map(roomList);
   }
   **/

  public Map getMap() {
    return this.map;
  }


  // view put inside controller

  //move method -- Yijie Li

  /**
   * control the movement for player
   *
   * @param direction
   */
  public void movePlayer(String direction) {
    int moveResult = player.move(direction, map);
    HealthStatus healthStatus = player.getHealthStatus();

    // display blocking message according to the next room
    Room nextRoom = getNextRoom(player.getCurrentRoom(),direction);
    String blockMessage;
    if (nextRoom != null) {
      blockMessage = nextRoom.getDescription();
    } else {
      blockMessage = "You can't go that way.";
    }

    // control the view display according to the result of the player move
    switch (moveResult) {
      case 1:
        view.displayMessage("You enter the  " + player.getCurrentRoom().getRoom_name());
        break;
      case 0:
        view.displayMessage(" >> You cannot go into that direction! \n" + healthStatus.getHealthMessage());
        break;
      case -1:
        view.displayMessage("The path is blocked "+ blockMessage);
        break;
      case -2:
        view.displayMessage("Invalid direction! \n Can only use N, W, S, E");
        break;
    }

  }

  private Room getNextRoom(Room currentRoom, String direction) {
    int nextRoomNumber = switch (direction) {
      case "N" -> currentRoom.getN();
      case "W" -> currentRoom.getW();
      case "S" -> currentRoom.getS();
      case "E" -> currentRoom.getE();
      default -> -1;
    };
    if (nextRoomNumber < 0) {
      for (Room room : map.getRooms()) {
        if (room.getRoom_number() == Math.abs(nextRoomNumber)) {
          return room;
        }
      }
    }
    return null;
  }

  // look method and examine --Alexender
  public void examineItem() {
    return;
  }

  public void lookItem() {
    return;
  }


  //take and drop item --Amy
  public void takeItem(String itemName) {
    Room currentRoom = player.getCurrentRoom();
    List<Item> roomItems = currentRoom.getItem();

    for (Iterator<Item> it = roomItems.iterator(); it.hasNext();) {
      Item item = it.next();
      if (item.getName().equalsIgnoreCase(itemName)) {
        it.remove();
        boolean result = player.pickUpItem(item);
        if (result) {
          view.displayMessage(itemName + " added to your inventory.");
        } else {
          view.displayMessage("Weight exceeds capacity, cannot pick up " + itemName + ".");
        }
        return;
      }
    }
    view.displayMessage("Item '" + itemName + "' not found in this room.");
  }

  public void dropItem(String itemName) {
    List<Item> inventory = player.getInventory();
    for (Iterator<Item> it = inventory.iterator(); it.hasNext();) {
      Item item = it.next();
      if (item.getName().equalsIgnoreCase(itemName)) {
        boolean result = player.dropItem(item);
        if (result) {
          view.displayMessage(itemName + " dropped here in " + player.getCurrentRoom());
        } else {
          view.displayMessage("Drop failed: item '"
                  + itemName +
                  "' could not be removed from inventory.");
        }
        return;
      }
    }
    view.displayMessage("You don't have an item named '" + itemName + "' in your inventory.");
  }
  

  public void solveMonster(Item item, Monster monster) {
    int result = player.solveMonster(item, monster);
    if (result == -2) {
      view.displayMessage("Not a valid monster or item.");
    }
    else if (result == 0) {
      view.displayMessage("The solution is text, not an item.");
    } else if (result == -1) {
      view.displayMessage("The item does not match the Monster's solution.");
    } else if (result == 1) {
      view.displayMessage("Monster solved using the correct item! (Item usage decreased by 1)");
    }
  }

  public void solveMonster(String magicWords, Monster monster) {
    int result = player.solveMonster(magicWords, monster);
    if (result == -2) {
      view.displayMessage("Not a valid monster or magic word.");
    }
    else if (result == 0) {
      view.displayMessage("The solution is an item, not text.");
    } else if (result == -1) {
      view.displayMessage("The magic word does not match the Monster's solution.");
    } else if (result == 1) {
      view.displayMessage("Monster solved using the correct magic word!");
    }
  }

  private void solvePuzzle(Item item, Puzzle puzzle) {
    int result = player.solvePuzzle(item, puzzle);
    if (result == -2) {
      view.displayMessage("Not a valid puzzle or item.");
    }
    else if (result == 0) {
      view.displayMessage("The solution is text, not an item.");
    } else if (result == -1) {
      view.displayMessage("The item does not match the Puzzle's solution.");
    } else if (result == 1) {
      view.displayMessage("Puzzle solved using the correct item! (Item usage decreased by 1)");
    }
  }

  public void solvePuzzle(String magicWords, Puzzle puzzle) {
    int result = player.solvePuzzle(magicWords, puzzle);
    if (result == -2) {
      view.displayMessage("Not a valid puzzle or magic word.");
    }
    else if (result == 0) {
      view.displayMessage("The solution is an item, not a magic word.");
    } else if (result == -1) {
      view.displayMessage("The magic word does not match the Puzzle's solution.");
    } else if (result == 1) {
      view.displayMessage("Puzzle solved using the correct magic word!");
    }
  }

  /**
   * display the inventory to the user.
   */
  public void showInventory() {
    List<Item> inventory = player.getInventory();

    String inventoryMessage;
    if (inventory.isEmpty()) {
      inventoryMessage = "There's nothing in your inventory yet.";
    } else {
      StringBuilder sb = new StringBuilder();
      for (Iterator<Item> it = inventory.iterator(); it.hasNext();) {
        Item item = it.next();
        sb.append(item.getName());
        if (it.hasNext()) {
          sb.append(", ");
        }
      }
      inventoryMessage = "Items in your inventory: " + sb.toString();
    }

    view.displayMessage(inventoryMessage + "\n" + player.getHealthStatus().getHealthMessage());
  }

  /**
   * answer the puzzle.
   *
   * @param answer string answer of user input
   */
  public void answerPuzzle(String answer) {
    if (answer == null) {
      view.displayMessage("So what's your answer?: ");
    }
    Puzzle puzzle = player.getCurrentRoom().getPuzzles();
    if(puzzle == null) {
      view.displayMessage("There's no puzzle to be solve.");
      return;
    }
    solvePuzzle(answer, puzzle);
  }

  /**
   * quit method.
   */
  public void quit() {
    view.displayMessage("Thanks for playing!\nYour score is " + player.getScore());
    System.exit(0);
  }


  /**
   * process the command.
   *
   * @param command a string list of command
   */
  public void getCommand(String[] command) {
    String action = command[0];
    String item = command[1];

    switch (action) {
      case "n", "north", "s", "south", "e", "east", "w", "west":
        movePlayer(action);
        break;
      case "i","inventory":
        showInventory();
        break;
      case "t","take":
        takeItem(item);
        break;
      case "d","drop":
        dropItem(item);
        break;
      case "x", "examine":
        examineItem();
        break;
      case "l","look":
        lookItem();
        break;
      case "u","use":
        break;
      case "a","answer":
        answerPuzzle(item);
        break;
      case "q","quit":
        quit();
        break;
      default:
        view.displayMessage("Invalid command: " + action);
        break;
    }
  }

  /**
   * while the game is not over, continue to fetch command from user input.
   */
  public void gameLoop() {
    boolean gameOver = false;
    while (!gameOver) {
      String[] command = view.getInput();
      getCommand(command);
    }
  }


  // save and load game -- Chen


  // Dostring- Abdullahi Abdirahman:

  // don't print anything inside player, fixure,room,etc class
  // return a string inside player, etc instead
  // print everything inside view Class

}
