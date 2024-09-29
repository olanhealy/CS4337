package com.example.demo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class HttpClientApp {
    private static final String API_URL = "http://localhost:8080/hello";
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java HttpClientApp <numberOfRequests> <threadPoolSize>");
            return;
        }
        int numberOfRequests = Integer.parseInt(args[0]);
        int threadPoolSize = Integer.parseInt(args[1]);
        ExecutorService executor =
                Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i < numberOfRequests; i++) {
            executor.submit(() -> sendRequest());
        }
        executor.shutdown();
    }
    private static void sendRequest() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Response: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}