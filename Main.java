import java.io.IOException;

public class Main {
    
    public static void main(String[] args) throws IOException {
        TimeTracker tracker = new TimeTracker();
        tracker.printToday();
        tracker.printWeek();

        tracker.addTime(30, 0);

        tracker.printToday();
        tracker.printWeek();
        

    }

}