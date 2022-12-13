import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Variable {

    private String name;
    private List<String> var_outcome;
    private List<Variable> var_parents;
    private List<String> var_children;
    private List<Double> var_cpt;
    private Map<List<String>, Map<String, Double>> cptMap;


    Variable(String name) {
        this.name = name;
        this.var_outcome = new ArrayList<>();
        this.var_parents = new ArrayList<>();
        this.var_children = new ArrayList<>();
        this.var_cpt = new ArrayList<>();
        this.cptMap = new HashMap<>();
    }


    //hash map {
    //     key: array list parents outcome ,
    //     value: {
    //       hash map {
    //       key: outcome of the variable
    //       value: double value of the query
    public void CreateCPT() {
//        System.out.println(var_cpt);


        // this root node
        if (this.var_parents.isEmpty()) {
            Map<String, Double> prob = new HashMap<>();
            for (int i = 0; i < this.var_outcome.size(); i++) {
                prob.put(this.var_outcome.get(i), this.var_cpt.get(i));
            }
            this.cptMap.put(null, new HashMap<>(prob));

        } else {

            // get the outcomes of the parents
            List<List<String>> all_outcome = new ArrayList<>();
            for (int i = 0; i < this.var_parents.size(); i++) {
                all_outcome.add(this.var_parents.get(i).var_outcome);
            }

            //get all the possible Sequences of the parents' outcome value
            List<List<String>> Sequences = new ArrayList<>(generateAllSequences(all_outcome));



            // map  all the value of the variable outcome
            List<Map<String, Double>> store = new ArrayList<>();
            for (int i = 0; i < this.var_cpt.size(); ) {
                Map<String, Double> prob = new HashMap<>();
                for (int j = 0; j < this.var_outcome.size(); j++) {
                    prob.put(this.var_outcome.get(j),this.var_cpt.get(i++));
                }
                store.add(prob);
            }



            //map all the possible sequences of the parents and the variable outcome
            for (int i = 0; i < Sequences.size(); i++) {
                this.cptMap.put(Sequences.get(i),new HashMap<>(store.get(i)));
            }



            }
        //System.out.println(this.cptMap);
        }


    public static List<List<String>> generateAllSequences(List<List<String>> lists) {
        List<List<String>> sequences = new ArrayList<>();
        if (lists.isEmpty()) {
            return sequences;
        }

        List<String> firstList = lists.get(0);
        for (String value : firstList) {
            List<String> sequence = new ArrayList<>();
            sequence.add(value);
            sequences.add(sequence);
        }

        for (int i = 1; i < lists.size(); i++) {
            List<String> list = lists.get(i);
            List<List<String>> newSequences = new ArrayList<>();
            for (List<String> sequence : sequences) {
                if (list.size() > sequence.size()) {
                    for (String value : list) {
                        List<String> newSequence = new ArrayList<>(sequence);
                        newSequence.add(value);
                        newSequences.add(newSequence);
                    }
                } else {
                    // Add the remaining values to the end of the sequence
                    sequence.addAll(list);
                    newSequences.add(sequence);
                }
            }
            sequences = newSequences;
        }

        return sequences;
    }

    public String getName() {
        return name;
    }

    public Map<List<String>, Map<String, Double>> getCptMap() {
        return cptMap;
    }

    public List<Double> getVar_cpt() {
        return var_cpt;
    }

    public void setVar_outcome(List<String> var_outcome) {
        this.var_outcome = var_outcome;
    }


    public List<Variable> getVar_parents() {
        return var_parents;
    }

    public List<String> getVar_children() {
        return var_children;
    }

    public List<String> getVar_outcome() {
        return var_outcome;
    }

    @Override
    public String toString() {
        return "Name: " + name;

    }


}
