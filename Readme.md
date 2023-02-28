# Currency-Exchange
## This program enables users to exchange currencies at the current exchange rate, sourced from the National Bank of Poland (NBP).

### Instructions for launching the application:<br/>

1. Download the Git-Hub repository clone.<br/>
2. Unzip the zip file on your computer.<br/>
3. Open the Currency-Exchange-main folder with Android Studio.<br/>
4. Launch the project using an emulator or a real device.<br/>

# Brief overview of the application
## Main screen of the application
![Main screen of the application.](https://i.imgur.com/Yx8zY0B.png)
### Located here are:<br/>

- An editable field labeled 'You send' where the user can input the amount of foreign currency to be exchanged and expects to see the calculated value in the 'They receive' field.
- An editable field labeled 'They receive' where the user can input the amount of PLN they want to exchange and expects to see the calculated value of how much foreign currency they need to send in the 'You send' field.
- A text field labeled 'Rate' showing the exchange rate used for the calculation.
- A text field labeled 'Effective date' showing the date of the last exchange rate update.
- A 'Charge' button to initiate the calculation process.
- A 'Clear' button to clear all editable fields for ease of user interaction with the program.<br/><br/>

## Currency selection and inputting data in EditText<br/>
![Selection and inputting](https://i.imgur.com/fYUPZLW.png)

- When the country flag icon is pressed in the 'You send' field, a dropdown list is implemented, allowing the user to select the desired currency for exchange.
- When any of the editable fields are pressed, a keyboard opens for inputting data. The keyboard is set to 'numberDecimal' to limit the user's ability to input letters and make the application more user-friendly. While letters would not interfere with the calculation, it is better to limit the user's access to unnecessary functionality if possible.
  
**Note**: *Currently, the program only supports three foreign currencies for exchange, but the architecture of this application is designed in such a way that when there is a need to expand the list of currencies, changes will only need to be made in two places in the application. These changes will not affect the rest of the application's functionality and will not require changing any code unrelated to the currency enumeration class.*
<br/><br/>

## Warning dialogs<br/>

![Warning dialogs](https://i.imgur.com/OC6nk8T.png)<br/>

If the user misuses the application and presses the Charge button, they will receive a warning dialog with an appropriate warning message and instructions.

### There are five types of warnings provided:
- No internet connection
- Both input fields are empty
- Both input fields are filled
- The "You send" field is not filled correctly
- The "They receive" field is not filled correctly
<br/><br/>

## Result of the program action<br/>

![Result](https://i.imgur.com/cLXrgnn.png)<br/>

If the user has entered all the required data properly and presses the "Charge" button, the calculated amount of money will be displayed in the field that the user left blank. 

Additionally, the "Rate" and "Effective Date" fields will show the corresponding information about the exchange rate and the date of currency update.
___

## Overview of the Application Architecture<br/>
> The MVVM pattern was utilized in the development of this program, with the absence of the Model layer due to the unnecessary storage of data within the application after closing. Full isolation between the View and ViewModel layers was implemented to ensure that the View had no knowledge of data management or computational processes.

## List of Classes in the Application and Their Purpose

- MainActivity - is the main activity of the Currency Exchange app, which observes the UI state of the app through the ExchangeViewModel and updates the UI accordingly. It also handles user interactions through various listeners and creates a popup menu to change the sender currency code. Additionally, it displays warning dialogs to the user if there are any errors in the UI state.
- ExchangeViewModel  - class is an Android ViewModel that handles user input events and business logic for a currency exchange app. It uses a MutableStateFlow to manage the UI state and communicates with an ExchangeApi to retrieve exchange rates for different currencies.
 - ScreenEvent - class is a sealed interface used to represent various events that can occur on a screen. It includes different types of events such as Charge, Clear, ChangeSenderCurrencyCode, ChangeSenderCurrencyValue, ChangeReceiverCurrencyValue, and HideError. These events are used to update the UI state of the screen and handle user interactions.
 - ViewModelFactory - class creates instances of ViewModel subclasses in an Android app using an Application instance and an InternetCheckerWrapper instance. It implements the ViewModelProvider.Factory interface and returns an instance of the requested ViewModel class with given parameters, throwing an exception if the class is unknown. The InternetCheckerWrapper parameter injects a wrapper around the Android system's ConnectivityManager to make the app more testable, allowing us to mock network connectivity checks in unit tests and test app behavior under various network conditions without physically manipulating settings.
 - InternetCheckerWrapper -  interface defines a method isConnected that takes an Application instance as a parameter and returns a Boolean value indicating whether the device is connected to the internet.
 - InternetChecker - class implements the InternetCheckerWrapper interface and provides an implementation of the isConnected method using the Android system's ConnectivityManager. It checks for network connectivity using the device's active network and returns a Boolean value indicating whether the device is connected to either Wi-Fi or cellular data. This class is used to wrap the Android system's ConnectivityManager and make it more easily testable by allowing for mocking of network connectivity in unit tests.
 - ExchangeService - class is responsible for communicating with an external API to fetch exchange rate data. It defines a Retrofit interface that specifies an HTTP GET request to the API endpoint, with the desired currency code as a path parameter. The response is expected to be in JSON format and is converted to a RateInfo data class using GsonConverterFactory. The ExchangeApi object creates a singleton instance of ExchangeService using the Retrofit builder and lazy initialization.
 - RateInfo - data class that represents the response from the NBP API when retrieving exchange rate information for a specific currency code. It contains information such as the currency code, currency name, rates for the given currency, and the table name.
 - ExchangeRate - data class that represents a single exchange rate for a specific currency on a given date. It contains information such as the effective date and the mid-point rate.
 - UiState - class represents the current state of the UI in an Android currency exchange application. It contains properties such as the sender currency code, sender and receiver currency values, currency exchange rate, effective date, and an optional error message. The class also defines enums for the sender currency code and possible UI error states.
 - ViewModelTest -  class is a unit test class for testing the behavior of the ExchangeViewModel class. It contains several test methods that simulate user events and check if the state of the ExchangeViewModel class is updated correctly. The class uses Mockito and PowerMockito libraries to mock dependencies and simulate behavior of the Android application context. The tests cover various scenarios such as updating sender/receiver currency codes and values, clearing values, charging, and error handling.
___
