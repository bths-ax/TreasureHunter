/**
 * The Shop class controls the cost of the items in the Treasure Hunt game.<p>
 * The Shop class also acts as a go between for the Hunter's buyItem() method.<p>
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */
import java.util.Scanner;

public class Shop
{
	// constants
	private static final int WATER_COST = 2;
	private static final int ROPE_COST = 4;
	private static final int MACHETE_COST = 6;
	private static final int HORSE_COST = 12;
	private static final int BOAT_COST = 20;

	// instance variables
	private double markdown;
	private double priceMod;
	private Hunter customer;

	//Constructor
	public Shop(double markdown, double priceMod)
	{
		this.priceMod = priceMod;
		this.markdown = markdown;
		customer = null;
	}

	/** method for entering the shop
	 * @param hunter  the Hunter entering the shop
	 * @param buyOrSell  String that determines if hunter is "B"uying or "S"elling
	 */
	public void enter(Hunter hunter, String buyOrSell)
	{
		customer = hunter;

		Scanner scanner = new Scanner(System.in);
		if (buyOrSell.equals("B") || buyOrSell.equals("b"))
		{
			System.out.println("Welcome to the shop! We have the finest wares in town.");
			System.out.println("Currently we have the following items:");
			System.out.println(inventory());
			System.out.print("What're you lookin' to buy? ");
			String item = normalizeItemName(scanner.nextLine());
			int cost = checkMarketPrice(item, true);
			if (cost == 0)
			{
				System.out.println("We ain't got none of those.");
			}
			else
			{
				System.out.print("It'll cost you " + cost + " gold. Buy it (y/n)? ");
				String option = scanner.nextLine();

				if (option.equals("y") || option.equals("Y"))
				{
					buyItem(item);
				}
			}
		}
		else
		{
			System.out.println("What're you lookin' to sell? ");
			System.out.print("You currently have the following items: " + customer.getInventory());
			String item = normalizeItemName(scanner.nextLine());
			int cost = checkMarketPrice(item, false);
			if (cost == 0)
			{
				System.out.println("We don't want none of those.");
			}
			else
			{
				System.out.print("It'll get you " + cost + " gold. Sell it (y/n)? ");
				String option = scanner.nextLine();

				if (option.equals("y") || option.equals("Y"))
				{
					sellItem(item);
				}
			}
		}
	}

	/** Normalizes an item name (Changes it to proper casing)
	  * @param itemName the inputted item name
	  * @return the modified string containing the proper item name
	  */
	private String normalizeItemName(String itemName) {
		itemName = itemName.toLowerCase();
		if (itemName.equals("w") || itemName.equals("water")) {
			return "Water";
		} else if (itemName.equals("r") || itemName.equals("rope")) {
			return "Rope";
		} else if (itemName.equals("m") || itemName.equals("machete")) {
			return "Machete";
		} else if (itemName.equals("h") || itemName.equals("horse")) {
			return "Horse";
		} else if (itemName.equals("b") || itemName.equals("boat")) {
			return "Boat";
		} else {
			return "";
		}
	}

	/** A method that returns a string showing the items available in the shop (all shops sell the same items)
	 *
	 * @return  the string representing the shop's items available for purchase and their prices 
	 */
	public String inventory()
	{
		String str = "(W)ater: " + getCostOfItem("Water") + " gold\n";
		str += "(R)ope: " + getCostOfItem("Rope") + " gold\n";
		str += "(M)achete: " + getCostOfItem("Machete") + " gold\n";
		str += "(H)orse: " + getCostOfItem("Horse") + " gold\n";
		str += "(B)oat: " + getCostOfItem("Boat") + " gold\n";

		return str;
	}

	/**
	 * A method that lets the customer (a Hunter) buy an item.
	 * @param item The item being bought.
	 */
	public void buyItem(String item)
	{
		int costOfItem = checkMarketPrice(item, true);
		if (customer.buyItem(item, costOfItem))
		{
			System.out.println("Ye' got yerself a " + item + ". Come again soon.");
		}
		else
		{        
			System.out.println("Hmm, either you don't have enough gold or you've already got one of those!");
		}
	}

	/**
	 * A pathway method that lets the Hunter sell an item.
	 * @param item The item being sold.
	 */
	public void sellItem(String item)
	{
		int buyBackPrice = checkMarketPrice(item, false);
		if (customer.sellItem(item, buyBackPrice))
		{
			System.out.println("Pleasure doin' business with you.");
		}
		else
		{
			System.out.println("Stop stringin' me along!");
		}
	}

	/**
	 * Determines and returns the cost of buying or selling an item.
	 * @param item The item in question.
	 * @param isBuying Whether the item is being bought or sold.
	 * @return The cost of buying or selling the item based on the isBuying parameter.
	 */
	public int checkMarketPrice(String item, boolean isBuying)
	{
		if (isBuying)
		{
			return getCostOfItem(item);
		}
		else
		{
			return getBuyBackCost(item);
		}
	}

	/**
	 * Checks the item entered against the costs listed in the static variables,
	 * and multiplies it by the price modifier
	 * 
	 * @param item The item being checked for cost.
	 * @return The cost of the item or 0 if the item is not found.
	 */
	public int getCostOfItem(String item)
	{
		int itemCost = 0;
		if (item.equals("Water")) {
			itemCost = WATER_COST;
		} else if (item.equals("Rope")) {
			itemCost = ROPE_COST;
		} else if (item.equals("Machete")) {
			itemCost = MACHETE_COST;
		} else if (item.equals("Horse")) {
			itemCost = HORSE_COST;
		} else if (item.equals("Boat")) {
			itemCost = BOAT_COST;
		}

		if (customer.isCheating() && itemCost != 0)
			return 1;

		return (int)(itemCost * priceMod);
	}

	/**
	 * Checks the cost of an item and applies the markdown.
	 * 
	 * @param item The item being sold.
	 * @return The sell price of the item.
	 */
	public int getBuyBackCost(String item)
	{
		int cost = (int)(getCostOfItem(item) * markdown);
		if (customer.isCheating() && cost != 0)
			return 1;
		return cost;
	}
}
