import java.util.Random;

public class Perceptron {
    static int objectCounter = 0;
    static int connectionCounter = 0;

    byte[] inputs;
    float[] weights;
    Perceptron[] predecessor = null;
    Perceptron[] successor = null;
    int predIndex;
    int sucIndex;

    Perceptron(byte[] inputs) {
        Random random = new Random();
        int length = inputs.length;
        this.inputs = inputs;
        this.weights = new float[length];
        for (int i = 0; i < length; i++) {
            weights[i] = random.nextFloat();
        }
        this.predIndex = 0;
        this.sucIndex = 0;
        objectCounter++;
    }

    byte getOutput(){
        int sum = 0;
        for (int i = 0; i < inputs.length; i++) {
            sum+=inputs[i] * weights[i];
        }
        return (byte) ((byte) 1/(1+Math.exp(-sum)));
    }


    public static void setPredConnection(Perceptron pred, Perceptron curr){
        pred.successor[pred.sucIndex] = curr;
        curr.predecessor[curr.predIndex] = pred;
        pred.sucIndex++;
        curr.predIndex++;
        connectionCounter++;
    }
    //public static void setSucConnection(Perceptron curr, Perceptron suc){
    //    curr.successor[curr.sucIndex] = suc;
    //    suc.predecessor[suc.predIndex] = curr;
    //    curr.sucIndex++;
    //    suc.predIndex++;
    //}
}
