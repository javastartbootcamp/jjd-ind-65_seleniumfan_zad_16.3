package pl.javastart.task;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final int PATTERN_WITHOUT_TIME = 10;
    public static final String CUSTOM_DATATIME_PATTERN_PREFIX = "t";

    public static void main(String[] args) {
        Main main = new Main();
        main.run(new Scanner(System.in));
    }

    public LocalDateTime additionBasedOnSign(LocalDateTime dateTime, char letter, int amount) {
        return switch (letter) {
            case 'y' -> dateTime.plusYears(amount);
            case 'M' -> dateTime.plusMonths(amount);
            case 'd' -> dateTime.plusDays(amount);
            case 'h' -> dateTime.plusHours(amount);
            case 'm' -> dateTime.plusMinutes(amount);
            case 's' -> dateTime.plusSeconds(amount);
            default -> throw new IllegalStateException("Unexpected value: " + letter);
        };
    }

    public LocalDateTime substractionBasedOnSign(LocalDateTime dateTime, char letter, int amount) {
        return switch (letter) {
            case 'y' -> dateTime.minusYears(amount);
            case 'M' -> dateTime.minusMonths(amount);
            case 'd' -> dateTime.minusDays(amount);
            case 'h' -> dateTime.minusHours(amount);
            case 'm' -> dateTime.minusMinutes(amount);
            case 's' -> dateTime.minusSeconds(amount);
            default -> throw new IllegalStateException("Unexpected value: " + letter);
        };
    }

    public void run(Scanner scanner) {
        String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd.MM.yyyy HH:mm:ss"};

        System.out.println("Podaj datę:");
        String inputDate = scanner.nextLine();
        if (inputDate.startsWith(CUSTOM_DATATIME_PATTERN_PREFIX)) {
            LocalDateTime dateTime = getLocalDateTime(inputDate);
            displayDataTimeInDifferentTimesZones(dateTime);
        } else {
            if (inputDate.length() == PATTERN_WITHOUT_TIME) {
                inputDate += " 00:00:00";
            }
            for (String form : patterns) {
                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ofPattern(form));
                } catch (DateTimeParseException e) {
                    continue;
                }
                displayDataTimeInDifferentTimesZones(dateTime);
            }
        }
    }

    private LocalDateTime getLocalDateTime(String inputDate) {
        String[] signs = getFields(inputDate, "[^+-]");
        String[] fields = getFields(inputDate, "[+-]");

        LocalDateTime dateTime = LocalDateTime.now();
        for (int i = 0; i < signs.length; i++) {
            int number = Integer.parseInt(fields[i].substring(0, fields[i].length() - 1));
            char letter = fields[i].charAt(fields[i].length() - 1);
            switch (signs[i]) {
                case "+" -> dateTime = additionBasedOnSign(dateTime, letter, number);
                case "-" -> dateTime = substractionBasedOnSign(dateTime, letter, number);
                default -> throw new IllegalStateException("Unexpected value: " + signs[i]);
            }
        }
        return dateTime;
    }

    private static String[] getFields(String inputDate, String regex) {
        return Arrays.stream(inputDate.split(regex))
                .skip(1)
                .filter(field -> !field.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }

    private void displayDataTimeInDifferentTimesZones(LocalDateTime dateTime) {
        System.out.println("Czas lokalny: " + dateTime.format(FORMATTER));

        ZonedDateTime utcDateTime = dateTime.atZone(ZoneId.of("Europe/Warsaw")).withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println("UTC: " + utcDateTime.format(FORMATTER));

        getTimeIn("Europe/London", "Londyn", utcDateTime);
        getTimeIn("America/Los_Angeles", "Los Angeles", utcDateTime);
        getTimeIn("Australia/Sydney", "Sydney", utcDateTime);
    }

    private void getTimeIn(String zoneId, String city, ZonedDateTime utcDateTime) {
        ZoneId zone = ZoneId.of(zoneId);
        ZonedDateTime dateTime = utcDateTime.withZoneSameInstant(zone);
        System.out.println(city + ": " + dateTime.format(FORMATTER));
    }
}
