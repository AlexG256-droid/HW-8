import java.io.IOException;

import enginedriver.GameEngineApp;

public class SmokeTest {
  public static void main(String[] args) throws IOException {
    // smoke tests - first send synthetic data via a string
    //String s = "Sir Mix-A-Lot\nT NOTEBOOK\nN\nT HAIR CLIPPERS\nT KEY\nD NOTEBOOK\nQuit";
    //BufferedReader stringReader = new BufferedReader(new StringReader(s));
    GameEngineApp gameEngineApp = new GameEngineApp();
    gameEngineApp.start("src/json_data");


    // Next, comment the above and uncomment this to do some ad-hoc testing by hand via System.in
    // enginedriver.GameEngineApp gameEngineApp = new enginedriver.GameEngineApp("./resources/museum.json", new InputStreamReader(System.in), System.out);
    // gameEngineApp.start();

  }
}