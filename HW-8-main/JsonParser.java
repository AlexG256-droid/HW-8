import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonParser {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            GameData gameData = objectMapper.readValue(new File("D:\\document-new semster\\CS-5004\\hw8\\align_quest_game_elements.json"), GameData.class);

            for (Item item : gameData.items) {
                System.out.println(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
