import java.io.IOException;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) throws IOException {
        TimeTracker tracker = new TimeTracker();

        if (tracker.recordsAreEmpty()) {
            System.out.println("\nThere are no tasks being tracked\n");
        } else {
            tracker.printToday();
            tracker.printWeek();
        }
        
        if (args.length == 0 || !"-p".equals(args[0])) {
            Scanner keyboard = new Scanner(System.in);
            runTracker(tracker, keyboard);
            keyboard.close();
        }
    }

    public static void runTracker(TimeTracker tracker, Scanner scan) throws IOException {
        String userInput;
        while (tracker.recordsAreEmpty()) {
            System.out.print("Would you like to add a new task (y/n)? ");
            userInput = scan.nextLine();
            if ("n".equalsIgnoreCase(userInput) || "no".equalsIgnoreCase(userInput)) {
                System.out.println();
                return;
            }

            if ("y".equalsIgnoreCase(userInput) || "yes".equalsIgnoreCase(userInput)) {
                System.out.print("Enter task to add: ");
                userInput = scan.nextLine();
                tracker.addNewTask(userInput);
                tracker.printToday();
                tracker.printWeek();
            }
        }

        boolean running = true;
        
        while (running) {
            tracker.printMainMenu();
            System.out.print("Enter Selection: ");
            userInput = scan.nextLine();

            switch (userInput) {
                case "1" -> logTime(tracker, scan);
                case "2" -> addDailyTask(tracker, scan);
                case "3" -> removeDailyTask(tracker, scan);
                case "4" -> {
                    tracker.writeToFile();
                    running = false;
                }
                default -> System.out.println("Invalid selection\n");
                
            }

            if (running) {
                tracker.printToday();
                tracker.printWeek();
            }
        }
    }

    public static void logTime(TimeTracker tracker, Scanner scan) {
        String userInput;
        String msg = "Enter task to log time for: ";
        String msg2 = "Enter number of minutes worked: ";
        int index, minutes;

        tracker.printAddRemoveMenu();
        System.out.print(msg);
        userInput = scan.nextLine().trim();

        while (tracker.isInvalidTaskIndex(userInput)) {
            System.out.println("Invalid selection\n");
            System.out.print(msg);
            userInput = scan.nextLine().trim();
        }

        index = Integer.parseInt(userInput) - 1;
        if (index == tracker.getCurrentRecord().getTaskNames().size()) {
            return;
        }

        System.out.print(msg2);
        userInput = scan.nextLine().trim();

        while (!userInput.matches("[1-9]\\d*")) {
            System.out.println("Must be a positive number\n");
            System.out.print(msg2);
            userInput = scan.nextLine();
        }

        minutes = Integer.parseInt(userInput);
        tracker.addTime(minutes, index);
    }

    public static void addDailyTask(TimeTracker tracker, Scanner scan) {
        System.out.print("Enter task to add: ");
        String userInput = scan.nextLine();
        tracker.addNewTask(userInput);
    }

    public static void removeDailyTask(TimeTracker tracker, Scanner scan) {
        String msg = "Enter task to remove: ";
        String userInput;

        tracker.printAddRemoveMenu();
        System.out.print(msg);
        userInput = scan.nextLine();

        while (tracker.isInvalidTaskIndex(userInput)) {
            System.out.println("Invalid selection\n");
            System.out.print(msg);
            userInput = scan.nextLine();
        }

        int index = Integer.parseInt(userInput) - 1;
        if (index == tracker.getCurrentRecord().getTaskNames().size()) {
            return;
        }

        System.out.print("Are you sure (y/n)? ");
        userInput = scan.nextLine();
        if ("y".equalsIgnoreCase(userInput) || "yes".equalsIgnoreCase(userInput)) {
            String removedTask = tracker.removeTask(index);
            System.out.printf("\n\"%s\" removed from tasks\n", removedTask);
        }   
    }

    public static void clearScreen() {
        try {
            var clearCommand = System.getProperty("os.name").contains("Windows")
                    ? new ProcessBuilder("cmd", "/c", "cls")
                    : new ProcessBuilder("clear");
            clearCommand.inheritIO().start().waitFor();
        }
        catch (IOException | InterruptedException e) {
            System.out.println("Exception in clearScreen() method");
        }
    }

}