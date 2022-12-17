
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

        List<List<String>> queryParameter = getParameter(query); //{var_query , var_outcome}

        if (hasAnswer(queryParameter)) {
            return directAns(queryParameter);
        } else {
            String ans = "";
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
            //hidden_outcome = generateAllSequences(hidden_outcome);
            hidden_outcome = getAllCombinations(hidden_outcome);
//            System.out.println("all outcome of the hidden " + hidden_outcome);

            all_var.clear();
            all_var.addAll(queryParameter.get(0));
            all_var.addAll(hidden_list);
            //System.out.println(all_var);


            //define the counter for the number of sum and multiply oppressions
            int plus_counter = 0;
            int multi_counter = 0;

            double sumOfOuterQuery, sumOfInnerQuery = 0, sumUpper = 0, sumAllQuery = 0;
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
                       // System.out.println("--- " + sumOfInnerQuery+ " --------------");
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
            String formatted = df.format(sumUpper / sumAllQuery);

            ans += formatted + "," + plus_counter + "," + multi_counter + "\n";
            return ans;
        }

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




    /**
     * Truth table:
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
//        List<List<String>> sequences = new ArrayList<>();
//        if (inputLists.isEmpty()) {
//            return sequences;
//        }
//
//        List<String> firstList = inputLists.get(0);
//        for (String value : firstList) {
//            List<String> sequence = new ArrayList<>();
//            sequence.add(value);
//            sequences.add(sequence);
//        }
//
//        for (int i = 1; i < inputLists.size(); i++) {
//            List<String> list = inputLists.get(i);
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
//      //  return sequences;

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


    public String VariableElimination(String query) {

        //{var_query , var_outcome}
        List<List<String>> queryParameter = getParameter(query);
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

       // System.out.println(varList);


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
      //  System.out.println(varList);
        List<Factor> factors = new ArrayList<Factor>();
        int removeIndex = 0;

        for (String s : varList) {
            Factor factor = new Factor();
            factor.toFactor(this.bayesian.get(s), evidence, evidence_outcome);


            //todo add the case if the factor is one line only
            if (factor.getSize() > 1) {
                factors.add(factor);
            }
        }

     //   System.out.println(factors);

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
                for (int i = 0; i < factors.size(); i++) {
                    // System.out.println(factors.get(i).getVarOfTheFactor());
                    if (factors.get(i).getVarOfTheFactor().contains(hidden_var)) {
                        index_factor.add(i);
                    }
                }
                //System.out.println(index_factor);

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

            ////////////////////////////////////
            for(int i=0; i<factors.size()-1; i++) {
                factors.set(0, factors.get(0).join(factors.get(1)));
                factors.remove(1);

            }

   //     System.out.println("--------------"+ factors.get(0).getMulti_counter());
            int sum_x=0,sum_plus=0;
            sum_x += factors.get(0).getMulti_counter();
            sum_plus += factors.get(0).getPlus_counter();
            int index_var = factors.get(0).getVarOfTheFactor().indexOf(var);

            double prob=0 , sum=-1;


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
                sum_plus++;
            }
            if (temp.get(index_var).equals(outcome_var)) {
                prob = factors.get(0).getFactor().get(first_list.get(0)).get(temp);
            }

        }

        return "" +new DecimalFormat("0.00000").format (prob/sum) +","+sum_plus+","+sum_x +"\n";


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

            for (String var_name:diff_var){
                var = this.bayesian.get(var_name);
                num_line *= var.getVar_outcome().size();
            }
//            System.out.println(factor_a.getSize());
//            System.out.println(factor_b.getSize());
//            System.out.println(factor_a);
//            System.out.println(factor_b);
            num_line = num_line - Math.max(factor_a.getSize(),factor_b.getSize());


            if (num_line<min_num_line){
                min_num_line =num_line;}

            if (num_lines_and_indexes.containsKey(num_line)){
                List<Integer> indexs = num_lines_and_indexes.get(num_line);
                indexs.add(index_factor.get(i));
                indexs.add(index_factor.get(i+1));
            }else {
                index_to_put = new ArrayList<Integer>();
                index_to_put.add(index_factor.get(i));
                index_to_put.add(index_factor.get(i+1));
                num_lines_and_indexes.put(num_line, index_to_put);
            }
        }
        if(num_lines_and_indexes.get(min_num_line).size() == 2)
            return num_lines_and_indexes.get(min_num_line);
        else {
            int min_ascii = Integer.MAX_VALUE;
            ArrayList<Integer> indexes_to_return = new ArrayList<Integer>();
            for(int i = 0; i < num_lines_and_indexes.get(min_num_line).size()-1; i+=2 ) {
                int sum_ascii = 0;
                for(String var_name : findDiffVars(factors.get(num_lines_and_indexes.get(min_num_line).get(i)).getVarOfTheFactor(),
                        factors.get(num_lines_and_indexes.get(min_num_line).get(i+1)).getVarOfTheFactor(),query_var_to_Join)) {
                    for(int j = 0; j < var_name.length(); j++)
                        sum_ascii += (int)var_name.charAt(j);
                }
                if(sum_ascii < min_ascii) {
                    min_ascii = sum_ascii;
                    indexes_to_return.clear();
                    indexes_to_return.add(num_lines_and_indexes.get(min_num_line).get(i));
                    indexes_to_return.add(num_lines_and_indexes.get(min_num_line).get(i+1));
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

    private boolean ancestor(String name, List<String> evidence) {
        if (evidence.isEmpty()) return false;
        if (evidence.contains(name)) return true;
        for (String s : evidence) {
            if (ancestor(name, bayesian.get(s).getParentsName()))
                return true;
        }
        return false;


    }


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


}

