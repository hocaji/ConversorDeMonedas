// Importa las clases necesarias de la librería Gson y de Java estándar
import com.google.gson.Gson;       // Para convertir JSON a objetos Java
import java.io.IOException;        // Para manejar excepciones de entrada/salida (ej. problemas de red)
import java.net.URI;               // Para Uniform Resource Identifier (URLs)
import java.net.http.HttpClient;   // Cliente HTTP estándar de Java (requiere Java 11+)
import java.net.http.HttpRequest;  // Para construir peticiones HTTP
import java.net.http.HttpResponse; // Para manejar respuestas HTTP
import java.util.Map;              // Para trabajar con el mapa de tasas de conversión
import java.util.Scanner;          // Para leer la entrada del usuario desde la consola

public class ConversorMonedasPuroJava {

    // --- CONFIGURACIÓN DE LA API ---
    // ¡¡¡MUY IMPORTANTE!!!
    // REEMPLAZA "TU_API_KEY_AQUI" con la clave API que obtuviste de ExchangeRate-API.com
    // Esta clave es solo para fines de demostración y puede tener limitaciones o expirar.
    private static final String API_KEY = "2090ecca26bd8ba5806f1a63"; // <-- ¡¡¡PON TU CLAVE AQUÍ!!!
    // URL base de la API. La versión 6 es la más reciente.
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/";
    // Moneda base para la llamada a la API. La capa gratuita de ExchangeRate-API
    // a menudo requiere que la moneda base para las llamadas sea USD.
    private static final String BASE_CURRENCY_FOR_API_CALL = "USD";

    // Instancias de HttpClient y Gson. Se inicializan una vez y se reutilizan para eficiencia.
    private static final HttpClient httpClient = HttpClient.newHttpClient(); // Cliente HTTP estándar de Java
    private static final Gson gson = new Gson();                               // Objeto Gson para manejar JSON

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Objeto para leer la entrada del usuario desde la consola

        System.out.println("--- Conversor de Monedas (MXN a USD, EUR, GBP) ---");
        System.out.println("Obteniendo tasas de cambio actuales desde la API...");

