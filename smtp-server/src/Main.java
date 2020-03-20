import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Main {

	/*
	 * Comentarios
	 * 
	 * Web para probar expresiones regulares con java: https://www.freeformatter.com/java-regex-tester.html
	 * 
	 * Funciones
	 * 
	 * sendServerMessage: Forma un mensaje del servidor con un codigo y una string, añade <CRLF> al final
	 * waitClientMessage: Pide una string de respuesta y la devuelve
	 * waitClientMessageRegex: Pide una string de respuesta, la parsea segun la expresion regular y devuelve lo importante
	 * waitMultilineClientMessage: Pide varias string, aun sin terminar
	 */
	
	private static Scanner scanner;

	public static void main(String[] args) {

		String SERVER_NAME = "server.local";
		int SERVER_PORT = 25;
		
		ServerSocket Serv_Socket = null;
		Socket Socket_Connection = null;
		BufferedReader input = null;
	    PrintWriter output = null;
		
		String data = "";
		
		
		
		testSocketConnectionServer(SERVER_PORT, Serv_Socket, Socket_Connection, data, input, output);
		
		sendServerMessage(220, SERVER_NAME + " Service Ready");
		waitClientMessageRegex("(HELO) (([a-zA-Z0-9]+)(\\.([a-zA-Z0-9]+))*)");	// HELO client.example.com\r\n
        
        
		sendServerMessage(250, "Request mail action okay, completed");
		waitClientMessageRegex("(MAIL FROM:) <([a-zA-Z0-9]+@(([a-zA-Z0-9]+)(.([a-zA-Z0-9]+))+))>");	// MAIL FROM: <user@example.com>\r\n
        
		sendServerMessage(250, "OK");
		waitClientMessageRegex("((DATA))");	// DATA\r\n
              
        sendServerMessage(354, "Start mail input; end with <CRLF>.<CRLF>");
        waitMultilineClientMessage();
        //Subject:<Subject>\r\n
        //From:<email@domain.com>\r\n
        //To:<user1@server.local>,<user2@server.local>\r\n
        // LINEA VACIA AQUI
        // Esto es un mensaje de prueba, /nDos lineas..
        // \r\n.\r\n.
        
		sendServerMessage(250, "OK");
		waitClientMessageRegex("((QUIT))");	// QUIT\r\n
        
		sendServerMessage(221, SERVER_NAME + " Service closing transmission channel");
        
	}
	
	public static void sendServerMessage(int code, String comment){
        String message = code + " " + comment + "\r\n";
        
        System.out.print(message);
	}
	
	public static String waitClientMessage(){
        scanner = new Scanner(System. in);
        String message = scanner.nextLine();
        
        return message;
	}
	
	public static String waitClientMessageRegex(String pattern){
        scanner = new Scanner(System. in);
        String message = scanner.nextLine();
        
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(message);

        if (m.matches()) {
        	return m.group(2);	// 0: whole matched expression, 1: first expression in brackets, 2: second exp, ...
        }else {
        	System.out.println("Error: Incorrect message for client");
        	return null;
        }
        
	}
	
	public static String waitMultilineClientMessage(){
		scanner = new Scanner(System. in);
		String message = "";
		
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			message = message.concat(line + "\r\n");
			if( message.contains("\r\n.\r\n") ) break;
		}

        return message;
	}
	
	public static void testSocketConnectionServer(int port, ServerSocket sv_soc, Socket conn_soc, String data, BufferedReader input, PrintWriter output)
	{
		try {
			sv_soc = new ServerSocket(port);
			System.out.println("Trying to connect the Client...");
			
			while(data.compareTo("END") != 0)
			{
				conn_soc = sv_soc.accept();	
				System.out.println("Connection accepted.");
				input = new BufferedReader(new InputStreamReader(conn_soc.getInputStream()));
				output = new PrintWriter(conn_soc.getOutputStream(), true);
				data =  input.readLine();
			    System.out.println("Server receives: "+data);
		        output.println(data);			
		        conn_soc.close();		
			}
			
			sv_soc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
