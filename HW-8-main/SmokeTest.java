import java.io.IOException;

public class SmokeTest {
  public static void main(String[] args) {

    String jsonPath = "saves/align_quest_game_elements.json";

    try {
      GameController gameController = new GameController(jsonPath);
      System.out.println("GameController successfully initialized。");

      gameController.gameLoop();
    } catch (IOException e) {
      System.err.println("error " + e.getMessage());
    }
  }
}