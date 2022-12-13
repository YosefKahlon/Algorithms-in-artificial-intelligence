
import java.text.DecimalFormat;
import java.util.*;

public class Network {

    Map<String, Variable> bayesian;
    List<Variable> variableList;

    public Network(List<Variable> variableList) {
        this.variableList = variableList;
        this.bayesian = new HashMap<>();
        for (Variable var : variableList) {
            this.bayesian.put(var.getName(), var);
        }
    }

    // print network
    public void printNetwork() {
        System.out.println("==========");
        for (int i = 0; i < this.variableList.size(); i++) {
            System.out.println(this.variableList.get(i).toString());
            System.out.println("Parents: " + this.variableList.get(i).getVar_parents());
            System.out.println("CPT: " + this.variableList.get(i).getCptMap());
            System.out.println("==========");
        }

    }


    public String simpleDeduction(String query) {
        String ans = "";
        //{var_query , var_outcome}
        List<List<String>> queryParameter = getParameter(query);
        if (hasAnswer(queryParameter)) {
            // System.out.println("hasAnswer");


            double value = getProbability(
                    queryParameter.get(0).get(0),
                    queryParameter.get(1).get(0),
                    queryParameter.get(0),
                    queryParameter.get(1));

            DecimalFormat df = new DecimalFormat("0.00000");
            String formatted = df.format(value);

            ans += formatted + ",0,0\n";
            return ans;
        } else {

            //System.out.println("query " + query);
            List<String> all_var = new ArrayList<>(this.bayesian.keySet());

            // find hidden variable
            List<String> hidden_list = new ArrayList<>();
            for (int i = 0; i < all_var.size(); i++) {
                if (!queryParameter.get(0).contains(all_var.get(i))) {
                    hidden_list.add(all_var.get(i));
                }
            }

//            System.out.println("all var " + all_var);
//            System.out.println("query var " + queryParameter.get(0));
       //    System.out.println("hidden var " + hidden_list);

            //get the outcomes of the hidden variables
            List<List<String>> hidden_outcome = new ArrayList<>();
            for (String hidden_var : hidden_list) {
                hidden_outcome.add(bayesian.get(hidden_var).getVar_outcome());
            }
           // System.out.println("hidden outcome " + hidden_outcome);

            //get all the sequence of the outcome of the hidden variable
            hidden_outcome = generateAllSequences(hidden_outcome);
//            System.out.println("all outcome of the hidden " + hidden_outcome);


            all_var.clear();
            all_var.addAll(queryParameter.get(0));
            all_var.addAll(hidden_list);
            //System.out.println(all_var);


            //define the counter for the number of sum and multiply oppressions
            int plus_counter = 0;
            int multi_counter = 0;

            double sumOfOuterQuery, sumOfInnerQuery, sumUpper=0 ,sumAllQuery = 0;
            boolean startInnerQuery, startOuterQuery, upper_part = true;


            List<String> all_outcome = new ArrayList<>();

            // run on the query variable outcome list
            for (int i = 0; i < bayesian.get(all_var.get(0)).getVar_outcome().size(); i++) {
                sumOfOuterQuery = 0;
                startOuterQuery = true;

                //set outcome for the query
                queryParameter.get(1).set(0, bayesian.get(all_var.get(0)).getVar_outcome().get(i));


                //every iteration try different combination of the hidden variable with query outcome
                for (int j = 0; j < hidden_outcome.size(); j++) {
                    sumOfInnerQuery = 1;


                    all_outcome.clear();
                    all_outcome.addAll(queryParameter.get(1));
                    all_outcome.addAll(hidden_outcome.get(j));
                   // System.out.println("all outcome " + all_outcome);

                    startInnerQuery = true;
                    for (int k = 0; k < all_var.size(); k++) {

                        //start a new query
                        //so also start the sum from here
                        // P(B=T,J=T,M=T,A=T,E=T)
                        if (startInnerQuery) {
                            sumOfInnerQuery = getProbability(
                                    all_var.get(k),
                                    all_outcome.get(k),
                                    all_var, all_outcome);
                            startInnerQuery = false;
                        } else {
                            sumOfInnerQuery *= getProbability(
                                    all_var.get(k),
                                    all_outcome.get(k),
                                    all_var,
                                    all_outcome);
                            multi_counter++;
                        }

                    }

                    if (startOuterQuery) {
                        sumOfOuterQuery = sumOfInnerQuery;
                        startOuterQuery = false;
                    } else {
                        sumOfOuterQuery += sumOfInnerQuery;
                        plus_counter++;
                    }

                }


                //upper part
                if (upper_part) {
                    sumUpper = sumOfOuterQuery;
                    sumAllQuery = sumOfOuterQuery;
                    upper_part = false;
                }
                //lower part
                else {
                    sumAllQuery += sumOfOuterQuery;
                    plus_counter++;
                }


            }

            DecimalFormat df = new DecimalFormat("0.00000");
            String formatted = df.format(sumUpper/sumAllQuery);

            ans += formatted + ","+plus_counter+","+multi_counter+"\n";
            return ans;
        }

    }

