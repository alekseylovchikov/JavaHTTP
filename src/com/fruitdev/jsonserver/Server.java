package com.fruitdev.jsonserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.json.*;

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

            String jsonStr = "{ \"name\": \"John\", \"age\": 27 }";
            String name = "";
            int age = 0;

            try {
                JSONObject obj = new JSONObject(jsonStr);
                name = obj.getString("name");
                age = obj.getInt("age");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                readInputHeaders();
                writeResponse("<html><head><title>JSON</title></head><body><h1>Name: " + name +"<br />Age: " + age + "</h1></body></html>");
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
