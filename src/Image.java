public class Image {
    private final byte[] pixelVector;
    private final byte label;

    public Image(byte[] pixelVector, byte label) {
        this.pixelVector = pixelVector;
        this.label = label;
    }

    public byte[] getPixels() {
        return pixelVector;
    }

    public byte getNumVal() {
        return label;
    }
}

//public class Image<T> {
//    private final T[] pixelVector;
//    private final T label;
//
//    public Image(T[] pixelVector, T label) {
//        this.pixelVector = pixelVector;
//        this.label = label;
//    }
//
//    public T[] getPixels() {
//        return pixelVector;
//    }
//
//    public T getNumVal() {
//        return label;
//    }
//}
