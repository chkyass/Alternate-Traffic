import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;




public class Moniteur extends Thread {
	
	//static Fenetre f = new Fenetre("Route");
	private long nbH;
	private long nbB;
    private int saturation =Data.VISIBILITE;
    boolean pecs = false;
	
	//Reseau
	private int portFH = 6050;
	private int portFB = 6051;
	private int portG = 6800;
	private InetAddress hote=null;
	private BufferedReader inFH = null;
	private BufferedReader inFB = null;
	private PrintWriter outFH= null;
	private PrintWriter outFB= null;
	private Socket scFH;
	private Socket scFB; 
	private static int couleur = 0;
	private InetAddress adresse;
	private DatagramSocket socketGen;
	
	private void initComF(){
		try {
			hote = InetAddress.getLocalHost();
			adresse = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {}

		try{					
			scFH = new Socket(hote, portFH);
			inFH = new BufferedReader(new InputStreamReader(scFH.getInputStream()));
			outFH = new PrintWriter(scFH.getOutputStream(),true);
			scFB = new Socket(hote, portFB);
			inFB = new BufferedReader(new InputStreamReader(scFB.getInputStream()));
			outFB = new PrintWriter(scFB.getOutputStream(),true);
			socketGen = new DatagramSocket();
	

		}catch(IOException e){
			System.out.println("prout");
		}
	}


		
	public void envoyer_a_G(String s){
		try{ 
		    byte[] message;
		    message = s.getBytes();
			int msglen = message.length;
			DatagramPacket packet = new DatagramPacket(message, msglen,adresse, portG);   
			socketGen.send(packet);
			}catch(Exception e){}
		}
	
	
	public void run(){
		initComF();
		while(true){
			try {
				outFH.print("nb?\n");
				outFH.flush();
				outFB.print("nb?\n");
				outFB.flush();
				String s1;
				String s2;
				while(true){
					if(inFH.ready()) {
						s1 = inFH.readLine();
						break;
					}
				}
				while(true){
					if(inFB.ready()) {
						s2 = inFB.readLine();
						break;
					}
				}
			    nbH =(long) Integer.parseInt(s1);
			    nbB =(long) Integer.parseInt(s2);
			    long duree = 1;
			    if(nbH !=0 && nbB != 0){
				    if(couleur%2==0){
						duree = nbH/nbB;
					}else{
						duree = nbH/nbB;
					}
			    }
                envoyer_a_G(Integer.toString(couleur%2));
			    sleep(duree*500+Data.F*200);
                envoyer_a_G(Integer.toString(-1));
				if(Generateur.verif(-1)){
				    while(true){
						if(inFH.ready()){
							inFH.readLine();
							break;
						}
					    if(inFB.ready()){
					    	inFB.readLine();
					    	break;
					    }
				    }
				}else sleep(500);
				couleur++;
				if(nbH+nbB>= saturation && !pecs){
                    pecs = true;
					envoyer_a_G("HB");
                }
				if(pecs && nbH+nbB <=Data.VISIBILITE/2) {
					pecs = false;
					envoyer_a_G("FS");
				}
				}catch (NumberFormatException | IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}

}
