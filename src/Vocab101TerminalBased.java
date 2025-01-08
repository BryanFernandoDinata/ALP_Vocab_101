import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Vocab101TerminalBased
{
    static Scanner s = new Scanner(System.in);
    static Random r = new Random();

    static String urlString = "https://api.api-ninjas.com/v1/dictionary?word=";
    final static String apiKey = "wduaakTTOQAnYqECqJlacg==5UKQTaX2IDiNeSZC"; 
    static JSONParser parser = new JSONParser();
    static JSONObject randomWordObject;

    static String[] vocabularies;
    static String[] vocabulariesDescriptionLoaded;
    static String[] vocabulariesDiscoveredLoaded;
    static String[] definitions = new String[99999];
    static String whatPlayerType;

    static int chosenMainMenuOption = 0;
    static char playAgain = 'Y';

    static int randomVocabIndex = -1;
    static int quizIndex = 0;

    // saving to dictionary 
    static int mistakesMade = 0;
    //quizz
    static ArrayList<String> discoveredVocabularies = new ArrayList<String>(); // vocabularies yang sudah di spell right
    static ArrayList<String> discoveredVocabulariesDescription = new ArrayList<String>();

    static ArrayList<String> vocabulariesThatIsTypedRight = new ArrayList<String>(); 
    static ArrayList<String> vocabulariesThatIsTypedRightDescription = new ArrayList<String>(); 
    static ArrayList<String> vocabulariesThatIsGuessedRight = new ArrayList<String>(); // vocabularies yang sudah di tebak dari definisinya bener saat quiz

    static String filePath = "discoveredVocabs.txt";

    static float currentTimeLimit = 5f;
    static float maxTimeLimit = 5f;
    
        
    public static void main(String[] args) throws ParseException, IOException
    {
        GetAvailableVocab();
        LoadVocabAndDescriptionWithoutPrinting();
        
        while (playAgain == 'Y')
        {
            do
            {
                PrintMainMenu();
                chosenMainMenuOption = s.nextInt();
            }

            while(chosenMainMenuOption <= 0 || chosenMainMenuOption > 3);
            switch (chosenMainMenuOption) 
                {
                    case 1:
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.println();
                        System.out.println("---------> Round " + (i+1) + " <---------");

                        PrintRandomizedVocab();
                        
                        do
                        {
                            System.out.print("Please type " + vocabularies[randomVocabIndex] + ": ");
                            CheckPlayerInput();
                        }
                        while(!whatPlayerType.equals(vocabularies[randomVocabIndex]));
                            
                        //PrintVocabDescription();
                            

                        if(i == 4)
                        {
                            Quiz();
                            do
                            {
                                System.out.print("Play again ? (Y/N) : ");
                                playAgain = s.next().charAt(0);
                                playAgain = Character.toUpperCase(playAgain);
                            }
                            while(playAgain != 'y' && playAgain != 'n' && playAgain != 'N' && playAgain != 'Y');
                            if(playAgain == 'N' || playAgain == 'n')
                            {
                                SaveToDictionary();
                            }
                        }
                    }
                        
                    break;
                case 2:
                    OpenDictionary();
                    break;
                case 3:
                    System.out.println(" Thank you :)");
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }
    public static void OpenDictionary()
    {
        LoadFromDictionary();
        
    }
    public static void CheckPlayerInput() throws ParseException, IOException
    {
        whatPlayerType = s.next() + s.nextLine();

        if(whatPlayerType.equals(vocabularies[randomVocabIndex]))
        {
            PrintVocabDescription();

            discoveredVocabularies.add(vocabularies[randomVocabIndex]); 
            vocabulariesThatIsTypedRight.add(vocabularies[randomVocabIndex]);
        }
    }
    public static void PrintMainMenu()
    {
        System.out.println();
        //System.out.println("--> Welcome to Vocab 101 <--");
        System.out.println();
        System.out.println(
        "██╗   ██╗ ██████╗  ██████╗ █████╗ ██████╗      ██╗ ██████╗  ██╗\r\n" + //
        "██║   ██║██╔═══██╗██╔════╝██╔══██╗██╔══██╗    ███║██╔═████╗███║\r\n" + //
        "██║   ██║██║   ██║██║     ███████║██████╔╝    ╚██║██║██╔██║╚██║\r\n" + //
        "╚██╗ ██╔╝██║   ██║██║     ██╔══██║██╔══██╗     ██║████╔╝██║ ██║\r\n" + //
        " ╚████╔╝ ╚██████╔╝╚██████╗██║  ██║██████╔╝     ██║╚██████╔╝ ██║\r\n" + //
        "  ╚═══╝   ╚═════╝  ╚═════╝╚═╝  ╚═╝╚═════╝      ╚═╝ ╚═════╝  ╚═╝");
        System.out.println(" 1. Play");
        System.out.println(" 2. Open Dictionary");
        System.out.println(" 3. Quit");
        System.out.print(" Chosen option : ");
    }
    public static void PrintRandomizedVocab()
    {
        System.out.println();
        if(vocabulariesDiscoveredLoaded != null)
        {       
            for(int i = 0; i < vocabulariesDiscoveredLoaded.length - 1; i++)
            {
                do
                {   
                    randomVocabIndex = r.nextInt(vocabularies.length);
                }while(vocabularies[randomVocabIndex].equalsIgnoreCase(vocabulariesDiscoveredLoaded[i]));
            }
        }else
        {
            randomVocabIndex = r.nextInt(vocabularies.length);
        }
        System.out.println("New Vocabulary : " + vocabularies[randomVocabIndex]);
    }
    public static void PrintVocabDescription() throws ParseException, IOException 
    {
        URL url = new URL(urlString + vocabularies[randomVocabIndex]);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Api-Key", apiKey);
        
        int responseCode = connection.getResponseCode();
        //System.out.println(responseCode);
        if (responseCode == 200) 
        { // HTTP OK
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) 
            {
                response.append(inputLine);
            }
            in.close();

            // Parse the response as JSON
            //JSONParser parser = new JSONParser();
            randomWordObject = (JSONObject) parser.parse(response.toString());
            String definition = (String) randomWordObject.get("definition"); // Extract only the "definition" field
            
            System.out.print("Definition of the word: "); //+ response.toString());
            //System.out.println(definition);
            if (definition != null && !definition.isEmpty()) 
            {
                if(definition.charAt(0) == '1')
                {
                    definitions = definition.split("\\d+\\.");
                    if (definitions.length > 1) 
                    {
                        //String[] firstDefinition = definitions[0].split(";");
                        definitions[1].replace("\n", "").replace("\r", "");
                        definitions[1].replace("\n1", "").replace("\r", "");
                        definitions[1].replace("\na", "").replace("\r", "");
                        System.out.println(definitions[1]);
                        discoveredVocabulariesDescription.add(definitions[1]);
                    } else 
                    {
                        System.out.println("No definitions available for this word");
                    }
                    vocabulariesThatIsTypedRightDescription.add(definitions[1]);
                }else
                {
                    definitions = definition.split(";");
                    if (definitions.length >= 0) 
                    {
                        //String[] firstDefinition = definitions[0].split(";");
                        definitions[0].replace("\n", "").replace("\r", "");
                        definitions[0].replace("\n1", "").replace("\r", "");
                        definitions[0].replace("\nA", "").replace("\r", "");
                        System.out.println(definitions[0]);
                        discoveredVocabulariesDescription.add(definitions[0]);
                        System.out.println("Added definitions for " + vocabularies[randomVocabIndex]);
                    } else 
                    {
                        System.out.println("No definitions available for this word");
                    }
                    vocabulariesThatIsTypedRightDescription.add(vocabularies[randomVocabIndex] + " has not been registered to the API");
                    discoveredVocabulariesDescription.add(vocabularies[randomVocabIndex] + "has not been registered to the API");
                    System.out.println("Added definitions for " + vocabularies[randomVocabIndex]);
                }
            }
            if(definition.equalsIgnoreCase(""))
            {
                System.out.println("No definitions available for this word");
            }
        } else 
        {
            System.out.println("GET request failed. Response Code: " + responseCode);
        }
    }
    public static void GetAvailableVocab()
    {
        try 
        {
            File myObj = new File("vocabList.txt");

            Scanner myReader = new Scanner(myObj);
            String data = "";

            while (myReader.hasNextLine()) 
            {
                data = myReader.nextLine();
                //System.out.println(data);
            }
            vocabularies = data.split(",");

            myReader.close();
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void SaveToDictionary()
    {
        discoveredVocabularies.add(vocabularies[randomVocabIndex]);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) 
        {
            for(int i = 0; i < discoveredVocabularies.size() - 1; i++)
            {
                if(i < (discoveredVocabularies.size() - 2))
                {
                    writer.write(discoveredVocabularies.get(i) + ",");//+
                }
                else
                {
                    writer.write(discoveredVocabularies.get(i));//+
                }
            }
            System.out.println("Data successfully written to " + filePath);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("discoveredVocabDescription.txt"))) 
        {
            for(int i = 0; i < discoveredVocabularies.size() - 1; i++)
            {
                if(i < (discoveredVocabularies.size() - 2))
                {
                    writer.write(discoveredVocabulariesDescription.get(i) + ":");
                }
                else
                {
                    writer.write(discoveredVocabulariesDescription.get(i));
                }
            }
            System.out.println("Data successfully written to " + "discoveredVocabDescription.txt");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    public static void LoadFromDictionary()
    {
        try 
        {
            File myObj = new File("discoveredVocabDescription.txt");
            File myObj2 = new File("discoveredVocabs.txt");

            Scanner myReader = new Scanner(myObj);
            Scanner myReader2 = new Scanner(myObj2);

            String data = "";
            String data2 = "";

            while (myReader.hasNextLine()) 
            {
                data = myReader.nextLine();
            }
            while (myReader2.hasNextLine()) 
            {
                data2 = myReader2.nextLine();    
            }

            vocabulariesDescriptionLoaded = data.split(":");
            vocabulariesDiscoveredLoaded = data2.split(",");
            myReader.close();
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println(" You haven't learned any vocabulary");
        }
        if(vocabulariesDescriptionLoaded != null)
        {
            for(int i = 0; i <= vocabulariesDescriptionLoaded.length - 1; i++)
            {
                System.out.println(" Vocab : " + vocabulariesDiscoveredLoaded[i]);
                System.out.println(" " + vocabulariesDescriptionLoaded[i]);
            }
        }   
    }
    public static void LoadVocabAndDescriptionWithoutPrinting()
    {
        try 
        {
            File myObj = new File("discoveredVocabDescription.txt");
            File myObj2 = new File("discoveredVocabs.txt");

            Scanner myReader = new Scanner(myObj);
            Scanner myReader2 = new Scanner(myObj2);

            String data = "";
            String data2 = "";

            while (myReader.hasNextLine()) 
            {
                data = myReader.nextLine();
                //System.out.println(data);
            }
            while (myReader2.hasNextLine()) 
            {
                data2 = myReader2.nextLine();    
            }

            vocabulariesDescriptionLoaded = data.split(":");
            vocabulariesDiscoveredLoaded = data2.split(",");

            for(int i = 0; i < vocabulariesDescriptionLoaded.length - 1; i++)
            {
                discoveredVocabularies.add(vocabulariesDiscoveredLoaded[i]);
                discoveredVocabulariesDescription.add(vocabulariesDescriptionLoaded[i]);
            }
            myReader.close();
        } 
        catch (FileNotFoundException e) 
        {
            
        }
    }
    public static void Quiz()
    {
        PrintQuizMenu();

        String theAns = "";

        for(int i = quizIndex; i < vocabulariesThatIsTypedRight.size(); i++)
        {
            do
            {
                System.out.print((i+1) + ". Definition : ");
                System.out.println(vocabulariesThatIsTypedRightDescription.get(i));
                System.out.println();

                System.out.println("What is the vocabulary that has that description?");
                System.out.print("Your answer : ");
                theAns = s.next() + s.nextLine();
            }while(!theAns.equalsIgnoreCase(vocabulariesThatIsTypedRight.get(i)));
            quizIndex++;
        }
    }
    public static void PrintQuizMenu()
    {
        System.out.println();

        System.out.println(
            " ██████╗ ██╗   ██╗██╗███████╗\r\n" + //
            "██╔═══██╗██║   ██║██║╚══███╔╝\r\n" + //
            "██║   ██║██║   ██║██║  ███╔╝ \r\n" + //
            "██║▄▄ ██║██║   ██║██║ ███╔╝  \r\n" + //
            "╚██████╔╝╚██████╔╝██║███████╗\r\n" + //
            " ╚══▀▀═╝  ╚═════╝ ╚═╝╚══════╝"
        );

        System.out.println();
    }
    public static void StartTimer()
    {
        Thread timerThread = new Thread(() -> 
        {
            try 
            {
                TimerFunction();
            } 
            catch (InterruptedException e) 
            {
                Thread.currentThread().interrupt();
                System.out.println("Timer interrupted");
            }
        });
        timerThread.start();
    }
    public static void TimerFunction() throws InterruptedException 
    {
        long startTime = System.currentTimeMillis();

        while (currentTimeLimit > 0 && !whatPlayerType.equals(vocabularies[randomVocabIndex])) 
        {
            TimeUnit.SECONDS.sleep(1);
            long timePassed = System.currentTimeMillis() - startTime;
            long secondsPassed = timePassed / 1000;

            if (secondsPassed == 60) 
            {
                secondsPassed = 0;
                startTime = System.currentTimeMillis();
            }

            if(!whatPlayerType.equals(vocabularies[randomVocabIndex]))
            {
                currentTimeLimit--;
                System.out.print("\r Please type " + vocabularies[randomVocabIndex] + "(" + currentTimeLimit + ") s : ");
                //System.out.print("\r Please type 'Mom' (" + currentTimeLimit + ") s : ");
            }
        }
    }
}
