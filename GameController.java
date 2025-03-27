import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

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
    int playerHealth = player.getHealth();

    // display health message according to player's health
    String healthMessage = (playerHealth > 0 && playerHealth <= 100) ? "You're still healthy and wide awake"
            :"You're soundly asleep";

    // display blocking message according to the next room
    Room nextRoom = getNextRoom(player.getCurrentRoom(),direction);
    String blockMessage = (nextRoom != null) ? nextRoom.getDescription() : "You can't go that way.";

    // control the view display according to the result of the player move
    switch (moveResult) {
      case 1:
        view.displayMoveMessage("You enter the  " + player.getCurrentRoom().getRoom_name());
        break;
      case 0:
        view.displayMoveMessage(" >> You cannot go into that direction! \n" + healthMessage);
        break;
      case -1:
        view.displayMoveMessage("The path is blocked "+ blockMessage);
        break;
      case -2:
        view.displayMoveMessage("Invalid direction! \n Can only use N, W, S, E");
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

  //take and drop item --Amy
  public String takeItem(String itemName) {
    Room currentRoom = player.getCurrentRoom();
    List<Item> roomItems = currentRoom.getItem();

    for (Iterator<Item> it = roomItems.iterator(); it.hasNext();) {
      Item item = it.next();
      if (item.getName().equalsIgnoreCase(itemName)) {
        it.remove();
        boolean result = player.pickUpItem(item);
        if (result) {
          return itemName + " added to your inventory.";
        } else {
          return "Weight exceeds capacity, cannot pick up " + itemName + ".";
        }
      }
    }
    return "Item '" + itemName + "' not found in this room.";
  }

  public String dropItem(String itemName) {
    List<Item> inventory = player.getInventory();
    for (Iterator<Item> it = inventory.iterator(); it.hasNext();) {
      Item item = it.next();
      if (item.getName().equalsIgnoreCase(itemName)) {
        boolean result = player.dropItem(item);
        if (result) {
          return itemName + " dropped here in " + player.getCurrentRoom();
        } else {
          return "Error dropping " + itemName + ".";
        }
      }
    }
    return "You don't have item '" + itemName + "' in your inventory.";
  }

  // useItem and answerPuzzle boolean methods to solve monster and puzzle -- David Liu.

  public String useItem(String itemName) {
    // Check for monster present in current room.
    Monster monster = currentRoom.getMonster();
    // If no monster, then return (no monster to use item on).
    if (monster == null) {
      return;
    }
    // Check that the player has the required item in their inventory.
    if (!player.hasItem(itemName)) {
      return "The solution must be the required item.";
    }
    // Solve the monster challenge using the item.
    int result = monster.solve(itemName);
    if (result == Challenge.SOLVE_SUCCESS) {
      // If useItem was successful, the monster goes to sleep and is removed from the room.
      currentRoom.setMonster(null);
      // Remove the used item from inventory after use.
      player.removeItem(itemName);
      return "Puzzle solved using the correct item!";
    }
    // If the wrong item was used, the monster is alive.
    return "The item does not match the challenge's solution.";
  }

  public String answerPuzzle(String answer) {
    // Check for a puzzle in the current room.
    Puzzle puzzle = currentRoom.getPuzzle();
    // No puzzle in this room.
    if (puzzle == null) {
      return;
    }
    // Solve the puzzle with the answer.
    int result = puzzle.solve(answer);
    if (result == Challenge.SOLVE_SUCCESS) {
      // If the answer is correct, clear the puzzle from the room.
      currentRoom.setPuzzle(null);
      return "Puzzle solved using the correct magic word!";
    }
    // Otherwise, the puzzle remains unsolved.
    return "The magic word does not match the puzzle's solution.";
  }

  // save and load game -- Chen


  // Dostring- Abdullahi Abdirahman:

  // don't print anything inside player, fixure,room,etc class
  // return a string inside player, etc instead
  // print everything inside view Class
  
}

