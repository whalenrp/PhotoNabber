import com.restfb.*;
import com.restfb.types.Photo;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private static Version VERSION = Version.VERSION_2_8;
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
                            FacebookClient.AccessToken accessToken = FacebookClient.AccessToken.fromQueryString(
                                    location.substring(tokenKeyIndex));
                            new Thread(() -> downloadPicturesForUser(accessToken)).start();
                            //frame.setVisible(false);
                        } else {
                            // TODO: Handle the case where we failed to log in
                        }
                    }
                }
            });
            webView.getEngine().load(
                "https://www.facebook.com/v2.8/dialog/oauth" +
                        "?client_id=" + APP_ID +
                        "&display=popup" +
                        "&response_type=token" +
                        "&scope=user_photos" +
                        "&redirect_uri=https://www.facebook.com/connect/login_success.html");
        });

        //Display the window.
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private static void downloadPicturesForUser(FacebookClient.AccessToken accessToken) {
        List<Photo> taggedPhotos = getPhotoUrls(accessToken, "tagged");
        List<Photo> uploadedPhotos = getPhotoUrls(accessToken, "uploaded");
    }

    private static List<Photo> getPhotoUrls(FacebookClient.AccessToken accessToken, String type) {
        System.out.println("access_token: " + accessToken.getAccessToken());
        DefaultFacebookClient client = new DefaultFacebookClient(accessToken.getAccessToken(), VERSION);
        Connection<Photo> photosConnection = client.fetchConnection(
            "me/photos", Photo.class, Parameter.with("fields", "images,created_time"), Parameter.with("type", type));
        int i = 0;
        List<Photo> results = new LinkedList<>();
        for (List<Photo> page : photosConnection) {
            for(Photo photo : page) {
                ++i;
                results.add(photo);
            }
            System.out.println("Pulled " + i + " photos with type " + type);
        }
        return results;
    }

    private static Photo.Image getLargestImage(Photo photo) {
        Photo.Image maxPhoto = null;
        int maxSeenPixels = -1;
        for (Photo.Image image : photo.getImages()) {
            int imagePixels = image.getHeight() * image.getWidth();
            if (imagePixels > maxSeenPixels) {
                maxPhoto = image;
            }
        }
        return maxPhoto;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}
