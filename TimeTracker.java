import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeTracker {
    private ArrayList<DailyRecord> weeklyRecords;
    private ArrayList<String> taskNames;
    private Map<String, Integer> weeklyTotalsByTask;

    public TimeTracker() {
        weeklyRecords = new ArrayList<DailyRecord>();
        // readFile() into weeklyRecords
        taskNames = getCurrentRecord().getTaskNames();
        weeklyTotalsByTask = getWeeklyTotals();
    }

    public DailyRecord getCurrentRecord() {
        return this.weeklyRecords.get(this.weeklyRecords.size() - 1);
    }

    public void printToday() {
        System.out.println("\nThis is where the date goes");
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

        return total + " hours";
    }
}
