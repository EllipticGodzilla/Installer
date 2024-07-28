import gui.Installer_frame;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.rmi.server.UID;
import java.util.Base64;

public class Installer {
    public static void main(String[] args) throws GitAPIException {
        Installer_frame.init(); //mostra la finestra per le opzioni di installazione
    }
}
