package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Panels_generator {
    public static final int START_PANEL = 0, //pannello iniziale, ci scrivi qualcosa e bom
                            REQUEST_PASSWORD = 1, //richiede la password con cui cifrare i file e che software vuole installare
                            SETUP_SERVER = 3; //deve richiedere le informazioni tipo nome, ip, etc...

    private static JPanel panel;

    public static PasswordField first_psw,
                                second_psw;
    public static JTextField ip_field,
                             link_field,
                             nome_field,
                             mail_field;
    public static JComboBox<String> software_dropdown;

    public static void init() {
        panel = new JPanel();
        panel.setBackground(new Color(58, 61, 63));
        panel.setLayout(new GridBagLayout());
    }

    public static JPanel get_panel(int index) {
        //rimuove tutti i componenti dal pannello
        panel.removeAll();

        //crea il nuovo pannello
        switch (index) {
            case START_PANEL:
                setup_first_panel();
                break;

            case REQUEST_PASSWORD:
                setup_select_password();
                break;

            case SETUP_SERVER:
                setup_server_info();
                break;

            default:
                return null;
        }

        panel.setPreferredSize(new Dimension(320, 250));
        panel.updateUI();

        return panel;
    }

    private static void setup_first_panel() { //il primo pannello non richiede nulla, mostra solo del testo
        //inizializza i componenti del pannello
        JTextArea textArea = new JTextArea();
        textArea.setText(
                "Benvenuto nell'installer per Godzilla!\n\n" +
                        "eh niente non so, dovrei mettere le condizioni di utilizzo\n" +
                        "ma non è un software in vendita quindi non so manco che sono\n" +
                        "vabbe questo è uno spazio inutile allora\n\n" +
                        "la cosa bella è che posso scrivere quello che voglio, lol\n" +
                        "ad esempio, sapete qual'è il calciatore preferito del papa?\n" +
                        "vabbe questa era ovvia, Ronaldo perchè è Cristiano\n\n" +
                        "ma invece sapete perchè il sale non va nel tiramisù?\n" +
                        "beh ovviamente perchè già sale di suo\n\n" +
                        "vabbuon direi che può bastare, torno a programmare sto coso\n\n\n" +
                        "ah aspe, nella remota possibila che qualcuno legga questa roba,\n" +
                        "per segnalare bug scrivimi a camillo.zamponi@gmail.com\n\n" +
                        "scherzo se arrivi a questo codice sicuro mi conosci di persona\n" +
                        "quindi puoi anche dirmelo a voce\n\n\n\n" +
                        "programmare l'installer è proprio una cosa che mi rompe i coglioni\n" +
                        "quasi peggio di analisi 1, sto facendo questo per non studiarla\n" +
                        "dai basta torno a programmare, byeee"
        );
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GodzillaScrollPane scroll = new GodzillaScrollPane(textArea);

        textArea.setBackground(Color.lightGray);
        textArea.setForeground(Color.BLACK);
        textArea.setEditable(false);

        //aggiunge i componenti al pannello
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(scroll, c);
    }

    private static void setup_select_password() { //in questo pannello viene definita una password
        //inizializza tutti i componenti
        JTextField msg1 = new MsgTextField("imposta una password per i file");

        JTextField msg2 = new MsgTextField("password:");
        JTextField msg3 = new MsgTextField("ripeti:");

        first_psw = new PasswordField();
        second_psw = new PasswordField();

        JPanel sep1 = new SepPanel(); //separatore

        JTextField msg4 = new MsgTextField("seleziona il software da scaricare");
        JTextField msg5 = new MsgTextField("scarica:");

        software_dropdown = new DownloadDropDown();

        JPanel sep2 = new SepPanel();

        //aggiunge tutti i componenti al pannello
        GridBagConstraints c = new GridBagConstraints();

        //prima linea [ msg1 ]
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.insets = new Insets(0, 0, 10, 0);
        panel.add(msg1, c);

        //seconda linea [ msg2 first_psw show_psw_button ]
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets.right = 10;
        panel.add(msg2, c);

        c.gridx = 1;
        c.insets.right = 0;
        panel.add(first_psw, c);

        c.gridx = 2;
        c.weightx = 0;
        panel.add(first_psw.get_toggle_button(), c);

        //terza linea [ msg3 second_psw show_psw_button ]
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 2;
        c.gridx = 0;
        c.weightx = 1;
        c.insets.right = 10;
        panel.add(msg3, c);

        c.gridx = 1;
        c.insets.right = 0;
        panel.add(second_psw, c);

        c.weightx = 0;
        c.gridx = 2;
        panel.add(second_psw.get_toggle_button(), c);

        //quarta linea [ sep1 ]
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 3;
        c.weighty = 1;
        panel.add(sep1, c);

        //quinta linea [ msg4 ]
        c.gridy = 4;
        c.weighty = 0;
        c.gridwidth = 3;
        panel.add(msg4, c);

        //sesta linea [ msg5 combo_box ]
        c.gridy = 5;
        c.gridx = 0;
        c.gridwidth = 1;
        c.insets.right = 10;
        panel.add(msg5, c);

        c.gridx = 1;
        c.gridwidth = 2;
        c.insets.right = 0;
        panel.add(software_dropdown, c);

        //settima linea [ sep2 ]
        c.gridy = 6;
        c.gridx = 0;
        c.weighty = 1;
        c.gridwidth = 3;
        panel.add(sep2, c);
    }

    private static void setup_server_info() {
        //inizializza tutti i componenti
        JTextField msg1 = new MsgTextField("inserisci le informazioni del server");
        JTextField msg2 = new MsgTextField("nome:");
        JTextField msg3 = new MsgTextField("ip:");
        JTextField msg4 = new MsgTextField("link:");
        JTextField msg5 = new MsgTextField("mail:");

        nome_field = new InputTextField();
        ip_field = new InputTextField();
        link_field = new InputTextField();
        mail_field = new InputTextField();

        JPanel sep = new SepPanel();

        //aggiunge tutti i componenti al pannello
        GridBagConstraints c = new GridBagConstraints();

        //linea 1 [ msg1 ]
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 10, 0);
        c.gridwidth = 2;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(msg1, c);

        //linea 2 [ msg2 nome_field ]
        c.gridy = 1;
        c.insets.right = 10;
        c.gridwidth = 1;
        panel.add(msg2, c);

        c.gridx = 1;
        c.insets.right = 0;
        panel.add(nome_field, c);

        //linea 3 [ msg3 ip_field]
        c.gridy = 2;
        c.gridx = 0;
        c.insets.right = 10;
        panel.add(msg3, c);

        c.gridx = 1;
        c.insets.right = 0;
        panel.add(ip_field, c);

        //linea 4 [ msg4 link_field ]
        c.gridy = 3;
        c.gridx = 0;
        c.insets.right = 10;
        panel.add(msg4, c);

        c.gridx = 1;
        c.insets.right = 0;
        panel.add(link_field, c);

        //linea 5 [ msg5 mail_field ]
        c.gridy = 4;
        c.gridx = 0;
        c.insets.right = 10;
        panel.add(msg5, c);

        c.gridx = 1;
        c.insets.right = 0;
        panel.add(mail_field, c);

        //linea 6 [ sep ]
        c.gridx = 0;
        c.gridy = 5;
        c.weighty = 1;
        c.gridwidth = 2;
        panel.add(sep, c);
    }
}

