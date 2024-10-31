import java.lang.Thread;
import java.util.ArrayList;
import java.net.Socket;
import java.net.ServerSocket;


public class commMngr extends Thread{
    private ArrayList<commThread> clients= new ArrayList<commThread>();
    private Socket clientSocket;
    private ServerSocket serverSocket;

    public commMngr(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    } 

    public void run(){
        System.out.println("Aguardando conexões de clientes na porta ...");
        while(!serverSocket.isClosed()){
            try {
                clientSocket = serverSocket.accept();
                commThread commThread = new commThread(clientSocket,clients);
                if (commThread != null) {
                    clients.add(commThread);
                    commThread.start();
                }
            }catch (java.net.SocketException e) {
                if(serverSocket.isClosed()){
                    System.out.println("Server socket está fechado, parando.");
                }else{
                    e.printStackTrace();
                }
            }catch (Exception e) {
                e.printStackTrace();
                interrupt();
            }
        }
    }

    public void close(){
        try{
            for (commThread Thread : clients) {
                Thread.close();
            }
            this.interrupt();
        }catch(Exception e){
            e.printStackTrace();
            this.interrupt();
        }
    }

    public void listarClientes(){
        for (commThread commThread : clients) {
            commThread.status();
        }
        clients.removeIf(client -> client.isInterrupted());
        clients.removeIf(client -> !client.isAlive());
        for (commThread Thread : clients) {
            System.out.println(Thread.getNome());
        }
    }

    public void whisper(String nome, String mensagem){
        for (commThread commThread : clients) {
            commThread.status();
        }
        clients.removeIf(client -> client.isInterrupted());
        clients.removeIf(client -> !client.isAlive());
        for (commThread Thread : clients) {
            if (Thread.getNome() == nome){
                Thread.entradaMensagem("O servidor sussurrou: "+mensagem);
                return;
            }else{
                continue;
            }
        }
    }

    public void broadcast(String mensagem){
        clients.removeIf(client -> !client.isAlive());
        clients.removeIf(client -> client.isInterrupted());
        for (commThread commThread : clients) {
            if (mensagem != null){
                commThread.entradaMensagem("Aviso do servidor: "+mensagem);
                return;
                
            }else{
                continue;
            }
        }

    }
}
