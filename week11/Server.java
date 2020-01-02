import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Server {
	
	private Scanner scanner;
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	int port = 8888;
	String echo;
	String response;
	
	public Server() {
		scanner = new Scanner(System.in);
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server open");
			
			socket = serverSocket.accept();
			
			System.out.println("New connection");
		} catch(IOException e) {
			System.out.printf("Server exception "+e.getMessage());
			e.printStackTrace();
		}
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			printWriter = new PrintWriter(
					socket.getOutputStream(), true);
		
			Runnable sending = new Runnable(){
				public void run(){
					send();
				}
			};
			Runnable recieving = new Runnable(){
				public void run(){
					recieve();
				}
			};
			Thread sendingThread = new Thread(sending);
			Thread reievingThread = new Thread(recieving);
			sendingThread.start();
			reievingThread.start();
			try {
				sendingThread.join();
				reievingThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				System.out.print("Socket closed");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new Server();
	}

	public void send(){
		do {
			echo = scanner.nextLine();
			printWriter.println(echo);
		}
		while(!echo.equals("exit"));
	}

	public void recieve(){
		while(true){
			try {
				response = bufferedReader.readLine();
				if(response != null){
					System.out.print("Other: "+response+"\n");
				}
			} catch(Exception e) {
				System.out.print("Disconnected\n");
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(1);
			}
		}
	}
}