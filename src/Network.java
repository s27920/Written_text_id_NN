public class Network {
    private Perceptron[] initLayer;
    private Perceptron[] terminalLayer;
    private final float learningRate = 0.1f;
    private byte currentLabel;
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
            initLayer[i].setInputs(new float[]{(float) ((pixelVector[i] & 0xFF)/256.0)});
        }
        Perceptron tmp = initLayer[0];
        Perceptron[] successors = null;
        Perceptron[] tmpSuccessors;
        while ((tmpSuccessors = tmp.getSuccessors()) !=null){
            tmp = tmpSuccessors[0];
            for (int i = 0; i < tmpSuccessors.length; i++) {
                tmpSuccessors[i].getOutsideInputs();
            }
            successors = tmpSuccessors;
        }
        float []activations = new float[successors.length];
        for (int i = 0; i < successors.length; i++) {
            activations[i] = successors[i].getOutput();
        }
        currentLabel = imageToClassify.getNumVal();
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
            float errorGradient = (error * (1-output*output));
            for (int j = 0; j < weights.length; j++) {
                weights[j] += learningRate * errorGradient * inputs[j];
            }
            sucErrorGradient[i] = errorGradient;
        }
        currLayer = currLayer[0].getPredecessors();
        while (currLayer != null){
            Perceptron[] sucLayer = currLayer[0].getSuccessors();

            float[] tmpErrorGradient = new float[currLayer.length];
            float[] weights;
            float errorGradient;
            for (int i = 0; i < currLayer.length; i++) {
                errorGradient = 0.0f;
                for (int j = 0; j < sucLayer.length; j++) {
                    weights = sucLayer[j].getWeights();
                    for (int k = 0; k < weights.length; k++) {
                        if (i == k){
                            errorGradient += sucErrorGradient[j] * weights[k];
                            break;
                        }
                    }
                }
                float[] inputs = currLayer[i].getInputs();
                float[] currWeights = currLayer[i].getWeights();
                float output = currLayer[i].getOutput();
                for (int j = 0; j < currWeights.length; j++) {
                    currWeights[j] += learningRate * (1-output*output) * errorGradient * inputs[j];
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

    private Perceptron[][] createNetwork(int[] structure){
        int intiLayerSize = structure[0];
        Perceptron[] initLayer = new Perceptron[intiLayerSize];
        for (int i = 0; i < intiLayerSize; i++) {
            Perceptron perceptron = new Perceptron(1);
            perceptron.setSuccessors(new Perceptron[structure[1]]);
            initLayer[i] = perceptron;
        }
        Perceptron[] terminalLayer = hiddenLayers(structure, initLayer, 1);

        return new Perceptron[][]{initLayer, terminalLayer};
    }
    private Perceptron[] hiddenLayers(int[] structure, Perceptron[] previousLayer, int index){
        int currLayerSize = structure[index];
        Perceptron[] currLayer = new Perceptron[currLayerSize];
        int previousLayerSize = previousLayer.length;
        for (int i = 0; i < currLayerSize; i++) {
            Perceptron perceptron = new Perceptron(previousLayerSize);
            perceptron.setPredecessors(new Perceptron[previousLayerSize]);
            perceptron.setSuccessors(new Perceptron[structure[index+1]]);
            for (Perceptron predecessor : previousLayer) {
                Perceptron.setPredConnection(predecessor, perceptron);
            }
            currLayer[i] = perceptron;
        }
        if(index < structure.length-2){
            return hiddenLayers(structure, currLayer, index+1);
        }else {
            return terminalLayer(structure, currLayer, index+1);
        }
    }
    private Perceptron[] terminalLayer(int[] structure, Perceptron[] previousLayer, int index){
        int currLayerSize = structure[index];
        Perceptron[] finalLayer = new Perceptron[currLayerSize];
        int previousLayerSize = previousLayer.length;
        for (int i = 0; i < currLayerSize; i++) {
            Perceptron perceptron = new Perceptron(previousLayerSize);
            perceptron.setPredecessors(new Perceptron[previousLayerSize]);
            for (Perceptron predecessor : previousLayer) {
                Perceptron.setPredConnection(predecessor, perceptron);
            }
            finalLayer[i] = perceptron;
        }
        System.out.println("construction successful");
        return finalLayer;
    }
}
