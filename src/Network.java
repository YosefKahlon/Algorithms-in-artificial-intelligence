
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
        System.out.println(query);
        //{variable of the query ,

        //the outcomes of the query variables}
        List<List<String>> queryParameter = getParameter(query);

        //check if there is a direct answer from the data todo
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



            System.out.println(hidden_outcome_of_query);

            System.out.println("var outcome" + bayesian.get(all_var.get(0)).getVar_outcome());
            // run on the query variable outcome list
            for (int i = 0; i < hidden_outcome_of_query.size(); i++) {
                sumOfOuterQuery = 0;
                startOuterQuery = true;

                if (!upper_part) {
                    System.out.println("--------------------------------------------------------------------");

//                    if (i== 1){
                    //set outcome for the query
                   // queryParameter.get(1).set(0, bayesian.get(all_var.get(0)).getVar_outcome().get(i));
                    queryParameter.get(1).set(0, hidden_outcome_of_query.get(i));
                        System.out.println("var outcome after change " + hidden_outcome_of_query.get(0));

////                    if (i== 2){
//                        queryParameter.get(1).set(0, hidden_outcome_of_query.get(1));
//                        System.out.println("var outcome after change " + hidden_outcome_of_query.get(1));
                    }
                  //  System.out.println("var outcome after change " + bayesian.get(all_var.get(0)).getVar_outcome()
                    //  .get(i));
              //  }

                //every iteration try different combination of the hidden variable with query outcome
                for (int j = 0; j < hidden_outcomes.size(); j++) {
                    sumOfInnerQuery = 1;


                    all_outcome.clear();
                    all_outcome.addAll(queryParameter.get(1));
                    all_outcome.addAll(hidden_outcomes.get(j));

                    System.out.println(all_outcome);

                    startInnerQuery = true;
                    for (int k = 0; k < all_var.size(); k++) {

                        //start a new inner query P(B=T,J=T,M=T,A=T,E=T)
                        //so also start to multiply from here
                        if (startInnerQuery) {
                            sumOfInnerQuery = getProbability( //todo
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


    /**
     * @param var
     * @param varOutcome
     * @param VarList
     * @param outcomeList
     * @return The probabilistic value.
     */
    private double getProbability(String var, String varOutcome, List<String> VarList, List<String> outcomeList) {

        //System.out.println(var);

        // not have parents,
        // so we can get the value directly from the CPt of this variable
        if (bayesian.get(var).getVar_parents().isEmpty()) {
//            System.out.println("empty");
//            System.out.println(this.bayesian.get(var).getCptMap());
//            System.out.println(this.bayesian.get(var).getCptMap().get(null).get(varOutcome));
            return this.bayesian.get(var).getCptMap().get(null).get(varOutcome);
        }
        // do have parents
        else {
            //search the index of the parent, so we can find witch outcome value need to take.
            int[] parentsIndex = new int[this.bayesian.get(var).getVar_parents().size()];
            List<String> name = new ArrayList<>();
            int j = 0;

            for (int i = 0; i < VarList.size(); i++) {
                if (this.bayesian.get(var).getParentsName().contains(VarList.get(i))) {
                    parentsIndex[this.bayesian.get(var).getVar_parents().indexOf(this.bayesian.get(VarList.get(i)))
                            ] = i;
                    name.add(VarList.get(i));
                    j++;
                    //}
                    if (j >= parentsIndex.length) break;
                }

            }
            // add the outcomes and return the probability
            List<String> parents_outcome = new ArrayList<>();
            for (int i = 0; i < this.bayesian.get(var).getVar_parents().size(); i++) {
                parents_outcome.add(outcomeList.get(parentsIndex[i]));
            }
        //    System.out.println("not empty" + this.bayesian.get(var).getCptMap());
//            System.out.println(this.bayesian.get(var).getCptMap().get(parents_outcome));
//            System.out.println(name);
//            System.out.println(parents_outcome);
//            System.out.println(this.bayesian.get(var).getCptMap().get(parents_outcome).get(varOutcome));
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

        //prants list
        if (this.bayesian.get(queryParameter.get(0).get(0)).getVar_parents().size() != queryParameter.get(0).size() - 1) {
            return false;
        }

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


    public String VariableElimination(String query) {

        //{variable of the query ,
        //the outcomes of the query variables}
        List<List<String>> queryParameter = getParameter(query);

        //check if there is a direct answer from the data todo
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
                if (ancestor(variable.getName(), queryParameter.get(0))) {
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


    private String VariableElimination(List<String> evidence, List<String> evidence_outcome, List<String> hidden_list, List<String> varList) {

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
        List<String> query_var_to_Join = new ArrayList<>(); //todo delete
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
                index_join = findJoin(factors, index_factor, query_var_to_Join); //todo i did not finish

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
        int multi_counter = 0, Plus_counter = 0;
        multi_counter += factors.get(0).getMulti_counter();
        Plus_counter += factors.get(0).getPlus_counter();


        int index_var = factors.get(0).getVarOfTheFactor().indexOf(var);

        double prob = 0, sum = -1;


        Set<List<String>> set = factors.get(0).getFactor().keySet();
        List<List<String>> first_list = new ArrayList<>(set);

        Set<List<String>> set1 = factors.get(0).getFactor().get(first_list.get(0)).keySet();
        List<List<String>> outcome_this_factor = new ArrayList<>(set1);
        List<String> temp = new ArrayList<>();
        for (List<String> strings : outcome_this_factor) {
            temp = new ArrayList<>(strings);
            if (sum == -1) {
                sum = factors.get(0).getFactor().get(first_list.get(0)).get(temp);
            } else {
                sum += factors.get(0).getFactor().get(first_list.get(0)).get(temp);
                Plus_counter++;
            }
            if (temp.get(index_var).equals(outcome_var)) {
                prob = factors.get(0).getFactor().get(first_list.get(0)).get(temp);
            }

        }

        return "" + new DecimalFormat("0.00000").format(prob / sum) + "," + Plus_counter + "," + multi_counter + "\n";


    }

    private List<Integer> findJoin(List<Factor> factors, List<Integer> index_factor, List<String> query_var_to_Join) {


//        List<Factor> factors1 = new ArrayList<>();
//        for (int i = 0; i < index_factor.size(); i++) {
//            factors1.add(factors.get(index_factor.get(i)));
//        }
//
//
//        // Create a list of objects to compare
////        List<TwoMinObjectFinder.MyObject> objects = createObjectList();
//
//        // Find the two minimum objects in the list
//        Factor min1 = null;
//        Factor min2 = null;
//        for (Factor obj : factors1) {
//            if (min1 == null || obj.getSize() < min1.getSize()) {
//                min2 = min1;
//                min1 = obj;
//            } else if (min2 == null || obj.getSize() < min2.getSize()) {
//                min2 = obj;
//            } else if (min1.getSize() == min2.getSize()) {
//                // If there are multiple objects with the same size,
//                // compare their ASCII values to find the minimum
//                int min1AsciiSum = sumAsciiValues(min1.getVarOfTheFactor());
//                int min2AsciiSum = sumAsciiValues(min2.getVarOfTheFactor());
//                int objAsciiSum = sumAsciiValues(obj.getVarOfTheFactor());
//                if (min2AsciiSum < min1AsciiSum) {
//                    Factor temp = min1;
//                    min1 = min2;
//                    min2 = temp;
//                }
//                if (objAsciiSum < min1AsciiSum) {
//                    min2 = min1;
//                    min1 = obj;
//                } else if (objAsciiSum < min2AsciiSum) {
//                    min2 = obj;
//                }
//            }
//        }
////        // Print the two minimum objects found
////        System.out.println("First minimum object: " + min1.getVarOfTheFactor());
////        System.out.println("Second minimum object: " + min2.getVarOfTheFactor());
//        List<Integer> index = new ArrayList<>();
//        index.add(factors.indexOf(min1));
//        index.add(factors.indexOf(min2));
//        System.out.println(index);
//
//        return index;

        int min_num_line = Integer.MAX_VALUE;
        List<String> diff_var;
        List<Integer> index_to_put;

        Map<Integer, List<Integer>> num_lines_and_indexes = new HashMap<Integer, List<Integer>>();

        Variable var;

        Factor factor_a, factor_b;

        for (int i = 0; i < index_factor.size() - 1; i++) {
            int num_line = 1;
            factor_a = factors.get(index_factor.get(i));
            factor_b = factors.get(index_factor.get(i + 1));
            diff_var = findDiffVars(factor_a.getVarOfTheFactor(), factor_b.getVarOfTheFactor(), query_var_to_Join);


            //find how many line this variable will add
            for (String var_name : diff_var) {
                var = this.bayesian.get(var_name);
                num_line *= var.getVar_outcome().size();
            }
//            System.out.println(factor_a.getSize());
//            System.out.println(factor_b.getSize());
//            System.out.println(factor_a);
//            System.out.println(factor_b);
            num_line = num_line - Math.max(factor_a.getSize(), factor_b.getSize());

            // witch a pair of factor will add the less number of row
            if (num_line < min_num_line) {
                min_num_line = num_line;
            }

            if (num_lines_and_indexes.containsKey(num_line)) {
                List<Integer> indexs = num_lines_and_indexes.get(num_line);
                indexs.add(index_factor.get(i));
                indexs.add(index_factor.get(i + 1));
            } else {
                index_to_put = new ArrayList<Integer>();
                index_to_put.add(index_factor.get(i));
                index_to_put.add(index_factor.get(i + 1));
                num_lines_and_indexes.put(num_line, index_to_put);
            }
        }
        if (num_lines_and_indexes.get(min_num_line).size() == 2)
            return num_lines_and_indexes.get(min_num_line);
        else {
            int min_ascii = Integer.MAX_VALUE;
            ArrayList<Integer> indexes_to_return = new ArrayList<Integer>();
            for (int i = 0; i < num_lines_and_indexes.get(min_num_line).size() - 1; i += 2) {
                int sum_ascii = 0;
                for (String var_name : findDiffVars(factors.get(num_lines_and_indexes.get(min_num_line).get(i)).getVarOfTheFactor(),
                        factors.get(num_lines_and_indexes.get(min_num_line).get(i + 1)).getVarOfTheFactor(), query_var_to_Join)) {
                    for (int j = 0; j < var_name.length(); j++)
                        sum_ascii += (int) var_name.charAt(j);
                }
                if (sum_ascii < min_ascii) {
                    min_ascii = sum_ascii;
                    indexes_to_return.clear();
                    indexes_to_return.add(num_lines_and_indexes.get(min_num_line).get(i));
                    indexes_to_return.add(num_lines_and_indexes.get(min_num_line).get(i + 1));
                }
            }
            return indexes_to_return;
        }

    }


    private List<String> findDiffVars(List<String> vars_name_a, List<String> vars_name_b, List<String> query_vars) {
        List<String> diff_vars = new ArrayList<String>();
        for (String var_name : vars_name_a) {
            if ((!query_vars.contains(var_name) || query_vars.get(0).equals(var_name)) && !diff_vars.contains(var_name))
                diff_vars.add(var_name);
        }
        for (String var_name : vars_name_b) {
            if ((!query_vars.contains(var_name) || query_vars.get(0).equals(var_name)) && !diff_vars.contains(var_name))
                diff_vars.add(var_name);
        }
        return diff_vars;

    }

    // Returns the sum of the ASCII values of the given string
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
     * This method checks if a variable is an ancestor of query or evidence variables.
     */
    private boolean ancestor(String name, List<String> parents) {
        if (parents.isEmpty()) return false;
        if (parents.contains(name)) return true;
        for (String var : parents) {
            if (ancestor(name, bayesian.get(var).getParentsName()))
                return true;
        }
        return false;


    }

}
