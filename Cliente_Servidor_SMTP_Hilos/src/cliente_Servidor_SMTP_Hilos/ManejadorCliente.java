package cliente_Servidor_SMTP_Hilos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ManejadorCliente implements Runnable {

    private Socket socket;

    public ManejadorCliente(Socket socket) {
        super();
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
        		new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Obtener datos del cliente
            List<String> destinatarios = new ArrayList<>();

            // Leer destinatarios hasta que se reciba la línea "Fin de la lista"
            String destinatario;
            while (!(destinatario = in.readLine()).equals("Fin de la lista")) {
                destinatarios.add(destinatario);
            }

            String asunto = in.readLine();
            String cuerpo = in.readLine();

            // Configurar propiedades y enviar el correo
            String email = "Cristhian.leiva.cruz@gmail.com"; //Tu dirección de correo
            String password = "quhbbrookxksutel"; // tu contraseña 

            Properties properties = new Properties();
            try {
            createEmail(properties, email, destinatarios,
            		asunto, cuerpo, password);
            out.println("Correo enviado correctamente");
            }catch(MessagingException e) {
            	e.printStackTrace();
            	out.println("No se puedo enviar el correo correctamente");
            }
            // Enviar confirmación al cliente
            

        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }

    public void createEmail(Properties properties, String email,
    						List<String> destinatarios, String asunto,
    						String cuerpo, String password) throws MessagingException {
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.user", email);
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.auth", "true");

        Session mSession = Session.getDefaultInstance(properties);

       
            MimeMessage mCorreo = new MimeMessage(mSession);
            mCorreo.setFrom(new InternetAddress(email));

            // Agregar múltiples destinatarios
            for (String destinatario : destinatarios) {
                mCorreo.addRecipient(Message.RecipientType.TO,
                		new InternetAddress(destinatario));
            }

            mCorreo.setSubject(asunto);
            mCorreo.setText(cuerpo, "UTF-8", "html");

            sendEmail(mSession, email, password, mCorreo);

        
    }
    public void sendEmail(Session mSession, String email, 
    		String password, MimeMessage mCorreo) throws MessagingException {
     
            Transport mTransport = mSession.getTransport("smtp");
            mTransport.connect(email, password);
            mTransport.sendMessage(mCorreo,
            		mCorreo.getRecipients(Message.RecipientType.TO));
            mTransport.close();
      
    }
}
