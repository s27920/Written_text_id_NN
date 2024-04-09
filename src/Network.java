import java.util.Arrays;
import java.util.List;

public class Network {
    int[] structure;
//    by default = {784,16,16,10}
    Perceptron[] initLayer;
    Perceptron[] terminalLayer;
    byte currentLabel;

    public Network(int[] structure) {
        this.structure = structure;
        Perceptron[][] layers = createNetwork(structure);
        this.initLayer = layers[0];
        this.terminalLayer = layers[1];
    }
    public int run(List<Image> imagesToClassify, int index){
        int length = imagesToClassify.size();
        while (index < length){
            float[] activations = getActivations(imagesToClassify.get(index));
            train(activations);
            System.out.println("value guessed: " + classify(activations) + " actual value: " + currentLabel + " for activations ");
            System.out.println(Arrays.toString(activations));
            index+=1;
        }
        return index;
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
    public void train(float[] activations){
        int depthCounter=1;
        float[] correct = new float[10];
        correct[currentLabel] = 1.0f;

        Perceptron[] currLayer = terminalLayer;
//        Perceptron[] predLayer = terminalLayer[0].getPredecessors();

        float[] sucErrorGradient = null;

        while (currLayer !=null){
            int currentLength = currLayer.length;
            if(currLayer == terminalLayer){

                sucErrorGradient = new float[currentLength];
                for (int i = 0; i < currentLength; i++) {
                    float[] tmpWeights = currLayer[i].getWeights();
                    float activation = activations[i];
                    float errorGradient = (correct[i] - activation);
                    float output = currLayer[i].getOutput();
                    float change = activation * errorGradient * ((2 / (1 + (output * output))) - 1);
                    sucErrorGradient[i] = change;
                    for (int j = 0; j < currentLength; j++) {
                        tmpWeights[j]+=change;
                    }
                    currLayer[i].setWeights(tmpWeights);
                }
                System.out.print("Depth: " + depthCounter +" gradient vector length: " + sucErrorGradient.length + " Gradient vector: " + Arrays.toString(sucErrorGradient));
            }else{
                System.out.print("Depth: " + depthCounter +" gradient: ");
                Perceptron[] sucLayer = currLayer[0].getSuccessors();
                float[] tmpErrorGradient = new float[currentLength];
                for (int i = 0; i < currentLength; i++) {
                    for (int j = 0; j < sucLayer.length; j++) {
                        float[] sucWeights = sucLayer[j].getWeights();
                        for (int k = 0; k < currentLength; k++) {
                            if (i == k){
                                tmpErrorGradient[i] += sucErrorGradient[j] * sucWeights[k];
                            }
                        }
                    }
                }

                System.out.println("gradient vector size: " + tmpErrorGradient.length + " gradient vector " + Arrays.toString(tmpErrorGradient));
                for (int i = 0; i < currentLength; i++) {
                    float[] weights = currLayer[i].getWeights();
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] += tmpErrorGradient[i];
                    }
                    currLayer[i].setWeights(weights);
                }
                sucErrorGradient = tmpErrorGradient.clone();
                depthCounter++;
                }
            currLayer=currLayer[0].getPredecessors();

        }
        System.out.println();
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
