/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dortIslem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCP_Client {

    private Socket clientSocket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private Thread clientThread;
    private javax.swing.JLabel questionLabel;
    private javax.swing.JLabel hint_label;
    private javax.swing.JLabel answer_label;

    protected void start(String host, int port, javax.swing.JLabel jLabelQuestion, javax.swing.JLabel jLabel_answer) throws IOException {

        clientSocket = new Socket(host, port);
        // client arayüzündeki history alanı, bütün olaylar buraya yazılacak
        this.questionLabel = jLabelQuestion;
        this.answer_label = jLabel_answer;
//        this.hint_label = jLabel_hint;
//, javax.swing.JLabel jLabel_hint
//

        // input  : client'a gelen mesajları okumak için
        // output : client'dan bağlı olduğu server'a mesaj göndermek için
        clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        clientInput = new ObjectInputStream(clientSocket.getInputStream());

        // server'ı sürekli dinlemek için Thread oluştur
        clientThread = new ListenThread();
        clientThread.start();
    }

    protected void writeQuestion(String question) {
        // client arayüzündeki label'a soruyu yaz        
        questionLabel.setText(question);
    }

    protected void writeHint(String hint) {
        // client arayüzündeki hint label'a soruyu yaz 
        hint_label.setText(hint);
    }

    protected void writeAnswer(String answer) {
        // client arayüzünde sorunun cevabını yaz. 
        answer_label.setText(answer);
    }

    protected void sendMessage(String mesaj) throws IOException {
        // gelen mesajı server'a gönder
        clientOutput.writeObject(mesaj);
    }

    class ListenThread extends Thread {

        // server'dan gelen mesajları dinle
        @Override
        public void run() {
            try {
                Object mesaj;
                // server mesaj gönderdiği sürece gelen mesajı al
                while ((mesaj = clientInput.readObject()) != null) {
                    // serverdan gelen mesajı arayüze yaz                    
                    System.out.println("Gonderilen mesaj : " + mesaj);
                    System.out.println("");
                    writeQuestion(String.valueOf(mesaj));
//                    answer_label.setText("yazdi"); 
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error - ListenThread : " + ex);
            }
        }

    }
}
