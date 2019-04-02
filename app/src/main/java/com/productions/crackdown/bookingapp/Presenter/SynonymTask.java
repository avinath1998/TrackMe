package com.productions.crackdown.bookingapp.Presenter;

import android.annotation.SuppressLint;
import android.os.Message;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Avinath on 4/26/2018.
 */

public class SynonymTask implements Runnable {

    private String word;
    private android.os.Handler handler;


    public SynonymTask(String word, android.os.Handler handler) {
        this.word = word;
        this.handler = handler;
    }

    /*
        Sending an arraylist of synonyms to the parent thread
        so that it can be displayed to the user
     */
    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        if (word != null) {
            final ArrayList<String> synonyms = getSynonyms(word);
            Message message = handler.obtainMessage();
            message.obj = synonyms;
            handler.sendMessage(message);
        }
    }

    /*
        Getting the synonyms
     */

    public ArrayList<String> getSynonyms(String word) {
        HttpURLConnection con = null;
        ArrayList<String> synonyms = new ArrayList<>();
        try {
            if (Thread.interrupted()) //if the thread is interupted then throw the exception
                throw new InterruptedException();
            URL url = new URL("http://thesaurus.altervista.org/thesaurus/v1?word="+word+"&language=en_US&key=smnWGgzK15NX2ENmGi2N&output=xml");
            con = (HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET"); //creating a get request
            con.connect();
            if (Thread.interrupted())
                throw new InterruptedException();

            InputStream inputStream = con.getInputStream();

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, null ); //setting the input to the xml pull parser as the input stream of the connection
            int eventType = parser.getEventType(); //getting the event type
            while ( eventType != XmlPullParser . END_DOCUMENT ) {
                String name = parser.getName (); //getting the name
                if ( eventType == XmlPullParser . START_TAG && name.equalsIgnoreCase ("synonyms" )) {
                    String [] words = parser.nextText().split("\\|");
                    for(String wordw:words){
                        synonyms.add(wordw); //adding the word into an arraylist
                    }
                }
                eventType = parser.next();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        con.disconnect(); //disconnecting
        return synonyms;
    }
}
