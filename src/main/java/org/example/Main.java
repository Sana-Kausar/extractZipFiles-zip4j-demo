package org.example;
import net.lingala.zip4j.*;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        extractWithZipInputStream("/sample.zip","samplePassword");
    }
    private static void unzip(String targetZipFilePath, String destinationZipFilePath, String password){
        try{
            ZipFile zipFile=new ZipFile(targetZipFilePath);
            if(zipFile.isEncrypted()){
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(destinationZipFilePath);
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }

    }
    private static void zip(List<String> inputFilePaths,String targetFilePath,String password){
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setCompressionLevel(CompressionLevel.HIGHER);

        ZipFile zipFile = new ZipFile(targetFilePath,password.toCharArray());
        for(String inputFilePath: inputFilePaths) {
            try {
                zipFile.addFile(new File(inputFilePath), zipParameters);
            } catch (ZipException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void extractWithZipInputStream(String inputFilePath,String password){
        LocalFileHeader localFileHeader;
        int readLen;
        byte[] readBuffer = new byte[4096];
        File zipFile=new File(inputFilePath);

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(zipFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream, password.toCharArray())) {
            while (true) {
                try {
                    if (!((localFileHeader = zipInputStream.getNextEntry()) != null)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                File extractedFile = new File(localFileHeader.getFileName());
                try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
                    while ((readLen = zipInputStream.read(readBuffer)) != -1) {
                        outputStream.write(readBuffer, 0, readLen);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}