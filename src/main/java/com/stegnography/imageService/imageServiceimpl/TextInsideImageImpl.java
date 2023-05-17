package com.stegnography.imageService.imageServiceimpl;

import com.stegnography.Repository.imagesRepository;
import com.stegnography.domain.images;
import com.stegnography.imageService.TextInsideImage;
import com.stegnography.imageService.imageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.UUID;

@Service
public class TextInsideImageImpl implements TextInsideImage {
    @Autowired
    private imagesRepository repository;
    @Autowired
    private imageService imageservice;
    public String embedText(byte[] data, String User_id, String text) {

        BufferedImage image = imageservice.toBufferedImage(data);
        int bitMask = 0x00000001;    // define the mask bit used to get the digit
        int bit;                // define a integer number to represent the ASCII number of a character
        int x = 0;                // define the starting pixel x
        int y = 0;                // define the starting pixel y
        for (int i = 0; i < text.length(); i++) {
            bit = (int) text.charAt(i);        // get the ASCII number of a character
            for (int j = 0; j < 8; j++) {
                int flag = bit & bitMask;    // get 1 digit from the character
                if (flag == 1) {
                    if (x < image.getWidth()) {
                        image.setRGB(x, y, image.getRGB(x, y) | 0x00000001);    // store the bit which is 1 into a pixel's last digit
                        x++;
                    } else {
                        x = 0;
                        y++;
                        image.setRGB(x, y, image.getRGB(x, y) | 0x00000001);    // store the bit which is 1 into a pixel's last digit
                    }
                } else {
                    if (x < image.getWidth()) {
                        image.setRGB(x, y, image.getRGB(x, y) & 0xFFFFFFFE);    // store the bit which is 0 into a pixel's last digit
                        x++;
                    } else {
                        x = 0;
                        y++;
                        image.setRGB(x, y, image.getRGB(x, y) & 0xFFFFFFFE);    // store the bit which is 0 into a pixel's last digit
                    }
                }
                bit = bit >> 1;                // get the next digit from the character
            }
        }

        // save the image which contains the secret information to another image file
        String password = null;
        try {
            File file2 = new File("src/main/resources/static/image/" + User_id + ".png");
            FileOutputStream out = new FileOutputStream(file2);
            PrintWriter writer = new PrintWriter(file2);
            writer.print("");
            writer.close();
            file2 = new File("src/main/resources/static/image/" + User_id + ".png");
            out = new FileOutputStream(file2);

            out.write(data);
            out.close();
            System.out.println("Encoded");

            password = UUID.randomUUID().toString();
            Optional<images> images = repository.findById(Long.parseLong(User_id));
            images images1 = images.get();
            images1.setUserid_password(User_id + password);

            repository.save(images1);

        } catch (IOException e) {

        }
        return password;
    }


}
