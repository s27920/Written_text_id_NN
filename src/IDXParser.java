import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class IDXParser implements Parseable {
    //imageFilePath = "t10k-images.idx3-ubyte" by default
    //labelFilePath = "t10k-labels.idx1-ubyte" by default
    Path imageFile;
    Path labelFile;

    public IDXParser(String imageFilePath, String labelFilePath) {
        imageFile = Paths.get(imageFilePath);
        labelFile = Paths.get(labelFilePath);
    }

    public void parse(){
        Flags flags = getFlags();
        try (FileChannel imageChannel = FileChannel.open(imageFile, StandardOpenOption.READ);
             FileChannel labelChannel = FileChannel.open(labelFile, StandardOpenOption.READ)) {
            long imagePosition = 16;
            long labelposition = 8;

            ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(flags.sizeX* flags.sizeY);
            ByteBuffer labelBuffer = ByteBuffer.allocateDirect(1);


            for (int i = 0; i < flags.imageCount; i++){

                int labelBytesRead= labelChannel.read(labelBuffer, labelposition);
                int imageBytesRead = imageChannel.read(pixelBuffer,imagePosition);
                byte[] pixelVector;

                if (labelBuffer.hasArray()){
                    byte[] byteArray = pixelBuffer.array();
                    int offset = pixelBuffer.arrayOffset() + pixelBuffer.position();
                    int length = pixelBuffer.remaining();
                    pixelVector = new byte[length];
                    System.arraycopy(byteArray, offset, pixelVector, 0, length);
                } else {
                    pixelVector = newArray(pixelBuffer);
                }
                labelBuffer.flip();
                byte label = labelBuffer.get();

                //potentially passed onto a shared resource idk yet
                Image image = new Image(pixelVector, label);

                imagePosition += imageBytesRead;
                labelposition += labelBytesRead;
                pixelBuffer.clear();
                labelBuffer.clear();
            }
            System.out.println("done " + "");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    private Flags getFlags(){
        try(FileChannel flagChannel = FileChannel.open(imageFile, StandardOpenOption.READ)) {
            ByteBuffer flagBuffer = ByteBuffer.allocateDirect(16);
            flagChannel.read(flagBuffer);
            byte[] flagVector = newArray(flagBuffer);
            int imageCount = (int) (flagVector[4] * Math.pow(256,3) + flagVector[5] * Math.pow(256,2)+ flagVector[6] * Math.pow(256,1)+ flagVector[7]);
            int dimX = (int) (flagVector[8] * Math.pow(256,3) + flagVector[9] * Math.pow(256,2)+ flagVector[10] * Math.pow(256,1)+ flagVector[11]);
            int dimY = (int) (flagVector[12] * Math.pow(256,3) + flagVector[13] * Math.pow(256,2)+ flagVector[14] * Math.pow(256,1)+ flagVector[15]);
            return new Flags(dimX, dimY, imageCount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static class Flags {
        private final int sizeX;
        private final int sizeY;
        private final int imageCount;
        private Flags(int sizeX, int sizeY, int imageCount) {
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.imageCount = imageCount;
        }
    }
    private byte[] newArray(ByteBuffer buffer){
        buffer.flip();
        byte[] toReturn = new byte[buffer.remaining()];
        buffer.get(toReturn);
        return toReturn;
    }
}
