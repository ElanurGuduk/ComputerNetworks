/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dortIslem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author Asus
 */
public class TCP_Server {

    private ServerSocket serverSocket;
    private Thread serverThread;
    private static int mySelection;

    protected static void setInfo(int selection) {
        mySelection = selection;
    }

    protected void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server baslatildi..");

        serverThread = new Thread(() -> {
            if (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client baglandi " + clientSocket);
                    // bağlanan her client için bir thread oluşturup dinlemeyi başlat
                    new ListenThread(clientSocket).start();
                } catch (IOException ex) {
                    System.out.println("Hata - client : " + ex);

                }

            }
        });
        serverThread.start();
    }

    protected String sendEasyQuestion() {
        Random random = new Random();
        String ipucu;
        String[] operations = {"topla", "cikar"};
        String[] results = {"dogru", "yanlis"};
        String operation_result = results[random.nextInt(2)];
        String operation = operations[random.nextInt(2)];

        int number_1, number_2;
        number_1 = random.nextInt(9) + 1;
        number_2 = random.nextInt(9) + 1;
        ipucu = String.valueOf(number_1 * number_2);
        String mesaj = "";
        String dogruCevap;
        int result;
        if (operation_result.equals("dogru")) {
            dogruCevap = "True";
            if (operation.equals("topla")) {
                result = number_1 + number_2;
                mesaj = String.valueOf(number_1) + "+" + String.valueOf(number_2) + "=" + String.valueOf(result);
            } else if (operation.equals("cikar")) {
                result = number_1 - number_2;
                mesaj = String.valueOf(number_1) + "-" + String.valueOf(number_2) + "=" + String.valueOf(result);
            }
        } // sonuc yanlis gonderildiginde
        else {
            dogruCevap = "False";
            if (operation.equals("topla")) {
                result = number_1 + number_2 + random.nextInt(3) + 1;
                mesaj = String.valueOf(number_1) + "+" + String.valueOf(number_2) + "=" + String.valueOf(result);
            } else {
                result = number_1 - number_2 + random.nextInt(3) + 1;
                mesaj = String.valueOf(number_1) + "-" + String.valueOf(number_2) + "=" + String.valueOf(result);
            }
        }
        return dogruCevap + " " + ipucu + " " + mesaj;
    }

    class ListenThread extends Thread {

        private final Socket clientSocket;
        private ObjectInputStream clientInput;
        private ObjectOutputStream clientOutput;

        private ListenThread(Socket clienSocket) {
            this.clientSocket = clienSocket;
        }

        @Override
        public void run() {

            System.out.println("Baglanan client icin thread olusturuldu :" + this.getName());
            try {
                // input  : client'dan gelen mesajları okumak için
                // output : server'a bağlı olan client'a mesaj göndermek için
                clientInput = new ObjectInputStream(clientSocket.getInputStream());
                clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());

                String s[];
                String dogruYanit;
                String ipucu;
                String soru;
                if (mySelection == 1) {
                    s = sendEasyQuestion().split(" ");
                    dogruYanit = s[0];
                    ipucu = "Rakamların çarpımı " + s[1] + "'e eşittir";
                    soru = s[2];
                    //Baglanan client'a soru gonder.(ilk soru)
                    clientOutput.writeObject(soru);

                    Object mesaj;
                    //client hata sayisi
                    int counter = 0;
                    // client mesaj gönderdiği sürece mesajı al ve yeni soru gonder.
                    while ((mesaj = clientInput.readObject()) != null) {
                        System.out.println("Client mesaj gonderdi :" + mesaj);
                        // Cevap veremediği!! veya yanlış cevap verdiği her soru için ipucu göstermeli
//                        if(!mesaj.equals(dogruYanit)){
//                            clientOutput.writeObject(ipucu); 
//                            //biraz bek
//                        }
                        s = sendEasyQuestion().split(" ");
                        dogruYanit = s[0];
                        ipucu = "Rakamların çarpımı " + s[1] + "'e eşittir";
                        soru = s[2];
                        clientOutput.writeObject(soru); 
                    }
                    //Client mesaj gondermediginde!!

                } else if (mySelection == 2) {

                } else if (mySelection == 3) {

                }

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Hata - ListenThread : " + ex);
            }
        }

    }
}
