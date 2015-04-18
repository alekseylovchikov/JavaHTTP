package com.fruitdev.jsonserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.io.BufferedReader;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws Throwable {

        int port = 3000;

        ServerSocket fruitSocket = new ServerSocket(port);

        while(true) {
            Socket s = fruitSocket.accept();
            System.err.println("Listen port: " + port);
            new Thread(new SocketProcessor(s)).start();
        }

    }

    private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;

        private SocketProcessor(Socket s) throws Throwable {

            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();

        }

        public void run() {

            File file = new File("/home/aleksey/Documents/secrets.json");

            JSONParser parser = new JSONParser();
            String
                    title   = "",
                    author  = "",
                    version = "",
                    date    = "";

            try {

                Object obj = parser.parse(new FileReader(file));

                JSONObject jsonObject = (JSONObject) obj;

                title = (String) jsonObject.get("title");
                version= (String) jsonObject.get("version");
                author = (String) jsonObject.get("author");
                date = (String) jsonObject.get("date");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                readInputHeaders();
                writeResponse("<html><head><title>JSON</title></head><body><h1>Title: " + title +"<br />Version: " + version + "<br />Author: " + author + "<br />Date: " + date + "</h1></body></html>");
            } catch (Throwable t) {
                /* do nothing */
            } finally {
                try {
                    s.close();
                } catch (Throwable t) {
                    /* do nothing */
                }
            }
            System.err.println("JSON to HTML...");

        }

        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: Java JSON Server\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }

        private void readInputHeaders() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while(true) {
                String s = br.readLine();
                if(s == null || s.trim().length() == 0) {
                    break;
                }
            }
        }

    }

}
