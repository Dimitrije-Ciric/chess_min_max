package org.example.helper;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoadImageHelper {

    public static Image load(String path) {
        URL ImageURL = LoadImageHelper.class.getResource(path);

        if(ImageURL == null) {
            return null;
        }

        return new ImageIcon(ImageURL).getImage();
    }

}
