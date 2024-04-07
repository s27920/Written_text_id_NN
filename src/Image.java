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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pixelVector.length; i++) {
            stringBuilder.append(pixelVector[i]);
            if ((i+1)%28==0){
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append("\nlabel: ").append(label);
        return String.valueOf(stringBuilder);
    }
}