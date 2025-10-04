package com.roqia.Drive_demo.utility;

import org.apache.tika.Tika;

public class FileUtility {
    private static final Tika tika = new Tika();

    public static String getFileExtension (String fileName){
       int indx = fileName.lastIndexOf('.');
        return fileName.substring(indx+1);
    }
    public static String getMimeTypeByFileName(String fileName){
        return tika.detect(fileName);
    }
}
