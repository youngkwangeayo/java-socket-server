package test;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.Port;

public class TestSocket {
    

    public static void main(String[] args) throws IOException {
        
        InetAddress inetAddress = InetAddress.getLocalHost();
        

        String serverIp = inetAddress.getHostAddress();

        System.out.println(serverIp);
        
        ServerSocket server = new ServerSocket(8811);
        
        
        System.out.println(	server.getInetAddress());
        
        Socket socket = server.accept();
        while (true) {
            
            System.out.println(socket.toString());
            // socket.connect(null);
            System.out.println(	server.getInetAddress()+"연결이라고?");
        }
        
        
        // server.setSocketFactory
        

    }
};


// class SoketServerL {

//     private ServerSocket server;

//     public SoketServerL () throws IOException{
//         this.server = new ServerSocket(8811);
//     }

    

// }



