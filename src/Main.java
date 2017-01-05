import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    private static String APP_ID = "588358218031324";
    private static String redirectUrl = "https://www.facebook.com/connect/login_success.html";
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);

        // Create a webview
        JFXPanel panel = new JFXPanel();
        frame.add(panel);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            panel.setScene(new Scene(webView));
            WebEngine engine = webView.getEngine();
            engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    String location = engine.getLocation();
                    int hashIndex = engine.getLocation().indexOf('#');
                    if (hashIndex != -1 && location.substring(0, location.indexOf('#')).equalsIgnoreCase(redirectUrl)) {
                        String tokenKey = "access_token";
                        int tokenKeyIndex = location.indexOf(tokenKey);
                        if (tokenKeyIndex != -1) {
                            int valueStartIndex = tokenKeyIndex + tokenKey.length() + 1; // include the '=' sign here
                            int endIndex = location.indexOf('&', valueStartIndex);
                            if (endIndex == -1) endIndex = location.length();
                            System.out.println("Location: " + location);
                            String accessToken = location.substring(valueStartIndex, endIndex);
                            new Thread(() -> downloadPicturesForUser(accessToken)).start();
                            frame.setVisible(false);
                        } else {
                            // TODO: Handle the case where we failed to log in
                        }
                    }
                }
            });
            webView.getEngine().load(
                "https://www.facebook.com/v2.8/dialog/oauth?client_id=588358218031324&display=popup&response_type=token&redirect_uri=https://www.facebook.com/connect/login_success.html");
        });

        //Display the window.
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);

        Properties prop = new Properties();
        InputStream is;
        String appSecret = null;
        try {
            is = new FileInputStream("secrets.properties");
            prop.load(is);
            is.close();
            appSecret = prop.getProperty("fb_app_secret");
        } catch (IOException e) {
            e.printStackTrace();
        }
        FacebookClient.AccessToken accessToken =
                new DefaultFacebookClient(Version.VERSION_2_8).obtainAppAccessToken(APP_ID, appSecret);

    }

    private static void downloadPicturesForUser(String accessToken) {
        System.out.println("DEBUG: " + accessToken);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
