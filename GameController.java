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
    String playername = view.getPlayerName();
    createPlayer(playername);
    // create player by default here

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
   * Constructor helper method, create a player for base setting
   * @return Void
   */
  public void createPlayer(String playerName) {
    if (map.getRooms() == null || map.getRooms().isEmpty()) {
      throw new IllegalStateException("empty map, can't create player");
    }
    Room startingRoom = map.getRooms().get(0);
    this.player = new Player(playerName, new ArrayList<>(), startingRoom);
    this.player.setCapacity(13);
  }


  public Map getMap() {
    return this.map;
  }


  /**
   * Saves the current game state, including game data and player data.
   *
   * @param gameDataOutputPath   the file path to save the game data.
   * @param playerDataOutputPath the file path to save the player data.
   */
  public void save(String gameDataOutputPath, String playerDataOutputPath) {
    SaveGameData.saveGameData(this.map, gameDataOutputPath);
    SavePlayerData.savePlayer(this.player, playerDataOutputPath);
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
    Room nextRoom = player.findRoomByNumber(player.getNextRoomNumber(direction),map);

    String message = "";
    // control the view display according to the result of the player move
    switch (moveResult) {
      case 1:
        message = ("You enter the  " + player.getCurrentRoom().getRoom_name());
        break;
      case 0:
        message = (" >> You cannot go into that direction! \n" + healthStatus.getHealthMessage());
        break;
      case -1:
        message = "The path is blocked " + nextRoom.getDescription();
        break;
      case -2:
        message = "Seems like there's no room....";
        break;
    }
    view.displayMessage(message);
  }

  private Room getNextRoom(Room currentRoom, String direction) {
    int nextRoomNumber = switch (direction) {
      case "n","north"-> currentRoom.getN();
      case "w","west" -> currentRoom.getW();
      case "s","south"-> currentRoom.getS();
      case "e","east" -> currentRoom.getE();
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





  private void lookAround() {
    Room currentRoom = player.getCurrentRoom();
    view.displayMessage("\n===== You are currently in: " + currentRoom.getDescription() + " =====\n");

    // get items from rooms
    view.displayMessage("Items available in this room:");
    for (int i = 0; i < currentRoom.getItem().size(); i++) {
      view.displayMessage("  • " + currentRoom.getItem().get(i).getName());
    }

    // get fixures from rooms
    view.displayMessage("\nFixtures present in the room:");
    for (int i = 0; i < currentRoom.getFixtures().size(); i++) {
      view.displayMessage("  • " + currentRoom.getFixtures().get(i).getName());
    }

    // get ACTIVE!!!!! puzzle from rooms
    view.displayMessage("\nPuzzles:");
    if (currentRoom.getPuzzles() != null) {
      if (currentRoom.getPuzzles().isActive()) {
        view.displayMessage("  → " + currentRoom.getPuzzles().getName());
      }
    }

    // get ACTIVE!!! MONSTER from rooms
    view.displayMessage("\nMonsters:");
    if (currentRoom.getMonsters() != null) {
      if (currentRoom.getMonsters().isActive()) {
        view.displayMessage("  → " + currentRoom.getMonsters().getName());
      }
    }
  }

  public void examine(String stuff) {
    Room currentRoom = player.getCurrentRoom();

    // check room have this stuff item
    for (int i = 0; i < currentRoom.getItem().size(); i++) {
      if (currentRoom.getItem().get(i).getName().equalsIgnoreCase(stuff)) {
        view.displayMessage("\nItem: " + currentRoom.getItem().get(i).getDescription());
        view.displayMessage("Uses remaining: " + currentRoom.getItem().get(i).getUses_remaining());
      }
    }

    // check did player has this item
    for (int i = 0; i < player.getInventory().size(); i++) {
      if (player.getInventory().get(i).getName().equalsIgnoreCase(stuff)) {
        view.displayMessage("\nItem: " + player.getInventory().get(i).getDescription());
        view.displayMessage("Uses remaining: " + player.getInventory().get(i).getUses_remaining());
      }
    }

    // check current room has this fixure
    for (int i = 0; i < currentRoom.getFixtures().size(); i++) {
      if (currentRoom.getFixtures().get(i).getName().equalsIgnoreCase(stuff)) {
        view.displayMessage("\nFixture: " + currentRoom.getFixtures().get(i).getDescription());
      }
    }

    // check room has this puzzle
    if (currentRoom.getPuzzles() != null && currentRoom.getPuzzles().getName().equalsIgnoreCase(stuff)) {
      view.displayMessage("\nPuzzle: " + currentRoom.getPuzzles().getDescription());
    }

    // check room has this monster
    if (currentRoom.getMonsters() != null && currentRoom.getMonsters().getName().equalsIgnoreCase(stuff)) {
      view.displayMessage("\nMonster: " + currentRoom.getMonsters().getDescription());
    }
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
    // Determine if the item player inputted is fixture
    for (Fixture fixture : currentRoom.getFixtures()) {
      if (fixture.getName().equalsIgnoreCase(itemName)) {
        view.displayMessage(itemName + " is a immovable fixture. You can't pick that up.");
        return;
      }
    }

    // Nothing found in fixtures and items list
    view.displayMessage("No item or fixture named '" + itemName + "' found in this room.");
  }

  public void dropItem(String itemName) {
    List<Item> inventory = player.getInventory();
    for (Iterator<Item> it = inventory.iterator(); it.hasNext();) {
      Item item = it.next();
      if (item.getName().equalsIgnoreCase(itemName)) {
        boolean result = player.dropItem(item);
        if (result) {
          view.displayMessage(itemName + " dropped here in " + player.getCurrentRoom().getRoom_name());
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
    }else if (result == -3) {
      view.displayMessage("Item remaining_use less than 1");
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
    } else if (result == -3) {
      view.displayMessage("Item remaining_use less than 1");
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
   * answer the puzzle by using a magic
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
   * answer the puzzle by using an item
   *
   * @param item string answer of user input
   */
  public void answerPuzzle_Item(String item) {
    if (item == null) {
      view.displayMessage("So what's item you want to use?: ");
    }
    Puzzle puzzle = player.getCurrentRoom().getPuzzles();
    if(puzzle == null) {
      view.displayMessage("There's no puzzle to be solve.");
      return;
    }
    for(int i =0; i<player.getInventory().size(); i++) {
      String name =player.getInventory().get(i).getName().trim().toLowerCase();
      item= item.trim().toLowerCase();
      if(name.equals(item)) {
        solvePuzzle(player.getInventory().get(i),puzzle);
        return;
      }
    }
    view.displayMessage("you don't have this item");
  }

  /**
   * answer the puzzle by using a magic
   *
   * @param answer string answer of user input
   */
  public void answerMonster(String answer) {
    if (answer == null) {
      view.displayMessage("So what's your answer?: ");
    }
    Monster monster = player.getCurrentRoom().getMonsters();
    if(monster  == null) {
      view.displayMessage("There's no puzzle to be solve.");
      return;
    }
    solveMonster(answer, monster);
  }

  /**
   * answer the puzzle by using an item
   *
   * @param item string answer of user input
   */
  public void answerMonster_Item(String item) {
    if (item == null) {
      view.displayMessage("So what's item you want to use?: ");
    }
    Monster monster = player.getCurrentRoom().getMonsters();
    if(monster== null) {
      view.displayMessage("There's no puzzle to be solve.");
      return;
    }
    for(int i =0; i<player.getInventory().size(); i++) {
      String name =player.getInventory().get(i).getName().trim().toLowerCase();
      item= item.trim().toLowerCase();
      if(name.equals(item)) {
        solveMonster(player.getInventory().get(i),monster);
        return;
      }
    }
    view.displayMessage("you don't have this item");
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
    String stuff = command[1];
    // 做任何action 都挨打

    switch (action) {
      case "n", "north", "s", "south", "e", "east", "w", "west":
        movePlayer(action);
        break;
      case "i","inventory":
        showInventory();
        break;
      case "t","take":
        takeItem(stuff);
        break;
      case "d","drop":
        dropItem(stuff);
        break;
      case "x", "examine":
        examine(stuff);
        break;
      case "l","look":
        lookAround();
        break;
      case "u","use":
        if(this.player.getCurrentRoom().getPuzzles() != null) {
          answerPuzzle_Item(stuff);
        }
        else if(this.player.getCurrentRoom().getMonsters() != null) {
          answerMonster_Item(stuff);
        }
        else{
          view.displayMessage("No puzzles nor monsters found.");
        }

        // USE ITEM TO SOLVE PUZZLE AND monster
        break;
      case "a","answer":
        if(this.player.getCurrentRoom().getPuzzles() != null) {
          answerPuzzle(stuff);
        }else if(this.player.getCurrentRoom().getMonsters() != null) {
          answerMonster(stuff);
        } else{
          view.displayMessage("No puzzles nor monsters found.");
        }
        break;
      case "q","quit":
        quit();
        break;
      case "save":
        //
        save("D:\\document-new semster\\CS-5004\\hw8\\save\\align_quest_game_elements_game.json",
                "D:\\document-new semster\\CS-5004\\hw8\\save\\align_quest_game_elements_player.json");
        view.displayMessage("Game saved.");
        break;
      case "load":
        try {
          this.map = LoadGameData.loadMap("D:\\document-new semster\\CS-5004\\hw8\\save\\align_quest_game_elements_game.json");
          Player loadedPlayer = PlayerLoad.loadPlayer(
                  "D:\\document-new semster\\CS-5004\\hw8\\save\\align_quest_game_elements_player.json",
                  "D:\\document-new semster\\CS-5004\\hw8\\align_quest_game_elements.json",
                  this.map
          );
          this.player = loadedPlayer;
          view.displayMessage("Game loaded.");
        } catch (IOException e) {
          e.printStackTrace();
          view.displayMessage("Error loading game.");
        }
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
      view.displayMenu();
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