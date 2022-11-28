import java.util.ArrayList;
import java.util.List;

public class variable {

    private String name;
    private ArrayList<variable> parents;
    // add cpt

    public variable(String name) {
        this.name = name;
        this.parents = new ArrayList<>();
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

}
