package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Service
public class CurrencyService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Method that gets the rate from the cached file or the currency API
     * @param date - date of the payment
     * @param from - currency of the payment
     * @param to - desired currency
     * @return - 200 OK with the rate, 404 Not Found if currency codes are not found,
     *           400 Bad Request if the date, from or to are not valid dates/currency codes
     */
    public ResponseEntity<Double> getCurrency(String date, String from, String to) {
        if (from  == null || from.length() != 3 || to == null || to.length() != 3)
            return ResponseEntity.badRequest().build();
        try {
            dateFormat.parse(date);
        } catch (NullPointerException | ParseException e) {
            return ResponseEntity.badRequest().build();
        }
        File file = new File(String.valueOf(Path.of("server", "src",
                "main", "resources", "rates", date, from, to + ".txt")));
        try {
            if (file.equals(new File("null"))) throw new FileNotFoundException();
            return ResponseEntity.ok(new Scanner(file).nextDouble());
        } catch (FileNotFoundException | NoSuchElementException e) {
            try {
                InputStream in = new URI(String.format(
                        "https://api.fxratesapi.com/historical?date=%s&base=%s",
                        date, from)).toURL().openStream();
                var obj = objectMapper.readTree(in);
                double rate = obj.get("rates").get(to).asDouble();
                new File(String.valueOf(Path.of("server", "src",
                        "main", "resources", "rates", date, from))).mkdirs();
                PrintWriter pw = new PrintWriter(file);
                pw.print(rate);
                pw.flush();
                return ResponseEntity.ok(rate);
            } catch (URISyntaxException ex) {
                return ResponseEntity.internalServerError().build();
            } catch (IOException | NullPointerException ex) {
                return ResponseEntity.notFound().build();
            }
        }
    }
}
