import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class GlobalServer{
	public static byte start;
	public static byte window;
	public static boolean acW;
	public static boolean acTam;
	public static boolean[] ACK;
}

public class Server extends Thread{

	public static void main(String[] args) throws IOException, InterruptedException {
		Scanner in = new Scanner(System.in);
        ServerServer serv = new ServerServer();
        ServerHost hos=new ServerHost();
        serv.start();
        hos.start();
        System.out.println("Aperte qualquer botão para iniciar");
        String x=in.nextLine();
        hos.run();
        serv.run();
	}

}class ServerServer extends Thread{
		public void run() {
		System.out.println("ONLINE");
		Scanner in = new Scanner(System.in);
        String end = "localhost";
        int port = 3002;
		try {
				GlobalServer g=new GlobalServer();
				DatagramSocket soque=new DatagramSocket();
			 	byte b[][] = new byte[2048][1024];
			 	File arq=new File("C:\\Users\\Rodrigo\\Desktop\\proco\\udp.zip");
			 	FileInputStream f=new FileInputStream(arq);
			 	InetAddress d= InetAddress.getByName("localhost");
			 	DatagramPacket[] vetor=new DatagramPacket[1024];
			 	byte tam=0;
			 	while(f.read(b[tam])!=-1) {
			 		vetor[tam]= new DatagramPacket(b[tam], b[tam].length, d, port);
			 		
			 		tam++;
			 	}
			 	System.out.println("Tamanho do pacote "+tam);
			 	while(!g.acW) {
			 		System.out.println("Esperando a janela");
			 	}
			 	g.ACK=new boolean[tam];
			 	
			 	while(!g.acTam) {
			 		byte[] saque= {tam};
			 		DatagramPacket inicial=new DatagramPacket(saque, saque.length, d, port);
			 		soque.send(inicial);
			 	}
			 	System.out.println("Inicio: "+g.start+" janela de: "+g.window);
			 	while(g.start<g.ACK.length) {
	            	byte i=g.start, oco=0;
	            
	            	while(oco<(Math.min(g.window, g.ACK.length-g.start))) {
	            		
	            		//System.out.println("Checando o pacote: "+i);
	            		if(!g.ACK[i]) {
	            			byte[] enviando=new byte[vetor[i].getData().length+2];
	            			enviando[0]=-1;
	            			enviando[1]=i;
	            			System.out.println("Enviando o pacote: "+enviando[1]);
	            			for(int h=2; h<=vetor[i].getData().length+1; h++) {
	            				enviando[h]=vetor[i].getData()[h-2];
	            			}
	            			DatagramPacket pacote=new DatagramPacket(enviando, enviando.length, d, port);
	            			soque.send(pacote);
	            		}
	            		oco++;
	            		i++;
	            	}
	            	while (g.start<g.ACK.length && g.ACK[g.start] ) g.start++;
	            }
			 	
        } catch (IOException e) {
			e.printStackTrace();
		} 
    }
}

class ServerHost extends Thread{   
	GlobalServer g=new GlobalServer();
	public void run() {
    	boolean x=true;
        try {
        	DatagramSocket soQuet = new DatagramSocket(3010);
        	System.out.println("SERVERHOST ONLINE");
            while(!g.acW) {
            	byte[] pri=new byte[1];
            	DatagramPacket f= new DatagramPacket(pri, pri.length);
            	soQuet.receive(f);
            	g.window=f.getData()[0];
            		g.acW=true;            		
            }
            System.out.println("Janela OK "+g.window);
            while(!g.acTam) {
            	byte[] pri=new byte[1];
            	DatagramPacket f= new DatagramPacket(pri, pri.length);
            	soQuet.receive(f);
            	g.acTam=(f.getData()[0]==-1);
            }
            System.out.println("Tamanho OK");
            while(x) {
            	byte[] pri=new byte[1];
            	DatagramPacket f= new DatagramPacket(pri, pri.length);
            	soQuet.receive(f);
            	System.out.println("Cliente recebeu: "+f.getData()[0]);
            	g.ACK[f.getData()[0]]=true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

