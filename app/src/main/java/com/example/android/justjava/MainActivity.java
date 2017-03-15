package com.example.android.justjava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {
    //Variables to store current quantity and price of the drink
    int quantity = 1;
    double price = 0;

    //Arrays to store names and prices of all possible drinks, and a variable to store the current selected drink
    String drinks[] = {"Americano", "Black Coffee", "Cappuccino", "Frappuccino", "Hot Chocolate", "Latte", "Mocha", "Tea"};
    double drinkPrices[] = {2.00, 1.55, 2.25, 3.40, 2.85, 2.25, 2.80, 2.85};
    String currentDrink;

    //A random number to represent the Drink of the Day, and a message used to display the Drink of the Day
    Random random = new Random();
    int drinkOfTheDayNum = random.nextInt(8);
    String drinkOfTheDayText = "20% discount on our Drink of the Day: ";

    //Creates objects for both buttons, to be used later
    Button reviewOrderButton;
    Button placeOrderButton;

    //Create objects for each CheckBox and variables for whether they are checked or not.
    // Also creates an object for the name field and a variable for the text within it. These are to be used later.
    CheckBox whippedCreamCheckBox;
    boolean whippedCreamChecked;

    CheckBox marshmallowsCheckBox;
    boolean marshmallowsChecked;

    CheckBox chocolateCheckBox;
    boolean chocolateChecked;

    EditText editText;
    String nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise variables for each CheckBox and variables for whether they are checked or not, and the name field object+variable
        whippedCreamCheckBox = (CheckBox) findViewById(R.id.whipped_cream_checkbox);
        whippedCreamChecked = whippedCreamCheckBox.isChecked();

        marshmallowsCheckBox = (CheckBox) findViewById(R.id.marshmallows_checkbox);
        marshmallowsChecked = marshmallowsCheckBox.isChecked();

        chocolateCheckBox = (CheckBox) findViewById(R.id.chocolate_checkbox);
        chocolateChecked = chocolateCheckBox.isChecked();

        editText = (EditText) findViewById(R.id.name);
        nameEditText = editText.getText().toString();


        //Objects for both buttons
        reviewOrderButton = (Button) findViewById(R.id.review_order_button);
        placeOrderButton = (Button) findViewById(R.id.place_order_button);

        //Sets up both buttons; place order button becomes visible only after the review order button is pressed
        reviewOrderButton.setVisibility(View.VISIBLE);
        placeOrderButton.setVisibility(View.GONE);

        //Adds the current Drink of the Day to the message, then displays that in the XML
        drinkOfTheDayText = drinkOfTheDayText + drinks[drinkOfTheDayNum];
        displayDrink(drinkOfTheDayText);
        //Applies the discount of 20% to the Drink of the Day
        drinkPrices[drinkOfTheDayNum] = drinkPrices[drinkOfTheDayNum] * 0.8;

        //Sets up the spinner (drop down menu) for selecting drinks
        final Spinner spinner = (Spinner) findViewById(R.id.drink_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, drinks);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        //Sets the position of the selected item to a variable, then assigns the current drink and displays the appropriate toppings
                        int position = spinner.getSelectedItemPosition();
                        currentDrink = drinks[position];
                        displayToppings();
                        //Sets the correct price for the selected drink
                        price = drinkPrices[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                }
        );
    }

    //This method is called when the order button is clicked.
    public void submitOrder(View view) {
        //Updates the checkbox variables, ensuring the values for the toppings are accurate+up to date
        whippedCreamChecked = whippedCreamCheckBox.isChecked();
        marshmallowsChecked = marshmallowsCheckBox.isChecked();
        chocolateChecked = chocolateCheckBox.isChecked();
        nameEditText = editText.getText().toString();

        //Formats the price in so that it will display with 2 decimal points
        String result = String.format("%.2f", calculatePrice(whippedCreamChecked, marshmallowsChecked, chocolateChecked));

        if (quantity > 0 && quantity <= 100) {
            //Displays order summary and sets the place order button to visible
            placeOrderButton.setVisibility(View.VISIBLE);
            displayMessage(createOrderSummary(nameEditText, result, whippedCreamChecked, marshmallowsChecked, chocolateChecked));
        } else {
            String priceMessage = "There was an error with your order. Please try again.";
            displayMessage(priceMessage);
        }
    }

    //Method to place the order and launch the email intent
    public void placeOrder(View v){
        //Updates the checkbox variables, ensuring the values for the toppings are accurate+up to date
        whippedCreamChecked = whippedCreamCheckBox.isChecked();
        marshmallowsChecked = marshmallowsCheckBox.isChecked();
        chocolateChecked = chocolateCheckBox.isChecked();
        nameEditText = editText.getText().toString();

        //Formats the price in so that it will display with 2 decimal points
        String result = String.format("%.2f", calculatePrice(whippedCreamChecked, marshmallowsChecked, chocolateChecked));

        //Sets up an intent to output the order into an email.
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "JustJava Order for "+nameEditText);
        intent.putExtra(Intent.EXTRA_TEXT, createOrderSummary(nameEditText, result, whippedCreamChecked, marshmallowsChecked, chocolateChecked));
        Log.i("Summary", intent.getStringExtra(intent.EXTRA_TEXT));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //Displays the correct number of toppings depending on the selected drink, and resets each CheckBox
    private void displayToppings() {
        //Sets up variables for each topping CheckBox and the TextView
        CheckBox whippedCreamCheckBox = (CheckBox) findViewById(R.id.whipped_cream_checkbox);
        CheckBox marshmallowsCheckBox = (CheckBox) findViewById(R.id.marshmallows_checkbox);
        CheckBox chocolateCheckBox = (CheckBox) findViewById(R.id.chocolate_checkbox);
        TextView toppingsTextView = (TextView) findViewById(R.id.toppings_text_view);

        if (currentDrink == "Tea") {
            whippedCreamCheckBox.setChecked(false);
            marshmallowsCheckBox.setChecked(false);
            chocolateCheckBox.setChecked(false);
            whippedCreamCheckBox.setVisibility(View.GONE);
            marshmallowsCheckBox.setVisibility(View.GONE);
            chocolateCheckBox.setVisibility(View.GONE);
            toppingsTextView.setVisibility(View.GONE);
        } else if (currentDrink == "Americano" || currentDrink == "Black Coffee" || currentDrink == "Cappuccino") {
            whippedCreamCheckBox.setChecked(false);
            marshmallowsCheckBox.setChecked(false);
            chocolateCheckBox.setChecked(false);
            whippedCreamCheckBox.setVisibility(View.GONE);
            marshmallowsCheckBox.setVisibility(View.GONE);
            chocolateCheckBox.setVisibility(View.VISIBLE);
            toppingsTextView.setVisibility(View.VISIBLE);
        } else if (currentDrink == "Frappuccino" || currentDrink == "Latte" || currentDrink == "Mocha") {
            whippedCreamCheckBox.setChecked(false);
            marshmallowsCheckBox.setChecked(false);
            chocolateCheckBox.setChecked(false);
            whippedCreamCheckBox.setVisibility(View.VISIBLE);
            marshmallowsCheckBox.setVisibility(View.GONE);
            chocolateCheckBox.setVisibility(View.VISIBLE);
            toppingsTextView.setVisibility(View.VISIBLE);
        } else {
            whippedCreamCheckBox.setChecked(false);
            marshmallowsCheckBox.setChecked(false);
            chocolateCheckBox.setChecked(false);
            whippedCreamCheckBox.setVisibility(View.VISIBLE);
            marshmallowsCheckBox.setVisibility(View.VISIBLE);
            chocolateCheckBox.setVisibility(View.VISIBLE);
            toppingsTextView.setVisibility(View.VISIBLE);
        }
    }

    //This method displays the given quantity value on the screen
    private void displayQuantity(int numberOfCoffees) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText(Integer.toString(numberOfCoffees));
    }

    ///This method displays the given text on the screen
    private void displayMessage(String message) {
        TextView orderSummaryTextView = (TextView) findViewById(R.id.price_text_view);
        orderSummaryTextView.setText(message);
    }

    //This method displays the Drink of the Day on the screen
    private void displayDrink(String message) {
        TextView priceTextView = (TextView) findViewById(R.id.drink_of_the_day);
        priceTextView.setText(message);
    }

    //This method adds 1 to the quantity of drinks
    public void increment(View view) {
        if (quantity < 100) {
            quantity += 1;
        } else {
            Toast.makeText(getApplicationContext(), "You cannot order more than 100 drinks!", Toast.LENGTH_SHORT).show();
        }
        displayQuantity(quantity);
    }

    //This method subtracts 1 to the quantity of drinks
    public void decrement(View view) {
        if (quantity > 1) {
            quantity -= 1;
        } else {
            Toast.makeText(getApplicationContext(), "You cannot have less than 1 drinks!", Toast.LENGTH_SHORT).show();
        }
        displayQuantity(quantity);
    }

    //Calculates the price of the order
    private double calculatePrice(boolean creamChecked, boolean marshmallowsChecked, boolean chocolateChecked) {
        double cream = 0;
        double marshmallows = 0;
        double chocolate = 0;

        if (creamChecked == true) {
            cream = 0.5;
        } else {
            cream = 0;
        }

        if (marshmallowsChecked == true) {
            marshmallows = 0.8;
        } else {
            marshmallows = 0;
        }

        if (chocolateChecked == true) {
            chocolate = 0.3;
        } else {
            chocolate = 0;
        }

        return ((quantity) * (price + cream + marshmallows + chocolate));
    }

    //This method creates the order summary for the user
    private String createOrderSummary(String name, String cost, boolean whippedCreamChecked, boolean marshmallowsChecked, boolean chocolateChecked) {
        //Creates a variable for the message; this variable will be added to in stages later on
        String message = "Name: " + name;
        //Adds the current drink to the order summary variable, then adds the correct message depending on which toppings the user has selected
        message += "\nDrink: " + currentDrink;
        if (whippedCreamChecked == true && marshmallowsChecked == true && chocolateChecked == true) {
            message += " with whipped cream, marshmallows and chocolate.";
        } else if (whippedCreamChecked == true && marshmallowsChecked == true && chocolateChecked == false) {
            message += " with whipped cream and marshmallows.";
        } else if (whippedCreamChecked == true && marshmallowsChecked == false && chocolateChecked == true) {
            message += " with whipped cream and chocolate.";
        } else if (whippedCreamChecked == true && marshmallowsChecked == false && chocolateChecked == false) {
            message += " with whipped cream.";
        } else if (whippedCreamChecked == false && marshmallowsChecked == true && chocolateChecked == true) {
            message += " with marshmallows and chocolate.";
        } else if (whippedCreamChecked == false && marshmallowsChecked == true && chocolateChecked == false) {
            message += " with marshmallows.";
        } else if (whippedCreamChecked == false && marshmallowsChecked == false && chocolateChecked == true) {
            message += " with chocolate.";
        } else {
            message += ".";
        }
        //Adds the remaining information to the order summary, then returns it as a String
        message += "\nQuantity: " + quantity;
        message += "\nTotal: £" + cost;
        message += "\nThank you!";
        return message;
    }
}