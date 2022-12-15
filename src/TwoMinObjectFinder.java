//import java.util.Arrays;
//import java.util.List;
//
//public class TwoMinObjectFinder {
//    public static void main(String[] args) {
//        // Create a list of objects to compare
//        List<MyObject> objects = createObjectList();
//
//        // Find the two minimum objects in the list
//        MyObject min1 = null;
//        MyObject min2 = null;
//        for (MyObject obj : objects) {
//            if (min1 == null || obj.size < min1.size) {
//                min2 = min1;
//                min1 = obj;
//            } else if (min2 == null || obj.size < min2.size) {
//                min2 = obj;
//            } else if (min1.size == min2.size) {
//                // If there are multiple objects with the same size,
//                // compare their ASCII values to find the minimum
//                int min1AsciiSum = sumAsciiValues(min1.tagName);
//                int min2AsciiSum = sumAsciiValues(min2.tagName);
//                int objAsciiSum = sumAsciiValues(obj.tagName);
//                if (objAsciiSum < min1AsciiSum) {
//                    min2 = min1;
//                    min1 = obj;
//                } else if (objAsciiSum < min2AsciiSum) {
//                    min2 = obj;
//                }
//            }
//        }
//
//        // Print the two minimum objects found
//        System.out.println("First minimum object: " + min1.tagName);
//        System.out.println("Second minimum object: " + min2.tagName);
//    }
//
//    // Returns the sum of the ASCII values of the given string
//    private static int sumAsciiValues(String str) {
//        int sum = 0;
//        for (int i = 0; i < str.length(); i++) {
//            sum += (int) str.charAt(i);
//        }
//        return sum;
//    }
//
//    // Returns a list of MyObject instances for testing
//    private static List<MyObject> createObjectList() {
//        // Create and return a list of objects
//        // (actual implementation will depend on the details of the MyObject class)
//        return Arrays.asList(
//                new MyObject(3, "Object1"),
//                new MyObject(1, "Object2"),
//                new MyObject(2, "Object3"),
//                new MyObject(1, "Object4"),
//                new MyObject(2, "Object5")
//        );
//    }
//
//    // This is a simple example class to represent the objects in the list
//    private static class MyObject {
//        int size;
//        String tagName;
//
//        public MyObject(int size, String tagName) {
//            this.size = size;
//            this.tagName = tagName;
//        }
//    }
//}
