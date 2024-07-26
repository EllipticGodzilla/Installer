import gui.Installer_frame;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.Base64;

public class Installer {
    public static void main(String[] args) {
        System.out.println("downloading file at the folder " + args[0]);
        Installer_frame.init(args[0]); //mostra la finestra per le opzioni di installazione
    }
}
