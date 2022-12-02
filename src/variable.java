import java.util.ArrayList;
import java.util.List;

public class variable {

    private String name;
    private List<variable> parents;
    private List<String> outcome;
    private List<variable> child;
    private List<Double> cpt;
    // add cpt

    public variable(String name) {
        this.name = name;
        this.parents = new ArrayList<>();
        this.outcome = new ArrayList<>();
        this.child = new ArrayList<>();
        this.cpt = new ArrayList<>();

    }

    public String getName() {
        return this.name;
    }

    public List<variable> get_parents() {
        return this.parents;
    }

    @Override
    public String toString() {

        if (this.get_parents().size() > 0) {
            return "Name:" + this.name + "\n" + "Parents " + this.parents.toString();
        } else {
            return "Name:" + this.name;
        }
    }

    public List<variable> getChild() {
        return child;
    }

    public void setChild(List<variable> child) {
        this.child = child;
    }

    public List<variable> getParents() {
        return parents;
    }

    public void setParents(List<variable> parents) {
        this.parents = parents;
    }

    public List<String> getOutcome() {
        return outcome;
    }

    public void setOutcome(List<String> outcome) {
        this.outcome = outcome;
    }

    public List<Double> getCpt() {
        return cpt;
    }
}
