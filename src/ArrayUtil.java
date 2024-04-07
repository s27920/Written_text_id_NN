public class ArrayUtil {
    public static int findMaxIndex(float[] floats){
        float maxVal = -1.0f;
        int indexToReturn = -1;
        for (int i = 0; i < floats.length; i++) {
            float currVal = floats[i];
            if (currVal > maxVal){
                maxVal = currVal;
                indexToReturn = i;
            }
        }
        return indexToReturn;
    }
}
