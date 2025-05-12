package com.example.enterpriseproject1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import java.text.DecimalFormat;

import java.util.*;
import java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;


import java.net.URL;
import java.util.ResourceBundle;

public class ShopTripController implements Initializable {

    private List<String> cartItems = new ArrayList<>(); // Use a List to store cart items

    private int itemCount = 0;

    private int itemNumber = 1;

    private double subtotal;

    @FXML
    private Label shoppingCartStatusLabel;
    @FXML
    private Label enterItemID;
    @FXML
    private Label enterQuantity;
    @FXML
    private Label details;
    @FXML
    private Label currentSubtotal;
    @FXML
    private TextField enterItemIdInput;
    @FXML
    private TextField enterQuantityInput;
    @FXML
    private TextField detailsInput;
    @FXML
    private TextField currentSubtotalInput;
    @FXML
    private TextField cartSlot1;
    @FXML
    private TextField cartSlot2;
    @FXML
    private TextField cartSlot3;
    @FXML
    private TextField cartSlot4;
    @FXML
    private TextField cartSlot5;
    @FXML
    private Button findItemButton;
    @FXML
    private Button viewCartButton;
    @FXML
    private Button emptyCartButton;
    @FXML
    private Button addToCartButton;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button exitButton;


    String csvFilePath = "src/inventory.csv";

    public Map search(String id) throws IOException {

        Map<String, String> foundItem = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
        String line;

        // Read each line from the CSV file
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");

            String itemID = values[0].trim();

            // If item's ID found, store it in a Map
            if(id.equals(itemID)) {
                foundItem.put("ID", itemID);

                String description = values[1].trim();
                foundItem.put("Description", description);

                String flag = values[2].trim();
                foundItem.put("Flag", flag);

                String count = values[3].trim();
                foundItem.put("Count", count);

                String price = values[4].trim();
                foundItem.put("Price", price);

                break;
            }
        }

