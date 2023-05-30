package com.stegnography.imageService.imageServiceimpl;

import com.stegnography.Repository.imagesRepository;
import com.stegnography.domain.images;
import com.stegnography.imageService.TextInsideImage;
import com.stegnography.imageService.imageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

@Service
public class TextInsideImageImpl implements TextInsideImage {
    @Autowired
    private imagesRepository repository;
    @Autowired
    private imageService imageservice;
//   public String embedText(byte[] data, String User_id, String text) {
//
//        BufferedImage image = imageservice.toBufferedImage(data);
//       int bitMask = 0xFE; // define the mask bit to clear the least significant bit of the pixel
//       int x = 0; // define the starting pixel x
//       int y = 0; // define the starting pixel y
//
//       for (int i = 0; i < text.length(); i++) {
//           char c = text.charAt(i); // get the next character from the message
//           int ascii = (int) c; // get the ASCII value of the character
//
//           // 8 digits form a character
//           for (int j = 0; j < 8; j++) {
//               int pixel = image.getRGB(x, y);
//               int bit = (ascii >> (7 - j)) & 0x01; // extract each bit of the character
//
//               // Set the least significant bit of the pixel to the extracted bit
//               int modifiedPixel = (pixel & bitMask) | bit;
//               image.setRGB(x, y, modifiedPixel);
//
//               x++;
//               if (x >= image.getWidth()) {
//                   x = 0;
//                   y++;
//               }
//           }
//       }
//
//        // save the image which contains the secret information to another image file
//        String password = null;
//        try {
//            File file2 = new File("src/main/resources/static/image/" + User_id + ".jpg");
//            FileOutputStream out = new FileOutputStream(file2);
//            PrintWriter writer = new PrintWriter(file2);
//            writer.print("");
//            writer.close();
//            file2 = new File("src/main/resources/static/image/" + User_id + ".jpg");
//            out = new FileOutputStream(file2);
//
//            out.write(data);
//            out.close();
//            System.out.println("Encoded");
//
//            password = UUID.randomUUID().toString();
//            Optional<images> images = repository.findById(Long.parseLong(User_id));
//            images images1 = images.get();
//            images1.setUserid_password(User_id + password);
//
//            repository.save(images1);
//
//        } catch (IOException e) {
//
//        }
//        return password;
//    }
//
//    public String extractText(BufferedImage image, String user_id, String password, int length) {
//        int bitMask = 0x00000001;   // define the mask bit used to get the digit
//        int bit;                    // define an integer number to represent the ASCII number of a character
//        int x = 0;                  // define the starting pixel x
//        int y = 0;                  // define the starting pixel y
//        StringBuilder message = new StringBuilder(); // to store the extracted message
//
//        for (int i = 0; i < length; i++) {
//            bit = 0; // reset the bit value for each character
//
//            // 8 digits form a character
//            for (int j = 0; j < 8; j++) {
//                if (x < image.getWidth()) {
//                    int pixel = image.getRGB(x, y);
//                    bit = (bit << 1) | (pixel & bitMask); // extract the least significant bit from the pixel
//                    x++;
//                } else {
//                    x = 0;
//                    y++;
//                    int pixel = image.getRGB(x, y);
//                    bit = (bit << 1) | (pixel & bitMask); // extract the least significant bit from the pixel
//                }
//                message.append((char) bit);
//            }
//
//             // append the character to the message
//        }
//        System.out.println(message.toString());
//        return message.toString();
//    }

    private static int bytesForTextLengthData = 4;
    private static int bitsInByte = 8;


    // Encode

    public String encode(String imagePath, String textPath, String pass) {
        BufferedImage originalImage = getImageFromPath(imagePath);
        BufferedImage imageInUserSpace = getImageInUserSpace(originalImage);
        //String text = getTextFromTextFile(textPath);
        String text = new Encryption().Encrypt(checkpassword(pass), textPath);

        byte imageInBytes[] = getBytesFromImage(imageInUserSpace);
        byte textInBytes[] = text.getBytes();
        byte textLengthInBytes[] = getBytesFromInt(textInBytes.length);
        try {
            encodeImage(imageInBytes, textLengthInBytes,  0);
            encodeImage(imageInBytes, textInBytes, bytesForTextLengthData*bitsInByte);
        }
        catch (Exception exception) {
            System.out.println("Couldn't hide text in image. Error: " + exception);

        }

        String fileName = imagePath;
        int position = fileName.lastIndexOf(".");
        if (position > 0) {
            fileName = fileName.substring(0, position);
        }

        //String finalFileName = fileName + "_with_hidden_message.png";
        //System.out.println("Successfully encoded text in: " + finalFileName);
        saveImageToPath(imageInUserSpace, new File("image_text_out.png"),"png");
        return "1234";
    }