class SepPanel extends JPanel {
    public SepPanel() {
        super();

        this.setFocusable(false);
        this.setBackground(new Color(58, 61, 63));
        this.setBorder(null);

        this.setPreferredSize(new Dimension(320, 250));
    }
}

class InputTextField extends JTextField {
    public InputTextField() {
        this.setBackground(new Color(108, 111, 113));
        this.setForeground(new Color(218, 221, 223));
        this.setPreferredSize(new Dimension(150, 18));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(68, 71, 73)),
                BorderFactory.createEmptyBorder(2, 2, 0, 2)
        ));
    }
}

class MsgTextField extends JTextField {
    public MsgTextField(String txt) {
        super(txt);

        this.setFocusable(false);
        this.setBackground(new Color(58, 61, 63));
        this.setForeground(new Color(218, 221, 223));
        this.setBorder(null);
    }
}

class PasswordField extends JPasswordField {
    private static final int WIDTH  = 150;
    private static final int HEIGHT = 18;

    private JButton toggle_button = null;
    private static ImageIcon[] eye_icons = new ImageIcon[] {
            new ImageIcon(PasswordField.class.getResource("/images/eye.png")),
            new ImageIcon(PasswordField.class.getResource("/images/eye_pres.png")),
            new ImageIcon(PasswordField.class.getResource("/images/eye_sel.png"))

    };
    private static ImageIcon[] no_eye_icons = new ImageIcon[] {
            new ImageIcon(PasswordField.class.getResource("/images/no_eye.png")),
            new ImageIcon(PasswordField.class.getResource("/images/no_eye_pres.png")),
            new ImageIcon(PasswordField.class.getResource("/images/no_eye_sel.png"))
    };

