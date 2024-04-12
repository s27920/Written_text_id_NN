import java.util.Random;

public class Perceptron {
    //TODO delete before finishing the project it exists for testing purposes
    static int objectCounter = 0;

    private float[] inputs;
    private float[] weights;
    private Perceptron[] predecessors = null;
    private Perceptron[] successors = null;
    private int sucIndex;

    Perceptron(int inputsSize) {
        Random random = new Random();
        this.inputs = new float[inputsSize];
        this.weights = new float[inputsSize];
        for (int i = 0; i < inputsSize; i++) {
            weights[i] = random.nextFloat();
        }
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
        //TODO think if this isn't causing issues through lowering precision and potential rounding errors especially in fringe cases
        double v = ((2 / (1 + Math.exp(-sum))) - 1);
        return (float) v;
    }

    public Perceptron[] getPredecessors() {
        return predecessors;
    }

    public void twoWayLink(Perceptron[] predecessors) {
        this.setPredecessors(predecessors);
        for (int i = 0; i < predecessors.length; i++) {
            predecessors[i].addSuccessor(this, predecessors[i].getSucIndex());
            Network.connectionCounter +=1;
        }
    }

    public void setSuccessors(Perceptron[] successors) {
        this.successors = successors;
    }
    public void addSuccessor(Perceptron perceptron, int index){
        this.successors[index] = perceptron;
        this.sucIndex++;
    }

    public int getSucIndex() {
        return sucIndex;
    }

    public float[] getWeights() {
        return weights;
    }
    public void setInputs(float[] inputs) {
        this.inputs = inputs;
    }
    public void setWeights(float[] weights) {
        this.weights = weights;
    }
    public Perceptron[] getSuccessors() {
        return successors;
    }

    public float[] getInputs() {
        return inputs;
    }

    public void setPredecessors(Perceptron[] predecessors) {
        this.predecessors = predecessors;
    }
}
