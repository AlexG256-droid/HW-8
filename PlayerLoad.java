import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerLoad {

  /**
   * Loads the player data from the specified JSON file,
   * // read player first!!!!!!!!!!!!! without doing that ,inventory will disapper!!!
   * converting the saved currentRoom string (room number) into the corresponding Room object.
   *
   * @param playerDataPath the file path of the player data JSON.
   * @param originalPath   the original game data JSON file path used to find inventory items and currentRoom.
   * @param map            the current game map.
   * @return a loaded Player instance, or null if an error occurs.
   */
  public static Player loadPlayer(String playerDataPath, String originalPath, Map map) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      // Load the original game data from the original path
      GameData gameData = objectMapper.readValue(new File(originalPath), GameData.class);

      // read the entire player JSON as a tree
      JsonNode playerNode = objectMapper.readTree(new File(playerDataPath));

      // basic string and int
      String name = playerNode.get("name").asText();
      int score = playerNode.get("score").asInt();
      int health = playerNode.get("health").asInt();
      int capacity = playerNode.get("capacity").asInt();

      // process inventory
      List<Item> inventory = new ArrayList<>();
      JsonNode inventoryNode = playerNode.get("inventory");
      if (inventoryNode != null && inventoryNode.isArray()) {
        for (int i = 0; i < inventoryNode.size(); i++) {
          Item item = objectMapper.treeToValue(inventoryNode.get(i), Item.class);
          inventory.add(item);
        }
      }

      // re-associate inventory items using the original gameData's items list
      List<Item> newInventory = new ArrayList<>();
      if (gameData.getItems() != null) {
        for (int i = 0; i < inventory.size(); i++) {
          Item invItem = inventory.get(i);
          for (Item originalItem : gameData.getItems()) {
            if (originalItem.getName().equalsIgnoreCase(invItem.getName())) {
              newInventory.add(originalItem);
              break;
            }
          }
        }
      } else {
        newInventory = inventory;
      }
      inventory = newInventory;

      // process currentRoom
      String currentRoomStr = playerNode.get("currentRoom").asText();
      Room currentRoom = null;
      if (currentRoomStr != null && !currentRoomStr.trim().isEmpty()) {
        int currentRoomNumber = Integer.parseInt(currentRoomStr);
        if (map != null) {
          List<Room> rooms = map.getRooms();
          for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoom_number() == currentRoomNumber) {
              currentRoom = rooms.get(i);
              break;
            }
          }
        }
      }

      // create a new Player object with the loaded data
      Player loadedPlayer = new Player(name, inventory, currentRoom);
      loadedPlayer.setScore(score);
      loadedPlayer.setHealth(health);
      loadedPlayer.setCapacity(capacity);

      return loadedPlayer;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}