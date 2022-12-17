//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class Main {
//    public static List<List<String>> getAllCombinations(List<List<String>> lists) {
//        List<List<String>> combinations = new ArrayList<>();
//        int[] indices = new int[lists.size()];
//        boolean done = false;
//        while (!done) {
//            List<String> combination = new ArrayList<>();
//            for (int i = 0; i < lists.size(); i++) {
//                combination.add(lists.get(i).get(indices[i]));
//            }
//            combinations.add(combination);
//
//            // Update indices
//            for (int i = lists.size() - 1; i >= 0; i--) {
//                indices[i]++;
//                if (indices[i] >= lists.get(i).size()) {
//                    indices[i] = 0;
//                } else {
//                    break;
//                }
//            }
//            done = true;
//            for (int index : indices) {
//                if (index != 0) {
//                    done = false;
//                    break;
//                }
//            }
//        }
//        return combinations;
//    }
//
//    public static void main(String[] args) {
//        List<List<String>> lists = new ArrayList<>();
//        lists.add(Arrays.asList("a", "b"));
//        lists.add(Arrays.asList("x", "y", "z"));
//        lists.add(Arrays.asList("1", "2"));
//
//        List<List<String>> combinations = getAllCombinations(lists);
//        for (List<String> combination : combinations) {
//            System.out.println(combination);
//        }
//    }
//}
