import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.util.function.BiConsumer;

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

    public HashMap<String,variable> getNetworkStructure(Document doc) {


        HashMap<String, variable> varMap = new HashMap<>();

        // Read the tag VARIABLE from the xml
        NodeList variableList = doc.getElementsByTagName("VARIABLE");


        for (int i = 0; i < variableList.getLength(); i++) {
            Node variableNode = variableList.item(i);
            String name = "";
            List<String> outcome = new ArrayList<>();

            if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList childNodes = variableNode.getChildNodes();


                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);


                    if (childNode.getNodeName().equals("NAME")) {

                        name = childNode.getTextContent();

                    } else if (childNode.getNodeName().equals("OUTCOME")) {
                        NodeList outcomeNode = childNode.getChildNodes();
                        for (int k = 0; k < outcomeNode.getLength(); k++) {

                            outcome.add(outcomeNode.item(k).getTextContent());
                        }

                    }

                }
                variable v = new variable(name);
                v.setOutcome(outcome);
                varMap.put(name, v);

            }
        }


        NodeList definitionList = doc.getElementsByTagName("DEFINITION");
        for (int i = 0; i < definitionList.getLength(); i++) {
            Node definitionNode = definitionList.item(i);


            if (definitionNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList childNodes = definitionNode.getChildNodes();
                String name = "";
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);

                    switch (childNode.getNodeName()) {
                        case "FOR":

                            name = childNode.getTextContent();

                            break;
                        case "GIVEN":
                            NodeList outcomeNode = childNode.getChildNodes();
                            for (int k = 0; k < outcomeNode.getLength(); k++) {

                                //add parents
                                varMap.get(name).getParents().add(varMap.get(outcomeNode.item(k).getTextContent()));

                                //add child
                                varMap.get(outcomeNode.item(k).getTextContent()).getChild().add(varMap.get(name));

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
                                varMap.get(name).getCpt().add(val);
                            }

                            break;
                    }

                }
            }
        }

//        varMap.forEach((key, value) -> {
//            System.out.println(key + "\n" + value.getParents() + " \n cpt" + value.getCpt() + "\n==========\n");
//        });

        return varMap;
    }


    public Document getDocument() {
        return this.document;
    }
}
