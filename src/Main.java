import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.google.gson.*;
import java.util.function.Predicate;
import java.text.SimpleDateFormat;



 class Composition {
    private String Title;
    private String Genre;
    private String Artist;
    private String Lyrics;
    private Date CreationDate;
    private double Duration;
    private String Format;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        this.Genre = genre;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        this.Artist = artist;
    }

    public String getLyrics() {
        return Lyrics;
    }

    public void setLyrics(String lyrics) {
        this.Lyrics = lyrics;
    }

    public void setCreationDate(Date creationDate) {
        this.CreationDate = creationDate;
    }


    public double getDuration() {
        return Duration;
    }

    public void setDuration(double duration) {
        this.Duration = duration;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        this.Format = format;
    }

    private Map<String, Double> Ratings = new HashMap<>();

    public void AddRating(String propertyName, double value) {
        Ratings.put(propertyName, value);
    }

    public double GetAverageRating() {
        if (Ratings.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (double value : Ratings.values()) {
            sum += value;
        }

        return sum / Ratings.size();
    }

    @Override
    public String toString() {
        return "Composition(Назва='" + Title + "', Жанр='" + Genre + "', Артист='" + Artist +
                "', Текст='" + Lyrics + "', Дата створення='" + CreationDate + "', Тривалість=" + Duration +
                ", Формат='" + Format + "', Рейтинг=" + GetAverageRating() + ")";
    }
}

class MyLinkedList<T> extends ArrayList<T> {
    public void RemoveIf(Predicate<T> predicate) {
        this.removeIf(predicate);
    }
}

public class Main {
    private static MyLinkedList<Composition> compositions = new MyLinkedList<>();
    private static final String FileName = "compositions.json";
    private static final String pattern = "Новий Рік|Різдво|Святкування Нового Року";


    public static void main(String[] args) {
        LoadCompositions();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Меню:");
            System.out.println("1. Додайти нову композицію");
            System.out.println("2. Переглянути список композицій");
            System.out.println("3. Знайти пісню, в якій згадується Новий рік");
            System.out.println("4. Відсортувати композиції за назвою");
            System.out.println("5. Відсортувати композиції за виконавцем");
            System.out.println("6. Відсортувати композиції за середнім рейтингом");
            System.out.println("7. Видалити композицію");
            System.out.println("8. Вийти з програми");
            System.out.print("Виберіть опцію: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    AddComposition();
                    break;

                case 2:
                    DisplayCompositions();
                    break;

                case 3:
                    SearchNR();
                    break;

                case 4:
                    compositions.sort((c1, c2) -> c1.getTitle().compareToIgnoreCase(c2.getTitle()));
                    System.out.println("Композиції відсортовані за назвою.");
                    break;

                case 5:
                    compositions.sort((c1, c2) -> c1.getArtist().compareToIgnoreCase(c2.getArtist()));
                    System.out.println("Композиції відсортовані за виконавцями.");
                    break;

                case 6:
                    compositions.sort((c1, c2) -> Double.compare(c2.GetAverageRating(), c1.GetAverageRating()));
                    System.out.println("Композиції відсортовані за середнім рейтингом (у порядку спадання).");
                    break;

                case 7:
                    RemoveComposition();
                    break;

                case 8:
                    SaveCompositions();
                    System.out.println("Програму припинено.");
                    return;

                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
                    break;
            }
        }
    }

    private static void RemoveComposition() {
        System.out.println("Введіть назву композиції, яку бажаєте видалити: ");
        Scanner scanner = new Scanner(System.in);
        String compositionTitle = scanner.nextLine();

        compositions.removeIf(composition -> composition.getTitle().equalsIgnoreCase(compositionTitle));

        System.out.println("Композиція з назвою '" + compositionTitle + "' видалена.");
    }

    private static void LoadCompositions() {
        File file = new File(FileName);

        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();
                Composition[] compositionArray = gson.fromJson(reader, Composition[].class);
                compositions.addAll(Arrays.asList(compositionArray));
                System.out.println("Дані успішно завантажено з compositions.json");
            } catch (IOException e) {
                System.out.println("Помилка під час завантаження даних із compositions.json: " + e.getMessage());
            }
        } else {
            System.out.println("compositions.json не знайдено.");
        }
    }

    private static void AddComposition() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть деталі композиції:");

        System.out.print("Назва: ");
        String title = scanner.nextLine();

        System.out.print("Жанр: ");
        String genre = scanner.nextLine();

        System.out.print("Артист: ");
        String artist = scanner.nextLine();

        System.out.print("Текст: ");
        String lyrics = scanner.nextLine();

        System.out.print("Дата створення (YYYY-MM-DD): ");
        Date creationDate = null;
        while (creationDate == null) {
            try {
                String dateString = scanner.nextLine();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                creationDate = dateFormat.parse(dateString);
            } catch (Exception e) {
                System.out.print("Недійсний формат дати. Будь ласка, використовуйте YYYY-MM-DD: ");
            }
        }

        System.out.print("Тривалість (у хвилинах): ");
        double duration = -1;
        while (duration < 0) {
            try {
                duration = scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.print("Недійсний формат тривалості. Введіть дійсний номер: ");
                scanner.nextLine(); // Очистка буфера
            }
        }

        scanner.nextLine(); // Очистка буфера

        System.out.print("Формат: ");
        String format = scanner.nextLine();

        Composition composition = new Composition();
        composition.setTitle(title);
        composition.setGenre(genre);
        composition.setArtist(artist);
        composition.setLyrics(lyrics);
        composition.setCreationDate(creationDate);
        composition.setDuration(duration);
        composition.setFormat(format);

        System.out.print("Введіть рейтинг: ");
        String propertyName = "rating";
        double rating = -1;
        while (rating < 0) {
            try {
                rating = scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.print("Недійсний формат оцінки. Введіть дійсний номер: ");
                scanner.nextLine(); // Очистка буфера
            }
        }

        composition.AddRating(propertyName, rating);

        compositions.add(composition);
        System.out.println("Композиція додана.");
    }

    private static void DisplayCompositions() {
        for (Composition composition : compositions) {
            System.out.println(composition);
        }
    }

    private static void SearchNR() {
        System.out.println("Список композицій, які містять слова, пов'язані із святкуванням Нового Року:");

        for (Composition composition : compositions) {
            String title = composition.getTitle();
            String lyrics = composition.getLyrics();

            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher titleMatcher = p.matcher(title);
            Matcher lyricsMatcher = p.matcher(lyrics);

            if (titleMatcher.find() || lyricsMatcher.find()) {
                System.out.println(composition);
            }
        }

        System.out.println("Шукати інші слова?");
        System.out.print("Введіть 'y'(так) або 'n' (ні): ");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        if (userInput.toLowerCase().equals("y")) {
            System.out.print("Введіть слово для пошуку : ");
            String searchPattern = scanner.nextLine();

            System.out.println("Список композицій, які містять слова, пов'язані із '" + searchPattern + "':");

            for (Composition composition : compositions) {
                String title = composition.getTitle();
                String lyrics = composition.getLyrics();

                Pattern p = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE);
                Matcher titleMatcher = p.matcher(title);
                Matcher lyricsMatcher = p.matcher(lyrics);

                if (titleMatcher.find() || lyricsMatcher.find()) {
                    System.out.println(composition);
                }
            }
        }
    }

    private static void SaveCompositions() {
        try (Writer writer = new FileWriter(FileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(compositions, writer);
        } catch (IOException e) {
            System.out.println("Помилка при збереженні даних у compositions.json: " + e.getMessage());
        }
    }
}

