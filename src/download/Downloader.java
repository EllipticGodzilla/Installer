package download;

import gui.Installer_frame;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public abstract class Downloader {
    private static final String client_jar = "https://github.com/EllipticGodzilla/Godzilla_client/blob/ea54cb16bb100df2b6c3d26f860990b386c34fc2/Godzilla.jar?raw=true",
                                server_jar = "https://github.com/EllipticGodzilla/Godzilla_server/blob/07c9897b9a560c17bf6d083971247f46a150fbf5/jar/Godzilla_server.jar?raw=true",
                                ca_dns_jar = "https://github.com/EllipticGodzilla/Godzilla_CA_DNS/blob/9d65925eaa83bd4bf3b9c51994bef86b0da137e2/jar/Godzilla_CA_DNS.jar?raw=true";

    public static void download_client(String path_to_download, char[] file_psw) throws IOException, NoSuchAlgorithmException {
        //scarica il file jar da github e lo salva nella cartella path_to_download
        path_to_download += "/Client";
        new File(path_to_download).mkdir(); //crea la cartella Client dove si vuole scaricare Godzilla
        download(client_jar, path_to_download + "/Godzilla.jar");

        //crea tutte le cartelle e file attorno al jar
        new File(path_to_download + "/database").mkdir();
        new File(path_to_download + "/images").mkdir();
        new File(path_to_download + "/mod").mkdir();
        new File(path_to_download + "/database/ServerList.dat").createNewFile();
        new File(path_to_download + "/database/TerminalLog.dat").createNewFile();

        //calcola il contenuto di ogni file da inserire nel jar
        new File(path_to_download + "/../files").mkdir();

        //FileCiperKey.dat
        byte[] psw_bytes = MessageDigest.getInstance("SHA3-512").digest(to_bytes(file_psw));
        byte[] key_test = Arrays.copyOfRange(psw_bytes, 32, 64);
        new File(path_to_download + "/../files/FileCipherKey.dat").createNewFile();
        FileOutputStream fos = new FileOutputStream(path_to_download + "/../files/FileCipherKey.dat");
        fos.write(key_test);
        fos.close();

        Installer_frame.exit(); //chiude l'installer
    }

    public static void download_server(String path_to_download, char[] file_psw, String name, String ip, String link, String mail) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        //download del jar
        path_to_download += "/Server";
        new File(path_to_download).mkdir();
        download(server_jar, path_to_download + "/Godzilla_server.jar");

        //crea tutte le cartelle e file attorno al jar
        new File(path_to_download + "/database").mkdir();
        new File(path_to_download + "/images").mkdir();
        new File(path_to_download + "/mod").mkdir();
        new File(path_to_download + "/database/clients_credentials.dat").createNewFile();
        new File(path_to_download + "/database/log.dat").createNewFile();

        //crea i file da inserire dentro al jar
        path_to_download += "/../files";
        new File(path_to_download).mkdir();
        new File(path_to_download + "/server_info.dat").createNewFile();
        new File(path_to_download + "/private.key").createNewFile();
        new File(path_to_download + "/FileKey.dat").createNewFile();

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
        FileOutputStream fos = new FileOutputStream(path_to_download + "/server_info.dat");
        fos.write(serverInfo_cont);
        fos.close();

        fos = new FileOutputStream(path_to_download + "/private.key");
        fos.write(private_cont);
        fos.close();

        fos = new FileOutputStream(path_to_download + "/FileKey.dat");
        fos.write(fileKey_cont);
        fos.close();
    }

    public static void download_ca_dns(String path_to_download, char[] file_psw) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        //download del jar
        path_to_download += "/CA_DNS";
        new File(path_to_download).mkdir();
        download(ca_dns_jar, path_to_download + "/Godzilla_CA_DNS.jar");

        //crea tutte le cartelle e i file attorno al jar
        new File(path_to_download + "/database").mkdir();
        new File(path_to_download + "/database/server_info.dat").createNewFile();

        //crea i file da inserire dentro al jar
        path_to_download += "/../files";
        new File(path_to_download).mkdir();
        new File(path_to_download + "/CAPrivateKey.dat").createNewFile();
        new File(path_to_download + "/CAPublicKey.dat").createNewFile();
        new File(path_to_download + "/FileKey.dat").createNewFile();

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
        FileOutputStream fos = new FileOutputStream(path_to_download + "/CAPrivateKey.dat");
        fos.write(encoder.doFinal(k_pair.getPrivate().getEncoded()));
        fos.close();

        fos = new FileOutputStream(path_to_download + "/CAPublicKey.dat");
        fos.write(encoder.doFinal(Base64.getEncoder().encode(k_pair.getPublic().getEncoded())));
        fos.close();

        fos = new FileOutputStream(path_to_download + "/FileKey.dat");
        fos.write(Arrays.copyOfRange(psw_hash, 32, 64));
        fos.close();
    }

    private static void download(String file_url, String file_path) throws IOException {
        byte[] jar_file = new BufferedInputStream(new URL(file_url).openStream()).readAllBytes();
        FileOutputStream fos = new FileOutputStream(file_path);
        fos.write(jar_file);
        fos.close();
    }

    private static byte[] to_bytes(char[] char_array) {
        byte[] byte_array = new byte[char_array.length];
        for (int i = 0; i < byte_array.length; i++) {
            byte_array[i] = (byte) char_array[i];
        }

        return byte_array;
    }
}

