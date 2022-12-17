import com.sun.xml.internal.txw2.Document;


import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Ex1 {

    private static String input = "input.txt";


    public static void main(String[] args) {

        /**
         * 1. read the input
         *  1.2. open the xml from the first line
         *  1.3. build the variable
         *  1.4 get the queries and algorithm
         * 2. build the network
         * 3. make the query
         *  3.1. add to output the result
         */

        int line_counter = 0;
        Document document;
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
                    System.out.println(line);

                    //Instantiate XML file
                    xml = new XmlReader(line);
                    variableList = xml.getVar(xml.getDocument());

                }
                // get the queries and algorithm
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
        //bayesian.printNetwork();


        try {
            FileWriter myWriter = new FileWriter("filename.txt");

            while (!queries.isEmpty()) {
                //System.out.println(queries.peek());
                String[] s1 = queries.poll().split("\\)");
                String[] algo_num = s1[1].split(",");
                String[] p_queries = s1[0].split("\\(");
              //  System.out.println(p_queries[1]);


                switch (algo_num[1]) {
                    case ("1"):
                       // System.out.println(bayesian.simpleDeduction(p_queries[1]));
                        myWriter.write(bayesian.simpleDeduction(p_queries[1]));
                        break;
                    case ("2"):
                        myWriter.write( bayesian.VariableElimination(p_queries[1]));
                       // System.out.println(bayesian.VariableElimination(p_queries[1]));
                        break;
                    case ("3"):
                        break;
                }
//break;

            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}