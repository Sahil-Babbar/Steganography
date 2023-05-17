package com.stegnography.imageService;

import java.io.IOException;

public interface TextInsideImage {
    String embedText(byte[] data, String User_id, String text) throws IOException;
}
