import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaRefactorer {
    public static void main(String[] args) {
        // Шляхи до файлів (можна змінити на власні)
        String inputPath = "InputFile.java";
        String outputPath = "OutputFile.java";

        try {
            // 1. Читаємо весь вміст файлу в рядок
            String content = Files.readString(Paths.get(inputPath));

            // 2. Спочатку обробляємо коментарі /* ... */
            // Використовуємо кастомну функцію для коректної обробки багаторядковості
            content = convertBlockCommentsToSingle(content);

            // 3. Замінюємо 'public' на 'private'
            // Використовуємо \b, щоб замінити саме слово, а не частини інших слів
            content = content.replaceAll("\\bpublic\\b", "private");

            // 4. Записуємо результат у новий файл
            Files.writeString(Paths.get(outputPath), content);

            System.out.println("Обробка завершена успішно. Результат у файлі: " + outputPath);

        } catch (IOException e) {
            System.err.println("Помилка при роботі з файлом: " + e.getMessage());
        }
    }

    /**
     * Метод для перетворення блочних коментарів /* ... *\/ у рядкові //
     */
    private static String convertBlockCommentsToSingle(String content) {
        // Регулярний вираз для знаходження тексту між /* та */
        Pattern pattern = Pattern.compile("/\\*(.*?)\\*/", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            sb.append(content, lastEnd, matcher.start());

            String commentBody = matcher.group(1);
            // Розбиваємо на рядки
            String[] lines = commentBody.split("\\R");

            for (String line : lines) {
                // Використовуємо .strip() замість .stripHorizontal()
                // Це видалить зайві пробіли з обох боків
                String cleanLine = line.strip();
                if (!cleanLine.isEmpty()) {
                    sb.append("// ").append(cleanLine).append(System.lineSeparator());
                }
            }

            lastEnd = matcher.end();
        }
        sb.append(content.substring(lastEnd));
        return sb.toString();
    }
}