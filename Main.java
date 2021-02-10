import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String filename = "sampleImage.bmp";
        try {
            ImageAlterations imageToAlter = new ImageAlterations(filename);
            byte[][][] pixels = imageToAlter.getPixelGrid();
            pixels = imageToAlter.alterColor(pixels); //Uncomment to alter teh image colors
//            pixels = imageToAlter.swapColors(pixels,0,1); //Uncomment to swap red and green colors
//            pixels = imageToAlter.swapColors(pixels,0,2); //Uncomment to swap red and blue colors
//            pixels = imageToAlter.swapColors(pixels,2,1); //Uncomment to swap blue and green colors
//            pixels = imageToAlter.verticalFlip(pixels); //Uncomment to mirror flip the image along a vertical line
//            pixels = imageToAlter.horizontalFlip(pixels); //Uncomment to mirror flip the image along the horizontal line
//            pixels = imageToAlter.changeContrast(pixels,0.5); //Uncomment to increase contrast
//            pixels = imageToAlter.changeContrast(pixels,5); //Uncomment to decrease contrast
            imageToAlter.saveImage(pixels,"alteredImageBonus.bmp");
        } catch(IOException ioException){
            System.out.println("Error in Mage Alterations");
            ioException.getStackTrace();
        }
    }
}
