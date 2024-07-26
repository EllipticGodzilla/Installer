package gui;

import download.Downloader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Installer_frame {
    private static JFrame frame;
    public static String path = null;

    public static final int DOWNLOAD_CLIENT = 0,
                            DOWNLOAD_SERVER = 1,
                            DOWNLOAD_CA_DNS =2;

    public static int to_download;
    public static char[] psw = null;

//    public static JTextField dns_ip,
//                             ca_pKey,
//                             sName,
//                             sIp,
//                             sLink,
//                             sMail,
//                             sCertificate;
//    private static byte[] prv_key;
//    public static String pub_key;

    /*
    * il valore di progress indica a che punto dell'installazione è arrivato, i suoi valori significano:
    * 0 -> idle, deve ancora decidere che software installare (se è scelto ca_dns inizia subito a fare il download)
    *  // se ha scelto client o server
    * 1 -> deve decidere la password per cifrare i file
    *  // se ha scelto client
    * 2 -> inserisce l'indirizzo ip del dns e la chiave pubblica della ca (inizia il download client)
    *  //se ha scelto server
    * 3 -> chiede le informazioni di configurazione per il server (inizia il download server)
    * 4 -> mostra la chiave pubblica del server e aspetta il certificato
    * pre CA DNS non ci sono index perché non è necessario inserire nessuna informazione
     */
    private static int progress = 0;

    public static void init(String downloadPath) {
        path = downloadPath;

        //inizializza il frame e Panels_generator
        frame = new JFrame("Godzilla Installer");
        frame.setSize(500, 343);
        frame.setResizable(false);
        frame.getContentPane().setBackground(new Color(58, 61, 63));
        frame.getContentPane().setLayout(new GridBagLayout());

        Panels_generator.init();

        //inizializza i componenti nel frame
        JPanel top_panel = new JPanel();
        JPanel bottom_panel = new JPanel();

        JButton next = new JButton();
        JButton back = new JButton();
        ImagePanel iPanel = new ImagePanel(new ImageIcon(Installer_frame.class.getResource("/images/background.png")));

        top_panel.setLayout(new GridBagLayout());
        bottom_panel.setLayout(new GridBagLayout());

        top_panel.setBackground(Color.RED);
        top_panel.setBackground(new Color(58, 61, 63));
        bottom_panel.setBackground(new Color(58, 61, 63));

        top_panel.setPreferredSize(new Dimension(500, 260));
        bottom_panel.setPreferredSize(new Dimension(500, 40));

        next.setIcon(new ImageIcon(Installer_frame.class.getResource("/images/fwd.png")));
        next.setPressedIcon(new ImageIcon(Installer_frame.class.getResource("/images/fwd_pres.png")));
        next.setRolloverIcon(new ImageIcon(Installer_frame.class.getResource("/images/fwd_sel.png")));
        back.setIcon(new ImageIcon(Installer_frame.class.getResource("/images/back.png")));
        back.setPressedIcon(new ImageIcon(Installer_frame.class.getResource("/images/back_pres.png")));
        back.setRolloverIcon(new ImageIcon(Installer_frame.class.getResource("/images/back_sel.png")));

        next.setPreferredSize(new Dimension(60,20));
        back.setPreferredSize(new Dimension(60, 20));

        next.setBorder(null);
        back.setBorder(null);

        next.addActionListener(next_panel);
        back.addActionListener(prev_panel);

        //aggiunge tutti gli elementi al frame
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(10, 10, 0, 10);
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        top_panel.add(iPanel, c);

        c.weightx = 1;
        c.insets.left = 0;
        c.gridx = 1;
        top_panel.add(Panels_generator.get_panel(Panels_generator.START_PANEL), c);

        c = new GridBagConstraints();

        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.PAGE_START;
        bottom_panel.add(back, c);

        c.insets.left = 0;
        c.gridx = 1;
        bottom_panel.add(next, c);

        c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        frame.getContentPane().add(top_panel, c);

        c.gridy = 1;
        frame.getContentPane().add(bottom_panel, c);

        //rende il frame visibile
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static ActionListener next_panel = e -> {
        try {
            switch (progress) {
                case 0: //si è appena aperto l'installer
                    Panels_generator.get_panel(Panels_generator.REQUEST_PASSWORD);
                    progress++;
                    break;

                case 1: //ha appena inserito la password e scelto che software scaricare
                    if (validate_psw()) {
                        psw = Panels_generator.first_psw.getPassword(); //memorizza la password inserita
                        to_download = Panels_generator.software_dropdown.getSelectedIndex();

                        if (to_download == DOWNLOAD_CLIENT) { //scarica il client
                            Downloader.download_client(path, Panels_generator.first_psw.getPassword());
                        }
                        else if (to_download == DOWNLOAD_SERVER) { //richiede ulteriori informazioni per il server
                            Panels_generator.get_panel(Panels_generator.SETUP_SERVER);
                            progress++;
                        }
                        else if (to_download == DOWNLOAD_CA_DNS) { //scarica il dns
                            progress = 3; //salta uno che è il setup del server
                            Downloader.download_ca_dns(path, Panels_generator.first_psw.getPassword());
                        }
                    }
                    break;

                case 2: //ha inserito le informazioni per scaricare il server
                    if (validate_server()) { //se le informazioni date sono considerate valide
                        Downloader.download_server(
                                path,
                                Panels_generator.first_psw.getPassword(),
                                Panels_generator.nome_field.getText(),
                                Panels_generator.ip_field.getText(),
                                Panels_generator.link_field.getText(),
                                Panels_generator.mail_field.getText()
                        );
                    }
                    break;

            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    };

    private static ActionListener prev_panel = e -> {
        switch (progress) {
            case 0: //non ha ancora fatto nulla, non può tornare in dietro
                break;

            case 1: //stava richiedendo la password ed il tipo di software da scaricare
                Panels_generator.get_panel(Panels_generator.START_PANEL);
                progress = 0;
                break;

            case 2: //se stava richiedendo le informazioni per scaricare server
                Panels_generator.get_panel(Panels_generator.REQUEST_PASSWORD);
                progress = 1;
                break;
        }
    };

    private static boolean validate_psw() { //controlla che le password inserite coincidano e che siano lunghe almeno 5 caratteri
        if (Panels_generator.first_psw.getPassword().length <= 5 || Arrays.compare(Panels_generator.first_psw.getPassword(), Panels_generator.second_psw.getPassword()) != 0) { //se è stata inserita una password troppo corta, o se sono diverse
            red_border(Panels_generator.first_psw);
            red_border(Panels_generator.second_psw);

            return false;
        }
        return true;
    }

    private static boolean validate_server() {
        boolean valid = true;

        if (Panels_generator.nome_field.getText().isEmpty()) { //se non è stato inserito un nome
            red_border(Panels_generator.nome_field);
            valid = false;
        }

        if (Panels_generator.ip_field.getText().isEmpty() || !valid_ip(Panels_generator.ip_field.getText())) { //se non è stato inserito nessun ip o se quello inserito non è valido
            red_border(Panels_generator.ip_field);
            valid = false;
        }

        if (Panels_generator.link_field.getText().isEmpty()) { //non è stato inserito nessun link
            red_border(Panels_generator.link_field);
            valid = false;
        }

        if (Panels_generator.mail_field.getText().isEmpty()) { //non è stata inserita nessuna mail
            red_border(Panels_generator.mail_field);
            valid = false;
        }

        return valid;
    }

    public static boolean valid_ip(String ip) {
        Pattern ip_pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
        Matcher matcher = ip_pattern.matcher(ip); //cerca il pattern nell'ip

        return matcher.find();
    }

    private static boolean validate_ca_dns() {
        if (Panels_generator.ip_field.getText().isEmpty() || !valid_ip(Panels_generator.ip_field.getText())) { //se non è stato inserito nessun ip o se quello inserito non è valido
            red_border(Panels_generator.ip_field);
            return false;
        }
        return true;
    }

    public static void red_border(JTextField field) {
        field.setBorder(BorderFactory.createLineBorder(Color.red.darker()));
        new Thread(() -> {
            try {
                Thread.sleep(500);

                field.setBorder(BorderFactory.createLineBorder(new Color(68, 71, 73)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void exit() {
        System.exit(progress); //se si ferma a progress = 1 ed esegue il download ha scaricato il client, a 2 ha scaricato il server, a 3 CA_DNS
    }
}