    private boolean clear_text = false;

    public PasswordField() {
        super();

        this.setBackground(new Color(108, 111, 113));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(68, 71, 73)),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        this.setFont(new Font("Arial", Font.BOLD, 14));
        this.setForeground(new Color(218, 221, 223));

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setMinimumSize(this.getPreferredSize());

        this.setEchoChar('*'); //nasconde il testo
        gen_toggle_button(); //genera il pulsante per togglare la visibilità del testo
    }

    public JButton get_toggle_button() {
        return toggle_button;
    }

    private void gen_toggle_button() { //genera un pulsante che premuto toggla la visibilità del testo
        toggle_button = new JButton();

        //inizializza la grafica del pulsante con le icone dell'occhio senza la barra
        toggle_button.setIcon(eye_icons[0]);
        toggle_button.setPressedIcon(eye_icons[1]);
        toggle_button.setRolloverIcon(eye_icons[2]);

        toggle_button.setBorder(null);

        //aggiunge action listener e ritorna il pulsante
        toggle_button.addActionListener(toggle_list);
    }

    private ActionListener toggle_list = e -> {
        if (clear_text) //se in questo momento il testo si vede in chiaro
        {
            setEchoChar('*'); //nasconde il testo

            //modifica le icone del pulsante
            toggle_button.setIcon(eye_icons[0]);
            toggle_button.setPressedIcon(eye_icons[1]);
            toggle_button.setRolloverIcon(eye_icons[2]);
        }
        else //se in questo momeno il testo è nascosto
        {
            setEchoChar((char) 0); //mostra il testo

            //modifica le icone del pulsante
            toggle_button.setIcon(no_eye_icons[0]);
            toggle_button.setPressedIcon(no_eye_icons[1]);
            toggle_button.setRolloverIcon(no_eye_icons[2]);
        }

        clear_text = !clear_text;
    };
}

class DownloadDropDown extends JComboBox<String> {

    public DownloadDropDown() {
        super(new String[] {"Client", "Server", "CA DNS"});

        this.setBackground(new Color(108, 111, 113));
        this.setForeground(new Color(218, 221, 223));
        this.setBorder(BorderFactory.createLineBorder(new Color(68, 71, 73)));
        this.setPreferredSize(new Dimension(100, 25));

        ComboBoxRenderer renderer = new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(100, 16));
        renderer.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        this.setRenderer(renderer);
    }

    static class ComboBoxRenderer extends JLabel implements ListCellRenderer<String> {
        boolean list_init = true;

        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            if (list_init) { //setta
                list.setSelectionBackground(new Color(108, 111, 113));
                list.setSelectionForeground(new Color(218, 221, 223));

                list_init = false;
            }

            if (isSelected) {
                setBackground(new Color(158, 161, 163));
            }
            else {
                setBackground(new Color(108, 111, 113));
            }

            setText(value);

            return this;
        }
    }
}