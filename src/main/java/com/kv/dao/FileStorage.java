package com.kv.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class FileStorage <T, U> {
    private static String DEVICE_UPTIME_MONITORING_DB_SUB_DIRECTORY_NAME = "";

    private static String DB_BASE_URL = null;
    @Autowired private ObjectMapper objectMapper;

    FileStorage(@Autowired Environment environment) {
        String dbBaseUrl = "";
        if(environment.getProperty("kv.datasource.url") != null) {
            DB_BASE_URL = environment.getProperty("kv.datasource.url");
            DEVICE_UPTIME_MONITORING_DB_SUB_DIRECTORY_NAME = "\\device-uptime-monitoring";
        }
    }

    public T save(T t, U pk) {
        String className = t.getClass().getSimpleName();
        File file = new File(getAbsoluteFilePath(String.valueOf(pk), className));
        try {
            if (!file.exists()) {
                Boolean isNewFileCreated = file.createNewFile();
                FileWriter fw = new FileWriter(file);
                String jsonString = objectMapper.writeValueAsString(t);
                fw.write(jsonString);
                fw.flush();
                fw.close();
                System.out.println("JSON String: " + jsonString);
            }
        } catch (IOException ioException) {
            System.out.println("Error occurred while saving file for filename: " + pk);
            System.out.println("Error message " + ioException.getMessage());

        }
        return t;
    }

    private String getAbsoluteFilePath(String fileName, String fileSubDirectory) {
        String finalDirectory = DB_BASE_URL + DEVICE_UPTIME_MONITORING_DB_SUB_DIRECTORY_NAME + "\\" + fileSubDirectory;
        File file = new File(finalDirectory);
        if(!file.exists() && !file.isDirectory());
            file.mkdir();
        return finalDirectory + "\\" + fileName + ".txt";
    }
}
