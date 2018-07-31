import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Vehicule extends Thread {
		public static int noC=1;
		static int dernier2=-1;
		static Map<Integer,Vehicule> VH=new HashMap<Integer,Vehicule>();
		static Map<Integer,Vehicule> VB=new HashMap<Integer,Vehicule>();
		static List<Vehicule> listeVehicules = new LinkedList<Vehicule>();
		static final Object verrouListe = new Object();
		static final int portFeuH = 6050;
		static final int portFeuB = 6051;
		
		
		InetAddress adresse;
		DatagramSocket socket;
		int no;
		String nom;
		public int pos;
		public boolean voieBasse;
		boolean dernier1;
		
		Vehicule(){
			nom=new Integer(no).toString();
			//majList(0,this);
			dernier1 = false;
		}
		
		public static Vehicule creeInstance (int h){
			Vehicule v = new Vehicule();
			if(h==0){
				synchronized(VH){
					if(VH.get(Data.R)==null){
						VH.put(Data.R, v);
						v.voieBasse=false;
						v.pos = Data.R;
						v.no = v.noC;
						v.noC++;
						majList(0,v);
						return v;
					}
				}
			}else if(h==1){
				synchronized(VB){
					if(VB.get(-Data.R) == null){
						VB.put(-Data.R, v);
						v.voieBasse = true;
						v.pos= -Data.R;
						v.no = v.noC;
						v.noC++;
						majList(0,v);
						return v;
					}
				}
			}
			return null;
		}
		
		public void setDernier1(){
			dernier1=false;
		}
		
		public static void majList(int p, Vehicule v){
			synchronized(verrouListe){
				if(v !=null) {
					listeVehicules.add(v);
				}
				else if(p!=-1){
					for(Vehicule f : Vehicule.listeVehicules){
						if(f.no==p ) f.dernier1=false;
					}
					//Vehicule.listeVehicules.get(p).setDernier1();
				}
			}
		}
		
		public void run(){
			initCom();
			while((voieBasse && pos<=Data.R)||(!voieBasse && pos>=-Data.R)){
				if(voieBasse){
					synchronized(VB){
						if(pos == ((-Data.F)-1) && !Generateur.getCouleur('B')) {
							continue;
						}
						if(VB.get(new Integer(pos+1))==null){
							VB.remove(pos);
							pos+=1;
							VB.put(new Integer(pos), this);
							if(pos == -Data.F-Data.VISIBILITE)envoyermessage("-1",portFeuB);
							else if(pos==-Data.F){
								//System.out.println("vehicule visible --"+"voie basse ="+voieBasse+" numero ="+no%10 + " FB ="+!Generateur.getCouleur('B')+" FB ="+!Generateur.getCouleur('H'));
								envoyermessage("-2",portFeuB);
								dernier1 = true;
								majList(dernier2, null);
								dernier2 = no;
								Generateur.verif(1);
								//System.out.println(dernier2);
							}
							else if(dernier1 && pos == Data.F){
								envoyermessage("-3",portFeuB);
								Generateur.verif(0);
								//System.out.println("Liberation---->voie basse ="+voieBasse+" numero ="+no%10+" FB ="+Generateur.getCouleur('B')+" FH ="+Generateur.getCouleur('H')); 
							}
						}
					}
					try {sleep(150);} catch (InterruptedException e) {e.printStackTrace();}
				}else{
					synchronized(VH){
						if(pos == ((Data.F)+1) && !Generateur.getCouleur('H')){
							continue;
						}
						
						if(VH.get(new Integer(pos-1))==null){
							//System.out.println("prout");
							VH.remove(pos);
							pos-=1;
							VH.put(new Integer(pos), this);
							if(pos == Data.F+Data.VISIBILITE) envoyermessage("-1", portFeuH);
							else if(pos==Data.F){
								//System.out.println("vehicule visible --"+"voie basse ="+voieBasse+" numero ="+no%10 + " FB ="+!Generateur.getCouleur('B')+" FB ="+!Generateur.getCouleur('H'));
								envoyermessage("-2",portFeuH);
								dernier1 = true;
								majList(dernier2, null);
								dernier2 = no;
								Generateur.verif(1);
							}
							else if(dernier1 && pos ==-Data.F){
								//System.out.println("Liberation---->voie basse ="+voieBasse+" numero ="+no%10+" FB ="+Generateur.getCouleur('B')+" FB ="+Generateur.getCouleur('H')); 
								envoyermessage("-3",portFeuH);
								Generateur.verif(0);
							}
						}
					}
					try {sleep(150);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
			if(voieBasse) Vehicule.VB.remove(pos);
			else Vehicule.VH.remove(pos);
		}
		
		public void initCom(){
			try {
				adresse = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				socket = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public void envoyermessage(String s, int por){
			try{ 
			   byte[] message;
			   message = s.getBytes();
			   int msglen = message.length;
			   DatagramPacket packet = new DatagramPacket(message, msglen,adresse, por);   
			   socket.send(packet);
			}catch(Exception e){}
		}
		
		
}
