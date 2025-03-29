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

      // process inventory as string array, then look up matching items from gameData
      List<Item> inventory = new ArrayList<>();
      JsonNode inventoryNode = playerNode.get("inventory");
      if (inventoryNode != null && inventoryNode.isArray()) {
        for (int i = 0; i < inventoryNode.size(); i++) {
          String itemName = inventoryNode.get(i).asText();
          // get item from gameData if same itemName
          if (gameData.getItems() != null) {
            for (Item originalItem : gameData.getItems()) {
              if (originalItem.getName().equalsIgnoreCase(itemName)) {
                inventory.add(originalItem);
                break;
              }
            }
          }
        }
      }

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
