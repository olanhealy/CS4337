package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.List;

public class HttpClientApp{
    private static final String API_URL = "http://localhost:8080/";
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    public static void main(String[] args) {
        if (args.length < 3){
            System.out.println("""
                    Usage: java HttpClientApp <numberOfRequests> <threadPoolSize> <requestFlag>\s
                     %s\s
                     %s\s
                     %s\s
                     %s\s
                     %s\s
                     %s\s
                    
                    -h for hello world\
                    
                    -i for insert emails\
                    
                    -g for get users\
                    
                    -u for update the first numberOfRequests emails to timestamp emails\
                    
                    -d delete the first numberOfRequests emails\
                    
                    -e to export the data
                    
                    -c to continuously export the data over a period of 15 minutes""");
            return;
        }
        int numberOfRequests = Integer.parseInt(args[0]);
        int threadPoolSize = Integer.parseInt(args[1]);
        String requestFlag = args[2];
        
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        Runnable requestType;

        switch (requestFlag){
            case "-h":
                requestType = () -> helloRequest();
                break;
            case "-i":
                requestType = () -> insertRequest();
                break;
            case "-g":
                requestType = () -> getRequest();
                break;
            case "-u":
                //changes the 1st n requests to a timestamp email
                requestType = () -> updateRequest();
                break;
            case "-d":
                //deletes the 1st n requests to a timestamp email
                requestType = () -> deleteRequest();
                break;
            case "-e":
                requestType = () -> exportRequest();
                break;
            case "-c":
                requestType = () -> continuousRequests();
                break;
            default:
                System.out.println("Unknown request flag: " + requestFlag);
                return;
        }
        for (int i = 0; i < numberOfRequests; i++){
            executorService.submit(requestType);
        }
        executorService.shutdown();
    }

    private static void continuousRequests() {
        for(int i = 0; i < 900; i++) {
            exportRequest();
        }
    }

    private static void exportRequest(){
        try {
            URL url = new URL(API_URL + "export-data");
            HttpURLConnection conn= (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            if(conn.getInputStream() != null) {
                getAndPrintResult(conn.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void requestWithPayload(HttpURLConnection connection, String payload) throws IOException{
            try(OutputStream os = connection.getOutputStream()){
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            getAndPrintResult(connection.getInputStream());
    }

    private static void getAndPrintResult(InputStream connectionIn) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(connectionIn));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        System.out.println("Response: " + response.toString());
    }

    private static void helloRequest(){
        try {
            URL url = new URL(API_URL + "hello");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            getAndPrintResult(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getRequest(){
        try {
            URL url = new URL(API_URL + "users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            getAndPrintResult(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertRequest(){
        try {
            URL url = new URL(API_URL + "user");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            final int id = HttpClientApp.NEXT_ID.getAndIncrement();

            connection.setRequestProperty("Content-Type", "application/json");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            Date date = new Date();
            String formattedDate = sdf.format(date);
            String jsonInputString = String.format("{\"email\":\"%s_%d@gmail.com\"}", formattedDate, id);
            requestWithPayload(connection, jsonInputString);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void updateRequest() {
        try {
            URL url = new URL(API_URL + "user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            final int id = HttpClientApp.NEXT_ID.getAndIncrement();
            conn.setRequestProperty("Content-Type", "application/json");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            Date dateTime = new Date();
            String formattedDate = sdf.format(dateTime);//to make the emails unique
            String jsonInputString = String.format("{\"id\": \"%d\",\"email\":\"%s_%d@gmail.com\"}",id,formattedDate, id);
            requestWithPayload(conn, jsonInputString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteRequest() {
        try {
            URL url = new URL(API_URL + "user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);
            final int id = HttpClientApp.NEXT_ID.getAndIncrement();
            conn.setRequestProperty("Content-Type", "application/json");
            String jsonInputString = String.format("{\"id\": \"%d\"}", id);

            requestWithPayload(conn, jsonInputString);
        }catch (Exception e){
        e.printStackTrace();
        }
    }

    private static void sendRequest() {
        try{
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Response: " + response.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
