
import java.text.DecimalFormat;
import java.util.*;

public class Network {
    /**
     * This class represents the Bayesian network and the required algorithms.
     */
    private Map<String, Variable> bayesian;
    private List<Variable> variableList;

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
            System.out.println("CPT SIZE: " + this.variableList.get(i).getCptMap().size());
            System.out.println("==========");
        }

    }

    /**
     * This algorithm returns the probability of the query according to a simple inference from the Bayesian network,
     * where the formula for P(B=T|J=T,M=T) when A & E are the hidden =
     * P(B=T,J=T,M=T)/P(J=T,M=T) =
     * <p>
     * P(B=T,J=T,M=T,A=T,E=T) + P(B=T,J=T,M=T,A=F,E=T) + P(B=T,J=T,M=T,A=T,E=F) + P(B=T,J=T,M=T,A=F,E=F) /
     * ---------------------------------------------------------------------------------------------------
     * P(B=T,J=T,M=T,A=T,E=T) + P(B=T,J=T,M=T,A=F,E=T) + P(B=T,J=T,M=T,A=T,E=F) + P(B=T,J=T,M=T,A=F,E=F) +
     * P(B=F,J=T,M=T,A=T,E=T) + P(B=F,J=T,M=T,A=F,E=T) + P(B=F,J=T,M=T,A=T,E=F) + P(B=F,J=T,M=T,A=F,E=F)
     *
     * @param query
     * @return The probabilistic value in the required format.
     */
    public String simpleInference(String query) {
        //{variable of the query ,

        //the outcomes of the query variables}
        List<List<String>> queryParameter = getParameter(query);

        if (hasAnswer(queryParameter)) {
            return directAns(queryParameter);

        } else {
            String ans = "";

            List<String> all_var = new ArrayList<>(this.bayesian.keySet());

            // find hidden variable
            List<String> hidden_list = new ArrayList<>();
            for (int i = 0; i < all_var.size(); i++) {
                if (!queryParameter.get(0).contains(all_var.get(i))) {
                    hidden_list.add(all_var.get(i));
                }
            }


            //get the outcomes of the hidden variables
            List<List<String>> hidden_outcomes = new ArrayList<>();
            for (String hidden_var : hidden_list) {
                hidden_outcomes.add(bayesian.get(hidden_var).getVar_outcome());
            }


            //get all the combinations of the outcomes of the hidden variables
            hidden_outcomes = getAllCombinations(hidden_outcomes);


            all_var.clear();
            all_var.addAll(queryParameter.get(0));
            all_var.addAll(hidden_list);


            //define the counter for the number of sum and multiply oppressions
            int plus_counter = 0;
            int multi_counter = 0;

            double sumOfOuterQuery = 0, sumOfInnerQuery = 0, sumUpper = 0, sumAllQuery = 0;
            boolean startInnerQuery, startOuterQuery, upper_part = true;


            List<String> all_outcome = new ArrayList<>();

            List<String> hidden_outcome_of_query = new ArrayList<>();
            hidden_outcome_of_query.add(queryParameter.get(1).get(0));

            for (int i = 0; i < this.bayesian.get(queryParameter.get(0).get(0)).getVar_outcome().size(); i++) {
                if (!queryParameter.get(1).get(0).equals(this.bayesian.get(queryParameter.get(0).get(0)).getVar_outcome().get(i))) {
                    hidden_outcome_of_query.add(this.bayesian.get(queryParameter.get(0).get(0)).getVar_outcome().get(i));
                }
            }

            // run on the query variable outcome list
            for (int i = 0; i < hidden_outcome_of_query.size(); i++) {
                sumOfOuterQuery = 0;
                startOuterQuery = true;

                //change the outcome for the query variable after the upper part of this formula
                if (!upper_part) {
                    queryParameter.get(1).set(0, hidden_outcome_of_query.get(i));
                }


                //every iteration try different combination of the hidden variable with query outcome
                for (int j = 0; j < hidden_outcomes.size(); j++) {
                    sumOfInnerQuery = 1;


                    all_outcome.clear();
                    all_outcome.addAll(queryParameter.get(1));
                    all_outcome.addAll(hidden_outcomes.get(j));


                    startInnerQuery = true;
                    for (int k = 0; k < all_var.size(); k++) {

                        //start a new inner query P(B=T,J=T,M=T,A=T,E=T)
                        //so also start to multiply from here
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

                    //start outer query P(..) * p(..)
                    //so also start to sum from here
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
            String formatted = df.format(sumUpper / sumAllQuery);

            ans += formatted + "," + plus_counter + "," + multi_counter + "\n";
            return ans;
        }

    }


    /**
     * Truth table:
     *
     * @param lists is list of list
     * @return all the combinations from values of the lists
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
     * In this method we search for the probabilistic value, by search the indexes
     * of the variable parents and get the outcomes we need from the input of the function.
     *
     * @return The probabilistic value.
     */
    private double getProbability(String var, String varOutcome, List<String> VarList, List<String> outcomeList) {
        // not have parents,
        // so we can get the value directly from the CPT of this variable
        if (bayesian.get(var).getVar_parents().isEmpty()) {
            return this.bayesian.get(var).getCptMap().get(null).get(varOutcome);
        } else {


            //If the var variable does have parents, then the function needs to
            // find the index of the parent variables in the
            // VarList input, so that it can look up the corresponding outcomes in the outcomeList input.
            Map<String, Integer> parentIndices = new HashMap<>();
            for (int i = 0; i < VarList.size(); i++) {
                if (this.bayesian.get(var).getParentsName().contains(VarList.get(i))) {
                    // store the index of the parent variables
                    parentIndices.put(VarList.get(i), i);
                }
            }

            // add the outcomes and return the probability
            List<String> outcome = new ArrayList<>();
            for (String parent : this.bayesian.get(var).getParentsName()) {
                outcome.add(outcomeList.get(parentIndices.get(parent)));
            }

            return this.bayesian.get(var).getCptMap().get(outcome).get(varOutcome);
        }
    }


    /**
     * This method check if the given query have direct answer.
     *
     * @param queryParameter
     * @return boolean
     */


    private boolean hasAnswer(List<List<String>> queryParameter) {
        // Check if the query is empty
        if (queryParameter == null || queryParameter.isEmpty() || queryParameter.get(0).isEmpty()) {
            return false;
        }

        String variable = queryParameter.get(0).get(0);
        // Check if the variable exists in the Bayesian network
        if (!this.bayesian.containsKey(variable)) {
            return false;
        }

        // Check if the query contains all the parents of the variable
        List<Variable> parents = this.bayesian.get(variable).getVar_parents();
        if (parents.size() != queryParameter.get(0).size() - 1) {
            return false;
        }
        for (int i = 1; i < queryParameter.get(0).size(); i++) {
            boolean found = false;
            for (Variable parent : parents) {
                if (parent.getName().equals(queryParameter.get(0).get(i))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
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


        List<String> var_query = new ArrayList<>();
        List<String> var_outcome = new ArrayList<>();

        String[] var_and_val = query.split("\\|");

        // the query variable of
        String[] first_var = var_and_val[0].split("=");
        var_query.add(first_var[0]);
        var_outcome.add(first_var[1]);

//
        //System.out.println(query);
        if (query.contains("|")) {
            // the evidence variables
            String[] evidence = var_and_val[1].split(",");
            String[] evi_;
            for (int i = 0; i < evidence.length; i++) {
                evi_ = evidence[i].split("=");
                var_query.add(evi_[0]);
                var_outcome.add(evi_[1]);
            }
        }
        List<List<String>> parameter = new ArrayList<>();
        parameter.add(var_query);
        parameter.add(var_outcome);
        return parameter;

    }


    /**
     * @param queryParameter
     * @return The probabilistic value in the required format.
     */
    public String directAns(List<List<String>> queryParameter) {
        String ans = "";
        double value = getProbability(
                queryParameter.get(0).get(0),
                queryParameter.get(1).get(0),
                queryParameter.get(0),
                queryParameter.get(1));

        DecimalFormat df = new DecimalFormat("0.00000");
        String formatted = df.format(value);

        ans += formatted + ",0,0\n";
        return ans;
    }


    /**
     * This algorithm answers a more efficient query than the simple inference algo,
     * Since this involves eliminating variables from the model that are not relevant to the query or the evidence,
     * and replacing them with new factors that represent the dependence between the remaining variables.
     * @param query
     * @return The probabilistic value in the required format.
     */
    public String VariableElimination(String query) {

        //{variable of the query ,
        //the outcomes of the query variables}
        List<List<String>> queryParameter = getParameter(query);

        //check if there is a direct answer from the data
        if (hasAnswer(queryParameter)) {
            return directAns(queryParameter);
        }


        // first remove unnecessary variables:
        // we can remove any leaf node that is not a query variable or an evidence variable.
        // every variable that is not an ancestor of a
        // query variable or evidence variable is irrelevant to the query.
        List<String> varList = new ArrayList<>(queryParameter.get(0));
        for (Variable variable : this.variableList) {
            if (!varList.contains(variable.getName())) {
                if (isAncestor(variable.getName(), queryParameter.get(0))) {
                    varList.add(variable.getName());
                }
            }
        }


        // find hidden variable
        List<String> hidden_list = new ArrayList<>();
        for (String variable : varList) {
            if (!queryParameter.get(0).contains(variable)) {
                hidden_list.add(variable);
            }
        }


        //sort hidden list according to the ABC order
        Collections.sort(hidden_list);
        return VariableElimination(queryParameter.get(0), queryParameter.get(1), hidden_list, varList);

    }

    /**
     * the steps for this algorithm:
     * Create factors for the evidence variables in the model.
     * Join the factors according to the hidden variables in the model.
     * Eliminate the factors according to the hidden variables, one by one, until there are no more hidden variables.
     * Join the remaining factors and eliminate the factors according to the query variable.
     * Normalize the final result to obtain the probability of the query variable given the evidence.
     */
    private String VariableElimination(List<String> evidence, List<String> evidence_outcome, List<String> hidden_list, List<String> varList) {
        String ans= "";
        List<String> query_var = new ArrayList<>();

        String var = evidence.get(0);
        String outcome_var = evidence_outcome.get(0);
        evidence.remove(0);
        evidence_outcome.remove(0);


        List<Factor> factors = new ArrayList<Factor>();
        int removeIndex = 0;


        //create factor for each variable
        for (String variable : varList) {
            Factor factor = new Factor();
            factor.toFactor(this.bayesian.get(variable), evidence, evidence_outcome);

            //If an instantiated CPT becomes one
            //valued, discard the factor
            if (factor.getSize() > 1) {
                factors.add(factor);
            }

        }


        List<Integer> index_join = new ArrayList<>();
        List<String> query_var_to_Join = new ArrayList<>();
        query_var_to_Join.add(var);
        query_var_to_Join.addAll(evidence);


        // While loop, as long as there is a Hidden variable we will execute:
        // 1. A variable 𝐻∈𝐻𝑖𝑑𝑑𝑒𝑛 is chosen.
        // 2. We will unite (join) all the factors that contain H.
        // 3. We will eliminate the variable of H from the unified factor that we received.
        while (!hidden_list.isEmpty()) {
            String hidden_var = hidden_list.remove(0);

            List<Integer> index_factor = new ArrayList<>();


            while (factors.size() > 1) {
                index_factor.clear();

                //find all the factors that contains the hidden var
                //and save there index
                for (int i = 0; i < factors.size(); i++) {

                    if (factors.get(i).getVarOfTheFactor().contains(hidden_var)) {
                        index_factor.add(i);
                    }
                }


                //if there is only one factor that contains the hidden var, we save this factor
                if (index_factor.size() < 2) break;

                // find two factor to make join
                index_join = findJoin(factors, index_factor, query_var_to_Join);

                //join the two factor and add the new to the list of the factor
                //Factor joinResult = factors.get(index_join.get(0)).join(factors.get(index_join.get(1)));
                //factors.set(index_join.get(0), joinResult);
                factors.set(index_join.get(0), factors.get(index_join.get(0)).join(factors.get(index_join.get(1))));
                removeIndex = index_join.get(1);
                factors.remove(removeIndex);
                //System.out.println(factors);

            }
            factors.get(index_join.get(0)).elimination(hidden_var);
        }

        //Join all remaining factors
        for (int i = 0; i < factors.size() - 1; i++) {
            factors.set(0, factors.get(0).join(factors.get(1)));
            factors.remove(1);
        }

        // get the number of multiply and sum operations
        int multi_counter = 0, plus_counter = 0;
        multi_counter += factors.get(0).getMulti_counter();
        plus_counter += factors.get(0).getPlus_counter();
        double probability = 0, sum =0;
        int var_index = factors.get(0).getVarOfTheFactor().indexOf(var);

        Set<List<String>> set = factors.get(0).getFactor().keySet();
        List<List<String>> first_list = new ArrayList<>(set);

        Set<List<String>> set1 = factors.get(0).getFactor().get(first_list.get(0)).keySet();
        List<List<String>> outcome_this_factor = new ArrayList<>(set1);
        List<String> outcome = new ArrayList<>();
        boolean bool = true;
        //normalization of the probability
        for (List<String> i : outcome_this_factor) {
            outcome = new ArrayList<>(i);
            if (bool) {
                sum = factors.get(0).getFactor().get(first_list.get(0)).get(outcome);
                bool = false;
            } else {
                sum += factors.get(0).getFactor().get(first_list.get(0)).get(outcome);
                plus_counter++;
            }
            if (outcome.get(var_index).equals(outcome_var)) {
                probability = factors.get(0).getFactor().get(first_list.get(0)).get(outcome);
            }

        }

        DecimalFormat df = new DecimalFormat("0.00000");
        String formatted = df.format(probability / sum);

        ans += formatted + "," + plus_counter + "," + multi_counter + "\n";
        return ans;
    }


    /**
     * this method iterates through all pairs of factors in index_factor,
     * calculates the number of lines that would be added by joining them,
     * and keeps track of the pair that would add the minimum number of lines.
     * If there is a tie for the minimum number of lines, it selects
     * the pair with the lowest sum of ASCII values for the variables in the difference
     * between the two factor's variables
     *
     * @param factors
     * @param index_factor
     * @param query_var_to_Join
     * @return
     */
    private List<Integer> findJoin(List<Factor> factors, List<Integer> index_factor, List<String> query_var_to_Join) {
        List<Integer> minIndexes = new ArrayList<>();
        int minNumLines = Integer.MAX_VALUE;

        for (int i = 0; i < index_factor.size() - 1; i++) {
            Factor factorA = factors.get(index_factor.get(i));
            Factor factorB = factors.get(index_factor.get(i + 1));
            List<String> intersect = findIntersect(factorA.getVarOfTheFactor(), factorB.getVarOfTheFactor(), query_var_to_Join);

            int numLines = 1;
            for (String varName : intersect) {
                Variable var = this.bayesian.get(varName);
                numLines *= var.getVar_outcome().size();
            }
            numLines = factorA.getSize() * factorB.getSize() / numLines;

            if (numLines < minNumLines) {
                minNumLines = numLines;
                minIndexes.clear();
                minIndexes.add(index_factor.get(i));
                minIndexes.add(index_factor.get(i + 1));
            } else if (numLines == minNumLines) {
                int sumAscii = sumAsciiValues(differences(factorA.getVarOfTheFactor(), factorB.getVarOfTheFactor(), query_var_to_Join));

                int minAscii = 0;
                for (String varName : differences(factors.get(minIndexes.get(0)).getVarOfTheFactor(),
                        factors.get(minIndexes.get(1)).getVarOfTheFactor(), query_var_to_Join)) {
                    for (int j = 0; j < varName.length(); j++) {
                        minAscii += (int) varName.charAt(j);
                    }
                }

                if (sumAscii < minAscii) {
                    minIndexes.clear();
                    minIndexes.add(index_factor.get(i));
                    minIndexes.add(index_factor.get(i + 1));
                }
            }
        }

        return minIndexes;
    }


    /**
     * Helper function that finds the intersection of two lists of strings.
     * It takes in two lists listA and listB, and a third list queryVarsToJoin,
     * and returns a new list containing only the elements that are present in both listA and listB.
     */
    private List<String> findIntersect(List<String> listA, List<String> listB, List<String> queryVarsToJoin) {
        List<String> intersect = new ArrayList<>();
        for (String element : listA) {
            if (listB.contains(element) && queryVarsToJoin.contains(element)) {
                intersect.add(element);
            }
        }
        return intersect;
    }

    /**
     * in order to calculate the sum of ASCII values
     * This function iterates through both listA and listB,
     * and adds elements to the diff list if they are not present in the other
     * list and are present in queryVarsToJoin. At the end, it returns the diff list.
     * it is used to find the difference between the two lists of variables
     *
     * @param listA
     * @param listB
     * @param queryVarsToJoin
     * @return
     */
    private List<String> differences(List<String> listA, List<String> listB, List<String> queryVarsToJoin) {
        List<String> diff = new ArrayList<>();
        for (String element : listA) {
            if (!listB.contains(element) && queryVarsToJoin.contains(element)) {
                diff.add(element);
            }

        }
        for (String element : listB) {
            if (!listA.contains(element) && queryVarsToJoin.contains(element)) {
                diff.add(element);
            }
        }
        return diff;
    }


    /**
     * @return the sum of the ASCII values of the given string
     */
    private static int sumAsciiValues(List<String> varNameList) {
        int sum = 0;
        for (int i = 0; i < varNameList.size(); i++) {
            for (int j = 0; j < varNameList.get(i).length(); j++) {
                sum += (int) varNameList.get(i).charAt(j);
            }
        }
        return sum;
    }


    /**
     * his method checks if a variable is an ancestor of query or evidence variables
     * using (BFS) algorithm.
     */
    private boolean isAncestor(String name, List<String> parents) {

        Queue<String> queue = new LinkedList<>();
        queue.addAll(parents);
        while (!queue.isEmpty()) {
            String var = queue.poll();
            // If the name variable is found in any of the parent lists,
            if (var.equals(name)) {
                return true;
            }
            queue.addAll(bayesian.get(var).getParentsName());
        }

        return false;
    }


}
