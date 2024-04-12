import java.util.Random;

public class Perceptron {
    static int objectCounter = 0;
    static int connectionCounter = 0;
    private float[] inputs;
    private float[] weights;
    private Perceptron[] predecessors = null;
    private Perceptron[] successors = null;
    private int predIndex;
    private int sucIndex;

    Perceptron(int inputsSize) {
        Random random = new Random();
        this.inputs = new float[inputsSize];
        this.weights = new float[inputsSize];
        for (int i = 0; i < inputsSize; i++) {
            weights[i] = random.nextFloat();
        }
        this.predIndex = 0;
        this.sucIndex = 0;
        objectCounter++;
    }

    public void getOutsideInputs() {
            float[] outputs = new float[inputs.length];
            for (int i = 0; i < predecessors.length; i++) {
                outputs[i] = predecessors[i].getOutput()*weights[i];
            }
            this.inputs = outputs;
    }

    float getOutput(){
        float sum = 0.0f;
        for (int i = 0; i < inputs.length; i++) {
            sum+=inputs[i] * weights[i];
        }
        return (float) ((2/(1+Math.exp(-sum)))-1);
    }

    public static void setPredConnection(Perceptron pred, Perceptron curr){
        pred.successors[pred.sucIndex] = curr;
        curr.predecessors[curr.predIndex] = pred;
        pred.sucIndex++;
        curr.predIndex++;
        connectionCounter++;
    }

    public Perceptron[] getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(Perceptron[] predecessors) {
        this.predecessors = predecessors;
    }

    public void setSuccessors(Perceptron[] successors) {
        this.successors = successors;
    }

    public float[] getWeights() {
        return weights;
    }
    public void setInputs(float[] inputs) {
        this.inputs = inputs;
    }
    public Perceptron[] getSuccessors() {
        return successors;
    }

    public float[] getInputs() {
        return inputs;
    }
}
