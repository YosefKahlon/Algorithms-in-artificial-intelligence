import java.util.*;

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
//        System.out.println("------------------------------------");
//
//
//            System.out.println(this.name);
//            System.out.println(var_cpt);
//        System.out.println(var_cpt.size());

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
            //List<List<String>> Sequences = new ArrayList<>(generateAllSequences(all_outcome));
            List<List<String>> Sequences = new ArrayList<>(getAllCombinations(all_outcome));
          //  System.out.println(Sequences.size());

            // map  all the value of the variable outcome
            List<Map<String, Double>> store = new ArrayList<>();
            for (int i = 0; i < this.var_cpt.size(); ) {
                Map<String, Double> prob = new HashMap<>();
                for (int j = 0; j < this.var_outcome.size(); j++) {
                    prob.put(this.var_outcome.get(j), this.var_cpt.get(i++));
                }
                store.add(prob);
            }


            //map all the possible sequences of the parents and the variable outcome
            for (int i = 0; i < Sequences.size(); i++) {
                this.cptMap.put(Sequences.get(i), new HashMap<>(store.get(i)));
            }


        }
      //  System.out.println(this.cptMap);
    }

    public static List<List<String>> getAllCombinations(List<List<String>> lists) {
        List<List<String>> combinations = new ArrayList<>();
        int[] indices = new int[lists.size()];
        boolean done = false;
        while (!done) {
            List<String> combination = new ArrayList<>();
            for (int i = 0; i < lists.size(); i++) {
                combination.add(lists.get(i).get(indices[i]));
            }
            combinations.add(combination);

            // Update indices
            for (int i = lists.size() - 1; i >= 0; i--) {
                indices[i]++;
                if (indices[i] >= lists.get(i).size()) {
                    indices[i] = 0;
                } else {
                    break;
                }
            }
            done = true;
            for (int index : indices) {
                if (index != 0) {
                    done = false;
                    break;
                }
            }
        }
        return combinations;
    }



    public static List<List<String>> generateAllSequences(List<List<String>> lists) {
//        List<List<String>> sequences = new ArrayList<>();
//        if (lists.isEmpty()) {
//            return sequences;
//        }
//
//        List<String> firstList = lists.get(0);
//        for (String value : firstList) {
//            List<String> sequence = new ArrayList<>();
//            sequence.add(value);
//            sequences.add(sequence);
//        }
//
//        for (int i = 1; i < lists.size(); i++) {
//            List<String> list = lists.get(i);
//            List<List<String>> newSequences = new ArrayList<>();
//            for (List<String> sequence : sequences) {
//                if (list.size() > sequence.size()) {
//                    for (String value : list) {
//                        List<String> newSequence = new ArrayList<>(sequence);
//                        newSequence.add(value);
//                        newSequences.add(newSequence);
//                    }
//                } else {
//                    // Add the remaining values to the end of the sequence
//                    sequence.addAll(list);
//                    newSequences.add(sequence);
//                }
//            }
//            sequences = newSequences;
//        }
//        System.out.println("sequences: "+sequences);
//       return sequences;

        List<List<String>> combinations = new ArrayList<>();

        // outer loop: iterate through each list in the input
        for (List<String> list : lists) {
            // initialize a new list of combinations for the current list
            List<List<String>> newCombinations = new ArrayList<>();

            // inner loop: iterate through each element in the current list
            for (String element : list) {
                // if this is the first list, add each element as a new combination
                if (combinations.isEmpty()) {
                    newCombinations.add(Collections.singletonList(element));

                } else {
                    // otherwise, add each element to each existing combination
                    for (List<String> combination : combinations) {
                        List<String> newCombination = new ArrayList<>(combination);
                        newCombination.add(element);

                        newCombinations.add(newCombination);

                    }
                }
            }

            // replace the existing combinations with the new combinations
            combinations = newCombinations;
            // System.out.println(combinations);
        }

      //  System.out.println("combinations: "+combinations);
        return combinations;
   //    return sequences;
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

    public List<String> getParentsName() {
        List<String> names = new ArrayList<>();
        for (Variable name : this.var_parents) {
            names.add(name.getName());
        }
        return names;
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

    //todo
    public Map<List<String>, Double> getCptLine(List<String> evidence, List<String> evidence_outcome) {
        //System.out.println("----------------------------");
        //System.out.println(this.name);
        Map<List<String>, Double> ans = new HashMap<>();
        // System.out.println(this.cptMap);

        Set<List<String>> set = this.cptMap.keySet();
        List<List<String>> parents_outcome = new ArrayList<>(set);
        List<String> event_outcome = new ArrayList<>();
        boolean flag = true;

        for (int i = 0; i < parents_outcome.size(); i++) {
           //  System.out.println(parents_outcome.get(i));

            flag = true;
            for (int j = 0; j < evidence.size(); j++) {
                if (this.getParentsName().contains(evidence.get(j))
                        && !parents_outcome.get(i).get(this.getParentsName().indexOf(evidence.get(j))).equals(evidence_outcome.get(j))) {
                    flag = false;
                }
            }

            if (flag) {
                event_outcome.clear();
                //-------------------------------------

                if (parents_outcome.get(i) != null) {
                    event_outcome.addAll(parents_outcome.get(i));
                }

                // variable is one of the evidence
                // use only his outcome from the query
                // with his parent outcome value
                if (evidence.contains(this.name)) {
                    event_outcome.add(evidence_outcome.get(evidence.indexOf(name)));
                    List<String> key = new ArrayList<>(event_outcome);
                    ans.put(key, this.cptMap.get(parents_outcome.get(i)).get(evidence_outcome.get(evidence.indexOf(name))));
                    //System.out.println(ans);
                }
                //----------------------------------------

                //get all outcomes of variable
                else {
                    int size = event_outcome.size();
                    for (int j = 0; j < this.var_outcome.size(); j++) {

                        if (size < event_outcome.size()) {
                            event_outcome.remove(size);
                        }
                        event_outcome.add(this.var_outcome.get(j));
                        List<String> key = new ArrayList<>(event_outcome);
                        ans.put(key, this.cptMap.get(parents_outcome.get(i)).get(this.var_outcome.get(j)));
                        //  System.out.println(ans);
                    }
                }
            }
        }

        return ans;



    }





}
