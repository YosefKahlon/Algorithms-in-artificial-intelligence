import java.util.*;

public class Factor {

    private Map<List<String>, Map<List<String>, Double>> factor;
    private List<String> varOfTheFactor;
    //define the counter for the number of the multiply oppression
    private int multi_counter;
    private int plus_counter;

    public Factor() {
        this.factor = new HashMap<>();
        this.varOfTheFactor = new ArrayList<>();
        multi_counter = 0;
        plus_counter = 0;

    }

    public Map<List<String>, Map<List<String>, Double>> getFactor() {
        return factor;
    }

    public void toFactor(Variable var, List<String> evidence, List<String> evidence_outcome) {
        this.varOfTheFactor.addAll(var.getParentsName());
        this.varOfTheFactor.add(var.getName());

        //todo
        this.factor.put(this.varOfTheFactor, var.getCptLine(evidence, evidence_outcome));
        //System.out.println(this.factor);
    }

    public List<String> getVarOfTheFactor() {
        return varOfTheFactor;
    }

    @Override
    public String toString() {
        return factor.toString() + "\n";
    }

    public int getSize() {
        return factor.get(this.varOfTheFactor).size();
    }



    public Factor join(Factor factor) {

        Factor result = new Factor();
        Map<List<String>, Double> probability = new HashMap<>();
        List<String> new_outcome = new ArrayList<>();
        List<String> resultFactorVar = new ArrayList<>(this.varOfTheFactor);
        Map<Integer, Integer> varIndex = new HashMap<>();

        //define the counter for the number of the multiply oppression
        int multi_counter = 0;


        for (int i = 0; i < factor.varOfTheFactor.size(); i++) {
            String var = factor.varOfTheFactor.get(i);
            // add the massing variable to the result factor variable name
            if (!resultFactorVar.contains(var)) {
                resultFactorVar.add(factor.varOfTheFactor.get(i));
            } else {
                //save the index of both
                varIndex.put(
                        this.varOfTheFactor.indexOf(var),
                        factor.varOfTheFactor.indexOf(var));
            }

        }


        //get the list of the outcome of this factor
        Set<List<String>> set = this.factor.keySet();
        List<List<String>> first_list = new ArrayList<>(set);
        Set<List<String>> set1 = this.factor.get(first_list.get(0)).keySet();
        List<List<String>> outcome_this_factor = new ArrayList<>(set1);

        //get the list of the outcome of the other factor
        Set<List<String>> set2 = factor.factor.keySet();
        List<List<String>> second_list = new ArrayList<>(set2);
        Set<List<String>> set3 = factor.factor.get(second_list.get(0)).keySet();
        List<List<String>> outcome_other_factor = new ArrayList<>(set3);


        for (int i = 0; i < outcome_this_factor.size(); i++) {
            List<String> a = outcome_this_factor.get(i);
            for (int j = 0; j < outcome_other_factor.size(); j++) {
                List<String> b = outcome_other_factor.get(j);

                boolean bool = true;

             //   System.out.println(varIndex);
                for(int k : varIndex.keySet()){
                 //   System.out.println(k);
                   // System.out.println(a.get(k));
                   // System.out.println(b.get(varIndex.get(k)));
                    if (!a.get(k).equals(b.get(varIndex.get(k)))) {
                        bool = false;
                        break;
                    }

                }
                if (bool) {
                    new_outcome = new ArrayList<>(outcome_this_factor.get(i));
                    for (int k = 0; k < b.size(); k++) {
                        if (!varIndex.containsValue(k)) {
                            new_outcome.add(b.get(k));
                        }
                    }

                    probability.put(new_outcome,
                            this.factor.get(first_list.get(0)).get(a) * factor.factor.get(second_list.get(0)).get(b));
                    multi_counter++;
                }


            }

        }
        result.setParameter(resultFactorVar, probability);
        //result.factor.put(resultFactorVar, probability);
        add_Multi(multi_counter);
        add_Multi(factor.getMulti_counter());


        result.add_Plus(factor.getPlus_counter());
        result.add_Plus(getPlus_counter());
        result.add_Multi(getMulti_counter());

        return result;
    }


    private void setParameter(List<String> resultFactorVar, Map<List<String>, Double> probability) {
        this.factor.put(resultFactorVar, probability);
        this.varOfTheFactor = resultFactorVar;
    }

    public int getMulti_counter() {
        return multi_counter;
    }

    public void add_Multi(int multi_counter) {
        this.multi_counter += multi_counter;
    }

    public int getPlus_counter() {
        return plus_counter;
    }

    public void add_Plus(int plus_counter) {
        this.plus_counter += plus_counter;
    }

    public void elimination(String hidden_var) {

       // System.out.println(this.factor);
        Set<List<String>> set = this.factor.keySet();
        List<List<String>> first_list = new ArrayList<>(set);
        // System.out.println(first_list);
        Set<List<String>> set1 = this.factor.get(first_list.get(0)).keySet();
        List<List<String>> outcome_this_factor = new ArrayList<>(set1);
       // System.out.println(outcome_this_factor);

        int plus_counter = 0;
        Map<List<String>, Double> probability = new HashMap<>();
        List<String> temp = new ArrayList<>();


        for (int i = 0; i < outcome_this_factor.size(); i++) {
            temp = new ArrayList<>(outcome_this_factor.get(i));
          //  System.out.println(temp);
            temp.remove(this.varOfTheFactor.indexOf(hidden_var));
            if (probability.containsKey(temp)) {
                probability.replace(temp, probability.get(temp),
                        probability.get(temp) + this.factor.get(first_list.get(0)).get(outcome_this_factor.get(i)));
                plus_counter++;
            } else {
                probability.put(temp,
                        this.factor.get(first_list.get(0)).get(outcome_this_factor.get(i)));
                // System.out.println(probability);
            }
        }
        //System.out.println(probability);


        this.add_Plus(plus_counter);
      //  System.out.println("elimination");
//        result.add_Plus(getPlus_counter());
//        result.add_Multi(getMulti_counter());


      //  System.out.println("plus "+ plus_counter);
        this.varOfTheFactor.remove(hidden_var);
        setParameter(this.varOfTheFactor, probability);

    }


}
