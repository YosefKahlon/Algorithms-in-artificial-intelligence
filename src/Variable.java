import java.util.*;


/**
 * This class represents the existing vertices in the Bayesian network.
 * When the features are:
 * 1. Name
 * 2. List of parents
 * 3. A list of a list of values
 * 4. CPT - Table
 */


public class Variable {

    private final String name;
    private List<String> var_outcome;
    private List<Variable> var_parents;
    private List<Double> var_cpt;
    private Map<List<String>, Map<String, Double>> cptMap;


    Variable(String name) {
        this.name = name;
        this.var_outcome = new ArrayList<>();
        this.var_parents = new ArrayList<>();
        this.var_cpt = new ArrayList<>();
        this.cptMap = new HashMap<>();
    }


    /**
     * @return the name of this variable.
     */
    public String getName() {
        return name;
    }


    /**
     * @return CPT
     */
    public Map<List<String>, Map<String, Double>> getCptMap() {
        return cptMap;
    }


    /**
     * @return list of value for create the CPT
     */
    public List<Double> getVar_cpt() {
        return var_cpt;
    }

    /**
     * @param var_outcome
     */
    public void setVar_outcome(List<String> var_outcome) {
        this.var_outcome = var_outcome;
    }

    /**
     * @return list of the parents for this variable
     */
    public List<Variable> getVar_parents() {
        return var_parents;
    }

    /**
     * @return list of the name of the parents for this variable
     */
    public List<String> getParentsName() {
        List<String> names = new ArrayList<>();
        for (Variable name : this.var_parents) {
            names.add(name.getName());
        }
        return names;
    }


    /**
     * @return list of the outcome for this variable
     */
    public List<String> getVar_outcome() {
        return var_outcome;
    }

    @Override
    public String toString() {
        return "Name: " + name;

    }


    /**
     * This method built the CPT table,
     * where the table is built in the following way:
     * Map {
     * key: list of parents outcome ,
     * value: Map {
     * key: outcome of the variable.
     * value: value of the query type double.
     * } }
     */
    public void CreateCPT() {

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
            for (Variable var_parent : this.var_parents) {
                all_outcome.add(var_parent.var_outcome);
            }

            //get all the possible combinations of the parents' outcome value
            List<List<String>> combinations = new ArrayList<>(getAllCombinations(all_outcome));


            // map  all the value of the variable outcome
            List<Map<String, Double>> store = new ArrayList<>();
            for (int i = 0; i < this.var_cpt.size(); ) {

                Map<String, Double> prob = new HashMap<>();

                for (int j = 0; j < this.var_outcome.size(); j++) {
                    prob.put(this.var_outcome.get(j), this.var_cpt.get(i++));
                }
                store.add(prob);
            }


            //map all the possible combinations of the parents and the variable outcome
            for (int i = 0; i < combinations.size(); i++) {
                this.cptMap.put(combinations.get(i), new HashMap<>(store.get(i)));
            }

        }

    }


    /**
     * This method returns all possible combinations
     * to create a truth table for the possible values of each variable and its parents.
     *
     * @param lists of list of outcome
     * @return all possible combinations.
     */
    private static List<List<String>> getAllCombinations(List<List<String>> lists) {
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

    /**
     * This method looks for the appropriate value of each variable according to its parents
     * and the possible outcomes they have and according to the value the variable receives
     * for example if it is a known variable then we will take only the value given
     * in the query for it, and otherwise we will use all the possibilities for it.
     * Finally, the method will return a map that will become a factor.
     * @param evidence
     * @param evidence_outcome
     * @return  {key: outcome parents + variable , value: The probabilistic value }
     */
    public Map<List<String>, Double> getCptLines(List<String> evidence, List<String> evidence_outcome) {

        Map<List<String>, Double> CPTLine = new HashMap<>();


        Set<List<String>> set = this.cptMap.keySet();

        //Get lists of parents' outcomes
        List<List<String>> parents_outcome = new ArrayList<>(set);

        List<String> new_outcome = new ArrayList<>();


        boolean flag = true;

        for (int i = 0; i < parents_outcome.size(); i++) {

            flag = true;

            for (int j = 0; j < evidence.size(); j++) {

                if (this.getParentsName().contains(evidence.get(j))
                        && !parents_outcome.get(i).get(this.getParentsName().indexOf(evidence.get(j))).equals(evidence_outcome.get(j))) {
                    flag = false;
                }
            }

            if (flag) {
                new_outcome.clear();


                if (parents_outcome.get(i) != null) {
                    new_outcome.addAll(parents_outcome.get(i));
                }

                //-------------------------------------
                // this variable is one of the evidence
                // use only its given outcome from the query
                // with the outcomes values of its parent
                if (evidence.contains(this.name)) {

                    //get the given outcome of the variable
                    new_outcome.add(evidence_outcome.get(evidence.indexOf(name)));

                    List<String> key = new ArrayList<>(new_outcome);
                    Double value = this.cptMap.get(parents_outcome.get(i)).get(evidence_outcome.get(evidence.indexOf(name)));
                    CPTLine.put(key, value);

                }

                //----------------------------------------
                //use every outcome of the variable
                else {
                    int remover = new_outcome.size();
                    for (int j = 0; j < this.var_outcome.size(); j++) {

                        if (remover < new_outcome.size()) {
                            new_outcome.remove(remover);
                        }

                        new_outcome.add(this.var_outcome.get(j));

                        List<String> key = new ArrayList<>(new_outcome);
                        Double value = this.cptMap.get(parents_outcome.get(i)).get(this.var_outcome.get(j));
                        CPTLine.put(key, value);

                    }
                }
            }
        }

        return CPTLine;

    }

}