        try {
            // 1. Obtener las tasas de cambio desde la API
            // La API nos devolverá un mapa de tasas donde la clave es la moneda (ej. "MXN")
            // y el valor es cuántas unidades de esa moneda equivalen a 1 unidad de la
            // BASE_CURRENCY_FOR_API_CALL (en este caso, USD).
            Map<String, Double> conversionRates = getExchangeRates(BASE_CURRENCY_FOR_API_CALL);

            // Verificar si se pudieron obtener las tasas de cambio.
            if (conversionRates == null || conversionRates.isEmpty()) {
                System.out.println("ERROR: No se pudieron obtener las tasas de cambio. " +
                        "Verifica tu conexión a Internet, tu clave API, o el estado del servicio.");
                return; // Si no hay tasas, el programa termina.
            }

            // Asegurarse de que las tasas necesarias (MXN, EUR, GBP) estén presentes en la respuesta de la API.
            // Si la API no las proporciona, no podemos realizar la conversión.
            if (!conversionRates.containsKey("MXN") || !conversionRates.containsKey("EUR") || !conversionRates.containsKey("GBP")) {
                System.out.println("ERROR: Las tasas de cambio requeridas (MXN, EUR, GBP) no están disponibles con la moneda base " + BASE_CURRENCY_FOR_API_CALL + ".");
                System.out.println("Por favor, verifica la documentación de la API para tu plan.");
                return;
            }

            // Obtener las tasas específicas que usaremos para la conversión de MXN a otras monedas.
            // La API nos da: 1 USD = X MXN, 1 USD = Y EUR, 1 USD = Z GBP
            double usdToMxnRate = conversionRates.get("MXN"); // Valor de 1 USD en MXN
            double usdToEurRate = conversionRates.get("EUR"); // Valor de 1 USD en EUR
            double usdToGbpRate = conversionRates.get("GBP"); // Valor de 1 USD en GBP

            System.out.println("\nTasas de cambio obtenidas (Base: " + BASE_CURRENCY_FOR_API_CALL + "):");
            System.out.printf("1 USD = %.4f MXN\n", usdToMxnRate);
            System.out.printf("1 USD = %.4f EUR\n", usdToEurRate);
            System.out.printf("1 USD = %.4f GBP\n", usdToGbpRate);

            // 2. Pedir al usuario la cantidad en Pesos Mexicanos (MXN) a convertir.
            System.out.print("\nIngresa la cantidad en Pesos Mexicanos (MXN) a convertir: ");
            double cantidadMXN;

            // Bucle de validación para asegurar que el usuario ingrese un número válido.
            while (!scanner.hasNextDouble()) {
                System.out.println("Entrada inválida. Por favor, ingresa un número para la cantidad.");
                scanner.next(); // Consume la entrada incorrecta para evitar un bucle infinito
                System.out.print("Ingresa la cantidad en Pesos Mexicanos (MXN) a convertir: ");
            }
            cantidadMXN = scanner.nextDouble(); // Lee la cantidad numérica ingresada por el usuario

            // 3. Realizar las conversiones:
            // Primero convertimos MXN a USD.
            // Si 1 USD equivale a 'usdToMxnRate' MXN, entonces para convertir una cantidad de MXN a USD,
            // dividimos esa cantidad de MXN por 'usdToMxnRate'.
            double cantidadUSD = cantidadMXN / usdToMxnRate;

            // Luego, convertimos esa cantidad en USD a EUR y GBP.
            // Esto es directo ya que tenemos las tasas de USD a EUR y USD a GBP.
            double cantidadEUR = cantidadUSD * usdToEurRate;
            double cantidadGBP = cantidadUSD * usdToGbpRate;

            // 4. Mostrar los resultados de la conversión
            System.out.println("\n--- Resultados de la Conversión ---");
            System.out.printf("%.2f MXN equivale a:\n", cantidadMXN);
            System.out.printf("- %.2f USD (Dólares Estadounidenses)\n", cantidadUSD);
            System.out.printf("- %.2f EUR (Euros)\n", cantidadEUR);
            System.out.printf("- %.2f GBP (Libras Esterlinas)\n", cantidadGBP);

        } catch (IOException e) {
            // Captura y maneja errores relacionados con la red o la comunicación HTTP.
            System.err.println("Error de conexión o de red al intentar obtener las tasas de cambio: " + e.getMessage());
            System.err.println("Asegúrate de tener conexión a Internet y que tu clave API sea válida.");
            e.printStackTrace(); // Imprime el rastro completo de la excepción para depuración
        } catch (InterruptedException e) {
            // Captura y maneja interrupciones que puedan ocurrir durante la petición HTTP.
            System.err.println("La petición HTTP fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt(); // Re-interrumpe el hilo actual
            e.printStackTrace();
        } catch (Exception e) {
            // Captura cualquier otro tipo de error inesperado que pueda ocurrir durante la ejecución.
            System.err.println("Ha ocurrido un error inesperado durante la ejecución: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // El bloque 'finally' siempre se ejecuta, haya ocurrido una excepción o no.
            // Es crucial para cerrar recursos como el Scanner para evitar fugas de recursos.
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * Realiza una petición GET a la API de ExchangeRate-API para obtener las tasas de cambio actuales.
     * La moneda base para las llamadas a la API gratuita suele ser USD.
     *
     * @param baseCode El código de la moneda base desde la cual se obtendrán las tasas (ej. "USD").
     * @return Un mapa (Map<String, Double>) donde las claves son los códigos de moneda
     * (ej. "MXN") y los valores son sus respectivas tasas de conversión respecto
     * a la moneda base. Retorna 'null' si la petición no fue exitosa, o si la API reportó un error.
     * @throws IOException Si ocurre un problema de red, la URL es inválida, o la respuesta HTTP no es exitosa
     * en un nivel fundamental (ej. código de estado HTTP 4xx/5xx).
     * @throws InterruptedException Si la petición HTTP es interrumpida.
     */
    private static Map<String, Double> getExchangeRates(String baseCode) throws IOException, InterruptedException {
        // Construye la URL completa para la API de tasas de cambio.
        // Ejemplo: https://v6.exchangerate-api.com/v6/TU_API_KEY_AQUI/latest/USD
        String apiUrl = API_BASE_URL + API_KEY + "/latest/" + baseCode;

        // Construye el objeto HttpRequest para la petición HTTP.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl)) // Establece la URL a la que se hará la petición
                .GET() // Especifica el método GET (es el predeterminado, pero es buena práctica indicarlo)
                .build();    // Crea la instancia de HttpRequest

        // Ejecuta la petición y maneja la respuesta.
        // El método .send() devuelve un HttpResponse. BodyHandlers.ofString() especifica
        // que el cuerpo de la respuesta debe manejarse como una String.
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Verifica si la respuesta HTTP fue exitosa (código de estado en el rango 200-299).
        if (response.statusCode() != 200) {
            // Si la respuesta no es exitosa, lanza una IOException con detalles del error HTTP.
            throw new IOException("Petición a la API fallida: Código HTTP " + response.statusCode() + " - Mensaje: " + response.body());
        }

        // Obtiene el cuerpo de la respuesta como una cadena JSON.
        String jsonResponse = response.body();

        // Deserializa la cadena JSON en un objeto ExchangeRateResponse utilizando Gson.
        ExchangeRateResponse apiResponse = gson.fromJson(jsonResponse, ExchangeRateResponse.class);

        // Verifica el campo "result" dentro del payload JSON de la API. Esto es importante porque la API
        // puede devolver un código HTTP 200 (éxito) pero el "result" JSON podría ser "error" (ej. clave API inválida).
        if ("success".equals(apiResponse.getResult())) {
            return apiResponse.getConversionRates(); // Retorna el mapa de tasas de conversión
        } else {
            // Si el campo "result" no es "success", imprime el mensaje de error de la API y retorna null.
            System.err.println("La API reportó un error en la respuesta JSON: " + apiResponse.getResult());
            return null;
        }
    }
}