    /**
     *  Truth table:
     *
     * @param inputLists is list of list
     * @return all the sequence from values of the lists
     */
    public static List<List<String>> generateAllSequences(List<List<String>> inputLists) {
        List<List<String>> combinations = new ArrayList<>();

    // outer loop: iterate through each list in the input
        for (List<String> list : inputLists) {
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

        return combinations;

    }


    /**
     * @param var
     * @param varOutcome
     * @param queryVar
     * @param queryOutcome
     * @return
     */
    private double getProbability(String var, String varOutcome, List<String> queryVar, List<String> queryOutcome) {


        // not have parents,
        // so we can get the value directly from the cpt of this var
        if (bayesian.get(var).getVar_parents().isEmpty()) {
            return this.bayesian.get(var).getCptMap().get(null).get(varOutcome);
        }
        // do have parents
        else {
            //search the index of the parent, so we can find witch outcome value need to take.
            int[] parentsIndex = new int[this.bayesian.get(var).getVar_parents().size()];

            int j = 0;

            for (int i = 0; i < queryVar.size(); i++) {
                if (this.bayesian.get(var).getVar_parents().toString().contains(queryVar.get(i))) {
                    parentsIndex[this.bayesian.get(var).getVar_parents().indexOf(this.bayesian.get(queryVar.get(i)))]
                            = i;
                    j++;
                    //System.out.println(parentsIndex.length);
                    if (j >= parentsIndex.length) break;

                }
            }
            // add the outcomes and return the probability
            List<String> parents_outcome = new ArrayList<>();
            for (int i = 0; i < this.bayesian.get(var).getVar_parents().size(); i++) {
                parents_outcome.add(queryOutcome.get(parentsIndex[i]));
            }
            return this.bayesian.get(var).getCptMap().get(parents_outcome).get(varOutcome);

        }
    }


    /**
     * This method check if  the given query have direct answer.
     *
     * @param queryParameter
     * @return boolean
     */
    private boolean hasAnswer(List<List<String>> queryParameter) {


        // if it is a variable we don't know
        for (int i = 0; i < queryParameter.get(0).size(); i++) {
            String var = queryParameter.get(0).get(i);
            if (!this.bayesian.containsKey(var)) {
                return false;
            }
        }

//        //prants list
//        if (this.bayesian.get(queryParameter.get(0).get(0)).getVar_parents().size() != queryParameter.get(0).size()-1){
//            return false;
//        }

        for (int i = 1; i < queryParameter.get(0).size(); i++) {
            //System.out.println("this is test " +this.bayesian.get(queryParameter.get(0).get(0)).getVar_parents() );

            if (!this.bayesian.get(queryParameter.get(0).get(0)).getVar_parents().toString().contains(queryParameter.get(0).get(i))) {
                // System.out.println("this is also ");
                return false;
            }
        }

        return true;
    }


    /**
     * @param query
     * @return list of two list when the first is the variable of the query and the second list is the outcome of the
     * query variable order at the same index as in the variable list.
     */
    private List<List<String>> getParameter(String query) {
        //System.out.println(query);
        List<String> var_query = new ArrayList<>();
        List<String> var_outcome = new ArrayList<>();

        String[] var_and_val = query.split("\\|");

        // the query variable of
        String[] first_var = var_and_val[0].split("=");
        var_query.add(first_var[0]);
        var_outcome.add(first_var[1]);

        // the evidence variables
        String[] evidence = var_and_val[1].split(",");
        String[] evi_;
        for (int i = 0; i < evidence.length; i++) {
            evi_ = evidence[i].split("=");
            var_query.add(evi_[0]);
            var_outcome.add(evi_[1]);
        }

        List<List<String>> prm = new ArrayList<>();
        prm.add(var_query);
        prm.add(var_outcome);
        return prm;
    }
}
