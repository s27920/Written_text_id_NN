public class Network {
    int[] structure;
//    by default = {784,16,16,10}
    public Network(int[] structure) {
        this.structure = structure;
        createNetwork(structure);
    }

    public static void createNetwork(int[] structure){
        int intiLayerSize = structure[0];
        Perceptron[] initLayer = new Perceptron[intiLayerSize];
        for (int i = 0; i < intiLayerSize; i++) {
            Perceptron perceptron = new Perceptron(new byte[structure[0]]);
            perceptron.successor = new Perceptron[structure[1]];
            initLayer[i] = perceptron;
        }
        hiddenLayers(structure, initLayer, 1);
    }
    private static void hiddenLayers(int[] structure, Perceptron[] previousLayer, int index){
        int currLayerSize = structure[index];
        Perceptron[] currLayer = new Perceptron[currLayerSize];
        int previousLayerSize = previousLayer.length;
        for (int i = 0; i < currLayerSize; i++) {
            Perceptron perceptron = new Perceptron(new byte[previousLayerSize]);
            perceptron.predecessor = new Perceptron[previousLayerSize];
            perceptron.successor = new Perceptron[structure[index+1]];
            for (Perceptron predecessor : previousLayer) {
                Perceptron.setPredConnection(predecessor, perceptron);
            }
            currLayer[i] = perceptron;
        }
        if(index < structure.length-2){
            hiddenLayers(structure, currLayer, index+1);
        }else {
            terminalLayer(structure, currLayer, index+1);
        }
    }
    private static void terminalLayer(int[] structure, Perceptron[] previousLayer, int index){
        int currLayerSize = structure[index];
        Perceptron[] currLayer = new Perceptron[currLayerSize];
        int previousLayerSize = previousLayer.length;
        for (int i = 0; i < currLayerSize; i++) {
            Perceptron perceptron = new Perceptron(new byte[previousLayerSize]);
            perceptron.predecessor = new Perceptron[previousLayerSize];
            for (Perceptron predecessor : previousLayer) {
                Perceptron.setPredConnection(predecessor, perceptron);
            }
            currLayer[i] = perceptron;
        }
    }
}
