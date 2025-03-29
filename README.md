# HW-8

For HW 7, we had a very general and broad idea of what classes, interfaces, and sequences our group wanted for our implementation of our project. We had very basic methods planned out for each of our classes/interfaces. For HW 8, we added some additional classes that we thought would be useful to make our test cases work. We also expanded our UML from HW 7 to match our most recent design of the project.

Item (class): We initially had Item designed as an interface for multiple "item" classes, but for HW 8 we ended up deciding to make it an individual class to make things simpler. We also added extra variable names and methods (mostly getters and setters) to calculate the remaining number of uses, its value, and determining whether or not the item is currently being used by the player.

Player: We added methods so that the player class can move the player by assigning the avatar to different rooms, determine whether or not the avatar is asleep, get the room number of the "next room" about to be entered, "solve" or defeat monsters, pick up or drop an item, and check the avatar's inventory.

GameController: This class determines how the user's input with the use of the standard MVC architectural pattern. The input is determined by each of the methods in the class (e.g. (M) for movePlayer (String direction), which moves the player to a different room depending on the given direction).

GameData: This class basically takes data used in the GameController class in and stores it in different lists.

Challenge: This class determines the specifics of each challenge. Generally, it keeps track of the value, target, solution, and whether or not it's active for both puzzles and monsters.

Puzzle: We initially made this class as just an extension to the Room class but then decided to also make it an extension of the Challenge abstract class to further specify what it is and what it does.

Monster: We initially made this class to have similar stats to the player class but eventually we decided to connect it to the Challenge abstract class instead to further specify what it is and what it does.

Fixture: Designed to be a type of room that also takes in a specific puzzle.

Room: Designed to be a type of room that also determines it's location based on direction (North, East, West, or South) and what may be in the room (e.g. monster, puzzle).

View: This class reads and displays the user's input given by the game controller after every "turn".

Map: This class gets the data of the rooms and stores the rooms themselves in a list.
