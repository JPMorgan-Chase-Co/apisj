package org.apis.keystore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apis.config.SystemProperties;
import org.apis.crypto.ECKey;
import org.apis.db.sql.DBManager;
import org.apis.db.sql.LedgerRecord;
import org.apis.gui.manager.AppManager;
import org.apis.util.AddressUtil;
import org.apis.util.ByteUtil;
import org.apis.util.ConsoleUtil;
import org.apis.util.FastByteComparisons;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
                            if(data.alias == null || data.alias.isEmpty()) {
                                data.alias = AddressUtil.getShortAddress(data.address);
                            }
                            keyStoreDataList.add(data);
                        }
                    }catch(JsonSyntaxException ignored) {}
                }
            }
        }

        return keyStoreDataList;
    }

    private String readFile(File file) {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while(line != null) {
                sb.append(line).append(System.lineSeparator());
                line = br.readLine();
            }

            br.close();
            return sb.toString().replaceAll("Crypto","crypto");
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

    public KeyStoreData savePrivateKeyStore(byte[] privateKey, String alias, String password) {
        String keystoreStr = KeyStoreUtil.getEncryptKeyStore(privateKey, alias, password);

        return savePrivateKeyStore(keystoreStr);
    }

    public KeyStoreData savePrivateKeyStore(byte[] privateKey, String alias, char[] password) {
        return savePrivateKeyStore(privateKey, alias, String.valueOf(password));
    }

    public KeyStoreData savePrivateKeyStore(String keystoreJsonData){
        KeyStoreData data = new GsonBuilder().create().fromJson(keystoreJsonData, KeyStoreData.class);
        if(data == null) {
            return null;
        }

        // 기존 파일을 삭제한다.
        deleteKeystore( ByteUtil.hexStringToBytes(data.address));

        // 파일을 저장한다.
        PrintWriter writer;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(config.keystoreDir() + "/"
                    + KeyStoreUtil.getFileName(data)), StandardCharsets.UTF_8), true);
            writer.print(keystoreJsonData);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return data;
        }

        return data;
    }

    public KeyStoreData savePrivateKeyStore(String keystoreJsonData, String path){

        KeyStoreData data = new GsonBuilder().create().fromJson(keystoreJsonData, KeyStoreData.class);
        if(data == null) {
            return null;
        }

        // 기존 파일을 삭제한다.
        deleteKeystore(ByteUtil.hexStringToBytes(data.address), path);

        // 파일을 저장한다.
        PrintWriter writer;
        try {
            System.out.println("path : "+path);
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path + "/"
                    + KeyStoreUtil.getFileName(data)), StandardCharsets.UTF_8), true);
            writer.print(keystoreJsonData);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return data;
        }

        return data;
    }

    public KeyStoreData savePrivateKeyStore(KeyStoreData data){
        if(data == null) {
            return null;
        }

        // 기존 파일을 삭제한다.
        deleteKeystore(ByteUtil.hexStringToBytes(data.address));
        String keystoreStr =  data.toString();
        // 파일을 저장한다.
        PrintWriter writer;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(config.keystoreDir() + "/"
                    + KeyStoreUtil.getFileName(data)), StandardCharsets.UTF_8), true);
            writer.print(keystoreStr);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }

    public KeyStoreData savePrivateKeyStore(String alias, char[] password){
        byte[] privateKey = SecureRandom.getSeed(32);
        return savePrivateKeyStore(privateKey, alias, password);
    }

    public byte[] createPrivateKey(String password) {
        byte[] privateKey = SecureRandom.getSeed(32);
        savePrivateKeyStore(privateKey, "", password);

        return privateKey;
    }

    public ECKey findKeyStoreFile(byte[] address, String password) throws NotSupportCipherException, InvalidPasswordException, KeystoreVersionException, NotSupportKdfException {
        List<KeyStoreData> keyDataList = loadKeyStoreFiles();
        KeyStoreData foundKey = null;
        for(KeyStoreData key : keyDataList) {
            byte[] keyBytes = ByteUtil.hexStringToBytes(key.address);

            if(FastByteComparisons.equal(keyBytes, address)) {
                foundKey = key;
            }
        }

        if(foundKey == null) {
            return null;
        }

        byte[] privateKey = KeyStoreUtil.decryptPrivateKey(foundKey.toString(), password);
        return ECKey.fromPrivate(privateKey);
    }

    /**
     * Look for a parameter address in the keystore file list.
     *
     * @param address The address to looking for in the keystore list
     * @return TRUE if address exists
     */
    public boolean findKeyStoreFile(byte[] address) {
        List<KeyStoreData> keyDataList = loadKeyStoreFiles();
        for(KeyStoreData key : keyDataList) {
            byte[] keyBytes = ByteUtil.hexStringToBytes(key.address);

            if(FastByteComparisons.equal(keyBytes, address)) {
                return true;
            }
        }

        return false;
    }

    public boolean findKeyStoreFile(String address) {
        return findKeyStoreFile(ByteUtil.hexStringToBytes(address));
    }


    public static KeyStoreData checkKeystoreFile(File file){
        if(file == null) return null;
        if(!file.exists()) return null;

        long l = file.length();
        if(l > 10240) {
            return null;
        }

        try {
            String allText = AppManager.fileRead(file);

            if(allText.length() > 0) {
                int error = allText.indexOf("{");
                allText = allText.substring(error, allText.length());
                KeyStoreData keyStoreData = new Gson().fromJson(allText, KeyStoreData.class);
                return keyStoreData;
            } else {
                return null;
            }
        } catch (com.google.gson.JsonSyntaxException e) {
            return null;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean matchPassword(String keystoreJsonData, char[] password){
        boolean result = false;
        try {
            KeyStoreUtil.decryptPrivateKey(keystoreJsonData, String.valueOf(password));
            result = true;
        }catch (InvalidPasswordException e){
        }catch (Exception e) {
        }
        return result;
    }

    public static byte[] getPrivateKey(String keystoreJsonData, String password){
        byte[] decryptedKey = null;
        try {
            decryptedKey = KeyStoreUtil.decryptPrivateKey(keystoreJsonData, password);
        }catch (InvalidPasswordException e){
        }catch (Exception e) {
        }
        return decryptedKey;
    }

    public void deleteKeystore(byte[] address){
        deleteKeystore(address, config.keystoreDir());
    }

    public void deleteKeystore(byte[] address, String path){
        if(address == null || ByteUtil.toHexString(address).length() < 40){
            return ;
        }

        File keystore = new File(path);
        if(!keystore.exists()) {
            if(!keystore.mkdirs()) {
                System.out.println("keystore.mkdirs ! ");
                return ;
            }
        }

        File[] keyList = keystore.listFiles();
        Gson gson = new GsonBuilder().create();

        // check delete file list
        List<File> deleteFiles = new ArrayList<>();
        if(keyList != null) {
            for (File file : keyList) {
                if (file.isFile()) {
                    if(file.length() < 10240) {
                        try {
                            String fileText = readFile(file);
                            KeyStoreData data = gson.fromJson(fileText, KeyStoreData.class);

                            if (data != null) {
                                if (ByteUtil.toHexString(address).equals(data.address)) {
                                    deleteFiles.add(file);
                                }
                            }
                        } catch (JsonSyntaxException ignored) {
                        }
                    }
                }
            }
        }

        // delete file list
        for(int i=0; i<deleteFiles.size(); i++){
            File deleteFile = deleteFiles.get(i);
            deleteFile.delete();
        }
    }

    public void updateWalletAlias(String address, String alias) {
        // Ledger
        if(AppManager.getInstance().isLedger(address)) {
            LedgerRecord ledger = DBManager.getInstance().selectLedger(ByteUtil.hexStringToBytes(address));
            if(DBManager.getInstance().updateLedgers(ledger.getAddress(), ledger.getPath(), alias)) return;
        }

        List<KeyStoreData> fileList = loadKeyStoreFiles();
        KeyStoreData changeData = null;
        for(KeyStoreData data : fileList){
            if (data.address.equals(address)) {
                changeData = data;
                //파일삭제
                deleteKeystore(ByteUtil.hexStringToBytes(address));
                break;
            }
        }

        if(changeData != null){
            changeData.alias = alias;

            String keystoreJsonData = changeData.toString();

            // 파일을 저장한다.
            PrintWriter writer;
            try {
                writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(config.keystoreDir() + "/"
                        + KeyStoreUtil.getFileName(changeData)), StandardCharsets.UTF_8), true);
                writer.print(keystoreJsonData);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateWalletPassword(String address, char[] currentPassword, char[] newPassword) {
        List<KeyStoreData> fileList = loadKeyStoreFiles();
        KeyStoreData changeData = null;
        for(KeyStoreData data : fileList){
            if (data.address.equals(address)) {
                changeData = data;
                break;
            }
        }
        if(changeData != null){
            try {
                byte[] privateKey = KeyStoreUtil.decryptPrivateKey(changeData.toString(), String.valueOf(currentPassword));

                for(KeyStoreData data : fileList){
                    if (data.address.equals(address)) {
                        changeData = data;
                        //파일삭제
                        deleteKeystore(ByteUtil.hexStringToBytes(address));
                        break;
                    }
                }

                savePrivateKeyStore(privateKey, changeData.alias, newPassword);

                return true;
            } catch (KeystoreVersionException e) {
                e.printStackTrace();
            } catch (NotSupportKdfException e) {
                e.printStackTrace();
            } catch (NotSupportCipherException e) {
                e.printStackTrace();
            } catch (InvalidPasswordException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return false;
    }
}
