import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TimeTracker {
    private final String FILE_NAME = "time_log.txt";
    
    private ArrayList<DailyRecord> weeklyRecords;
    private ArrayList<String> taskNames;
    private ArrayList<String> oldRecords;
    private Map<String, Integer> weeklyTotalsByTask;

    public TimeTracker() throws IOException {
        weeklyRecords = new ArrayList<DailyRecord>();
        oldRecords = new ArrayList<String>();
        readFile(FILE_NAME);
        if (weeklyRecords.isEmpty()) {
            taskNames = new ArrayList<String>();
            weeklyTotalsByTask = new HashMap<>();
        } else {
            updateTotalsAndTasks();
        }
    }

    public DailyRecord getCurrentRecord() {
        return this.weeklyRecords.get(this.weeklyRecords.size() - 1);
    }

    public void printToday() {
        System.out.println();
        System.out.println(LocalDate.now());
        System.out.println("--------");
        for (DailyTask task : getCurrentRecord().getTasks()) {
            System.out.printf("%s: %s\n", task.getName(), minutesToHoursString(task.getMinutes()));
        }
        System.out.println("\n");
    }

    public void printWeek() {
        System.out.println("Past Seven Days");
        System.out.println("---------------");
        for (String name : taskNames) {
            int totalMinutes = weeklyTotalsByTask.getOrDefault(name, 0);
            System.out.printf("%s: %s\n", name, minutesToHoursDoubleString(totalMinutes));
        }
        System.out.println("\n");
    }

    public void printMainMenu() {
        String[] options = {"Log Time", "Add Daily Task", "Remove Daily Task", "Quit"};
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s\n", (i + 1), options[i]);
        }
        System.out.println();
    }

    public void printAddRemoveMenu() {
        int i = 0;
        while (i < taskNames.size()) {
            System.out.printf("%d. %s\n", (i + 1), taskNames.get(i));
            i++;
        }
        i++;
        System.out.println(i + ". Main Menu\n");
    }

    public void addTime(int minutes, int index) {
        getCurrentRecord().getTasks().get(index).addMinutes(minutes);
        weeklyTotalsByTask = getWeeklyTotals();
    }

    public void addNewTask(String taskName) {
        LocalDate today = LocalDate.now();
        if (recordsAreEmpty()) {
            weeklyRecords.add(new DailyRecord(today));
        }
        getCurrentRecord().add(new DailyTask(today, taskName, 0));
        taskNames.add(taskName);
        weeklyTotalsByTask = getWeeklyTotals();
    }

    public String removeTask(int index) {
        String taskName = getCurrentRecord().getTasks().get(index).getName();
        getCurrentRecord().getTasks().remove(index);
        taskNames = getCurrentRecord().getTaskNames();
        weeklyTotalsByTask = getWeeklyTotals();
        return taskName;
    }

    public boolean recordsAreEmpty() {
        return weeklyRecords.isEmpty();
    }

    public boolean isInvalidTaskIndex(String indexString) {
        if (indexString == null) {
            return true;
        }
        
        if (!indexString.trim().matches("\\d+")) {
            return true;
        }
        
        int index = Integer.parseInt(indexString);
        return index < 1 || index > taskNames.size() + 1;
    }

    public void writeToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(FILE_NAME, "UTF-8")) {
            for (String line : oldRecords) {
                writer.println(line);
            }
            for (DailyRecord record : weeklyRecords) {
                for (DailyTask task : record.getTasks()) {
                    String line = String.format("%s;%s;%d", task.getDate(), task.getName(), task.getMinutes());
                    writer.println(line);
                }
            }
        }
    }

    private void readFile(String fileName) throws IOException {
        Path path = Path.of(fileName);
        if (Files.notExists(path)) {
            Files.createFile(path);
        } else {
            try (Scanner fileReader = new Scanner(path)) {
                String line;
                String[] parts;
                LocalDate newDate, currentDate = null;
                DailyRecord currentRecord = null;

                while (fileReader.hasNextLine()) {
                    line = fileReader.nextLine();
                    parts = line.split(";");

                    newDate = LocalDate.parse(parts[0]);
                    if (dateNotInPastWeek(newDate)) {
                        oldRecords.add(line);
                        continue;
                    }

                    if (currentDate == null) {
                        currentRecord = new DailyRecord(newDate);
                        currentDate = newDate;
                    }

                    if (newDate.isAfter(currentDate)) {
                        weeklyRecords.add(currentRecord);
                        currentDate = newDate;
                        currentRecord = new DailyRecord(newDate);
                    }

                    currentRecord.add(new DailyTask(newDate, parts[1], Integer.parseInt(parts[2])));
                }

                if (currentRecord != null) {
                    weeklyRecords.add(currentRecord);
                }      

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }            
        }
    }

    private void addRecord(LocalDate date) {
        taskNames = getCurrentRecord().getTaskNames();
        DailyRecord newRecord = new DailyRecord(date);
        for (String taskName : taskNames) {
            newRecord.add(new DailyTask(date, taskName, 0));
        }
        weeklyRecords.add(newRecord);
    }

    private HashMap<String, Integer> getWeeklyTotals() {
        HashMap<String, Integer> totals = new HashMap<>();

        for (String name : taskNames) {
            int total = 0;
            for (DailyRecord day : weeklyRecords) {
                for (DailyTask task : day.getTasks()) {
                    if (name.equals(task.getName())) {
                        total += task.getMinutes();
                    }
                }
            }
            totals.put(name, total);
        }

        return totals;
    }

    private void updateTotalsAndTasks() {
        LocalDate today = LocalDate.now();
        if (getCurrentRecord().getDate().isBefore(today)) {
                addRecord(today);
            } else {
                taskNames = getCurrentRecord().getTaskNames();
            }

            weeklyTotalsByTask = getWeeklyTotals();
    }

    private static String minutesToHoursString(int mins) {
        int hours = mins / 60;
        int minutes = mins % 60;
        StringBuilder builder = new StringBuilder();

        if (hours == 1) {
            builder.append("1 hour, ");
        } else {
            builder.append(String.format("%d hours, ", hours));
        }

        builder.append(String.format("%d minute", minutes));
        if (minutes != 1) {
            builder.append("s");
        }

        return builder.toString();
    }

    private static String minutesToHoursDoubleString(int mins) {
        int hours = mins / 60;
        int minutes = mins % 60;
        double total = hours + ((double) minutes / 60);

        return String.format("%.1f hours", total);
    }

    private static boolean dateNotInPastWeek(LocalDate date) {
        LocalDate start = LocalDate.now().minusDays(6);
        return date.isBefore(start);
    }
}
