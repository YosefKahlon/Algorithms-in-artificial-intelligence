
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


/**
 * This class gets as input the XML file
 * and builds the variables of the network from it.
 */
public class XmlReader {

    private Document document;

    //https://initialcommit.com/blog/how-to-read-xml-file-in-java
    public XmlReader(String xml_file) {

        try {
            File file = new File(xml_file);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.parse(file);

        } catch (
                FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param doc
     * @return list of variables
     */
    public List<Variable> getVar(Document doc) {

        HashMap<String, Variable> varMap = new HashMap<>();

        // Read the tag VARIABLE from the xml
        NodeList varList = doc.getElementsByTagName("VARIABLE");


        //run on VARIABLE tag
        for (int i = 0; i < varList.getLength(); i++) {
            Node variableNode = varList.item(i);
            String name = "";
            List<String> outcome = new ArrayList<>();

            // has sub-elements
            if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList childNodes = variableNode.getChildNodes();

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);

                    //get the name of the variable
                    if (childNode.getNodeName().equals("NAME")) {
                        name = childNode.getTextContent();

                        //get the outcome of the variable
                    } else if (childNode.getNodeName().equals("OUTCOME")) {
                        NodeList outcomeNode = childNode.getChildNodes();
                        for (int k = 0; k < outcomeNode.getLength(); k++) {
                            outcome.add(outcomeNode.item(k).getTextContent());
                        }
                    }


                }
                Variable v = new Variable(name);
                v.setVar_outcome(outcome);
                varMap.put(name, v);
            }


        }


        //run on DEFINITION tag
        NodeList definitionList = doc.getElementsByTagName("DEFINITION");
        for (int i = 0; i < definitionList.getLength(); i++) {
            Node definitionNode = definitionList.item(i);

            // has sub-elements
            if (definitionNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList childNodes = definitionNode.getChildNodes();
                String name = "";
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);

                    switch (childNode.getNodeName()) {

                        // which variable is it
                        case "FOR":
                            name = childNode.getTextContent();
                            break;

                        // the parents of the variable
                        case "GIVEN":
                            NodeList varParents = childNode.getChildNodes();
                            for (int k = 0; k < varParents.getLength(); k++) {

                                //add parents
                                varMap.get(name).getVar_parents().add(
                                        varMap.get(varParents.item(k).getTextContent())
                                );

                            }

                            break;

                        //cpt
                        case "TABLE":
                            NodeList cpt_value = childNode.getChildNodes();
                            String s1 = cpt_value.item(0).getTextContent();


                            //split string
                            List<String> temp_list = new ArrayList<String>(Arrays.asList(s1.split(" ")));

                            //get the value
                            for (String s : temp_list) {
                                double val = new Double(s);
                                varMap.get(name).getVar_cpt().add(val);
                            }

                            break;
                    }

                }
            }
        }
        //create CPT for each variable
        varMap.forEach((key, value) -> {
            varMap.get(key).CreateCPT();
        });


        return new ArrayList<Variable>(varMap.values());
    }

    public Document getDocument() {
        return this.document;
    }
}
