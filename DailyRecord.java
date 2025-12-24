import java.time.LocalDate;
import java.util.ArrayList;

public class DailyRecord {
    private LocalDate date;
    private ArrayList<DailyTask> tasks;

    public DailyRecord(LocalDate date) {
        this.date = date;
        this.tasks = new ArrayList<DailyTask>();
    }

    public LocalDate getDate() {
        return date;
    }

    public ArrayList<DailyTask> getTasks() {
        return tasks;
    }

    public void add(DailyTask task) {
        tasks.add(task);
    }

    public ArrayList<String> getTaskNames() {
        ArrayList<String> names = new ArrayList<>();
        for (DailyTask task: tasks) {
            names.add(task.getName());
        }
        return names;    
    }
}
