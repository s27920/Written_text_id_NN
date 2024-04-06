public class Main {
    public static void main(String[] args) {
        //imageFilePath = "t10k-images.idx3-ubyte" by default
        //labelFilePath = "t10k-labels.idx1-ubyte" by default
        Parser parser = new Parser("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte");
        parser.run();
    }
}
