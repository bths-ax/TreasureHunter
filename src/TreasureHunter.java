/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all of the display based on the messages it receives from the Town object.
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */
import java.util.Scanner;

public class TreasureHunter
{
	//Instance variables
	private Town currentTown;
	private Hunter hunter;
	private String gameMode;
	private boolean gameEnded;

	//Constructor
	/**
	 * Constructs the Treasure Hunter game.
	 */
	public TreasureHunter()
	{
		// these will be initialized in the play method
		currentTown = null;
		hunter = null;
		gameMode = "";
		gameEnded = false;
	}

	// starts the game; this is the only public method
	public void play()
	{
		welcomePlayer();
		enterTown();
		showMenu();
	}

	/**
	 * Creates a hunter object at the beginning of the game and populates the class member variable with it.
	 */
	private void welcomePlayer()
	{
		Scanner scanner = new Scanner(System.in);

		System.out.println("Welcome to TREASURE HUNTER!");
		System.out.println("Going hunting for the big treasure, eh?");
		System.out.print("What's your name, Hunter? ");
		String name = scanner.nextLine();

		// set hunter instance variable
		hunter = new Hunter(name, 10);

		// set game mode
		System.out.print("What difficulty would you like to play on? ([e]asy/[n]ormal/[h]ard): ");
		while (gameMode.length() == 0) {
			String mode = scanner.nextLine().toLowerCase();
			if (mode.equals("easy") || mode.equals("e")) {
				gameMode = "E";
			} else if (mode.equals("normal") || mode.equals("n")) {
				gameMode = "N";
			} else if (mode.equals("hard") || mode.equals("h")) {
				gameMode = "H";
			} else if (mode.equals("cheat")) {
				gameMode = "C";
				hunter.setCheating(true);
			} else {
				System.out.print("Not a valid difficulty, please retry: ");
			}
		}
	}

	/**
	 * Creates a new town and adds the Hunter to it.
	 */
	private void enterTown()
	{
		// normal mode default values
		double markdown = 0.5;
		double toughness = 0.4;
		double priceMod = 1;
		double brawlWinChance = 0.5;
		double brawlGoldExtra = 0;

		if (gameMode.equals("E")) {
			// easy mode, lower buy value, higher resell value, and easier towns
			markdown = 0.9;
			toughness = 0.2;
			priceMod = 0.5;
			brawlWinChance = 0.75;
			brawlGoldExtra = 5;
		} else if (gameMode.equals("H")) {
			// hard mode, higher buy value, lower resell value, and tougher towns
			markdown = 0.25;
			toughness = 0.75;
		}

		// note that we don't need to access the Shop object
		// outside of this method, so it isn't necessary to store it as an instance
		// variable; we can leave it as a local variable
		Shop shop = new Shop(markdown, priceMod);

		// creating the new Town -- which we need to store as an instance
		// variable in this class, since we need to access the Town
		// object in other methods of this class
		currentTown = new Town(shop, toughness, brawlWinChance, brawlGoldExtra);

		// calling the hunterArrives method, which takes the Hunter
		// as a parameter; note this also could have been done in the
		// constructor for Town, but this illustrates another way to associate
		// an object with an object of a different class
		currentTown.hunterArrives(hunter);
	}

	/**
	 * Displays the menu and receives the choice from the user.<p>
	 * The choice is sent to the processChoice() method for parsing.<p>
	 * This method will loop until the user chooses to exit.
	 */
	private void showMenu()
	{
		Scanner scanner = new Scanner(System.in);
		String choice = "";

		while (!gameEnded)
		{
			System.out.println();
			System.out.println(currentTown.getLatestNews());
			System.out.println("***");
			System.out.println(hunter);
			System.out.println(currentTown);
			System.out.println("(B)uy something at the shop.");
			System.out.println("(S)ell something at the shop.");
			System.out.println("(M)ove on to a different town.");
			System.out.println("(L)ook for trouble!");
			System.out.println("(H)unt for treasure!");
			System.out.println("Give up the hunt and e(X)it.");
			System.out.println();
			System.out.print("What's your next move? ");
			choice = scanner.nextLine();
			processChoice(choice);
		}
	}

	/**
	 * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
	 * @param choice The action to process.
	 */
	private void processChoice(String choice)
	{
		if (choice.equals("B") || choice.equals("b") || choice.equals("S") || choice.equals("s"))
		{
			currentTown.enterShop(choice);
		}
		else if (choice.equals("M") || choice.equals("m"))
		{
			if (currentTown.leaveTown())
			{
				//This town is going away so print its news ahead of time.
				System.out.println(currentTown.getLatestNews());
				enterTown();
			}
		}
		else if (choice.equals("L") || choice.equals("l"))
		{
			currentTown.lookForTrouble();
			if (hunter.getGold() <= 0) {
				System.out.println();
				System.out.println("You lost all your gold!");
				System.out.println("Now you no longer have the funds required to adventure");
				System.out.println("You lose!");
				gameEnded = true;
			}
		}
		else if (choice.equals("H") || choice.equals("h"))
		{
			currentTown.huntForTreasure();
			if (hunter.hasAllTreasures()) {
				System.out.println();
				System.out.println("You found all 3 treasures across the world!");
				System.out.println("You win!");
				gameEnded = true;
			}
		}
		else if (choice.equals("X") || choice.equals("x"))
		{
			System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
			gameEnded = true;
		}
		else
		{
			System.out.println("Yikes! That's an invalid option! Try again.");
		}
	}
}
