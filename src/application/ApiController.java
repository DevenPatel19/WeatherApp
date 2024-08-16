package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ApiController {

    private final String API_KEY = "YOUR_OWN_API_KEY";
    private final String CURRENT_ENDPOINT = "http://api.weatherstack.com/current";
    private final String FORECAST_ENDPOINT = "http://api.weatherstack.com/forecast";

    /**
     * Fetches current weather data from the WeatherStack API for a given city and
     * unit.
     * 
     * @param city The city for which to fetch the weather data.
     * @param unit The unit of measurement ("m" for Celsius, "f" for Fahrenheit).
     * @return A WeatherData object containing the fetched data, or null if an error
     *         occurs.
     */
    public WeatherData fetchWeatherData(String city, String unit) {
        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");
            String urlString = CURRENT_ENDPOINT + "?access_key=" + API_KEY + "&query=" + encodedCity + "&units=" + unit;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());

            // System.out.println("Current Weather Response: " + response.toString()); //
            // Debugging

            if (!jsonResponse.containsKey("current")) {
                System.out.println("Error: 'current' key not found in the API response.");
                return null;
            }

            JSONObject current = (JSONObject) jsonResponse.get("current");

            // Extracting weather data
            String locationName = (String) ((JSONObject) jsonResponse.get("location")).get("name");
            String weatherDescriptions = ((JSONArray) current.get("weather_descriptions")).get(0).toString();
            Double temperature = ((Number) current.get("temperature")).doubleValue();
            Long humidity = ((Number) current.get("humidity")).longValue();
            String weatherIcons = ((JSONArray) current.get("weather_icons")).get(0).toString();
            String isDay = (String) current.get("is_day");
            String localtime = (String) ((JSONObject) jsonResponse.get("location")).get("localtime");

            return new WeatherData(locationName, weatherDescriptions, temperature, humidity, weatherIcons, isDay,
                    localtime);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches forecast data from the WeatherStack API for a given city and unit.
     * 
     * @param city The city for which to fetch the forecast data.
     * @param unit The unit of measurement ("m" for Celsius, "f" for Fahrenheit).
     * @return A Map of dates to ForecastData objects, or null if an error occurs.
     */
    public Map<String, ForecastData> fetchForecastData(String city, String unit) {
        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");
            String urlString = FORECAST_ENDPOINT + "?access_key=" + API_KEY + "&query=" + encodedCity + "&units="
                    + unit;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());

            // System.out.println("Forecast Response: " + response.toString()); // Debugging

            if (!jsonResponse.containsKey("forecast")) {
                System.out.println("Error: 'forecast' key not found in the API response.");
                return null;
            }

            JSONObject forecast = (JSONObject) jsonResponse.get("forecast");

            Map<String, ForecastData> forecastDataMap = new HashMap<>();
            for (Object key : forecast.keySet()) {
                JSONObject dayForecast = (JSONObject) forecast.get(key);
                JSONObject astro = (JSONObject) dayForecast.get("astro");
                ForecastData data = new ForecastData(

                        String.valueOf(((Number) dayForecast.get("avgtemp")).doubleValue()),
                        String.valueOf(((Number) dayForecast.get("maxtemp")).doubleValue()),
                        String.valueOf(((Number) dayForecast.get("mintemp")).doubleValue())

                );
                forecastDataMap.put((String) dayForecast.get("date"), data);
            }

            return forecastDataMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Inner classes for WeatherData and ForecastData
    public static class WeatherData {
        private String locationName;
        private String description;
        private Double temperature;
        private Long humidity;
        private String weatherIcons;
        private String isDay;
        private String localtime;

        public WeatherData(String locationName, String description, Double temperature, Long humidity,
                String weatherIcons, String isDay, String localtime) {
            this.locationName = locationName;
            this.description = description;
            this.temperature = temperature;
            this.humidity = humidity;
            this.weatherIcons = weatherIcons;
            this.isDay = isDay;
            this.localtime = localtime;
        }

        public String getLocationName() {
            return locationName;
        }

        public String getDescription() {
            return description;
        }

        public Double getTemperature() {
            return temperature;
        }

        public Long getHumidity() {
            return humidity;
        }

        public String getWeatherIcons() {
            return weatherIcons;
        }

        public boolean getIsDay() {
            return isDay.equalsIgnoreCase("yes");
        }

        public String getLocalTime() {
            return localtime;
        }
    }

    public static class ForecastData {
        private String avgTemperature;
        private String maxTemperature;
        private String minTemperature;

        // Constructor
        public ForecastData(String avgTemperature, String maxTemperature, String minTemperature) {
            this.avgTemperature = avgTemperature;
            this.maxTemperature = maxTemperature;
            this.minTemperature = minTemperature;

        }

        // Getters and setters

        public String getAvgTemperature() {
            return avgTemperature;
        }

        public void setAvgTemperature(String avgTemperature) {
            this.avgTemperature = avgTemperature;
        }

        public String getMaxTemperature() {
            return maxTemperature;
        }

        public void setMaxTemperature(String maxTemperature) {
            this.maxTemperature = maxTemperature;
        }

        public String getMinTemperature() {
            return minTemperature;
        }

        public void setMinTemperature(String minTemperature) {
            this.minTemperature = minTemperature;
        }

    }
}
