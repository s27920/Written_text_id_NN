import java.util.Arrays;

public class Network {
    private Perceptron[] initLayer;
    private Perceptron[] terminalLayer;
    private final float learningRate = 0.1f;
    private byte currentLabel;

    public static int connectionCounter= 0;
    public Network(int[] structure) {
        Perceptron[][] layers = createNetwork(structure);
        this.initLayer = layers[0];
        this.terminalLayer = layers[1];
    }
    public void train(Image[] imagesToClassify, int index){

        long start = System.nanoTime();
        System.out.println("training network.\nPlease wait...");
        int total = 0;
        int correct = 0;
        while (index < imagesToClassify.length){
            float[] activations = getActivations(imagesToClassify[index]);
            if (classify(activations) == currentLabel){
                correct++;
            }
            total++;
            backProp();
            index+=1;
        }
        System.out.println(( ("training time: " + ((int)(System.nanoTime() - start) / 1_000_000_000))) + " seconds");
        System.out.println("correct: " + correct);
        System.out.println("total: " + total);
        System.out.println("accuracy: " + ((double) correct)/ ((double) total) +"%");
        for (int i = 0; i < 20; i++) {
            train(imagesToClassify, 0);
        }
    }

    public float[] getActivations(Image imageToClassify){
        byte[] pixelVector = imageToClassify.getPixels();
        for (int i = 0; i < initLayer.length; i++) {
            initLayer[i].setInputs(new float[]{(float) ((pixelVector[i] & 0xFF)/255.0)});
        }
        Perceptron[] currLayer = initLayer;
        for(;currLayer.length > 0; currLayer = currLayer[0].getSuccessors()){
            for (Perceptron current : currLayer) {
                current.getOutsideInputs();
            }
            if (currLayer[0].getSuccessors() == null) {
                break;
            }
        }

        currentLabel = imageToClassify.getLabel();
        int currLength = currLayer.length;
        float []activations = new float[currLength];
        for (int i = 0; i < currLength; i++) {
            activations[i] = currLayer[i].getOutput();
        }
        return activations;
    }

    public void backProp(){
        float[] correct = new float[10];
        correct[currentLabel] = 1.0f;

        Perceptron[] currLayer = terminalLayer;
        float[] sucErrorGradient = new float[currLayer.length];

        for (int i = 0; i < currLayer.length; i++) {
            float[] weights = currLayer[i].getWeights();
            float[] inputs = currLayer[i].getInputs();
            float error = (correct[i] - currLayer[i].getOutput());
            float output = currLayer[i].getOutput();
            float errorGradient = (error * ((1-output)*output));
            for (int j = 0; j < weights.length; j++) {
                weights[j] += learningRate * errorGradient * inputs[j];
            }
            sucErrorGradient[i] = errorGradient;
        }
        currLayer = currLayer[0].getPredecessors();
        while (currLayer != null){
            Perceptron[] sucLayer = currLayer[0].getSuccessors();
            float[] tmpErrorGradient = new float[currLayer.length];
            for (int i = 0; i < currLayer.length; i++) {
                float errorGradient = 0.0f;
                for (int j = 0; j < sucLayer.length; j++) {
                    float[] weights = sucLayer[j].getWeights();
                    for (int k = 0; k < weights.length; k++) {
                        if (i == k){
                            errorGradient += sucErrorGradient[j] * weights[k];
                        }
                    }
                }
                float[] inputs = currLayer[i].getInputs();
                float[] weights = currLayer[i].getWeights();
                float output = currLayer[i].getOutput();
                for (int j = 0; j < weights.length; j++) {
                    weights[j] += learningRate * ((1-output)*output) * errorGradient * inputs[j];
                }
                tmpErrorGradient[i] = errorGradient;
            }
            sucErrorGradient = tmpErrorGradient;
            currLayer = currLayer[0].getPredecessors();
        }
    }

    public int classify(float[] activations){
        return ArrayUtil.findMaxIndex(activations);
    }


    //TODO network initialization could probably use some cleanup
    private Perceptron[][] createNetwork(int[] structure){
        int intiLayerSize = structure[0];
        Perceptron[] initLayer = new Perceptron[intiLayerSize];
        for (int i = 0; i < intiLayerSize; i++) {
            initLayer[i] = new Perceptron(1);
            initLayer[i].setSuccessors(new Perceptron[structure[1]]);
            initLayer[i].initWeights();
        }
        Perceptron[] terminalLayer = hiddenLayers(structure, initLayer);

        return new Perceptron[][]{initLayer, terminalLayer};
    }
    private Perceptron[] hiddenLayers(int[] structure, Perceptron[] previousLayer){
        Perceptron[] currLayer = new Perceptron[0];
        for (int i = 1; i < structure.length; i++) {
            currLayer = new Perceptron[structure[i]];
            for (int j = 0; j < structure[i]; j++) {
                Perceptron perceptron = new Perceptron(structure[i-1]);
                perceptron.setPredecessors(new Perceptron[structure[i-1]]);
                if (i+1 < structure.length){
                    perceptron.setSuccessors(new Perceptron[structure[i+1]]);
                }
                perceptron.twoWayLink(previousLayer);
                perceptron.initWeights();
                currLayer[j] = perceptron;
            }
            previousLayer = currLayer;
        }
        System.out.println("network constructed successfully");
        return currLayer;
    }
}
