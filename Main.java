import java.io.IOException;

public class Main {
    
    public static void main(String[] args) throws IOException {
        TimeTracker tracker = new TimeTracker();
        tracker.printToday();
        tracker.printWeek();
    }

}