/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tldr.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author drobles
 */
public class Util {
       public static void copyFile(String destino,String nombreArchivo, InputStream in)  {
    try {
        OutputStream out = new FileOutputStream(new File(destino + nombreArchivo));
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = in.read(bytes)) != -1) {
           out.write(bytes, 0, read);
        }
        in.close();
        out.flush();
        out.close();
       } catch (IOException e) {
            System.out.println(e.getMessage());
       }
    }  
}
