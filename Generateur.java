import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.io.IOException;


public class Generateur extends Thread{
	static Fenetre f = new Fenetre("Route");
	static boolean fini=false;
	private Random rnd = new Random();
	public static boolean vertH  = false;
	public static boolean vertB = false;
	private int port = 6800 ;
	private final static Object verrouCouleur = new Object(); 
	private DatagramSocket socket;
	private static boolean verif = false;
	private final static Object o = new Object();
	private boolean HB = false;
	private int attente;
	
	public static boolean verif(int i){
		synchronized(o){
			if(i == 1) verif = true;
			else if(i==0) verif = false;
		    return verif;
		}
	}
	
	public static boolean getCouleur(char c){
		synchronized(verrouCouleur){
			if(c =='H') return vertH;
			else if(c == 'B') return vertB;
			else return false;
		}
	}

	public static void setCouleur(char c, boolean couleur){
		synchronized(verrouCouleur){
			if(c =='H') vertH = couleur;
			else if(c == 'B') vertB = couleur;
		}
	}	
	void affiche(){
		Vehicule x;
		StringBuffer vbH = new StringBuffer("");
		StringBuffer vbB = new StringBuffer("");
		for(int i=-Data.R;i<=Data.R;i++){
			if(i == Data.F) {
				if(getCouleur('H')) vbH.append('V');
				else vbH.append('R');
			}
			else vbH.append(" ");
		}
		vbH.append("\n");
		for(int i=-Data.R;i<=Data.R;i++){
			//vbH.append(Math.abs(i)%10+"");
			x=Vehicule.VH.get(i);
			if(x!=null) vbH.append((x.no)%10);
			else vbH.append("_");
		}
		for(int i=-Data.R;i<=Data.R;i++){
			//vbB.append(Math.abs(i)%10+"");
			x=Vehicule.VB.get(i);
			if(x!=null ){
					if (x.pos!=0) vbB.append((x.no)%10);
					else{
						Integer no =(x.no)%10;
						vbH.setCharAt(3*(Data.R)+2,no.toString().charAt(0));
					}
			}else vbB.append("_");
			if(i==0) vbB.append("X");
		}
		vbB.append("\n");
		for(int i=-Data.R;i<=Data.R;i++){
			 if(i == (-Data.F)) {	
				if(getCouleur('B')) vbB.append('V');
				else vbB.append('R');
			}
			else vbB.append(" ");
		}
		//System.out.println(vbH);
		Generateur.f.jtf1.setText(vbH.toString());
		//System.out.println(vbB);
		Generateur.f.jtf2.setText(vbB.toString());
	}


	private void receiveV(){
		byte[] buffer = new byte[1024];
		String msg;
		try {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);	
			msg = new String(buffer, 0,packet.getLength());
			if(msg.length()!=0){
				if(msg.equals("0")){
					//System.out.println("feuH vert");
					setCouleur('H',true);
				}
				else if(msg.equals("1")){
					//System.out.println("feuB vert");
					setCouleur('B',true);
				}
				else if(msg.equals("-1")) {
					setCouleur('H',false);
					setCouleur('B',false);
				}else if(msg.equals("HB")){
					HB=true;
					attente= 1000;
				}else if(msg.equals("FS")){
					HB = false;
				}
			}
		} catch (IOException e) {
		}
	}
	
	public void run() {		
		try {
			socket = new DatagramSocket(port);
			socket.setSoTimeout(50);
		} catch (SocketException e) {}
		
		while (!fini) {
				receiveV();
				Vehicule v=Vehicule.creeInstance(rnd.nextInt(2)) ;
				if(v!=null){
					v.start();
				}
			affiche();
			if(!HB) attente = rnd.nextInt(700);
			try {
				//if(!HB)attente = 100;
				sleep(attente);
			} catch (InterruptedException e) {}
		
		}
	}
}
