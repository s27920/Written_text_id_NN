public class Main {
    public static void main(String[] args) {
        String imageFilePath10k = "t10k-images.idx3-ubyte"; //by default
        String labelFilePath10k = "t10k-labels.idx1-ubyte"; //by default
        String imageFilePath60k = "train-images.idx3-ubyte";
        String labelFilePath60k = "train-labels.idx1-ubyte";

        IDXParser parser = new IDXParser(imageFilePath60k, labelFilePath60k);
        Network network = new Network(new int[]{784,256,128,10});

        long init = System.nanoTime();
        System.out.println(init);
        Image[] images = parser.parse();

        System.out.println("Perceptron count: " + Perceptron.objectCounter);
        System.out.println("Connection count: " + Network.connectionCounter);

        network.train(images, 0);
        long end = System.nanoTime();


        System.out.println("time: "+(end - init)/1_000_000_000.0);
    }
}
