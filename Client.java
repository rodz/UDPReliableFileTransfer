import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

class GlobalCliente{
	public static byte window;
	public static byte factor;
	public static byte start;
	public static boolean acTam;
	public static boolean AOK;
	public static boolean[] ACK;
	public static boolean[] sent;
	public static byte[][] fim;
}

public class Cliente {

	public static void main(String[] args) throws IOException {
		GlobalCliente g=new GlobalCliente();
		Scanner in = new Scanner(System.in);
        HostCliente host = new HostCliente();
        ServerCliente serer=new ServerCliente();
        System.out.println("Digite qualquer tecla para comecar");
        String j=in.nextLine();
        host.start();
        serer.start();
    
	}
}
class ServerCliente extends Thread{
	public void run() {
        String end = "localhost";
        GlobalCliente g=new GlobalCliente();
        int port = 3010;
        try {
        	DatagramSocket soQuet = new DatagramSocket();
            Scanner in = new Scanner(System.in);
            InetAddress d= InetAddress.getByName("localhost");
            System.out.println("Digite a precisao");
            g.factor = in.nextByte();
            System.out.println("Digite a janela");
            g.window = in.nextByte();
            byte[] janela= {g.window};
            DatagramPacket l=new DatagramPacket(janela, janela.length, d, port);
            soQuet.send(l);
            while(!g.acTam) {
            	
            }
            byte[] pg= {-1};
            DatagramPacket recebeAc=new DatagramPacket(pg, pg.length, d, port);
            soQuet.send(recebeAc);
            while(true) {
            	//Recebe os Acks
            		for(byte i=0; i<g.ACK.length; i++) {
            			if(g.ACK[i] && !g.sent[i]) {
            				System.out.println("Recebemos o "+i+" enviando ACK");
            				byte[] po= {i};
            				DatagramPacket receb=new DatagramPacket(po, po.length, d, port);
            				soQuet.send(receb);
            				g.sent[i]=true;
            			}
            		}
            		while (g.start<g.ACK.length&& g.ACK[g.start] && g.sent[g.start]) g.start++;
            	}
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
}

class HostCliente extends Thread {
	String dirPath = "C:\\Users\\Rodrigo\\Desktop\\Saida\\J.zip";
	GlobalCliente g=new GlobalCliente();
	
	public void run() {
		try {
			System.out.println("ONLINE");
			DatagramSocket socket = new DatagramSocket(3002);
			byte[] b=new byte[1026];
        	DatagramPacket f= new DatagramPacket(b, b.length);
			File arq = new File(dirPath);
			FileOutputStream saida=new FileOutputStream(dirPath);
			int i=0;
			while(!g.acTam) {
				socket.receive(f);
				byte TamanhoDoArq= f.getData()[0];
				g.ACK=new boolean[TamanhoDoArq];
				g.sent=new boolean[TamanhoDoArq];
				g.fim=new byte[TamanhoDoArq][];
				g.acTam=true;
			}
			System.out.println("Tamanho do Arquivo: "+g.ACK.length);
			int inicio=0, fim=g.window;
			while(g.start<g.ACK.length) {
			byte o=0;
			i=g.start;
			while(o<(Math.min(g.window, g.ACK.length-g.start)) ){
				System.out.println("I: "+i);
				i++;
				socket.receive(f);
				if(DiscartFactor(g.factor)) {
					byte[] jk=f.getData();
					if(jk.length>1&&jk[0]==-1) {
						if(g.ACK[jk[1]]) {
							g.sent[jk[1]]=false;
						}
						System.out.println("Recebemos o pacote: "+jk[1]);
						
					g.ACK[jk[1]]=true;
					//Marcamos o pacote como recebido
					System.out.println(jk.length);
					g.fim[jk[1]]=copy(jk);
					}
				}else {
					//Nao faz nada, o pedaço do arquivo nao chegou corretamente
				}
				o++;
			}
			while (g.start<g.ACK.length && g.ACK[g.start]) g.start++;
			}
			System.out.println("Pacote recebido OK");
			for(int l=0; l<g.ACK.length; l++) {
				System.out.println("Tamanho do pacote "+l+": "+g.fim[l].length);
				saida.write(g.fim[l], 2, 1024);;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
}
	
	public static boolean DiscartFactor(int constante) {
		Random r=new Random();
		return constante>r.nextInt(100);
	}
	public static boolean checkSum(byte[] tester) {
		return false;
	}
	public static byte[] copy(byte[] g) {
		byte[] resp=new byte[g.length];
		for(int v=0; v<g.length; v++) {
			resp[v]=g[v];
		}
		return resp;
	}
}
