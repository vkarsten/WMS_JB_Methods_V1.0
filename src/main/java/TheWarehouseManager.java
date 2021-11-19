package main.java;


import main.java.data.Item;
import main.java.data.PersonnelRepository;
import main.java.data.StockRepository;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static main.java.data.StockRepository.getItemsByWarehouse;

/**
 * Provides necessary methods to deal through the Warehouse management actions
 *
 * @author riteshp
 */
public class TheWarehouseManager {
    // =====================================================================================
    // Member Variables
    // =====================================================================================

    // To read inputs from the console/CLI
    private final Scanner reader = new Scanner(System.in);
    private final String[] userOptions = {
            "1. List items by warehouse", "2. Search an item and place an order", "3. Browse by category", "4. Quit"
    };
    // To refer the user provided name
    private String userName;
    // To refer the user provided password
    private String password;
    // To check if the user has logged in during the session
    private boolean loggedIn = false;

    // To refer warehouses and categories
    private Set<Integer> warehouses = StockRepository.getWarehouses();
    private Set<String> categories = StockRepository.getCategories();

    // To refer all actions during the session
    private static List<String> SESSION_ACTIONS = new ArrayList<>();

    // To refer to the items matching the current search
    private Map<Integer, List<Item>> matchingItemsPerWarehouse = new HashMap<>(this.warehouses.size());

    // =====================================================================================
    // Public Member Methods
    // =====================================================================================

    /** Welcome User */
    public void welcomeUser() {
        this.seekUserName();
        this.greetUser();
    }

    /** Ask for user's choice of action */
    public int getUsersChoice() {
        System.out.println("What would you like to do?");
        for (String option : this.userOptions) {
            System.out.println(option);
        }
        System.out.println("Type the number of the operation:");
        int choice;
        try {
            choice = this.reader.nextInt();
        } catch (InputMismatchException e) {
            choice = -1;
        }
        this.reader.nextLine();
        return choice;
    }

    /** Initiate an action based on given option */
    public void performAction(int option) {
        switch (option) {
            case 1:
                this.listItemsByWarehouse();
                break;
            case 2:
                this.searchItemAndPlaceOrder();
                break;
            case 3:
                this.browseByCategory();
                break;
            case 4:
                this.quit();
            default:
                System.out.println("The option you entered is not valid! Please try again.");
        }
    }

    /**
     * Confirm an action
     *
     * @return action
     */
   public boolean confirm(String message) {
       System.out.printf("%s (y/n)\n", message);
       return (this.reader.nextLine().toLowerCase().startsWith("y"));
   }

    /** End the application */
   public void quit() {
        System.out.printf("\nThank you for your visit, %s!\n", this.userName);
        listSessionActions();
        System.exit(0);
   }

    // =====================================================================================
    // Private Methods
    // =====================================================================================

    // Methods for general execution flow
    // =====================================================================================

    /** Get user's name via CLI */
   private void seekUserName() {
        System.out.println("Please enter your user name:");
        this.userName = this.reader.nextLine();
   }

    /** Get user's password via CLI */
    private void askPassword() {
        System.out.println("Please enter your password:");
        this.password = this.reader.nextLine();
    }

    /** log in the current user */
    private void logIn() {
        if (this.loggedIn) return;
        System.out.println("You need to log in for this action.");
        while (!this.loggedIn) {
            this.askPassword();

            if (PersonnelRepository.isUserValid(this.userName, this.password)) {
                System.out.println("You logged in successfully");
                this.loggedIn = true;
            } else if (this.confirm("This was not successful. Do you want to try again?")) {
                    this.seekUserName();
                } else return;
            }
    }

    /** Print a welcome message with the given user's name */
    private void greetUser() {
        System.out.printf("Hello %s!\n", this.userName);
    }

    /** log an action into the list of session actions */
    private void logSessionAction(String action) {
        SESSION_ACTIONS.add(action);
    }

    /** list all actions of this session */
    private void listSessionActions() {
        if (SESSION_ACTIONS.size() > 0) {
            System.out.println("In this session you have: ");
            int actionNumber = 1;
            for (String action : SESSION_ACTIONS) {
                System.out.printf("%d. %s \n", actionNumber, action);
                actionNumber++;
            }
        } else System.out.println("In this session you have not done anything.");
    }


