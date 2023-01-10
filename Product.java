/*
RMIT University Vietnam
Course: COSC2081 Programming 1
Semester: 2022C
Assessment: Assignment 3
Authors: Nguyen Quoc An, Pham Minh Hoang, Tran Gia Minh Thong, Yoo Christina
ID: s3938278, s3930051, s3924667, s3938331
Acknowledgement:
https://stackoverflow.com/questions/64678515/how-to-delete-a-specific-row-from-a-csv-file-using-search-string
https://www.youtube.com/watch?v=ij07fW5q4oo
https://www.tutorialspoint.com/how-to-overwrite-a-line-in-a-txt-file-using-java
https://www.w3schools.com/java/java_regex.asp
https://stackoverflow.com/questions/33443651/print-java-arrays-in-columns
https://stackoverflow.com/questions/1883345/whats-up-with-javas-n-in-printf
https://www.geeksforgeeks.org/pattern-compilestring-method-in-java-with-examples/

*/

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class Product {
    private final String id;
    private final String NAME;
    private int price;
    private String category;
    private int numberSold;
    private static final ArrayList<Product> productArrayList = new ArrayList<>();
    private static ArrayList<Product> productFilteredArrayList = new ArrayList<>();

    public Product(String id, String NAME, int price, String category, int numberSold) {
        this.id = id;
        this.NAME = NAME;
        this.price = price;
        this.category = category;
        this.numberSold = numberSold;
    }

    public static void initializeProduct() throws IOException {
        productArrayList.clear();
        Scanner fileScanner = new Scanner(Paths.get("product.txt"));
        while (fileScanner.hasNext()) {
//            read per line and split line into array
            List<String> productValues = Arrays.asList(fileScanner.nextLine().split(","));

//                store products in arraylist
            productArrayList.add(new Product(productValues.get(0), productValues.get(1),
                    parseInt(productValues.get(2)), productValues.get(3), parseInt(productValues.get(4))));
        }

        productFilteredArrayList = productArrayList;
    }

    public static void addProduct() throws IOException {
        while (true) {
            boolean errorFree = true;
            boolean categoryMatched = false;
            System.out.println("\nCreate a new product:\n");

            Scanner userInput = new Scanner(System.in);

            System.out.println("Product name: ");
            String productName = userInput.nextLine();

            System.out.println("Price: ");
            String productPrice = userInput.nextLine();

            System.out.println("Category: ");
            String productCategory = userInput.nextLine();

            if (!validateInput(productName, "^[a-zA-Z0-9 ]{3,}$")) {
                System.out.println("Invalid user name (only letters and digits, at least 3 characters)");
                errorFree = false;
            }

            //            To check if product already existed because name is unique
            for (Product productLoop : productArrayList) {
                if (productName.equalsIgnoreCase(productLoop.getNAME())) {
                    System.out.println("Product already exists");
                    errorFree = false;
                    break;
                }
            }

            if (validateInput(productPrice, "[0-9 ]+")) {
                if (Integer.parseInt(productPrice.replace(",", "")) < 1000) {
                    System.out.println("Invalid price (must be a number at least 1000 VND)");
                    errorFree = false;
                }
            } else {
                System.out.println("Invalid price (must be a number at least 1000 VND1)");
                errorFree = false;
            }

//            Categories can only be chosen from ones that already exists
            for (String functionLoop : Category.getCategoryArrayList()) {
                if (functionLoop.equalsIgnoreCase(productCategory)) {
                    categoryMatched = true;
                    break;
                }
            }

            if (!categoryMatched) {
                System.out.println("Category not found");
                errorFree = false;
            }

            if (errorFree) {
//                    generate product id
                String productID = UUID.randomUUID().toString();
//                    write info to file
                PrintWriter pw = new PrintWriter(new FileWriter("product.txt", true));
                pw.println(productID + "," + productName + "," + Integer.parseInt(productPrice
                        .replace(",", "")) + "," + productCategory + "," + 0);
                pw.close();
                System.out.println("Successfully added product\n");
                break;
            }
        }
        initializeProduct();
    }

    public static void removeProduct() throws IOException {

        System.out.println("Please enter the name of the product you want to delete: ");
        Scanner userInput = new Scanner(System.in);
        String productName = userInput.nextLine();
        if (lookupProductName(productName)) {
            Utility.deleteRowTextFile(productName,1, "product.txt");
        } else {
            System.out.println("No product found, please try again");
            removeProduct();
        }
        initializeProduct();
    }

    public static void updatePrice() throws IOException {
        System.out.println("Please enter the name of the product you want to update the price: ");
        Scanner userInput = new Scanner(System.in);
        String productName = userInput.nextLine();
        if (lookupProductName(productName)) {
            while (true) {
                System.out.println("Please enter the new price of product " + productName + ":");
                String productNewPrice = userInput.nextLine();

                if (validateInput(productNewPrice, "[0-9 ]+")) {
                    if (Integer.parseInt(productNewPrice.replace(",", "")) > 1000) {
                        Utility.updateTextFile(productName, productNewPrice, 1, 2, "product.txt");
                        break;
                    }
                } else {
                    System.out.println("Error: Invalid price (must be a number at least 1000 VND)");
                }
            }
        } else {
            System.out.println("No product found, please try again");
            updatePrice();
        }
        initializeProduct();
    }

    public static ArrayList<Product> getMostPopularProducts() {
        ArrayList<Product> mostPopularProducts = new ArrayList<>();
        int mostSold = productArrayList.get(0).getNumberSold();
        for (Product productLoop : productArrayList) {
            if (mostSold < productLoop.getNumberSold()) {
                mostSold = productLoop.getNumberSold();
            }
        }

        for (Product productLoop : productArrayList) {
            if (mostSold == productLoop.getNumberSold()) {
                mostPopularProducts.add(productLoop);
            }
        }

        return mostPopularProducts;
    }

    public static ArrayList<Product> getLeastPopularProducts() {
        ArrayList<Product> leastPopularProducts = new ArrayList<>();
        int leastSold = productArrayList.get(0).getNumberSold();
        for (Product productLoop : productArrayList) {
            if (leastSold > productLoop.getNumberSold()) {
                leastSold = productLoop.getNumberSold();
            }
        }

        for (Product productLoop : productArrayList) {
            if (leastSold == productLoop.getNumberSold()) {
                leastPopularProducts.add(productLoop);
            }
        }

        return leastPopularProducts;
    }

    public static void displayProducts() {
        System.out.printf("%-20s%-20s%-20s%s%n", "Product name", "Price (VND)", "Category", "Number Sold");
        if (!productFilteredArrayList.isEmpty()) {
            for (Product productLoop : productFilteredArrayList) {
                System.out.printf("%-20s%,-20d%-20s%,-20d%n", productLoop.getNAME(), productLoop.getPrice(),
                        productLoop.getCategory(), productLoop.getNumberSold());
            }
        } else {
            System.out.println("No products to display\n");
        }
    }

    public static void filterCategory() {
        ArrayList<Product> tempProductArrayList  = new ArrayList<>();

        while (true) {
            boolean foundCategory = false;

            System.out.println("Please enter the category you want to sort:");
            Scanner userInput = new Scanner(System.in);
            String inputCategory = userInput.nextLine();
            for (String categoryLoop : Category.getCategoryArrayList()) {
                if (inputCategory.equalsIgnoreCase(categoryLoop)) {
                    foundCategory = true;
                    for (Product productLoop : productFilteredArrayList) {
                        if (inputCategory.equalsIgnoreCase(productLoop.getCategory())) {
                            tempProductArrayList.add(productLoop);
                        }
                    }
                    productFilteredArrayList = tempProductArrayList;
                }
            }

            if (!foundCategory) {
                System.out.println("No category found, please try again");
            } else {
                break;
            }
        }
    }

    public static void filterPrice() {
        ArrayList<Product> tempProductArrayList  = new ArrayList<>();

        while (true) {
            boolean UpperLimit = true;

            Scanner userInput = new Scanner(System.in);
            System.out.println("Please enter the lower price filter (type none for no lower price filter)");
            String lowerPriceLimit = userInput.nextLine();

            System.out.println("Please enter the upper price filter (type none for no upper price filter)");
            String upperPriceLimit = userInput.nextLine();

//            Let the user choose if there aren't any upper limits or lower limits
            if (lowerPriceLimit.equalsIgnoreCase("none")) {
                lowerPriceLimit = "0";
            }

            if (upperPriceLimit.equalsIgnoreCase("none")) {
                UpperLimit = false;
            }

//            Validate the inputs first
            if (validateInput(lowerPriceLimit, "[0-9 ]+")){
                if (UpperLimit) {
//                    This case only happens when there IS an upper limit
//                    Lower limit has to be lower than upper limit
                    if (validateInput(upperPriceLimit, "[0-9 ]+")) {

                        if (Integer.parseInt(lowerPriceLimit.replace(",", "")) <
                                Integer.parseInt(upperPriceLimit.replace(",", ""))) {
                            for (Product productLoop : productFilteredArrayList) {
                                if (Integer.parseInt(lowerPriceLimit.replace(",", "")) < productLoop.getPrice() &&
                                        Integer.parseInt(upperPriceLimit.replace(",", "")) > productLoop.getPrice()) {
                                    tempProductArrayList.add(productLoop);
                                }
                            }
//                            Write the temporary ArrayList back into the filtered ArrayList
                            productFilteredArrayList = tempProductArrayList;
                            break;
                        } else {
                            System.out.println("Invalid price, lower price limit must be smaller than upper price limit");
                        }
                    } else {
                        System.out.println("Invalid upper price (must be a number)");
                    }
//                    This case only happens when there ISN'T an upper limit
                } else {
                    for (Product productLoop : productFilteredArrayList) {
                        if (Integer.parseInt(lowerPriceLimit.replace(",", "")) < productLoop.getPrice()) {
                            tempProductArrayList.add(productLoop);
                        }
                    }
                    productFilteredArrayList = tempProductArrayList;
                    break;
                }
            } else {
                System.out.println("Invalid lower price (must be a number)");
            }
        }
    }

    public static void clearFilterProduct() {
        productFilteredArrayList = productArrayList;
    }

    public static boolean lookupProductName(String productName) {
        boolean productFound = false;
//            Look for the product name
        for (Product productLoop : productArrayList) {
            if (productName.equalsIgnoreCase(productLoop.getNAME())) {
                productFound = true;
                break;
            }
        }

        if (!productFound) {
            System.out.println("Product not found");
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateInput(String userInput, String pattern) {
        Pattern validPattern = Pattern.compile(pattern);
        return validPattern.matcher(userInput).find();
    }

    public String getId() {
        return id;
    }

    public String getNAME() {
        return NAME;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNumberSold() {
        return numberSold;
    }

    public void setNumberSold(int numberSold) {
        this.numberSold = numberSold;
    }

    public static ArrayList<Product> getProductArrayList() {
        return productArrayList;
    }
}