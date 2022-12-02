import com.sun.xml.internal.txw2.Document;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner; // Import the Scanner class to read text files

public class Ex1 {

    private static String input = "input.txt";


    public static void main(String[] args) {

        /**
         * 1. read the input
         *  1.2. open the xml from the first line
         *  1.3. build the variable --> [ need to fix cpt]
         *  1.4 get the queries and algorithm
         * 2. build the network
         * 3. make the query
         *  3.1. add to output the result
         *
         *
         */

        int line_counter = 0;
        Document document;
        XmlReader xml = null;
        Queue<String> queries= new LinkedList<>();


        // Read the given input file
        try {
            File file = new File(input);
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();

                // open the xml file
                // the first line in the xml
                if (line_counter == 0) {
                    System.out.println(line);

                    //Instantiate XML file
                    xml = new XmlReader(line);
                }
                // get the queries and algorithm
                else {

                    //todo
                    // maybe add to queue
                    queries.add(line);
                }


                line_counter++;
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        System.out.println(queries);

        //Todo
        Network bayesian = new Network();
        assert xml != null;
        bayesian = new Network(xml.getNetworkStructure(xml.getDocument()));


    }
}