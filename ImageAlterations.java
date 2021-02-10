import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageAlterations {

    private byte[] header1;
    private byte[] headerSize;
    private byte[] widthBytes;
    private byte[] heightBytes;
    private byte[] header2;
    private byte[][][] pixelGrid;

    public ImageAlterations(String filename) throws IOException{
        File file = new File(filename);
            FileInputStream input = new FileInputStream(file);

            header1 = input.readNBytes(14);
            headerSize = input.readNBytes(4);
            widthBytes = input.readNBytes(4);
            heightBytes = input.readNBytes(4);
            header2 = input.readNBytes(28);

            int width = getWidth();
            int height = getHeight();

            pixelGrid = new byte[width][height][3];

            byte[] pixelArr = new byte[3];
            int w = 0;
            int h = 0;
            while (h<=height-1) {
                if (w == width-1) {
                    byte[] pixelTemp = input.readNBytes(4);
                    System.arraycopy(pixelTemp,0,pixelArr,0,3);
                    pixelGrid[w][h] = pixelArr;
                    w=0;
                    h++;
                } else {
                    pixelArr = input.readNBytes(3);
                    pixelGrid[w][h] = pixelArr;
                    w++;
                }
            }
    }

    public void saveImage(byte[][][] alteredPixels, String filename) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(header1);
        byteArrayOutputStream.write(headerSize);
        byteArrayOutputStream.write(widthBytes);
        byteArrayOutputStream.write(heightBytes);
        byteArrayOutputStream.write(header2);
        for (int y=0; y< alteredPixels[0].length; y++) {
            for (int x=0; x< alteredPixels.length; x++){
                byte[] pixel = alteredPixels[x][y];
                if (x==this.getWidth()-1){
                    byte[] endPixel = new byte[4];
                    System.arraycopy(pixel, 0, endPixel, 0, 3);
                    endPixel[3]=0;
                    byteArrayOutputStream.write(endPixel);
                } else {
                    byteArrayOutputStream.write(pixel);
                }
            }
        }
        FileOutputStream outputStream = new FileOutputStream(filename);
        byteArrayOutputStream.writeTo(outputStream);
        byteArrayOutputStream.close();
        outputStream.close();
        System.out.println("Completed bitmap alteration");
        System.out.println("See "+filename);
    }

    public byte[][][] rotateImage90Deg(byte[][][] bMat){
        int width = bMat.length;
        int height = bMat[0].length;
        byte[][][] transformedMat= new byte[height][width][3];
        for (int x=0; x< bMat[0].length/2; x++) {
            for (int y=0; y< bMat.length/2; y++) {
                byte[] pixelTmp = bMat[x][y];
                transformedMat[x][y] = bMat[y][height-1-x];
                transformedMat[y][width-1-x] = bMat[width-1-x][height-1-y];
                transformedMat[height-1-x][width-1-y] = bMat[width-1-y][x];
                transformedMat[height-1-y][x] = pixelTmp;
            }
        }
        setHeightBytes(width);
        setWidthBytes(height);
        return transformedMat;
    }

    public byte[][][] horizontalFlip(byte[][][] bMat){
        int width = bMat.length;
        int height = bMat[0].length;
        byte[][][] transformedMat = new byte[width][height][3];
        for (int y=0; y< transformedMat[0].length; y++) {
            for (int x=0; x< transformedMat.length; x++) {
                byte[] pixel = bMat[x][y];
                transformedMat[x][(height-1)-y] = pixel;
            }
        }
        return transformedMat;
    }

    public byte[][][] verticalFlip(byte[][][] bMat){
        int width = bMat.length;
        int height = bMat[0].length;
        byte[][][] transformedMat = new byte[width][height][3];
        for (int y=0; y< transformedMat[0].length; y++) {
            for (int x=0; x< transformedMat.length; x++) {
                byte[] pixel = bMat[x][y];
                transformedMat[(width-1)-x][y] = pixel;
            }
        }
        return transformedMat;
    }

    public byte[][][] changeContrast(byte[][][] bMat, double val){
        int width = bMat.length;
        int height = bMat[0].length;
        byte[][][] transformedMat = new byte[width][height][3];
        for (int y=0; y< transformedMat[0].length; y++) {
            for (int x = 0; x < transformedMat.length; x++) {
                byte[] pixel = bMat[x][y];
                for (int i = 0; i < pixel.length; i++) {
                    double b = Math.min(((int)pixel[i])/val, 255);
                    pixel[i] = (byte) (b);
                }
                transformedMat[x][y] = pixel;
            }
        }
        return transformedMat;
    }

    public byte[][][] swapColors(byte[][][] bMat, int swap1, int swap2) {
        if (swap1>2 || swap2>2){
            System.out.println("Error you can only swap colors 0,1,2");
            return bMat;
        }
        int width = bMat.length;
        int height = bMat[0].length;
        byte[][][] transformedMat = new byte[width][height][3];
        for (int y=0; y< transformedMat[0].length; y++) {
            for (int x = 0; x < transformedMat.length; x++) {
                byte[] pixel = bMat[x][y];
                if (pixel.length > 1) {
                    byte temp = pixel[swap1];
                    pixel[swap1] = pixel[swap2];
                    pixel[swap2] = temp;
                }
                transformedMat[x][y] = pixel;
            }
        }
        return transformedMat;
    }

    public int getWidth(){
        return convertBytesToInt(widthBytes);
    }

    public int getHeight(){
        return convertBytesToInt(heightBytes);
    }

    public byte[] getHeader1() {
        return header1;
    }

    public void setHeader1(byte[] header1) {
        this.header1 = header1;
    }

    public byte[] getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(byte[] headerSize) {
        this.headerSize = headerSize;
    }

    public byte[] getWidthBytes() {
        return widthBytes;
    }

    public void setWidthBytes(int width) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(width);
        this.widthBytes = b.array();
    }

    public byte[] getHeightBytes() {
        return heightBytes;
    }

    public void setHeightBytes(int height) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(height);
        this.heightBytes = b.array();
    }

    public byte[] getHeader2() {
        return header2;
    }

    public void setHeader2(byte[] header2) {
        this.header2 = header2;
    }

    public byte[][][] getPixelGrid() {
        return pixelGrid;
    }

    public void setPixelGrid(byte[][][] pixelGrid) {
        this.pixelGrid = pixelGrid;
    }

    private int convertBytesToInt(byte[] byteArr){
        ByteBuffer bb = ByteBuffer.wrap(byteArr);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int val = bb.getInt();
        bb.clear();
        return val;
    }

    private void printByteArr(byte[] byteArr){
        for (byte b: byteArr){
            System.out.print(b+ " ");
        }
        System.out.println();
    }
}
