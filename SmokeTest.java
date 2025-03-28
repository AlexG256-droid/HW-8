import java.io.IOException;

public class SmokeTest {
  public static void main(String[] args) {

    String jsonPath = "D:\\document-new semster\\CS-5004\\hw8\\align_quest_game_elements.json";
    try {
      GameController gameController = new GameController(jsonPath);
      System.out.println("GameController successfully initialized。");

      System.out.println("initial room " + gameController.getMap().getRooms().get(0).getRoom_name());
      
      gameController.gameLoop();
    } catch (IOException e) {
      System.err.println("error " + e.getMessage());
    }
  }
}