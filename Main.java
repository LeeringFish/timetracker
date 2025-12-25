import java.io.IOException;

public class Main {
    
    public static void main(String[] args) throws IOException {
        TimeTracker tracker = new TimeTracker();
        tracker.printToday();
        tracker.printWeek();
        tracker.printMainMenu();


        //tracker.addTime(30, 0);
        //tracker.addNewTask("Figure this out");
        //tracker.addTime(30, 0);
        //tracker.addTime(30, 4);

        //tracker.printToday();
        //tracker.printWeek();
        

    }

}