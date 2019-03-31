package com.google.android.gms.samples.vision.ocrreader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

public class CardInstance {
    public static HashMap<Integer, CardInstance> cards = new HashMap<>();
    public static int currentId = 0;
    final private StringBuilder company = new StringBuilder();

    private String cardNumber, accNumber, owner, valid;
    private int id;

    public CardInstance(
            final String cardNumber, String accNumber, String owner, String valid) {
        this.cardNumber = cardNumber;
        this.accNumber = accNumber;
        this.owner = owner;
        this.id = currentId++;
        this.valid = valid;
        cards.put(this.id, this);

        if(cardNumber != null && cardNumber.length() > 5){
            Thread thread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    company.append(executePost("https://lookup.binlist.net/" + cardNumber.replace(" ", "").substring(0,6), ""));
                }
            });
            thread.start();
        }
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getAccountNumber() {
        return this.accNumber;
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getValid() {
        return valid;
    }

    public String getCompany(){
        try {
            return company.toString().substring(company.toString().indexOf("\"bank\":{\"name\":\"", 0) + 16, company.toString().indexOf(",\"url\"") - 1);
        } catch (Exception e) {
            return  "";
        }
    }

    public String getPaymentSystem() {
        if(cardNumber == null) return null;
        String t = getCardNumber().substring(0, 2);
        if (t.charAt(0) == '2')
            return "МИР";
        else if (t.charAt(0) == '3') {
            if (t.equals("34") || t.equals("37"))
                return "American Express";
        } else if (t.charAt(0) == '4')
            return "VISA";
        else if (t.charAt(0) == '5') {
            if (t.equals("50") || t.equals("56") || t.equals("57") || t.equals("58"))
                return "Maestro";
            if (t.equals("51") || t.equals("52") || t.equals("53") || t.equals("54") || t.equals("55"))
                return "MasterCard";
        } else if (t.charAt(0) == '6') {
            if (t.equals("63") || t.equals("67"))
                return "Maestro";
        }
        return null;

    }

    public static String getPaymentSystem(String cardID) {
        if(cardID == null) return null;
        String t = cardID.substring(0, 2);
        if (t.charAt(0) == '2')
            return "МИР";
        else if (t.charAt(0) == '3') {
            if (t.equals("34") || t.equals("37"))
                return "American Express";
        } else if (t.charAt(0) == '4')
            return "VISA";
        else if (t.charAt(0) == '5') {
            if (t.equals("50") || t.equals("56") || t.equals("57") || t.equals("58"))
                return "Maestro";
            if (t.equals("51") || t.equals("52") || t.equals("53") || t.equals("54") || t.equals("55"))
                return "MasterCard";
        } else if (t.charAt(0) == '6') {
            if (t.equals("63") || t.equals("67"))
                return "Maestro";
        }
        return null;

    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s:%s:%s:%d",
                this.cardNumber != null ? this.cardNumber : "",
                this.accNumber != null ? this.accNumber : "",
                this.owner != null ? this.owner : "");
    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}