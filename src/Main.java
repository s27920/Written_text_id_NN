public class Main {
    public static void main(String[] args) {
        //imageFilePath = "t10k-images.idx3-ubyte" by default
        //labelFilePath = "t10k-labels.idx1-ubyte" by default
//        IDXParser parser = new IDXParser("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte");
//        parser.parse();

        Network network = new Network(new int[]{784,16,16,10});
        System.out.println(Perceptron.objectCounter);
        System.out.println(Perceptron.connectionCounter);
    }
}
