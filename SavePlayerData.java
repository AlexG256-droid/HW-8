import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

/**
 * Provides a utility method to save a Player object using Jackson.
 */
public class SavePlayerData {

  /**
   * Saves the player data into a JSON file using Jackson.
   *
   * @param player     the Player object to be saved.
   * @param outputPath the file path to save the JSON data.
   */
  public static void savePlayer(Player player, String outputPath) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(outputPath), player);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}