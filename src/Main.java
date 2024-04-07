import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //imageFilePath = "t10k-images.idx3-ubyte" by default
        //labelFilePath = "t10k-labels.idx1-ubyte" by default
        IDXParser parser = new IDXParser("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte");
        Network network = new Network(new int[]{784,16,16,10});

        List<Image> imageList = parser.parse();

        System.out.println("testing begins: ");
        System.out.println("Perceptron count: " + Perceptron.objectCounter);
        System.out.println("Connection count: " + Perceptron.connectionCounter);

        network.run(imageList, 0);
    }
}
