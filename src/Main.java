public class Main {
    public static void main(String[] args) {
        String imageFilePath10k = "t10k-images.idx3-ubyte"; //by default
        String labelFilePath10k = "t10k-labels.idx1-ubyte"; //by default
        String imageFilePath60k = "train-images.idx3-ubyte";
        String labelFilePath60k = "train-labels.idx1-ubyte";

        IDXParser parser = new IDXParser(imageFilePath10k, labelFilePath10k);
        Network network = new Network(new int[]{784,16,16,10});

        long init = System.nanoTime();
        System.out.println(init);
        Image[] images = parser.parse();

        System.out.println("testing begins: ");
        System.out.println("Perceptron count: " + Perceptron.objectCounter);
        System.out.println("Connection count: " + Perceptron.connectionCounter);

        System.out.println((network.run(images, 0)));
        long end = System.nanoTime();


        System.out.println("time: "+(end - init)/1_000_000_000.0);
    }
}
