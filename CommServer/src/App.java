import java.net.ServerSocket;
import java.util.Scanner;
import java.net.InetAddress; 

public class App {
    public static void main(String[] args) throws Exception {
        int porta = 6969; // Porta em que o servidor estará ouvindo
        Boolean running = true;
        Scanner scanner = new Scanner(System.in);
        String mensagem;
        String nomeDestinatario;
        String mensagemDestinatario;
        System.out.println("Digite o IP do servidor: ");
        String setIP= scanner.nextLine();
        if (setIP.isBlank()) {
            System.out.println("IP não definido, usando Localhost");
            setIP = "127.0.0.1";
        }
        InetAddress ip = InetAddress.getByName(setIP);
        ServerSocket serverSocket = new ServerSocket(porta, 0, ip);
        System.out.println("endereço "+serverSocket);
        commMngr commMngr = new commMngr(serverSocket);
        commMngr.start();   
        
        while (running) {
            mensagem = scanner.nextLine();
            if (mensagem.equals("/close")){
                running = false;
            }else if(mensagem.equals("/list")){
                commMngr.listarClientes();
            }else if (mensagem.startsWith("/whisper")){
                System.out.println("Digite o nome do destinatário: ");
                nomeDestinatario = scanner.nextLine();
                System.out.println("Digite a mensagem: ");
                mensagemDestinatario = scanner.nextLine();
                commMngr.whisper(nomeDestinatario, mensagemDestinatario);
            }else if(mensagem.equals("/help")){
                System.out.println("Lista de comandos: ");
                System.out.println("/close - Fecha a conexão com o servidor");
                System.out.println("/help - Lista os comandos disponíveis");
                System.out.println("/list - Lista os clientes conectados");
                System.out.println("/whisper - Envia uma mensagem privada para o cliente");
            }else{
                commMngr.broadcast(mensagem);
            }
        }
        System.out.println("Fechando servidor...");
        scanner.close();
        commMngr.close();
        commMngr.interrupt();
        serverSocket.close();
        
    }
}
