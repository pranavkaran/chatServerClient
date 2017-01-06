package ChatServerClient;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class Server {
	
	private static final int PORT = 9001;
	private static Map<String, PrintWriter> map = new HashMap<String, PrintWriter>();
    
    public static void main(String[] args) throws Exception {
    	System.out.println("Server is running...........");
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new ServerSocketFlow(listener.accept(), map).start();;
			}
		} finally {
			listener.close();
		}
    }    
}
