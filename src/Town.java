/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all of the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */
public class Town
{
	public static final String UNIQUE_TREASURE_1 = "GoldThing";
	public static final String UNIQUE_TREASURE_2 = "DiamondThing";
	public static final String UNIQUE_TREASURE_3 = "PlatinumThing";

	//instance variables
	private Hunter hunter;
	private Shop shop;
	private Terrain terrain;
	private String treasure;
	private boolean treasureFound;
	private String printMessage;
	private boolean toughTown;
	private double brawlWinChance;
	private double brawlGoldExtra;

	//Constructor
	/**
	 * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
	 * @param s The town's shoppe.
	 * @param t The surrounding terrain.
	 */
	public Town(Shop shop, double toughness, double brawlWinChance, double brawlGoldExtra)
	{
		this.shop = shop;
		this.terrain = getNewTerrain();
		this.treasure = getNewTreasure();
		this.treasureFound = false;

		// the hunter gets set using the hunterArrives method, which
		// gets called from a client class
		hunter = null;

		printMessage = "";

		// higher toughness = more likely to be a tough town
		toughTown = (Math.random() < toughness);

		this.brawlWinChance = brawlWinChance;
		this.brawlGoldExtra = brawlGoldExtra;
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

	// Health bar animation methods
	private String visualizeHpBar(int width, int hp, int maxHp) {
		int fillAmt = (int)Math.ceil((double)hp / maxHp * width);
		int emptAmt = width - fillAmt;

		String fill = "";
		for (int i = 0; i < fillAmt; i++)
			fill += "|";
		for (int i = 0; i < emptAmt; i++)
			fill += " ";
		return String.format("<[%s] %d/%d>", fill, hp, maxHp);
	}

	private void animateHpBars(int width, boolean winner) {
		int maxHp = 100;
		int ownHp = maxHp;
		int oppHp = maxHp;

		try {
			while (true) {
				// Generate damage amounts (winner always deals more damage)
				int oppDmg = 15 + (int)((Math.random() - 0.5) * 10);
				int ownDmg = oppDmg + (int)(Math.random() * 5 + 1);
				if (!winner) {
					int swapTmp = ownDmg;
					ownDmg = oppDmg;
					oppDmg = swapTmp;
				}

				// Since the Hunter always attacks first, make sure they don't
				// defeat the opponent if they are supposed to lose
				if (!winner && oppHp - ownDmg <= 0) {
					ownDmg = oppHp - (int)(Math.random() * 5 + 1);
				}

				// Make sure damage amounts don't exceed the remaining health
				// of the target
				ownDmg = Math.min(ownDmg, oppHp);
				oppDmg = Math.min(oppDmg, ownHp);

				// Hunter attacks opponent
				Thread.sleep(750);

				oppHp -= ownDmg;

				System.out.print("\n".repeat(100)); // scuffed screen clear
				System.out.println("You      " + visualizeHpBar(width, ownHp, maxHp));
				System.out.println("Opponent " + visualizeHpBar(width, oppHp, maxHp) + " -" + ownDmg);

				if (oppHp <= 0) break;

				// Opponent attacks Hunter
				Thread.sleep(750);

				ownHp -= oppDmg;

				System.out.print("\n".repeat(100)); // scuffed screen clear
				System.out.println("You      " + visualizeHpBar(width, ownHp, maxHp) + " -" + oppDmg);
				System.out.println("Opponent " + visualizeHpBar(width, oppHp, maxHp));

				if (ownHp <= 0) break;
			}

			// Print battle results
			Thread.sleep(750);
			System.out.print("\n".repeat(100)); // scuffed screen clear
			if (winner) System.out.println("You won!");
			else System.out.println("You lost!");
			System.out.println();
		} catch (InterruptedException _e) {}
	}

	/**
	 * Gives the hunter a chance to fight for some gold.<p>
	 * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
	 * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
	 */
	public void lookForTrouble()
	{
		double noTroubleChance;
		if (toughTown) {
			noTroubleChance = 0.66;
		} else {
			noTroubleChance = 0.33;
		}

		if (Math.random() > noTroubleChance) {
			printMessage = "You couldn't find any trouble";
		} else {
			printMessage = "";
			int goldDiff = (int)(Math.random() * 10) + 1;
			if (Math.random() < brawlWinChance || hunter.isCheating())
			{
				goldDiff += brawlGoldExtra;
				if (hunter.isCheating())
					goldDiff = 100;
				animateHpBars(50, true);
				printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
				printMessage += "\nYou won the brawl and receive " +  goldDiff + " gold.";
				hunter.changeGold(goldDiff);
			}
			else
			{
				animateHpBars(50, false);
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
		if (treasureFound) {
			System.out.println("You already found the treasure in this town, so there is no need to continue hunting here");
			System.out.println("It might be time to move to a new town");
			return;
		}

		if (Math.random() >= 0.5) { // Treasure found
			System.out.println("You found a " + treasure + "!");

			if (hunter.hasItemInKit(treasure)) {
				// Hunter already found this treasure; cannot find same one twice
				System.out.println("Too bad you already had one though, and because you are suuuch a good person you put it back down and left it for someone else to find.");
			} else {
				hunter.addItem(treasure);
				treasureFound = true;
			}
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
		double rnd = (int)(Math.random() * 6);
		if (rnd == 0) {
			return new Terrain("Mountains", "Rope");
		} else if (rnd == 1) {
			return new Terrain("Ocean", "Boat");
		} else if (rnd == 2) {
			return new Terrain("Plains", "Horse");
		} else if (rnd == 3) {
			return new Terrain("Desert", "Water");
		} else if (rnd == 4) {
			return new Terrain("Jungle", "Machete");
		} else {
			return new Terrain("Cave", "Lantern");
		}
	}

	/**
	 * Generates a random unique treasure for a town
	 * @return Name of the treasure
	 */
	private String getNewTreasure() {
		int rnd = (int)(Math.random() * 3);
		if (rnd == 0) {
			return UNIQUE_TREASURE_1;
		} else if (rnd == 1) {
			return UNIQUE_TREASURE_2;
		} else {
			return UNIQUE_TREASURE_3;
		}
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
