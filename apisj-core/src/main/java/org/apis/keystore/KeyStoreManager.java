package org.apis.keystore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apis.config.SystemProperties;
import org.apis.util.ConsoleUtil;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;

public class KeyStoreManager {
    private static KeyStoreManager manager = null;

    public static KeyStoreManager getInstance() {
        if(manager == null) {
            manager = new KeyStoreManager();
        }
        return manager;
    }

    private SystemProperties config;

    private KeyStoreManager() {
        config = SystemProperties.getDefault();
    }

    public List<KeyStoreData> loadKeyStoreFiles() {
        File keystore = new File(config.keystoreDir());
        if(!keystore.exists()) {
            if(!keystore.mkdirs()) {
                return new ArrayList<>();
            }
        }

        File[] keyList = keystore.listFiles();
        List<KeyStoreData> keyStoreDataList = new ArrayList<>();
        Gson gson = new GsonBuilder().create();

        if(keyList != null) {
            for (File file : keyList) {
                if (file.isFile()) {
                    try {
                        String fileText = readFile(file);
                        KeyStoreData data = gson.fromJson(fileText, KeyStoreData.class);

                        if (data != null) {
                            keyStoreDataList.add(data);
                        }
                    }catch(JsonSyntaxException ignored) {}
                }
            }
        }

        return keyStoreDataList;
    }

    private String readFile(File file) {
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while(line != null) {
                sb.append(line).append(System.lineSeparator());
                line = br.readLine();
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean createPrivateKeyCLI(byte[] privateKey) throws IOException {
        if(privateKey == null) {
            privateKey = SecureRandom.getSeed(32);;
        }

        char[] password1 = ConsoleUtil.readPassword(ConsoleUtil.ANSI_GREEN + "Please input your password : " + ConsoleUtil.ANSI_RESET);
        char[] password2 = ConsoleUtil.readPassword(ConsoleUtil.ANSI_GREEN + "Please confirm your password : " + ConsoleUtil.ANSI_RESET);

        if (Arrays.equals(password1, password2)) {
            String alias = ConsoleUtil.readLine(ConsoleUtil.ANSI_GREEN  + "Please input alias : " + ConsoleUtil.ANSI_RESET);

            savePrivateKeyStore(privateKey, alias, password1);
            config.setCoinbasePrivateKey(privateKey);
            return true;
        } else {
            System.out.println("Passwords do not match.");
            return false;
        }
    }

    private void savePrivateKeyStore(byte[] privateKey, String alias, char[] password) {
        String keystoreStr = KeyStoreUtil.getEncryptKeyStore(privateKey, alias, String.valueOf(password));

        KeyStoreData data = new GsonBuilder().create().fromJson(keystoreStr, KeyStoreData.class);
        if(data == null) {
            return;
        }

        // 파일을 저장한다.
        PrintWriter writer;
        try {
            writer = new PrintWriter(config.keystoreDir() + "/" + KeyStoreUtil.getFileName(data), "UTF-8");
            writer.print(keystoreStr);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}