    private static byte[] encodeImage(byte[] image, byte[] addition, int offset) {
        if (addition.length + offset > image.length) {
            throw new IllegalArgumentException("Image file is not long enough to store provided text");
        }
        for (int i=0; i<addition.length; i++) {
            int additionByte = addition[i];
            for (int bit=bitsInByte-1; bit>=0; --bit, offset++) {
                int b = (additionByte >>> bit) & 0x1;
                image[offset] = (byte)((image[offset] & 0xFE) | b);
            }
        }
        return image;
    }


    // Decode

    public String decode(String imagePath,String pass) {
        byte[] decodedHiddenText;
        try {
            BufferedImage imageFromPath = getImageFromPath(imagePath);
            BufferedImage imageInUserSpace = getImageInUserSpace(imageFromPath);
            byte imageInBytes[] = getBytesFromImage(imageInUserSpace);
            decodedHiddenText = decodeImage(imageInBytes);
            String hiddenText = new String(decodedHiddenText);
            //String outputFileName = "hidden_text.txt";
            //saveTextToPath(hiddenText, new File(outputFileName));
            //System.out.println("Successfully extracted text to: " + outputFileName);
            return (new Encryption().Decrypt(checkpassword(pass), hiddenText));
        } catch (Exception exception) {
            System.out.println("No hidden message. Error: " + exception);
            return "";
        }
    }

    private static byte[] decodeImage(byte[] image) {
        int length = 0;
        int offset  = bytesForTextLengthData*bitsInByte;

        for (int i=0; i<offset; i++) {
            length = (length << 1) | (image[i] & 0x1);
        }

        byte[] result = new byte[length];

        for (int b=0; b<result.length; b++ ) {
            for (int i=0; i<bitsInByte; i++, offset++) {
                result[b] = (byte)((result[b] << 1) | (image[offset] & 0x1));
            }
        }
        return result;
    }


    // File I/O methods

    private static void saveImageToPath(BufferedImage image, File file, String extension) {
        try {
            file.delete();
            ImageIO.write(image, extension, file);
        } catch (Exception exception) {
            System.out.println("Image file could not be saved. Error: " + exception);
        }
    }

    private static void saveTextToPath(String text, File file) {
        try {
            if (file.exists() == false) {
                file.createNewFile( );
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(text);
            bufferedWriter.close();
        } catch (Exception exception) {
            System.out.println("Couldn't write text to file: " + exception);
        }
    }

    private static BufferedImage getImageFromPath(String path) {
        BufferedImage image	= null;
        File file = new File(path);
        try {
            image = ImageIO.read(file);
        } catch (Exception exception) {
            System.out.println("Input image cannot be read. Error: " + exception);
        }
        return image;
    }

    private static String getTextFromTextFile(String textFile) {
        String text = "";
        try {
            Scanner scanner = new Scanner( new File(textFile) );
            text = scanner.useDelimiter("\\A").next();
            scanner.close();
        } catch (Exception exception) {
            System.out.println("Couldn't read text from file. Error: " + exception);
        }
        return text;
    }


    // Helpers

    private static BufferedImage getImageInUserSpace(BufferedImage image) {
        BufferedImage imageInUserSpace  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = imageInUserSpace.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose();
        return imageInUserSpace;
    }

    private static byte[] getBytesFromImage(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
        return buffer.getData();
    }

    private static byte[] getBytesFromInt(int integer) {
        return ByteBuffer.allocate(bytesForTextLengthData).putInt(integer).array();
    }

    private static String checkpassword(String pass)
    {
        String checkedString;
        if(pass.length() <= 24)
        {
            checkedString = pass + "abcdefghijklmnopqrstuvwx";
            return checkedString;
        }
        else
            return pass;
    }

}
