package main.java.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * The Data Repository
 *
 * @author riteshp
 *
 */
public class StockRepository {

    private static List<Item> ITEM_LIST = new ArrayList<Item>();

    /**
     * Load item records from the stock.json file
     */
    static {
        // System.out.println("Loading items");
        BufferedReader reader = null;
        try {
            ITEM_LIST.clear();

            reader = new BufferedReader(new FileReader("src/main/resources/stock.json"));
            Object data = JSONValue.parse(reader);
            if (data instanceof JSONArray) {
                JSONArray dataArray = (JSONArray) data;
                for (Object obj : dataArray) {
                    if (obj instanceof JSONObject) {
                        JSONObject jsonData = (JSONObject) obj;
                        Item item = new Item();
                        item.setState(jsonData.get("state").toString());
                        item.setCategory(jsonData.get("category").toString());
                        item.setWarehouse(Integer.parseInt(jsonData.get("warehouse").toString()));
                        String date = jsonData.get("date_of_stock").toString();
                        // System.out.println("Item Date " + date);
                        item.setDateOfStock(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date));
                        // System.out.println(item);

                        ITEM_LIST.add(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Get All items available in the repository
     *
     * @return
     */
    public static List<Item> getAllItems() {
        return ITEM_LIST;
    }

    // By Warehouse
    /**
     * Get the list of unique warehouse IDs
     *
     * @return
     */
    public static Set<Integer> getWarehouses() {
        Set<Integer> warehouses = new HashSet<Integer>();
        for (Item item : getAllItems()) {
            warehouses.add(item.getWarehouse());
        }
        return warehouses;
    }

    /**
     * Get the list of all items in a specific warehouse
     *
     * @param warehouse
     * @return
     */
    public static List<Item> getItemsByWarehouse(int warehouse) {
        return getItemsByWarehouse(warehouse, getAllItems());
    }

    /**
     * Get the list of items related to a specific warehouse in a given master-list
     *
     * @param warehouse
     * @return
     */
    public static List<Item> getItemsByWarehouse(int warehouse, List<Item> masterList) {
        List<Item> items = new ArrayList<Item>();
        for (Item item : masterList) {
            if (item.getWarehouse() == warehouse) {
                items.add(item);
            }
        }
        return items;
    }

    // By Category
    /**
     * Get the list of unique Categories
     *
     * @return
     */
    public static Set<String> getCategories() {
        Set<String> categories = new HashSet<String>();
        for (Item item : getAllItems()) {
            categories.add(item.getCategory());
        }
        return categories;
    }

    /**
     * Get the list of all items of a specific category
     *
     * @param category
     * @return
     */
    public static List<Item> getItemsByCategory(String category) {
        return getItemsByCategory(category, getAllItems());
    }

    /**
     * Get the list of items of a specific category in a given master-list
     *
     * @param category
     * @return
     */
    public static List<Item> getItemsByCategory(String category, List<Item> masterList) {
        List<Item> items = new ArrayList<Item>();
        for (Item item : masterList) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                items.add(item);
            }
        }
        return items;
    }
}