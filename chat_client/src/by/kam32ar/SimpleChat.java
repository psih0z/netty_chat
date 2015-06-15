package by.kam32ar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.text.PlainDocument;

import by.kam32ar.filter.DocumentIntFilter;
import by.kam32ar.server.logic.Client;
import by.kam32ar.server.logic.Message;
import by.kam32ar.server.logic.Room;
import by.kam32ar.server.utils.Utilites;

public class SimpleChat implements ChatInterface {

	private ChatClient chatClient;

	private JFrame mainWindow;
	private JTextField txtfldHost;
	private JTextField txtfldPort;
	private JTextField txtfldName;
	private JTextField txtfldMsg;
	private JTextField txtfldNewRoom;

	private JTextArea txtrChat;
	private JList<String> lstOnline;
	private JList<String> lstRooms;
	
	private JLabel lblRoom;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimpleChat window = new SimpleChat();
					window.mainWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimpleChat() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainWindow = new JFrame();
		mainWindow.setTitle("SimpleChat v.1.0");
		mainWindow.setBounds(100, 100, 640, 480);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.getContentPane().setLayout(new BorderLayout(5, 5));

		JPanel pnlTop = new JPanel();
		mainWindow.getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.X_AXIS));

		JLabel lblHost = new JLabel("Хост:");
		pnlTop.add(lblHost);

		txtfldHost = new JTextField();
		txtfldHost.setText("localhost");
		pnlTop.add(txtfldHost);
		txtfldHost.setColumns(10);

		JLabel lblPort = new JLabel("Порт:");
		pnlTop.add(lblPort);

		txtfldPort = new JTextField();
		txtfldPort.setText("9092");
		pnlTop.add(txtfldPort);
		txtfldPort.setColumns(10);

		PlainDocument document = (PlainDocument) txtfldPort.getDocument();
		document.setDocumentFilter(new DocumentIntFilter());

		JLabel lblName = new JLabel("Ник:");
		pnlTop.add(lblName);

		txtfldName = new JTextField();
		pnlTop.add(txtfldName);
		txtfldName.setColumns(10);

		JButton btnConnect = new JButton("Подключить");
		pnlTop.add(btnConnect);

		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String host = txtfldHost.getText();
				int port = Integer.parseInt(txtfldPort.getText());
				String name = txtfldName.getText();
				if (host.length() <= 0) {
					JOptionPane.showMessageDialog(null,
							"Необходимо правильно указать хост");
					return;
				}
				if (port <= 0 || port >= 65535) {
					JOptionPane.showMessageDialog(null,
							"Необходимо правильно указать порт");
					return;
				}
				if (name.length() <= 0) {
					JOptionPane.showMessageDialog(null,
							"Необходимо указать имя");
					return;
				}

				chatClient = new ChatClient(host, port, name);
				chatClient.setChatUI(SimpleChat.this);
				chatClient.run();
			}
		});

		JButton btnDisconnect = new JButton("Отключить");
		pnlTop.add(btnDisconnect);
		btnDisconnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chatClient.disconnect();
			}
		});

		JPanel pnlBottom = new JPanel();
		mainWindow.getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.X_AXIS));

		txtfldMsg = new JTextField();
		pnlBottom.add(txtfldMsg);
		txtfldMsg.setColumns(10);

		JButton btnMsg = new JButton("Отправить");
		pnlBottom.add(btnMsg);
		btnMsg.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = txtfldMsg.getText();
				if (msg.length() <= 0) {
					JOptionPane.showMessageDialog(null,
							"Необходимо задать сообщение");
					return;
				}
				
				chatClient.sendMsg(null, msg);
			}
		});
		
		JButton btnPrivateMsg = new JButton("Приватное сообщение");
		pnlBottom.add(btnPrivateMsg);
		btnPrivateMsg.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String nickName = lstOnline.getSelectedValue();
				if (nickName == null) {
					JOptionPane.showMessageDialog(null,
							"Необходимо выбрать пользователя");
					return;
				}
				String msg = txtfldMsg.getText();
				if (msg.length() <= 0) {
					JOptionPane.showMessageDialog(null,
							"Необходимо задать сообщение");
					return;
				}
				
				chatClient.sendPrivateMsg(nickName, msg);
			}
		});

		JPanel pnlRight = new JPanel();
		mainWindow.getContentPane().add(pnlRight, BorderLayout.EAST);
		pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));

		JPanel pnlRooms = new JPanel();
		pnlRight.add(pnlRooms);
		pnlRooms.setLayout(new BorderLayout(0, 0));

		JLabel lblRooms = new JLabel("Комнаты:");
		pnlRooms.add(lblRooms, BorderLayout.NORTH);
		lblRooms.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblRooms.setAlignmentY(Component.TOP_ALIGNMENT);

		lstRooms = new JList<String>();
		pnlRooms.add(lstRooms, BorderLayout.CENTER);
		lstRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstRooms.setModel(new DefaultListModel<String>());
		lstRooms.setSelectedIndex(0);

		JPanel pnlCreateRoom = new JPanel();
		pnlRooms.add(pnlCreateRoom, BorderLayout.SOUTH);
		pnlCreateRoom.setLayout(new BoxLayout(pnlCreateRoom, BoxLayout.X_AXIS));

		txtfldNewRoom = new JTextField();
		pnlCreateRoom.add(txtfldNewRoom);
		txtfldNewRoom.setColumns(10);

		JButton btnCreateRoom = new JButton("Создать");
		pnlCreateRoom.add(btnCreateRoom);
		btnCreateRoom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String roomName = txtfldNewRoom.getText();
				if (roomName.length() <= 0) {
					JOptionPane.showMessageDialog(null,
							"Необходимо задать название комнаты");
					return;
				}
				
				chatClient.createRoom(new Room(0, roomName));
			}
		});
		
		JButton btnEnterRoom = new JButton("Войти");
		pnlCreateRoom.add(btnEnterRoom);
		btnEnterRoom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String roomName = lstRooms.getSelectedValue();
				if (roomName == null) {
					JOptionPane.showMessageDialog(null,
							"Необходимо выбрать комнату");
					return;
				}
				
				chatClient.enterRoom(new Room(0, roomName));
			}
		});

		JPanel pnlOnline = new JPanel();
		pnlRight.add(pnlOnline);
		pnlOnline.setLayout(new BorderLayout(0, 0));

		JLabel lblOnline = new JLabel("Онлайн:");
		pnlOnline.add(lblOnline, BorderLayout.NORTH);
		lblOnline.setAlignmentX(Component.RIGHT_ALIGNMENT);

		lstOnline = new JList<String>();
		pnlOnline.add(lstOnline, BorderLayout.CENTER);
		lstOnline.setModel(new DefaultListModel<String>());

		txtrChat = new JTextArea();
		txtrChat.setLineWrap(true);
		txtrChat.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(txtrChat);
		mainWindow.getContentPane().add(scrollPane,
				BorderLayout.CENTER);
		
		lblRoom = new JLabel("Комната:");
		scrollPane.setColumnHeaderView(lblRoom);

	}

	@Override
	public void addMessages(List<Message> messages) {
		if (messages != null) {
			for (Message message : messages) {
				addMessage(message);
			}
		}
	}

	@Override
	public void addClients(List<Client> clients) {
		DefaultListModel<String> defaultListModel = (DefaultListModel<String>) lstOnline.getModel();
		for (Client client : clients) {
			defaultListModel.addElement(client.getNick());
		}
	}

	@Override
	public void addRooms(List<Room> rooms) {
		DefaultListModel<String> defaultListModel = (DefaultListModel<String>) lstRooms.getModel();
		for (Room room : rooms) {
			defaultListModel.addElement(room.getName());
		}
	}

	@Override
	public void clearMessage() {
		txtrChat.setText("");
	}

	@Override
	public void clearClients() {
		((DefaultListModel<String>) lstOnline.getModel()).removeAllElements();
	}

	@Override
	public void clearRooms() {
		((DefaultListModel<String>) lstRooms.getModel()).removeAllElements();
	}

	@Override
	public void addMessage(Message message) {
		txtrChat.append("<" + message.getNick() + " в "
				+ Utilites.intTimeToString(message.getTime()) + ">:  "
				+ message.getMessage() + "\n");
	}

	@Override
	public void addClient(Client client) {
		DefaultListModel<String> defaultListModel = (DefaultListModel<String>) lstOnline.getModel();
		defaultListModel.addElement(client.getNick());
	}

	@Override
	public void addRoom(Room room) {
		DefaultListModel<String> defaultListModel = (DefaultListModel<String>) lstRooms.getModel();
		defaultListModel.addElement(room.getName());
	}

	@Override
	public void removeClient(Client client) {
		DefaultListModel<String> defaultListModel = (DefaultListModel<String>) lstOnline.getModel();
		defaultListModel.removeElement(client.getNick());
	}

	@Override
	public void enterRoom(Room room) {
		lblRoom.setText("Комната: " + room.getName());
	}

}
