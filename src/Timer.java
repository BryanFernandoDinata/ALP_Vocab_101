import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class Timer 
{
    static long timeLimit = 5;
    static Scanner s = new Scanner(System.in);
    static boolean isTimeUp = false; // Shared flag for stopping input and timer
    static boolean wordGuessed = false; // Shared flag for correct input

    public static void main(String[] args) throws InterruptedException 
    {
        PrintMenu();

        // Start the timer thread
        Thread timerThread = new Thread(() -> 
        {
            try 
            {
                startTimer();
            } 
            catch (InterruptedException e) 
            {
                Thread.currentThread().interrupt();
                System.out.println("Timer interrupted");
            }
        });
        timerThread.start();

        // Handle user input in the main thread
        while (!isTimeUp && !wordGuessed) 
        {
            String input = s.next();
            if ("Mom".equalsIgnoreCase(input)) 
            {
                wordGuessed = true; // Set flag to true if correct input is entered
                System.out.println("\nGood job! Keep going.");
            } 
            else 
            {
                System.out.println("\nIncorrect. Try again.");
            }
        }

        // Stop the timer thread gracefully
        if (wordGuessed) 
        {
            System.out.println("\nCongratulations! You stopped the timer.");
        } 
        else 
        {
            System.out.println("\nTime is up! Game over.");
        }

        timerThread.join(); // Wait for the timer thread to finish
    }

    public static void startTimer() throws InterruptedException 
    {
        long displayMinutes = 0;
        long startTime = System.currentTimeMillis();

        while (timeLimit > 0 && !wordGuessed) 
        {
            TimeUnit.SECONDS.sleep(1);
            long timePassed = System.currentTimeMillis() - startTime;
            long secondsPassed = timePassed / 1000;

            if (secondsPassed == 60) 
            {
                secondsPassed = 0;
                startTime = System.currentTimeMillis();
            }

            if ((secondsPassed % 60) == 0) 
            {
                displayMinutes++;
            }

            if(wordGuessed == false)
            {
                timeLimit--;
                System.out.print("\r Please type 'Mom' (" + timeLimit + ") s : ");
            }
        }
        isTimeUp = true; // Set the flag to stop user input
    }

    public static void PrintMenu() {
        System.out.println("--------------------------Play-------------------------------");
    }
}
