import java.util.Map; // Necessary for the Map<String, Double> type

// Class that represents the structure of the JSON response from the ExchangeRate-API.com
public class ExchangeRateResponse {
    // Private fields that match the keys in the JSON returned by the API
    private String result; // Indicates the API call result ("success" or "error")
    private String base_code; // The base currency code used to fetch rates (e.g., "USD")
    private Map<String, Double> conversion_rates; // A map where the key is the currency code (e.g., "MXN")
    // and the value is the conversion rate relative to the base currency.

    // Empty constructor: Gson REQUIRES this to create instances of this class
    // when deserializing JSON.
    public ExchangeRateResponse() {
    }

    // --- Getter Methods to access data ---
    // Gson uses these getters (and setters if they exist) to populate the object
    public String getResult() {
        return result;
    }

    public String getBaseCode() {
        return base_code;
    }

    public Map<String, Double> getConversionRates() {
        return conversion_rates;
    }

    // --- Setter Methods (Optional, useful if you plan to modify the data after deserializing) ---
    public void setResult(String result) {
        this.result = result;
    }

    public void setBaseCode(String base_code) {
        this.base_code = base_code;
    }

    public void setConversionRates(Map<String, Double> conversion_rates) {
        this.conversion_rates = conversion_rates;
    }
}