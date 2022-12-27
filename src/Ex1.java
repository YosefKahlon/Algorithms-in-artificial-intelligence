//import com.sun.xml.internal.txw2.Document;


import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;


/**
 * ID: 209011840
 */


public class Ex1 {

    private static String input = "input.txt";
    private static String output = "output.txt";

    public static void main(String[] args) {

        /**
         * 1. read the input.txt
         *  1.2. open the XML from the first line
         *  1.3. build the variable
         *  1.4 get the queries and algorithm
         * 2. build the network
         * 3. make the query
         *  3.1. write result to output.txt
         */

        int line_counter = 0;
        XmlReader xml = null;
        Queue<String> queries = new LinkedList<>();
        List<Variable> variableList = new ArrayList<>();

        // Read the given input file
        try {
            File file = new File(input);
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();

                // open the xml file
                // the first line in the xml
                if (line_counter == 0) {

                    //Instantiate XML file
                    xml = new XmlReader(line);
                    variableList = xml.getVar(xml.getDocument());

                }
                // get the queries and algorithms
                else if (!line.isEmpty()) {
                    queries.add(line);
                }

                line_counter++;
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        Network bayesian = new Network(variableList);

        //print the network
        //bayesian.printNetwork();


        //write to the output file the result
        try {
            FileWriter myWriter = new FileWriter(output);

            while (!queries.isEmpty()) {
                // System.out.println(queries.peek());
                String[] s1 = queries.poll().split("\\)");
                String[] algo_num = s1[1].split(",");
                String[] p_queries = s1[0].split("\\(");
                // System.out.println(Arrays.toString(p_queries));


                switch (algo_num[1]) {
                    case ("1"):
                        // System.out.println(bayesian.simpleInference(p_queries[1]));
                        myWriter.write(bayesian.simpleInference(p_queries[1]));
                        break;
                    case ("2"):
                        myWriter.write(bayesian.VariableElimination(p_queries[1]));
                        // System.out.println(bayesian.VariableElimination(p_queries[1]));
                        break;
                    case ("3"):
                        //I didn't do it
                        myWriter.write("0,0,0\n");
                }


            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}