import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner; // Import the Scanner class to read text files

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Logic
{
    static Scanner s = new Scanner(System.in);
    static Random r = new Random();

    static String urlString = "https://api.api-ninjas.com/v1/dictionary?word=";
    final static String apiKey = "wduaakTTOQAnYqECqJlacg==5UKQTaX2IDiNeSZC"; // Replace with your actual API key
    static JSONParser parser = new JSONParser();
    static JSONObject randomWordObject;

    static String[] vocabularies;
    static String[] definitions = new String[99999];
    static String whatPlayerType;

    static int randomVocabIndex = -1;

    // saving to dictionary 
    static int mistakesMade = 0;
    //quizz
    static List<String> discoveredVocabularies = new ArrayList<String>(); // vocabularies yang sudah di spell right
    static List<String> vocabulariesThatIsGuessedRight = new ArrayList<String>(); // vocabularies yang sudah di tebak dari definisinya bener saat quiz
    static List<String> vocabulariesThatIsGuessedWrong = new ArrayList<String>();

    //Path path = Paths.get("output.txt"); // this is for deleting the file
    static String filePath = "output.txt";
    //int shouldDelete = 0;
    
    public static void CheckPlayerInput(String whatPlayerType) throws ParseException, IOException
    {
        if(whatPlayerType.equals(vocabularies[randomVocabIndex]))
        {
            PrintVocabDescription();
            discoveredVocabularies.add(vocabularies[randomVocabIndex]); 
        }
    }
    public static void PrintRandomizedVocab()
    {
        //System.out.println();
        randomVocabIndex = r.nextInt(vocabularies.length);
        Vocab101App.wordsToDiplay = vocabularies[randomVocabIndex];
        //System.out.println("New Vocabulary :" + vocabularies[randomVocabIndex]);
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
                definitions = definition.split("\\d+\\.");
                if (definitions.length > 1) 
                {
                    //String[] firstDefinition = definitions[0].split(";");
                    System.out.println(definitions[1]);
                    Vocab101App.definitionOfTheWord = definitions[1];
                } else 
                {
                    System.out.println("No definitions available for this word or the format is unexpected.");
                    Vocab101App.definitionOfTheWord = "No definitions available for this word or the format is unexpected.";
                }
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
    public static void ResetDictionaries()
    {

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
                //writer.newLine();  // Writes a new line after each string
            }
            System.out.println("Data successfully written to " + filePath);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
