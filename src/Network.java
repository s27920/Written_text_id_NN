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
    public void run(List<Image> imagesToClassify, int index){
        int length = imagesToClassify.size();
        float[] activations = getActivations(imagesToClassify.get(index));
        train(activations);
        System.out.println("value guessed: " + classify(activations) + " actual value: " + currentLabel);
        System.out.println(Arrays.toString(activations));
        index+=1;
        if(index < length) {
            run(imagesToClassify, index);
        }
    }

    public float[] getActivations(Image imageToClassify){
        byte[] pixelVector = imageToClassify.getPixels();
        for (int i = 0; i < initLayer.length; i++) {
            initLayer[i].setInputs(new float[]{(float) (pixelVector[i]/128.0)});
        }
        Perceptron tmp = initLayer[0];
        Perceptron[] successors = null;
        Perceptron[] tmpSuccessors;
        while ((tmpSuccessors = tmp.getSuccessor()) !=null){
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
        float[] correct = new float[10];
        correct[currentLabel] = 1.0f;
        Perceptron[] layer = terminalLayer;
        Perceptron[] prevLayer;
        while ((prevLayer = layer[0].predecessor)!=null){
            int length = prevLayer.length;
            System.out.println(layer.length);
            for (int i = 0; i < layer.length; i++) {
                float activation = activations[i];
                float errorGradient = correct[i] - activation;
                for (int j = 0; j < length; j++) {
                    prevLayer[j].getWeights()[i] += prevLayer[j].getOutput() * errorGradient * (1 - activation*activation);
                }
            }
            activations = new float[length];
            for (int i = 0; i < length; i++) {
                activations[i]=prevLayer[i].getOutput();
            }
            layer=prevLayer;
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
            perceptron.successor = new Perceptron[structure[1]];
            initLayer[i] = perceptron;
        }
        Perceptron[] terminalLayer = hiddenLayers(structure, initLayer, 1);

        return new Perceptron[][]{initLayer, terminalLayer} ;
    }
    private Perceptron[] hiddenLayers(int[] structure, Perceptron[] previousLayer, int index){
        int currLayerSize = structure[index];
        Perceptron[] currLayer = new Perceptron[currLayerSize];
        int previousLayerSize = previousLayer.length;
        for (int i = 0; i < currLayerSize; i++) {
            Perceptron perceptron = new Perceptron(previousLayerSize);
            perceptron.predecessor = new Perceptron[previousLayerSize];
            perceptron.successor = new Perceptron[structure[index+1]];
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
            perceptron.predecessor = new Perceptron[previousLayerSize];
            for (Perceptron predecessor : previousLayer) {
                Perceptron.setPredConnection(predecessor, perceptron);
            }
            finalLayer[i] = perceptron;
        }
        return finalLayer;
    }
}
