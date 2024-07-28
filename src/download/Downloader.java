package download;

import gui.Panels_generator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Paths;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public abstract class Downloader {
    private static final String client_rep = "https://github.com/EllipticGodzilla/Godzilla.git",
                                server_rep = "https://github.com/EllipticGodzilla/Godzilla_server.git",
                                ca_dns_rep = "https://github.com/EllipticGodzilla/Godzilla_CA_DNS.git";

    public static void download_client(char[] file_psw) throws IOException, NoSuchAlgorithmException, GitAPIException {
        String path_to_download = Panels_generator.dpath_field.getText();
        new File(path_to_download).mkdir(); //se non esiste crea la cartella in cui scaricare il software

        //scarica il file jar da github e lo salva nella cartella path_to_download

        clone_rep(client_rep, path_to_download + "/rep");

        //crea tutte le cartelle e file attorno al jar
        new File(path_to_download + "/database").mkdir();
        new File(path_to_download + "/images").mkdir();
        new File(path_to_download + "/mod").mkdir();
        new File(path_to_download + "/database/ServerList.dat").createNewFile();
        new File(path_to_download + "/database/TerminalLog.dat").createNewFile();
        new File(path_to_download + "/rep/out/production/Godzilla/files").mkdir();
        new File(path_to_download + "/rep/out/production/Godzilla/files/FileCipherKey.dat").createNewFile();

        //FileCiperKey.dat
        byte[] psw_bytes = MessageDigest.getInstance("SHA3-512").digest(to_bytes(file_psw));
        byte[] key_test = Arrays.copyOfRange(psw_bytes, 32, 64);

        FileOutputStream fos = new FileOutputStream(path_to_download + "/rep/out/production/Godzilla/files/FileCipherKey.dat");

        fos.write(key_test);
        fos.close();

        //genera il file jar
        gen_jar(path_to_download + "/rep/out/production/Godzilla", path_to_download + "/Godzilla.jar", "Main");
        delete_folder(new File(path_to_download + "/rep"));
    }

    public static void download_server(char[] file_psw, String name, String ip, String link, String mail) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, GitAPIException {
        String path_to_download = Panels_generator.dpath_field.getText();
        new File(path_to_download).mkdir(); //se non esiste crea la cartella in cui scaricare il software

        //download del jar
        clone_rep(server_rep, path_to_download + "/rep");

        //crea tutte le cartelle e file attorno al jar
        new File(path_to_download + "/database").mkdir();
        new File(path_to_download + "/images").mkdir();
        new File(path_to_download + "/mod").mkdir();
        new File(path_to_download + "/database/clients_credentials.dat").createNewFile();
        new File(path_to_download + "/database/log.dat").createNewFile();
        new File(path_to_download + "/rep/out/production/Godzilla_server/files").mkdir();
        new File(path_to_download + "/rep/out/production/Godzilla_server/files/server_info.dat").createNewFile();
        new File(path_to_download + "/rep/out/production/Godzilla_server/files/private.key").createNewFile();
        new File(path_to_download + "/rep/out/production/Godzilla_server/files/FileKey.dat").createNewFile();

        //crea il cipher con cui cifrare il contenuto dei file
        byte[] psw_hash = MessageDigest.getInstance("SHA3-512").digest(to_bytes(file_psw));
        SecretKey key = new SecretKeySpec(Arrays.copyOfRange(psw_hash, 0, 16), "AES");
        IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(psw_hash, 16, 32));
        Cipher encoder = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encoder.init(Cipher.ENCRYPT_MODE, key, iv);

        //genera le chiavi pubblica e privata del server
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair key_pair = kpg.generateKeyPair();

        byte[] prv_key = key_pair.getPublic().getEncoded();
        String pub_key = Base64.getEncoder().encodeToString(key_pair.getPublic().getEncoded());

        //testo in server_info.dat
        byte[] serverInfo_cont = encoder.doFinal((name + ";" + link + ";" + ip + ";" + pub_key + ";" + mail).getBytes());

        //testo in private.key
        byte[] private_cont = encoder.doFinal(prv_key);

        //testo in FIleKey.dat
        byte[] fileKey_cont = Arrays.copyOfRange(psw_hash, 32, 64);

        //scrive in tutti i file
        FileOutputStream fos = new FileOutputStream(path_to_download + "/rep/out/production/Godzilla_server/files/server_info.dat");
        fos.write(serverInfo_cont);
        fos.close();

        fos = new FileOutputStream(path_to_download + "/rep/out/production/Godzilla_server/files/private.key");
        fos.write(private_cont);
        fos.close();

        fos = new FileOutputStream(path_to_download + "/rep/out/production/Godzilla_server/files/FileKey.dat");
        fos.write(fileKey_cont);
        fos.close();

        gen_jar(path_to_download + "/rep/out/production/Godzilla_server", path_to_download + "/Godzilla_server.jar", "Godzilla_server");
        delete_folder(new File(path_to_download + "/rep"));
    }

    public static void download_ca_dns(char[] file_psw) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, GitAPIException {
        String path_to_download = Panels_generator.dpath_field.getText();
        new File(path_to_download).mkdir(); //se non esiste crea la cartella in cui scaricare il software

        //download del jar
        clone_rep(ca_dns_rep, path_to_download + "/rep");

        //crea tutte le cartelle e i file attorno al jar
        new File(path_to_download + "/database").mkdir();
        new File(path_to_download + "/database/server_info.dat").createNewFile();
        new File(path_to_download + "/rep/out/production/Godzilla_CA_DNS/files").mkdir();
        new File(path_to_download + "/rep/out/production/Godzilla_CA_DNS/files/CAPrivateKey.dat").createNewFile();
        new File(path_to_download + "/rep/out/production/Godzilla_CA_DNS/files/CAPublicKey.dat").createNewFile();
        new File(path_to_download + "/rep/out/production/Godzilla_CA_DNS/files/FileKey.dat").createNewFile();

        //genera chiave pubblica e privata della ca
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair k_pair = generator.generateKeyPair();

        //genera il cipher per cifrare le chiavi prima d'inserirle nei file
        byte[] psw_hash = MessageDigest.getInstance("SHA3-512").digest(to_bytes(file_psw));
        SecretKey key = new SecretKeySpec(Arrays.copyOfRange(psw_hash, 0, 16), "AES");
        IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(psw_hash, 16, 32));
        Cipher encoder = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encoder.init(Cipher.ENCRYPT_MODE, key, iv);

        //salva le due chiavi nei rispettivi file
        FileOutputStream fos = new FileOutputStream(path_to_download + "/rep/out/production/Godzilla_CA_DNS/files/CAPrivateKey.dat");
        fos.write(encoder.doFinal(k_pair.getPrivate().getEncoded()));
        fos.close();

        fos = new FileOutputStream(path_to_download + "/rep/out/production/Godzilla_CA_DNS/files/CAPublicKey.dat");
        fos.write(encoder.doFinal(Base64.getEncoder().encode(k_pair.getPublic().getEncoded())));
        fos.close();

        fos = new FileOutputStream(path_to_download + "/rep/out/production/Godzilla_CA_DNS/files/FileKey.dat");
        fos.write(Arrays.copyOfRange(psw_hash, 32, 64));
        fos.close();

        gen_jar(path_to_download + "/rep/out/production/Godzilla_CA_DNS/", path_to_download + "/Godzilla_CA_DNS.jar", "Godzilla_CA_DNS");
        delete_folder(new File(path_to_download + "/rep"));
    }

    private static void clone_rep(String rep_url, String target_path) throws GitAPIException {
        Git.cloneRepository()
                .setURI(rep_url)
                .setDirectory(Paths.get(target_path).toFile())
                .call();
    }

    private static void gen_jar(String src_path, String jar_path, String main_class) throws IOException {
        File jar_file = new File(jar_path);
        jar_file.createNewFile(); //crea il file jar

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, main_class);

        FileOutputStream jar_output = new FileOutputStream(jar_file);
        JarOutputStream jarOut = new JarOutputStream(jar_output, manifest);

        write_to_jar(src_path, new File(src_path), jarOut); //genera il jar

        jarOut.close();
        jar_output.close();
    }

    private static void write_to_jar(String root, File source, JarOutputStream jos) throws IOException {
        String name = source.getPath().replace("\\", "/");
        name = name.replace(root, "");

        if (source.isDirectory()) {
            if (!name.endsWith("/")) {
                name += "/";
            }
            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            jos.putNextEntry(entry);
            jos.closeEntry();
            for (File nestedFile : source.listFiles()) {
                write_to_jar(root, nestedFile, jos);
            }
        } else {
            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            jos.putNextEntry(entry);

            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {
                byte[] buffer = new byte[1024];
                while (true) {
                    int count = in.read(buffer);
                    if (count == -1)
                        break;
                    jos.write(buffer, 0, count);
                }
                jos.closeEntry();
            }
        }
    }

    private static void delete_folder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    delete_folder(file);
                }
                else {
                    file.delete();
                }
            }
        }

        folder.delete();
    }

    private static byte[] to_bytes(char[] char_array) {
        byte[] byte_array = new byte[char_array.length];
        for (int i = 0; i < byte_array.length; i++) {
            byte_array[i] = (byte) char_array[i];
        }

        return byte_array;
    }
}

