package com.stegnography.imageService;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface TextInsideImage {
//    String embedText(byte[] data, String User_id, String text) throws IOException;
//
//    String extractText(BufferedImage input, String user_id, String password, int length);

    String encode(String imagePath, String textPath,String pass);

    String decode(String imagePath,String pass);
}
