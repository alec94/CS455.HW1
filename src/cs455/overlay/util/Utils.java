package cs455.overlay.util;

/**
 * Created by Alec on 1/23/2017.
 * Collection of handy methods
 */
public class Utils {

    public static String[] removeElement(String[] array, String element){
        String[] temp = new String[array.length - 1];

        int j = 0;
        for (String anArray : array) {
            if (!anArray.equals(element)) {
                temp[j] = anArray;
                j++;
            }
        }

        return temp;
    }
}
