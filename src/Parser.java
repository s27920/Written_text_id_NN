import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Parser {
    //imageFilePath = "t10k-images.idx3-ubyte" by default
    //labelFilePath = "t10k-labels.idx1-ubyte" by default
    public void parse(String imageFilePath, String labelFilePath){
        Path imageFile = Paths.get(imageFilePath);
        Path labelFile = Paths.get(labelFilePath);
        try (FileChannel imageChannel = FileChannel.open(imageFile, StandardOpenOption.READ);
             FileChannel labelChannel = FileChannel.open(labelFile, StandardOpenOption.READ)) {
            long imagePosition = 16;
            long labelposition = 8;
            ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(784);
            ByteBuffer labelBuffer = ByteBuffer.allocateDirect(1);
            int labelBytesRead;

            while ((labelBytesRead = labelChannel.read(labelBuffer, labelposition))!=-1){
                int imageBytesRead = imageChannel.read(pixelBuffer,imagePosition);
                byte[] pixelVector;

                if (labelBuffer.hasArray()){
                    byte[] byteArray = pixelBuffer.array();
                    int offset = pixelBuffer.arrayOffset() + pixelBuffer.position();
                    int length = pixelBuffer.remaining();
                    pixelVector = new byte[length];
                    System.arraycopy(byteArray, offset, pixelVector, 0, length);
                } else {
                    pixelBuffer.flip();
                    pixelVector = new byte[pixelBuffer.remaining()];
                    pixelBuffer.get(pixelVector);
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
}
