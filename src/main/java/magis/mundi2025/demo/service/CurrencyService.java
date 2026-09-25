package magis.mundi2025.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {

    public static final List<String> SUPPORTED = List.of("EUR", "RON", "USD", "GBP");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    private Map<String, BigDecimal> rates = Map.of("EUR", BigDecimal.ONE);
    private Instant fetchedAt = Instant.EPOCH;
    private String lastMessage = "Rates not loaded yet";

    // NOU: ruleaza o data, la pornirea aplicatiei, ca prima alegere de moneda
    // sa nu depinda de o descarcare facuta chiar in acel moment
    @PostConstruct
    public synchronized void init() {
        refreshIfStale();
    }

    public synchronized String resolve(String requested) {
        refreshIfStale();

        if (requested == null) {
            return "EUR";
        }
        if (!SUPPORTED.contains(requested)) {
            return "EUR";
        }
        if (!rates.containsKey(requested)) {
            return "EUR";
        }
        return requested;
    }

    public synchronized BigDecimal getRate(String currency) {
        refreshIfStale();

        if (!rates.containsKey(currency)) {
            return BigDecimal.ONE;
        }
        return rates.get(currency);
    }

    // NOU: motivul pentru care nu s-au putut incarca cursurile
    public synchronized String getLastMessage() {
        return lastMessage;
    }

    public synchronized String getDebugInfo() {
        fetchedAt = Instant.EPOCH;
        refreshIfStale();
        return "Cursuri: " + rates + "\nUltimul mesaj: " + lastMessage;
    }

    private void refreshIfStale() {
        if (Duration.between(fetchedAt, Instant.now()).toHours() < 6) {
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.frankfurter.dev/v1/latest?base=EUR&symbols=RON,USD,GBP"))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                lastMessage = "server status " + response.statusCode();
                System.out.println("CURSURI: " + lastMessage + " " + response.body());
                // reincercam peste 1 minut
                fetchedAt = Instant.now().minus(Duration.ofMinutes(359));
                return;
            }

            JsonNode ratesNode = mapper.readTree(response.body()).get("rates");

            Map<String, BigDecimal> updated = new HashMap<>();
            updated.put("EUR", BigDecimal.ONE);

            for (String code : List.of("RON", "USD", "GBP")) {
                JsonNode value = ratesNode.get(code);
                if (value != null) {
                    updated.put(code, value.decimalValue());
                }
            }

            rates = updated;
            fetchedAt = Instant.now();
            lastMessage = "OK";
            System.out.println("CURSURI: actualizate " + updated);

        } catch (Exception e) {
            lastMessage = e.toString();
            System.out.println("EROARE CURSURI: " + e);
            fetchedAt = Instant.now().minus(Duration.ofMinutes(359));
        }
    }
}