import java.io.IOException;
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
    private Map<String, Integer> weeklyTotalsByTask;

    public TimeTracker() throws IOException {
        weeklyRecords = new ArrayList<DailyRecord>();
        readFile(FILE_NAME);
        // if (weeklyRecords.isEmpty || getCurrentRecord().getDate().isBefore(LocalDate.now())) add(DailyRecord())
        if (weeklyRecords.isEmpty()) {
            taskNames = new ArrayList<String>();
            weeklyTotalsByTask = new HashMap<>();
        } // else?
        taskNames = getCurrentRecord().getTaskNames();
        weeklyTotalsByTask = getWeeklyTotals();
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
            int totalMinutes = weeklyTotalsByTask.get(name);
            System.out.printf("%s: %s\n", name, minutesToHoursDoubleString(totalMinutes));
        }
        System.out.println("\n");
    }

    public void addTime(int minutes, int index) {
        // assuming correct index based on menu
        getCurrentRecord().getTasks().get(index).addMinutes(minutes);
        weeklyTotalsByTask = getWeeklyTotals();
    }

    public void addNewTask(String taskName) {
        getCurrentRecord().add(new DailyTask(LocalDate.now(), taskName, 0));
        taskNames.add(taskName);
        weeklyTotalsByTask = getWeeklyTotals();
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

                weeklyRecords.add(currentRecord);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }            
        }
        

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
