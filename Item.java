/**
 * Represents an item class in the game with various attributes. 
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
  * Default constructor that creates an item with default values:
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


  // Getter Methods 
  
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

  // Setter Methods 

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
   * Uses the item once available. 
   * @return true if item was used successfully, false if no uses remaining. 
   */
  public boolean use() {
    if (uses_remaining > 0) {
      uses_remaining--;
      return true;
    }
    return false;
  }

  /**
   * Restores all uses the item. 
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
   * Compares items by name.
   * @param obj Object to compare with.
   * @return true if items have same name.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Item item = (Item) obj;
    return name.equalsIgnoreCase(item.name);
  }


}
