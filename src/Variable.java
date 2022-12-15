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
                    prob.put(this.var_outcome.get(j), this.var_cpt.get(i++));
                }
                store.add(prob);
            }


            //map all the possible sequences of the parents and the variable outcome
            for (int i = 0; i < Sequences.size(); i++) {
                this.cptMap.put(Sequences.get(i), new HashMap<>(store.get(i)));
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

        for (int i = 0; i < parents_outcome.size(); i++) {
           // System.out.println(parents_outcome.get(i));

            event_outcome.clear();
            //-------------------------------------

            if (parents_outcome.get(i)!= null) {
                event_outcome.addAll(parents_outcome.get(i));
            }

            // variable is one of the evidence
            // use only his outcome from the query
            // with his parent outcome value
            if (evidence.contains(this.name)) {
                event_outcome.add(evidence_outcome.get(evidence.indexOf(name)));
                List<String> key = new ArrayList<>(event_outcome);
                ans.put(key,this.cptMap.get(parents_outcome.get(i)).get(evidence_outcome.get(evidence.indexOf(name))));
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

        return ans;
    }




    public Iterator<List<String>> parentsValsIter() {
        //	System.out.println(events_prob.keySet());
        return this.cptMap.keySet().iterator();
    }



    public Map<List<String>, Double> getEventsWith(Map<String, String> evidence) {
//		System.out.println(this.name);
//		System.out.println(evidence);

        Map<List<String>, Double> events_with_prob = new HashMap<List<String>, Double>();
        List<String> event;
        Iterator<List<String>> iter = parentsValsIter();
//		System.out.println(iter.next());
        boolean toAdd = true;
        ArrayList<String> event_vals = new ArrayList<String>();


        //run on [null] -> {t=0.1, f=0.9}
        while (iter.hasNext()) {
            event = iter.next();
            //System.out.println("event " + event);
            toAdd = true;

            // if evidence is child of the variable
            // {j, m}
            for (String var_name : evidence.keySet()) {
                if (this.getParentsName().contains(var_name) && !event.get(this.getParentsName().indexOf(var_name)).equals(evidence.get(var_name)))
                    toAdd = false;
            }
            // add
            if (toAdd) {
                event_vals.clear();

                //add the value
                if (event != null)
                    event_vals.addAll(event);
                //  the variable is one of the evidence
                if (evidence.containsKey(name)) {
//					System.out.println(event_vals);
                    event_vals.add(evidence.get(name));
                    //System.out.println(event_vals);
                    events_with_prob.put(new ArrayList<String>(event_vals),
                            this.cptMap.get(event).get(evidence.get(name)));
                    //System.out.println(events_with_prob);
                }
                else {
                    // event val size = 0
                    int event_vals_size = event_vals.size();
                    //	System.out.println(event_vals);
                    // run on the outcome list { t ,f }
                    for (String value : this.var_outcome) {
                        // 0 < 0
                        if (event_vals_size < event_vals.size()) {
                            event_vals.remove(event_vals_size);
                            //System.out.println(event_vals);
                        }
                        event_vals.add(value);
                        //System.out.println(event_vals);
                        //	System.out.println(events_prob.get(event).get(value));
                        events_with_prob.put(new ArrayList<String>(event_vals), this.cptMap.get(event).get(value));
                        //	System.out.println(events_with_prob);
                    }
                }
            }
        }
//		System.out.println("===== ");
//		System.out.println(this.name);
//		System.out.println(this.events_prob);
//		System.out.println(events_with_prob + " \n ===== "  );
        return events_with_prob;
    }
}
