import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageManipulator {

    public static void main(String[] args) {
        String filename = "sampleImage.bmp";
        File file = new File(filename);
        try {
            FileInputStream input = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] header1 = input.readNBytes(14);
            byte[] headerSize = input.readNBytes(4);
            byte[] widthBytes = input.readNBytes(4);
            byte[] heightBytes = input.readNBytes(4);
            byte[] header2 = input.readNBytes(28);

            byteArrayOutputStream.write(header1);
            byteArrayOutputStream.write(headerSize);
            byteArrayOutputStream.write(widthBytes);
            byteArrayOutputStream.write(heightBytes);
            byteArrayOutputStream.write(header2);

            int width = convertBytesToInt(widthBytes);
            int height = convertBytesToInt(heightBytes);

            byte[] pixelArr = new byte[3];
            int w = 1;
            while (pixelArr.length > 0) {
                if (w == width) {
                    pixelArr = input.readNBytes(4);
                    w=0;
                } else {
                    pixelArr = input.readNBytes(3);
                }
                byte[] alterBytes = alterColor(pixelArr);
                byteArrayOutputStream.write(alterBytes);
                w++;
            }

            String outputFile =  ".\\alteredImage.bmp";
            FileOutputStream outputStream = new FileOutputStream(new File(outputFile));
            byteArrayOutputStream.writeTo(outputStream);
            byteArrayOutputStream.close();
            outputStream.close();

            System.out.println("Completed bitmap alteration");
            System.out.println("See " + outputFile);
        } catch (
                IOException ioException) {
            System.out.println("Error reading image");
            ioException.getStackTrace();
        }
    }

    public static byte[] alterColor(byte[] bArr) {
        byte[] newArr = new byte[bArr.length];
        for (int i=0; i< bArr.length;i++){
            byte b = bArr[i];
            int pixel = (int) b;
            if (pixel < 0) {
                pixel = 256 + pixel;
            }
            if (pixel < 64) {
                newArr[i] = 0;
            } else if (pixel > 191) {
                newArr[i] = (byte) 255;
            } else {
                newArr[i] = (byte) 127;
            }
        }
        return newArr;
    }

    public static int convertBytesToInt(byte[] byteArr){
        ByteBuffer bb = ByteBuffer.wrap(byteArr);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int val = bb.getInt();
        bb.clear();
        return val;
    }
}
