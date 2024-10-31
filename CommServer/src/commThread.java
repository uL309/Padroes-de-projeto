import java.net.Socket;
import java.util.ArrayList;
import java.io.*;
import java.lang.Thread;
import java.net.SocketException;

public class commThread extends Thread {

    private Socket clientSocket;
    private String nome;
    private ArrayList<commThread> clients = new ArrayList<commThread>();
    private String mensagem = null;
    private boolean running = true;

    public commThread(Socket clientSocket, ArrayList<commThread> clients) {
        this.clientSocket = clientSocket;
        this.clients = clients;
    }

    public void run() {
        try {
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());
            // Cria um buffer de entrada para receber mensagens do cliente
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
            // Cria um buffer de saída para enviar mensagens para o cliente
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
            // Aguarda o envio de mensagens pelo cliente

            while (running) {
                mensagem = in.readLine();
                if (mensagem != null) {
                    if (mensagem.equals("/close")) {
                        in.close();
                        out.close();
                        clientSocket.close();
                        running = false;
                    } else if (mensagem.startsWith("/name")) {
                        mensagem = mensagem.replace("/name ", "");
                        setNome(mensagem);
                        System.out.println("Cliente " + nome + " entrou na sala.");
                        retransmitir(mensagem + " entrou na sala");
                    } else if (mensagem.startsWith("/list")) {
                        for (commThread commThread : clients) {
                            commThread.status();
                        }
                        clients.removeIf(client -> client.isInterrupted());
                        clients.removeIf(client -> !client.isAlive());
                        out.println("Lista de clientes conectados: ");
                        for (commThread commThread : clients) {
                            out.println("Cliente: " + commThread.getNome());
                        }
                    } else if (mensagem.startsWith("/whisper")) {
                        String[] mensagemSplit = mensagem.split(" ");
                        String nomeDestinatario = mensagemSplit[1];
                        String mensagemDestinatario = mensagemSplit[2];
                        for (commThread commThread : clients) {
                            if (commThread.getNome().equals(nomeDestinatario)) {
                                commThread.entradaMensagem(nome + " susurrou para você " + mensagem);
                                out.println("Mensagem enviada para o cliente " + nomeDestinatario + ": "+ mensagemDestinatario);
                            } else {
                                continue;
                            }
                        }
                    } else if (mensagem.startsWith("/help")) {
                        out.println("Lista de comandos: ");
                        out.println("/name <nome> - Altera o nome do cliente");
                        out.println("/list - Lista os clientes conectados");
                        out.println("/whisper <nome> <mensagem> - Envia uma mensagem privada para o cliente");
                        out.println("/close - Fecha a conexão com o servidor");
                        out.println("/help - Lista os comandos disponíveis");
                    } else {
                        System.out.println("Mensagem recebida do cliente " + nome + ": " + mensagem);
                        // Envia a mensagem para o cliente
                        retransmitir(nome + ": " + mensagem);
                    }
                }else{continue;}
            }
            System.out.println("Cliente " + nome + " desconectou.");
            retransmitir("Cliente " + nome + " desconectou.");
            clientSocket.close();
        } catch (SocketException e) {
            // Tratar a exceção de conexão resetada
            System.out.println("Conexão com o cliente foi resetada.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void retransmitir(String mensagem) {
        for (commThread commThread : clients) {
            commThread.status();
        }
        clients.removeIf(client -> client.isInterrupted());
        clients.removeIf(client -> !client.isAlive());
        for (commThread commThread : clients) {
            if (mensagem != null) {
                commThread.entradaMensagem(mensagem);
            }
        }
    }

    public String mandarMensagem() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String mensagem = in.readLine();
            return mensagem;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            this.interrupt();
            return null;
        }
    }

    public void close() {
        try {
            clientSocket.close();
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
            this.interrupt();
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void add(commThread commThread) {
        clients.add(commThread);
    }

    public void entradaMensagem(String mensagem) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(mensagem);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            this.interrupt();
        }
    }
    public void status(){
        if (clientSocket.isClosed()){
            this.interrupt();
        }
    }
}
