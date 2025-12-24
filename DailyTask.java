import java.time.LocalDate;

public class DailyTask {
    private LocalDate date;
    private String name;
    private int minutes;

    public DailyTask(LocalDate date, String name, int minutes) {
        this.date = date;
        this.name = name;
        this.minutes = minutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public int getMinutes() {
        return minutes;
    }

    public void addMinutes(int minutesAdded) {
        minutes += minutesAdded;
    }
}