    // Methods for menu option: list items by warehouse
    // =====================================================================================
    private void listItemsByWarehouse() {
        Map<Integer, Integer> totalItems = new HashMap<>(this.warehouses.size());

        for (int warehouse : this.warehouses) {
                System.out.println("\nItems in Warehouse " + warehouse);
                List<Item> warehouseItems = new ArrayList<>(StockRepository.getItemsByWarehouse(warehouse));

                listItems(warehouseItems);

                totalItems.put(warehouse, warehouseItems.size());
        }

        listTotalItemsPerWarehouse(totalItems);

        logSessionAction("Listed " + getTotalListedItems() + " items.");
    }

    /**
     * prints the list of all items in a given warehouse
     * @param warehouseItems, List of Items
     */
    private void listItems(List<Item> warehouseItems) {
            for (Item item : warehouseItems) {
                System.out.printf("- %s\n", item.toString());
            }
    }

    /**
     * prints the total amounts of items per warehouse
     * @param totalItemsPerWarehouse, Map of Integer (Warehouse number) and Integer (Items in the Warehouse)
     */
    private void listTotalItemsPerWarehouse(Map<Integer, Integer> totalItemsPerWarehouse) {
        for (Map.Entry<Integer, Integer> entry : totalItemsPerWarehouse.entrySet()) {
            System.out.printf("Total items in warehouse %d: %s\n", entry.getKey(), entry.getValue());
        }
    }

    /** return the total amount of items in all warehouses */
    private int getTotalListedItems() {
        return StockRepository.getAllItems().size();
    }

    // Methods for menu option: Search item and place order
    // =====================================================================================
    private void searchItemAndPlaceOrder() {
        String itemName = askItemToOrder();

        this.getMatchingItemLists(itemName);
        int totalAmount = this.getAvailableAmount();

        System.out.println("Amount available: " + totalAmount);

        if (totalAmount == 0) {
            this.printLocation("Not in stock");
        } else {
            this.listAllLocations();
            if (this.matchingItemsPerWarehouse.size() > 1) {
                this.printMaximumAvailability();
            }

            if (this.confirm("Would you like to order this item?")) {
                this.logIn();
                if (this.loggedIn) this.askAmountAndConfirmOrder(totalAmount, itemName.toLowerCase());
            }
        }

        logSessionAction("Searched " + getAppropriateIndefiniteArticle(itemName) + " " + itemName + ".");
    }

    /**
     * Ask the user to specify an Item to Order
     *
     * @return String itemName
     */
    private String askItemToOrder() {
        System.out.println("What is the name of the item?");
        return this.reader.nextLine();
    }

    /**
     * Calculate availability of the given item
     * @return integer total amount
     */
    private int getAvailableAmount() {
        int totalAmount = 0;

         for (List<Item> warehouseItems : matchingItemsPerWarehouse.values()) {
             totalAmount += warehouseItems.size();
         }

        return totalAmount;
    }

    /**
     * Create a map of all the occurrences of an item separated by warehouse
     * @param itemName, String, the name of the item
     * @return allAmounts, the matching items per warehouse
     */
    private void getMatchingItemLists(String itemName) {
        for (int warehouse : this.warehouses) {
            List<Item> matchingItems = this.find(itemName, warehouse);
            if (matchingItems.size() > 0) {
                this.matchingItemsPerWarehouse.put(warehouse, matchingItems);
            }
        }
    }

    /**
     * Find the item in a given warehouse
     *
     * @param item the item
     * @param warehouse the warehouse
     * @return matchingItems, a List of the corresponding items found in the warehouse
     */
    private List<Item> find(String item, int warehouse) {
        List<Item> warehouseItems = new ArrayList<>(StockRepository.getItemsByWarehouse(warehouse));
        List<Item> matchingItems = new ArrayList<>();

        for (Item warehouseItem : warehouseItems) {
            if (warehouseItem.toString().equalsIgnoreCase(item)) matchingItems.add(warehouseItem);
        }

        return matchingItems;
    }

    /** Print the location of an item without listing the available items
     *
     * @param location the location of the items
     */
    private void printLocation(String location) {
        System.out.println("Location: " + location);
    }

    /** Print the location of an item and lists the corresponding items and their warehouse */
    private void listAllLocations() {
        System.out.println("Location: ");

        for (List<Item> warehouseItems : matchingItemsPerWarehouse.values()) {
            for (Item item : warehouseItems) {
                System.out.printf("- Warehouse %d (in stock for %d days)\n", item.getWarehouse(), this.calculateNumberOfDaysInStock(item));
            }
        }
    }

    /**
     * Calculate the number of days a given item has been in stock
     * @param item Item
     * @return the number of days in stock, long
     */
    private long calculateNumberOfDaysInStock(Item item) {
        Date today = new Date();
        return TimeUnit.DAYS.convert(today.getTime() - item.getDateOfStock().getTime(), TimeUnit.MILLISECONDS);
    }

