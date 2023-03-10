package com.fca.xtreme;

import java.util.Comparator;
import java.util.Date;

public class Account {

    private String serviceName;
    private String username;
    private String password;
    private String lastChanged;
    private String previewImgUrl;

    public Account() {
        this.serviceName = "serviceName";
        this.username = "username";
        this.password = "password";
        this.lastChanged = "lastChanged";
        this.previewImgUrl = "previewImgUrl";
    }

    public Account(String serviceName, String username, String password, String lastChanged, String previewImgUrl) {
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
        this.lastChanged = lastChanged;
        this.previewImgUrl = previewImgUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        String dec = new String (decrypt(password.getBytes()));
        String dec2 = new String (decrypt(dec.getBytes()));
        return dec2;
    }
    public String getPasswordEnc() {
        String enc = new String (encrypt(password.getBytes()));
        return enc;
    }

    public void setPassword(String password) {
        String enc = new String (encrypt(password.getBytes()));
        this.password = enc;
    }

    public String getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(String lastChanged) {
        this.lastChanged = lastChanged;
    }

    public String getPreviewImgUrl() {
        return previewImgUrl;
    }

    public void setPreviewImgUrl(String previewImgUrl) {
        this.previewImgUrl = previewImgUrl;
    }

    public static Comparator<Account> ServiceNameComparator = new Comparator<Account>() {
        @Override
        public int compare(Account o1, Account o2) {
            String serviceName1 = o1.getServiceName().toLowerCase();
            String serviceName2 = o2.getServiceName().toLowerCase();

            return serviceName2.compareTo(serviceName1);
        }
    };

    public static Comparator<Account> UsernameComparator = new Comparator<Account>() {
        @Override
        public int compare(Account o1, Account o2) {
            String username1 = o1.getUsername();
            String username2 = o2.getUsername();

            return username1.compareTo(username2);
        }
    };

    public static Comparator<Account> LastChangedComparator = new Comparator<Account>() {
        @Override
        public int compare(Account o1, Account o2) {
            Date date1, date2;

            date1 = new Helpers().stringToDate(o1.getLastChanged());
            date2 = new Helpers().stringToDate(o2.getLastChanged());

            return date2.compareTo(date1);
        }
    };
    public byte[]  encrypt(byte[] frase){
        byte[] enc = new byte[frase.length];
        for(int i=0;i<frase.length;i++){
            enc[i] = (byte) ((i%2==0) ? frase[i]+1 : frase[i]-1);
        }

        return enc;
    }

    public byte[] decrypt(byte[] frase){
        byte[] enc = new byte[frase.length];
        for(int i=0;i<frase.length;i++){
            enc[i] = (byte) ((i%2==0) ? frase[i]-1 : frase[i]+1);
        }
        return enc;
    }
}
