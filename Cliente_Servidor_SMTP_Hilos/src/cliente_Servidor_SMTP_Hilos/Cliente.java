package cliente_Servidor_SMTP_Hilos;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class Cliente extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField_Para;
	private JTextField textField_Asunto;
	private JTextArea textArea_Cuerpo;
	private JButton btnEnviar;
	private final int PUERTO_SMTP = 587;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cliente frame = new Cliente();
					frame.setVisible(true);
					frame.iniciarListeners();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Cliente() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 744, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		panel_1.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("Para:");
		panel_2.add(lblNewLabel, BorderLayout.WEST);

		textField_Para = new JTextField();
		panel_2.add(textField_Para, BorderLayout.SOUTH);
		textField_Para.setColumns(10);

		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel_1 = new JLabel("Asunto:");
		panel_3.add(lblNewLabel_1, BorderLayout.WEST);

		textField_Asunto = new JTextField();
		panel_3.add(textField_Asunto, BorderLayout.SOUTH);
		textField_Asunto.setColumns(10);

		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel_2 = new JLabel("Cuerpo:");
		panel.add(lblNewLabel_2, BorderLayout.NORTH);

		textArea_Cuerpo = new JTextArea();
		panel.add(new JScrollPane(textArea_Cuerpo), BorderLayout.CENTER);

		// Botón de enviar
		btnEnviar = new JButton("Enviar");

		panel.add(btnEnviar, BorderLayout.SOUTH);
	}

	private void establecerConexion(List<String> destinatarios) {
	    final String HOST = "127.0.0.1";
	    
	    try (Socket sc = new Socket(HOST, PUERTO_SMTP);
	         BufferedReader in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
	         PrintWriter out = new PrintWriter(sc.getOutputStream(), true)) {

	        // Enviar datos del correo al servidor SMTP
	        for (String destinatario : destinatarios) {
	            out.println(destinatario);
	        }

	        out.println("Fin de la lista");
	        
	        out.println(textField_Asunto.getText());
	        out.println(textArea_Cuerpo.getText());

	        // Leer la respuesta del servidor (puedes personalizar esto según tus necesidades)
	        String respuestaServidor;
	        while ((respuestaServidor = in.readLine()) != null && !respuestaServidor.equals("Fin de la lista")) {
	            System.out.println("Respuesta del servidor: " + respuestaServidor);
	        }

	    } catch (IOException e) {
	        System.out.println(e.getMessage());
	    }
	}

	public void iniciarListeners() {
	    btnEnviar.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            String correosTexto = textField_Para.getText();

	            // Separar direcciones de correo electrónico usando punto y coma como delimitador
	            List<String> destinatarios = Arrays.asList(correosTexto.split(";"));

	            // Validar todos los correos electrónicos ingresados
	            if (destinatarios.stream().allMatch(Cliente.this::validarCorreoElectronico)) {
	                establecerConexion(destinatarios);
	            } else {
	                // Mostrar mensaje de error si algún correo electrónico no es válido
	                JOptionPane.showMessageDialog(null, "Uno o más correos electrónicos no son válidos", "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    });
	}

	
	private boolean validarCorreoElectronico(String correo) {
	    // Expresión regular para validar un correo electrónico
	    String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";

	    // Compilar la expresión regular
	    Pattern pattern = Pattern.compile(regex);

	    // Crear el objeto Matcher
	    Matcher matcher = pattern.matcher(correo);

	    // Verificar si el correo electrónico coincide con la expresión regular
	    return matcher.matches();
	}
}
