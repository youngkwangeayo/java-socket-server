import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TestNewSocket {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8811);
        System.out.println("WebSocket server started on port 8811");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();
            
            // 핸드셰이크 요청 읽기
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                System.out.println(line);
            }

            // 핸드셰이크 응답
            String response = "HTTP/1.1 101 Switching Protocols\r\n"
                            + "Upgrade: websocket\r\n"
                            + "Connection: Upgrade\r\n"
                            + "Sec-WebSocket-Accept: <computed-key>\r\n"
                            + "\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // 데이터 수신 및 처리
            byte[] buffer = new byte[1024];
            while (true) {
                int read = clientSocket.getInputStream().read(buffer);
                if (read == -1) {
                    break;
                }

                String receivedMessage = decodeWebSocketFrame(buffer, read);
                System.out.println("Received: " + receivedMessage);
                
                // 클라이언트에게 메시지 에코
                out.write(encodeWebSocketFrame(receivedMessage.getBytes(StandardCharsets.UTF_8)));
                out.flush();
            }

            clientSocket.close();
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
}