    /**
     * Print the location with the maximum availability of an item
     */
    private void printMaximumAvailability() {
        int maxSize = 0;
        int warehouse = 0;

        for (Map.Entry<Integer, List<Item>> warehouseItems : matchingItemsPerWarehouse.entrySet()) {
            if (warehouseItems.getValue().size() > maxSize) {
                maxSize = warehouseItems.getValue().size();
                warehouse = warehouseItems.getKey();
            }
        }

        System.out.printf("Maximum availability: %d in Warehouse %d\n", maxSize, warehouse);
    }


    /** Ask order amount and confirm order */
    private void askAmountAndConfirmOrder(int availableAmount, String item) {
        System.out.println("How many would you like to order?");
        int order = getOrderAmount(availableAmount);
        if (order > 0) {
            System.out.printf("Your order of %d %s is confirmed.\n", order, (order == 1) ? item : item+"s");
        } else {
            System.out.println("No order has been placed.");
        }
    }

    /**
     * Get amount of order
     *
     * @param availableAmount the total available amount of the item in question
     * @return desiredAmount the amount to be ordered
     */
    private int getOrderAmount(int availableAmount) {
        int desiredAmount;

        if (this.reader.hasNextInt()) {
            desiredAmount = this.reader.nextInt();
            this.reader.nextLine();
        } else {
            this.reader.nextLine();
            return -1;
        }

        if (desiredAmount > availableAmount) {
            System.out.println("There are not this many available. The maximum amount that can be ordered is " + availableAmount);
            return (this.confirm("Would you like to order this amount?")) ? availableAmount : -1;
        }

        return desiredAmount;
    }

    /**
     * Return the appropriate indefinite article for a given String, depending on whether it starts with a vowel
     * @param word String
     * @return the appropriate article, String
     */
    private String getAppropriateIndefiniteArticle(String word) {
        String vowels = "aeiou";
        return (vowels.indexOf(Character.toLowerCase(word.charAt(0))) != -1) ? "an" : "a";
    }

    // Methods for menu option: browse by category
    // =====================================================================================
    private void browseByCategory() {
        Map<Integer, String> categoryList = this.getCategoryMenu();
        this.showCategoryMenu(categoryList);

        int categoryNumber = this.getCategoryChoice();
        if (categoryNumber > 0 && categoryNumber <= categoryList.size()) {
            String category = categoryList.get(categoryNumber);
            this.printCategoryItems(category);
            logSessionAction("Browsed the category " + category  + ".");
        } else {
            System.out.println("This is not a valid category.");
        }
    }

    /** Create a menu of categories with corresponding numbers
     *
     * @return categoryList, a map of categories and their numbers
     */
    private Map<Integer, String> getCategoryMenu() {
        Map<Integer, String> categoryList = new HashMap<>();
        int count = 1;

        for (String category : this.categories) {
            categoryList.put(count, category);
            count++;
        }

        return categoryList;
    }

    /**
     * Print the menu of categories and the number of items in each category
     * @param categoryList a map of the categories and their numbers
     */
    private void showCategoryMenu(Map<Integer, String> categoryList) {
        for (Map.Entry<Integer, String> entry : categoryList.entrySet()) {
            int categoryNumber = entry.getKey();
            String category = entry.getValue();

            System.out.printf("%d. %s (%d)\n", categoryNumber, category, getAmountPerCategory(category));
        }
    }

    /**
     * Return the amount of items in a category
     * @param category, String, name of the category
     * @return int, amount of items
     */
    private int getAmountPerCategory(String category) {
        return StockRepository.getItemsByCategory(category).size();
    }

    /**
     * Ask for the user's choice of category
     * @return int, the chosen category
     */
    private int getCategoryChoice() {
        System.out.println("Type the number of the category to browse:");
        int choice;
        try {
            choice = this.reader.nextInt();
        } catch (InputMismatchException e) {
            choice = -1;
        }
        this.reader.nextLine();
        return choice;
    }

    /**
     * Print the available items in a chosen category and their location
     * @param category, String, the name of the category
     */
    private void printCategoryItems(String category) {
        System.out.printf("List of %ss available:\n", category.toLowerCase());

        for (Item item : StockRepository.getItemsByCategory(category)) {
            System.out.printf("%s %s, Warehouse %d\n", item.getState(), item.getCategory(), item.getWarehouse());
        }
    }
}
