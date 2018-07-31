import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;


class Feu extends Thread{

	private int nbVoiture = 0;
	private int port;
	private boolean changementOk = false;
	private InetAddress hote=null;
	private BufferedReader in = null;
	private PrintWriter out= null;
	private ServerSocket sc;
	private Socket s;
	DatagramSocket socket;
	
	
	public Feu(int port){
		this.port=port;
	}
	
	private void init(){
		try {
			hote = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {}

		try{					
			sc = new ServerSocket(port);
		}catch(Exception e){}
		try {
			s = sc.accept();
			while(!s.isConnected()){
				s=sc.accept();
			}
			in = new BufferedReader( new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(),true);
			socket = new DatagramSocket(port);
			socket.setSoTimeout(50);
		}catch (Exception e) {}
	}
	
	private void receiveV(){
		byte[] buffer = new byte[1024];
		String messageV;

		try {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);	
				socket.receive(packet);	
				messageV = new String(buffer, 0,packet.getLength());
				if(messageV.length()!=0){
					//System.out.println(nbVoiture);
					if(messageV.equals("-1")){
						nbVoiture++;
						messageV="";
					}else if(messageV.equals("-2")){
						nbVoiture--;
						messageV="";
					}else if(messageV.equals("-3")){
						changementOk = true;
						messageV="";
					}
				}
		} catch (IOException e) {
		}
	}
    
	private void receiveM(){
			try {
				if(in.ready()){
			        String s = in.readLine();
					if(s.equals("nb?")){
						out.print(nbVoiture+"\n");
						out.flush();
					}
				}
			} catch (NumberFormatException | IOException e) {}
	}
	public void run(){
	    init();
		while(true){
			receiveM();
			receiveV();
			if(changementOk){
				out.print("changementOK\n");
				out.flush();
				changementOk = false;
			}
		}
	}
	
	public static void main(String args[]) throws Exception{
		Feu f = new Feu(Integer.parseInt(args[0]));
		f.start();
	}
}
