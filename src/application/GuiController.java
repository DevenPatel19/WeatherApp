package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class GuiController {

    @FXML
    private VBox body;
    
    @FXML
    private ChoiceBox<String> unit_dropdown;
    
    @FXML
    private Label city;
    
    @FXML
    private Label clock;

    @FXML
    private TextField search_box;

    @FXML
    private Button search_btn;
       
    @FXML
    private ListView<String> historyListView;

    @FXML
    private Label day_temp;

    @FXML
    private ImageView day_icon;

    @FXML
    private Label humiditylabel;

    @FXML
    private Label weather_conditionslabel;

    // Add VBox and Label references for 6-day forecast
    @FXML
    private VBox vboxDay1;
    
    @FXML
    private Label day1Label;    
    
    @FXML
    private Label tempDay1; 
    @FXML
    private Label maxTempDay1;
    @FXML
    private Label minTempDay1;

    private ApiController apiController = new ApiController();
    private searchHistory searchHistory = new searchHistory();

    @FXML
    private void initialize() {
        setUpChoiceBox();
        historyListView.setItems(FXCollections.observableArrayList());
    }

    private void setUpChoiceBox() {
        if (unit_dropdown != null) {
            unit_dropdown.setItems(FXCollections.observableArrayList("Celsius", "Fahrenheit"));
            unit_dropdown.setValue("Celsius");
        } else {
            // Handle the case where unit_dropdown is null (e.g., log an error)
            System.err.println("unit_dropdown is null");
        }
    }

    @FXML
    private void handleFetchWeatherData() {
        try {
            String cityName = search_box.getText().trim();
            String selectedUnit = unit_dropdown.getValue();
            String unitParam = selectedUnit.equals("Fahrenheit") ? "f" : "m";

            ApiController.WeatherData weatherData = apiController.fetchWeatherData(cityName, unitParam);
            Map<String, ApiController.ForecastData> forecastDataMap = apiController.fetchForecastData(cityName, unitParam);

            if (weatherData != null && forecastDataMap != null) {
                city.setText(weatherData.getLocationName());
                weather_conditionslabel.setText(weatherData.getDescription());
                day_temp.setText(weatherData.getTemperature() + "°");
                humiditylabel.setText(weatherData.getHumidity() + "%");
                updateWeatherIcon(weatherData.getWeatherIcons());
                updateBackgroundImage(weatherData.getIsDay());
                updateClock(weatherData.getLocalTime());
                updateWeatherForecast(forecastDataMap, selectedUnit);

                searchHistory.addEntry(cityName);
                updateHistoryListView();
            } else {
                weather_conditionslabel.setText("Error: No City Found.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while fetching weather data: " + e.getMessage());
        }
    }

    private void updateWeatherForecast(Map<String, ApiController.ForecastData> forecastDataMap, String unit) {
        // Ensure the map contains at least 6 days of data
        int dayCount = 0;
        for (Map.Entry<String, ApiController.ForecastData> entry : forecastDataMap.entrySet()) {
            if (dayCount >= 2) break;
            
            String date = entry.getKey();
            ApiController.ForecastData forecastData = entry.getValue();
            
            String temperatureUnit = unit.equals("Fahrenheit") ? "° F" : "° C";

            // Debugging: Print the forecast data before updating the UI
            System.out.println("Updating forecast for date: " + date);
            System.out.println("Avg Temp: " + forecastData.getAvgTemperature() + temperatureUnit);
            System.out.println("Max Temp: " + forecastData.getMaxTemperature() + temperatureUnit);
            System.out.println("Min Temp: " + forecastData.getMinTemperature() + temperatureUnit);

            switch (dayCount) {
                case 0:
                    day1Label.setText("TOM");
                    tempDay1.setText(forecastData.getAvgTemperature() + temperatureUnit);
                    maxTempDay1.setText(forecastData.getMaxTemperature() + temperatureUnit);
                    minTempDay1.setText(forecastData.getMinTemperature() + temperatureUnit);
                    break;
//                case 1:
//                    day2Label.setText(date);
//                    tempDay2.setText(forecastData.getAvgTemperature() + temperatureUnit);
//                    maxTempDay2.setText(forecastData.getMaxTemperature() + temperatureUnit);
//                    minTempDay2.setText(forecastData.getMinTemperature() + temperatureUnit);
//                    break;
//                case 2:
//                    day3Label.setText(date);
//                    tempDay3.setText(forecastData.getAvgTemperature() + temperatureUnit);
//                    maxTempDay3.setText(forecastData.getMaxTemperature() + temperatureUnit);
//                    minTempDay3.setText(forecastData.getMinTemperature() + temperatureUnit);
//                    break;
//                case 3:
//                    day4Label.setText(date);
//                    tempDay4.setText(forecastData.getAvgTemperature() + temperatureUnit);
//                    maxTempDay4.setText(forecastData.getMaxTemperature() + temperatureUnit);
//                    minTempDay4.setText(forecastData.getMinTemperature() + temperatureUnit);
//                    break;
//                case 4:
//                    day5Label.setText(date);
//                    tempDay5.setText(forecastData.getAvgTemperature() + temperatureUnit);
//                    maxTempDay5.setText(forecastData.getMaxTemperature() + temperatureUnit);
//                    minTempDay5.setText(forecastData.getMinTemperature() + temperatureUnit);
//                    break;
//                case 5:
//                    day6Label.setText(date);
//                    tempDay6.setText(forecastData.getAvgTemperature() + temperatureUnit);
//                    maxTempDay6.setText(forecastData.getMaxTemperature() + temperatureUnit);
//                    minTempDay6.setText(forecastData.getMinTemperature() + temperatureUnit);
//                    break;
            }
            dayCount++;
        }
    }

    private void updateHistoryListView() {
        ObservableList<String> historyItems = FXCollections.observableArrayList();
        List<searchHistory.HistoryEntry> historyEntries = searchHistory.getHistory();

        // Debugging: Print the current search history
        System.out.println("Current search history:");
        for (searchHistory.HistoryEntry entry : historyEntries) {
            System.out.println(entry);
            historyItems.add(entry.toString());
        }

        historyListView.setItems(historyItems);
    }

    private void updateBackgroundImage(boolean isDaytime) {
        if (isDaytime) {
            body.getStyleClass().remove("night_background");
            body.getStyleClass().add("day_background");
        } else {
            body.getStyleClass().remove("day_background");
            body.getStyleClass().add("night_background");
        }
    }

    private void updateClock(String localTimeString) {
        if (localTimeString == null || localTimeString.isEmpty()) {
            clock.setText("Time data not available");
            return;
        }
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalDateTime localDateTime = LocalDateTime.parse(localTimeString, inputFormatter);
            clock.setText(localDateTime.format(outputFormatter));
        } catch (Exception e) {
            clock.setText("Invalid time format");
        }
    }

    private void updateWeatherIcon(String weatherIconSrc) {
        day_icon.setImage(new javafx.scene.image.Image(weatherIconSrc));
    }
    
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
