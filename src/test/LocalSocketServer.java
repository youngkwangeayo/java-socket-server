package test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpClient;

public class LocalSocketServer {
    
    public static void main(String[] args) {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String serverIp = inetAddress.getHostAddress();

            System.out.println("Server IP: " + serverIp);

            

            // 서버 소켓 생성 및 포트 8811로 바인딩
            ServerSocket server = new ServerSocket(8811);
            System.out.println("Server started at: " + serverIp + ":8811");
            
            while (true) {
                // 클라이언트 연결 대기
                Socket socket = server.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                // 클라이언트와의 통신을 처리하기 위한 입출력 스트림 생성
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // 클라이언트에게 연결 신호 전송
                String OK = "Connection established";
                out.println(OK);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received from client: " + message);
                    
                    // 클라이언트로 메시지 전송 (echo)
                    // out.println("Echo: " + message);
                }

                // 자원 해제
                in.close();
                out.close();
                socket.close();
                System.out.println("Client disconnected");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