        return foundItem;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Show and wait for user confirmation
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            // Reset text fields
            enterItemIdInput.clear();
            enterQuantityInput.clear();
        }
    }

    public Map findItem() throws IOException {
        String itemID = enterItemIdInput.getText();
        double total = 0.0;

        if (itemID.isBlank()) {
            // Item ID not entered, show a popup notification
            showAlert("Error", "Please enter an Item ID", Alert.AlertType.WARNING);

            return new HashMap<>();
        }

        Map item = search(itemID);

        if (item.isEmpty()) {
            // Item not found, show a popup notification
            showAlert("Error", "Item ID " + itemID + " not in file", Alert.AlertType.WARNING);

            return new HashMap<>();
        }

        // Get total price based on the quantity
        int orderQuantity = Integer.parseInt(enterQuantityInput.getText());

        // Determine discount percentage depending on quantity
        double percentageDisc = 0;
        if (orderQuantity >= 5 && orderQuantity <= 9) {
            percentageDisc = 0.1;
        }
        else if (orderQuantity >= 10 && orderQuantity <= 14) {
            percentageDisc = 0.15;
        }
        else if (orderQuantity >= 15) {
            percentageDisc = 0.20;
        }

        // Determine total based on discount
        double price = Double.parseDouble(String.valueOf(item.get("Price")));

        double discount = 0;
        if (percentageDisc == 0) {
            total = price * orderQuantity;
        }
        else {
            discount = (price * orderQuantity) * percentageDisc;
            total = (price * orderQuantity) - discount;
        }

        // Create a DecimalFormat instance with the desired pattern
        DecimalFormat totalDecimalFormat = new DecimalFormat("#.##");

        // Format the total using the DecimalFormat
        String formattedTotal = "$" + totalDecimalFormat.format(total);

        // Create a DecimalFormat instance with the desired pattern for discount
        DecimalFormat discountDecimalFormat = new DecimalFormat("#");

        // Format the discount using the DecimalFormat
        String formattedDiscount = discountDecimalFormat.format(percentageDisc * 100) + "% ";

        // Based on the quantity in stock handle errors or complete search.
        int qty = Integer.parseInt(String.valueOf(item.get("Count")));

        if (qty == 0) {
            showAlert("Error", "Sorry... that item is out of stock, please try another item", Alert.AlertType.WARNING);
        }
        else if (qty < orderQuantity) {
            showAlert("Error", "Insufficient stock. Only " + qty + " on hand. Please reduce the quantity.", Alert.AlertType.WARNING);
        }
        else {
            // Item found, update detailsInput label
            String details = item.get("ID") + " " + item.get("Description") + " $" + item.get("Price") + " "
                    + orderQuantity + " " + formattedDiscount + formattedTotal;

            // Update the subtotal for the entire order
            subtotal += total;

            // Display item's details
            detailsInput.setText(details);

            // Enable addToCartButton
            addToCartButton.setDisable(false);

            // Disable findItemButton
            findItemButton.setDisable(true);

        }

        return item;
    }

    public void emptyCart() {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Your shopping cart is already empty.", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Empty Cart");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to empty your shopping cart?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Clear the cartItems list
            cartItems.clear();

            // Update the cart slots in the GUI
            updateCartSlots();

            showAlert("Cart Emptied", "Your shopping cart has been emptied.", Alert.AlertType.INFORMATION);

            // Reset text fields
            currentSubtotalInput.clear();
            detailsInput.clear();

            // Reset Labels and Buttons
            itemNumber = 1;
            itemCount = 0;
            updateLabelsButtonsText();
        }
    }

    public void addToCart() {
        String itemDesc = detailsInput.getText();
        String[] values = itemDesc.split("\\s+(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");

        cartItems.add(itemDesc);

        // Update the cart slots dynamically
        updateCartSlots();

        
        // Create a DecimalFormat instance with the desired pattern
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Format the subtotal using the DecimalFormat
        String formattedSubtotal = "$" + decimalFormat.format(subtotal);

        // Display current order subtotal after adding to the cart
        currentSubtotalInput.setText(formattedSubtotal);

        showAlert("Item Added", "Item added to the cart.", Alert.AlertType.INFORMATION);

        // Update the item's number (MAX 5) and the total amount of items.
        itemNumber++;
        itemCount += Integer.parseInt(values[3]);

        // Update labels and buttons text to show what is the current item number
        updateLabelsButtonsText();

        // Handle if we reach max amount of items(5) in the cart
        if (itemNumber == 6) {
            // Disable Fields and Buttons
            addToCartButton.setDisable(true);
            findItemButton.setDisable(true);
            enterItemIdInput.setDisable(true);
            enterQuantityInput.setDisable(true);
        }
        else {
            // Disable addToCartButton
            addToCartButton.setDisable(true);

            // Enable findItemButton
            findItemButton.setDisable(false);

            if (itemNumber > 1) {
                // if we are no longer searching for the first item
                viewCartButton.setDisable(false);
                checkoutButton.setDisable(false);
            }
        }
    }

    public void viewCart() {

        // Create the string that will be display in the pop-up box for us to View Cart
        StringBuilder items = new StringBuilder();

        for(int i = 0; i < cartItems.size(); i++) {
            items.append(i+1).append(". ").append(cartItems.get(i)).append("\n");
        }

        String s = items.toString();

        showAlert("Current Shopping Cart Status", s, Alert.AlertType.INFORMATION);
    }

    private void updateCartSlots() {
        // Update the cart slots in the GUI based on the contents of the cartItems list
        List<TextField> cartSlots = List.of(cartSlot1, cartSlot2, cartSlot3, cartSlot4, cartSlot5);

        for (int i = 0; i < cartSlots.size(); i++) {
            if (i < cartItems.size()) {
                // String manipulation and Displaying in cart formatted string.
                String[] values = cartItems.get(i).split("\\s+(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                String cartItemFormatted = "Item " + itemNumber + " - " + "SKU: " + values[0] + ", Desc: " + values[1] +
                        ", Price Ea. " + values[2] + ", Qty: " + values[3] + ", Total: " + values[5];

                cartSlots.get(i).setText(cartItemFormatted);
            } else {
                cartSlots.get(i).clear();
            }
        }
    }

    private void updateLabelsButtonsText() {

        if (itemNumber > 5) {
            String itemLabelText = "Enter item ID for Item #5:";
            String quantityLabelText = "Enter quantity for Item #5:";
            String detailsLabelText = "Details for Item #5:";
            String subtotalLabelText = "Current Subtotal for 5 item(s) :";

            String cartStatus = "Your Current Cart With 5 Item(s)";

            String findItemButtonText = "Find Item #5";
            String addToCartButtonText = "Add Item #5 to Cart";

            enterItemID.setText(itemLabelText);
            enterQuantity.setText(quantityLabelText);
            details.setText(detailsLabelText);
            currentSubtotal.setText(subtotalLabelText);
            shoppingCartStatusLabel.setText(cartStatus);

            findItemButton.setText(findItemButtonText);
            addToCartButton.setText(addToCartButtonText);
        }
        else {
            String itemLabelText = "Enter item ID for Item #" + itemNumber + ":";
            String quantityLabelText = "Enter quantity for Item #" + itemNumber + ":";
            String subtotalLabelText = "Current Subtotal for " + itemCount + " item(s) :";

            String detailsLabelText = "";
            if(itemNumber <= 1) {
                detailsLabelText = "Details for Item #" + itemNumber + ":";
            }
            else {
                detailsLabelText = "Details for Item #" + (itemNumber - 1) + ":";
            }

            String cartStatus = "";
            if (!cartItems.isEmpty()) {
                cartStatus = "Your Current Cart With " + cartItems.size() + " Item(s)";
            }

            String findItemButtonText = "Find Item #" + itemNumber;
            String addToCartButtonText = "Add Item #" + itemNumber + " to Cart";

            enterItemID.setText(itemLabelText);
            enterQuantity.setText(quantityLabelText);
            details.setText(detailsLabelText);
            currentSubtotal.setText(subtotalLabelText);
            shoppingCartStatusLabel.setText(cartStatus);

            findItemButton.setText(findItemButtonText);
            addToCartButton.setText(addToCartButtonText);
        }
    }

    public void checkout() {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Your shopping cart is empty. Add items before checking out.", Alert.AlertType.INFORMATION);
            return;
        }

        // Get the current date and time with time zone
        ZonedDateTime nowWithZone = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm:ss a z");
        String formattedDateTime = nowWithZone.format(formatter);

        // Create a StringBuilder to build the invoice content
        StringBuilder invoiceContent = new StringBuilder();
        invoiceContent.append("Date: ").append(formattedDateTime).append("\n\n");
        invoiceContent.append("Number of line items: ").append(cartItems.size()).append("\n\n");
        invoiceContent.append("Item# / ID / Title / Price / Qty / Disc % / Subtotal\n\n");

        // Iterate through each item in the cartItems list
        for (int i = 0; i < cartItems.size(); i++) {
            String currItem = cartItems.get(i);

            // Append item details to the invoiceContent StringBuilder
            invoiceContent.append(i+1).append(". ").append(currItem).append(" \n");
        }

        invoiceContent.append("\n");

        // Calculate tax amount
        double taxRate = 0.06;
        double taxAmount = subtotal * taxRate;

        // Calculate order total
        double orderTotal = subtotal + taxAmount;

        // Create a DecimalFormat instance with the desired pattern
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Append additional information to the invoiceContent StringBuilder
        invoiceContent.append("\nOrder subtotal: $").append(decimalFormat.format(subtotal)).append("\n\n");
        invoiceContent.append("Tax rate: %").append(taxRate * 100).append("\n\n");
        invoiceContent.append("Tax amount: $").append(decimalFormat.format(taxAmount)).append("\n\n");
        invoiceContent.append("ORDER TOTAL: $").append(decimalFormat.format(orderTotal)).append("\n\n");
        invoiceContent.append("Thanks for shopping at ShopTrip.com");

        // Create an Alert with the invoice content
        Alert invoiceAlert = new Alert(Alert.AlertType.CONFIRMATION);
        invoiceAlert.setTitle("Final Invoice");
        invoiceAlert.setHeaderText(null);
        invoiceAlert.setContentText(invoiceContent.toString());

        // Show the invoice alert
        // Clear the cartItems list and update the GUI
        Optional<ButtonType> result = invoiceAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Write transaction details to transactions.csv
            writeTransactionToCSV();

            // Disable buttons and text fields after we checked out
            findItemButton.setDisable(true);
            addToCartButton.setDisable(true);
            checkoutButton.setDisable(true);
            enterItemIdInput.setDisable(true);
            enterQuantityInput.setDisable(true);

            updateLabelsButtonsText();
        }
    }

    private void writeTransactionToCSV() {
        String fileName = "transactions.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            // Get the current date and time with the default time zone
            ZonedDateTime now = ZonedDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyyHHmmss");
            String formattedDateTime = now.format(formatter);

            // Write transaction details for each item in the cart
            for (String cartItem : cartItems) {
                String [] values = cartItem.split("\\s+(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");

                String percentage = values[4].replaceAll("[^\\d]", "");;

                String itemFormatted = values[0] + ", " + values[1] + ", " + values[2].substring(1)
                        + ", " + values[3] + ", " + Double.parseDouble(percentage) + ", " + values[5];

                // Transaction format: Date, ID, Title, Price, Qty, Disc %, Subtotal, Formatted Date
                String transactionLine = String.format("%s, %s, %s",
                        formattedDateTime, itemFormatted,
                        now.format(DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm:ss a z")));
                writer.println(transactionLine);
            }
            writer.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        // Ask user for permission to close the application
        Alert closeAlert = new Alert(Alert.AlertType.WARNING);
        closeAlert.setTitle("Confirm exit!!!");
        closeAlert.setHeaderText(null);
        closeAlert.setContentText("Are you sure you want to close this app?");

        ButtonType buttonTypeOK = new ButtonType("OK - Exit");
        ButtonType buttonTypeNo = new ButtonType("No - Return");

        closeAlert.getButtonTypes().setAll(buttonTypeNo, buttonTypeOK);

        Optional<ButtonType> result = closeAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOK) {
            // Close the JavaFX application
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}