import java.util.List;


// a map class that contains a list of room
public class Map {
  private List<Room> rooms;
  private String name;
  private String version;

  public Map(List<Room> rooms, String name, String version) {
    this.rooms = rooms;
    this.name = name;
    this.version = version;
  }

  public List<Room> getRooms() {
    return rooms;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public void setRooms(List<Room> rooms) {
    this.rooms = rooms;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
