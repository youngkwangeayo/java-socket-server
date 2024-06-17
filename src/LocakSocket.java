import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class LocakSocket {

    public static void main(String[] args) {
        // 소켓서버를 열음
        try (ServerSocket serverSocket = new ServerSocket(8811)) {
            System.out.println("WebSocket server started on port 8811");

            // while이 없으면 한번 동작 후 끊기기 때문에 와일안에
            while (true) {
                // accept 함수가 연결을 대기하고 있다 연결이되면 밑에 로직이 실행이된다.
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream();
  
                // HTTP 요청 헤더 읽기
                String data;
                StringBuilder request = new StringBuilder();
                while (!(data = in.readLine()).isEmpty()) {
                    request.append(data).append("\r\n");
                }
                

                // WebSocket 키 추출
                String key = request.toString().lines()
                        .filter(line -> line.startsWith("Sec-WebSocket-Key:"))
                        .map(line -> line.split(":")[1].trim())
                        .findFirst()
                        .orElse(null);

                if (key != null) {
                    String responseKey = Base64.getEncoder().encodeToString(
                            MessageDigest.getInstance("SHA-1")
                                    .digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8))
                    );

                    // WebSocket 핸드셰이크 응답 전송
                    String response = "HTTP/1.1 101 Switching Protocols\r\n"
                            + "Upgrade: websocket\r\n"
                            + "Connection: Upgrade\r\n"
                            + "Sec-WebSocket-Accept: " + responseKey + "\r\n\r\n";
                    out.write(response.getBytes(StandardCharsets.UTF_8));
                    out.flush();

                    // "Connection established" 메시지 전송
                    String message = "Connection established";
                    out.write(encodeWebSocketFrame(message.getBytes(StandardCharsets.UTF_8)));
                    out.flush();

                    // 클라이언트로부터 오는 메시지 처리
                    while (true) {
                        byte[] buffer = new byte[1024];
                        int read = clientSocket.getInputStream().read(buffer);
                        if (read == -1) {
                            break;
                        }
                        String receivedMessage = decodeWebSocketFrame(buffer, read);
                        System.out.println("Received: " + receivedMessage);
                        
                        String sendMessage = receivedMessage + ": 너가 보낸거";
                        System.out.println(sendMessage);
                        // 클라이언트에게 메시지 에코
                        // out.write(encodeWebSocketFrame(receivedMessage.getBytes(StandardCharsets.UTF_8)));
                        out.write(encodeWebSocketFrame(sendMessage.getBytes(StandardCharsets.UTF_8)));
                        out.flush();
                    }
                }

                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] encodeWebSocketFrame(byte[] data) {
        byte[] frame = new byte[data.length + 2];
        frame[0] = (byte) 0x81;  // 0x80 (FIN) | 0x01 (text frame)
        frame[1] = (byte) data.length;
        System.arraycopy(data, 0, frame, 2, data.length);
        return frame;
    }

    private static String decodeWebSocketFrame(byte[] buffer, int length) {
        int offset = 0;

        // 첫 번째 바이트
        byte b1 = buffer[offset++];

        // 두 번째 바이트
        byte b2 = buffer[offset++];

        boolean masked = (b2 & 0x80) != 0;
        int dataLength = b2 & 0x7F;

        if (dataLength == 126) {
            dataLength = ((buffer[offset++] & 0xFF) << 8) | (buffer[offset++] & 0xFF);
        } else if (dataLength == 127) {
            dataLength = 0;
            for (int i = 0; i < 8; i++) {
                dataLength = (dataLength << 8) | (buffer[offset++] & 0xFF);
            }
        }

        byte[] masks = new byte[4];
        if (masked) {
            for (int i = 0; i < 4; i++) {
                masks[i] = buffer[offset++];
            }
        }

        byte[] data = new byte[dataLength];
        for (int i = 0; i < dataLength; i++) {
            data[i] = (byte) (buffer[offset++] ^ masks[i % 4]);
        }

        return new String(data, StandardCharsets.UTF_8);
    }

    private static String decodeWebSocketFrameBefore(byte[] buffer, int length) {
        int dataLength = buffer[1] & 0x7F;
        byte[] data = new byte[dataLength];
        System.arraycopy(buffer, 2, data, 0, dataLength);
        return new String(data, StandardCharsets.UTF_8);
    }
}
