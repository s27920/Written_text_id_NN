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
