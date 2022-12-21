/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all of the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */
public class Town
{
	// Keeps track of the treasures already generated
	private static final String TREASURES_DELIMITER = ";";
	private static String existingTreasures = "";

	//instance variables
	private Hunter hunter;
	private Shop shop;
	private Terrain terrain;
	private String treasure;
	private boolean treasureFound;
	private String printMessage;
	private boolean toughTown;

	//Constructor
	/**
	 * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
	 * @param s The town's shoppe.
	 * @param t The surrounding terrain.
	 */
	public Town(Shop shop, double toughness)
	{
		this.shop = shop;
		this.terrain = getNewTerrain();
		this.treasure = getNewTreasure();

		// the hunter gets set using the hunterArrives method, which
		// gets called from a client class
		hunter = null;

		printMessage = "";

		// higher toughness = more likely to be a tough town
		toughTown = (Math.random() < toughness);
	}

	public String getLatestNews()
	{
		return printMessage;
	}

	/**
	 * Assigns an object to the Hunter in town.
	 * @param h The arriving Hunter.
	 */
	public void hunterArrives(Hunter hunter)
	{
		this.hunter = hunter;
		printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

		if (toughTown)
		{
			printMessage += "\nIt's pretty rough around here, so watch yourself.";
		}
		else
		{
			printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
		}
	}

	/**
	 * Handles the action of the Hunter leaving the town.
	 * @return true if the Hunter was able to leave town.
	 */
	public boolean leaveTown()
	{
		boolean canLeaveTown = terrain.canCrossTerrain(hunter);
		if (canLeaveTown)
		{
			String item = terrain.getNeededItem();
			printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
			if (checkItemBreak())
			{
				hunter.removeItemFromKit(item);
				printMessage += "\nUnfortunately, your " + item + " broke.";
			}

			return true;
		}

		printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
		return false;
	}

	public void enterShop(String choice)
	{
		shop.enter(hunter, choice);
	}

	/**
	 * Gives the hunter a chance to fight for some gold.<p>
	 * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
	 * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
	 */
	public void lookForTrouble()
	{
		double noTroubleChance;
		if (toughTown)
		{
			noTroubleChance = 0.66;
		}
		else
		{
			noTroubleChance = 0.33;
		}

		if (Math.random() > noTroubleChance)
		{
			printMessage = "You couldn't find any trouble";
		}
		else
		{
			printMessage = "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
			int goldDiff = (int)(Math.random() * 10) + 1;
			if (Math.random() > noTroubleChance)
			{
				printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
				printMessage += "\nYou won the brawl and receive " +  goldDiff + " gold.";
				hunter.changeGold(goldDiff);
			}
			else
			{
				printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
				printMessage += "\nYou lost the brawl and pay " +  goldDiff + " gold.";
				hunter.changeGold(-1 * goldDiff);
			}
		}
	}

	/**
	 * Gives the hunter a chance to hunt for some treasure
	 * The chances of finding a treasure are always 50% regardless of the toughness of the town
	 */
	public void huntForTreasure() {
		if (Math.random() >= 0.5) { // Treasure found
			System.out.println("You found a " + treasure + "!");

			// Cannot find the same treasure twice
			if (treasureFound) {
				System.out.println("Too bad you already had one though, and because you are suuuch a good person you put it back down and left it for someone else to find.");
				return;
			}

			hunter.addItem(treasure);
			treasureFound = true;
		} else { // Nothing found
			System.out.println("You couldn't find any treasure.");
		}
	}

	public String toString()
	{
		return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
	}

	/**
	 * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
	 * 
	 * @return A Terrain object.
	 */
	private Terrain getNewTerrain()
	{
		double rnd = Math.random();
		if (rnd < .2)
		{
			return new Terrain("Mountains", "Rope");
		}
		else if (rnd < .4)
		{
			return new Terrain("Ocean", "Boat");
		}
		else if (rnd < .6)
		{
			return new Terrain("Plains", "Horse");
		}
		else if (rnd < .8)
		{
			return new Terrain("Desert", "Water");
		}
		else
		{
			return new Terrain("Jungle", "Machete");
		}
	}

	/**
	 * Generates a random unique treasure for a town
	 * @return Name of the treasure
	 */
	private String getNewTreasure() {
		String treasure = "";

		while (existingTreasures.indexOf(treasure) != -1) {
			int rnd = (int)(Math.random() * 6);
			if (rnd == 0) {
				treasure = "CopperThing";
			} else if (rnd == 1) {
				treasure = "BronzeThing";
			} else if (rnd == 2) {
				treasure = "GoldThing";
			} else if (rnd == 3) {
				treasure = "DiamondThing";
			} else if (rnd == 4) {
				treasure = "PlatinumThing";
			} else if (rnd == 5) {
				treasure = "UraniumThing"; // lol
			}
		}

		existingTreasures += treasure;
		existingTreasures += TREASURES_DELIMITER;

		return treasure;
	}

	/**
	 * Determines whether or not a used item has broken.
	 * @return true if the item broke.
	 */
	private boolean checkItemBreak()
	{
		double rand = Math.random();
		return (rand < 0.5);
	}
}
