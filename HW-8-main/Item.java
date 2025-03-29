/**
 * Represents an item in the game.
 *
 * Attributes:
 * name: name of the item.
 * weight: How much the item weighs. A player's avatar can carry a MAXIMUM of 13 weight unit* worth of items
 * max_uses: how many times the item can be used, if it was "full".
 * uses_remaining: how many uses are currently left for the item.
 * value: how much the item is worth
 * when_used: the text presented to the player when they successfully use the item in context.
 * description: A brief description of the item.
 * picture:  a picture representing the item (not relevant for HW8; might be used for the graphical version in HW9)
 */
public class Item {
  private String name;
  private int weight;
  private int max_uses;
  private int uses_remaining;
  private int value;
  private String when_used;
  private String description;
  private String picture;

  /**
   * Default constructor.
   *
   * Initializes default values:
   * name: "Unknown Item"
   * weight: 1
   * max_uses: 1
   * uses_remaining: 1
   * value: 0
   * when_used: an empty string
   * description: "No description provided."
   * picture: an empty string
   */
  public Item() {
    this.name = "Unknown Item";
    this.weight = 1;
    this.max_uses = 1;
    this.uses_remaining = 1;
    this.value = 0;
    this.when_used = "";
    this.description = "No description provided.";
    this.picture = "";
  }


  public String getName() {

    return name;
  }

  public int getWeight() {

    return weight;
  }

  public int getMax_uses() {

    return max_uses;
  }

  public int getUses_remaining() {

    return uses_remaining;
  }

  public int getValue() {

    return value;
  }

  public String getWhen_used() {

    return when_used;
  }

  public String getDescription() {

    return description;
  }

  public String getPicture() {

    return picture;
  }

  // setter method

  public void setName(String name) {
    if (name == null || name.trim().isEmpty()) {
      this.name = "Unknown Item";
    } else {
      this.name = name.trim();
    }
  }

  public void setWeight(int weight) {
    this.weight = Math.max(weight, 0);
  }

  public void setMax_uses(int max_uses) {
    this.max_uses = Math.max(max_uses, 0);
    this.uses_remaining = Math.min(this.uses_remaining, this.max_uses);
  }

  public void setUses_remaining(int uses_remaining) {
    this.uses_remaining = Math.min(Math.max(uses_remaining, 0), this.max_uses);
  }

  public void setValue(int value) {
    this.value = Math.max(value, 0);
  }

  public void setWhen_used(String when_used) {
    if (when_used != null) {
      this.when_used = when_used;
    } else {
      this.when_used = "";
    }
  }

  public void setDescription(String description) {
    if (description != null) {
      this.description = description;
    } else {
      this.description = "";
    }
  }

  public void setPicture(String picture) {
    if (picture != null) {
      this.picture = picture;
    } else {
      this.picture = "";
    }
  }

  /**
   * Uses the item once if possible
   * @return true if item was used successfully, false if no uses remaining
   */
  public boolean use() {
    if (uses_remaining > 0) {
      uses_remaining--;
      return true;
    }
    return false;
  }

  /**
   * Repairs the item
   */
  public void repair() {
    this.uses_remaining = this.max_uses;
  }

  /**
   * Checks if no uses remaining
   * @return true if no uses remain, false if usable
   */
  public boolean is_broken() {
    return uses_remaining <= 0;
  }



  /**
   * Returns a string representation of the Item.
   * @return A formatted string that shows the item attributes.
   */
  @Override
  public String toString() {
    return "Item{" +
            "name='" + name + '\'' +
            ", weight=" + weight +
            ", max_uses=" + max_uses +
            ", uses_remaining=" + uses_remaining +
            ", value=" + value +
            ", when_used='" + when_used + '\'' +
            ", description='" + description + '\'' +
            ", picture='" + picture + '\'' +
            '}';
  }

  /**
   * Compares items by name (case-insensitive)
   * @param obj Object to compare with
   * @return true if items have same name (case-insensitive)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Item item = (Item) obj;
    return name.equalsIgnoreCase(item.name);
  }


}