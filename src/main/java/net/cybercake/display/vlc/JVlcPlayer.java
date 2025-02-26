package net.cybercake.display.vlc;

import net.cybercake.display.utils.Log;
import net.cybercake.display.utils.YtDlpReceiver;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface;

import javax.swing.*;
import java.awt.*;

public class JVlcPlayer extends JPanel {

    private static final String[] vlcArgs = new String[]{

    };

    private final VlcManager vlcManager;

    private boolean ytConvert;
    private String originalUrl;

    @SuppressWarnings("CallToPrintStackTrace")
    protected JVlcPlayer(VlcManager manager, String url) {
        super(new BorderLayout());

        this.originalUrl = url;
        this.ytConvert = false;
        if (this.originalUrl.contains("yt:")) {
            this.originalUrl = this.originalUrl.substring("yt:".length());
            this.ytConvert = true;
        }

        this.vlcManager = manager;

        if (JVlcPlayer.this.ytConvert) {
            this.originalUrl = YtDlpReceiver.getRawLinkFor(this.originalUrl);
        }

        EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();

        this.add(mediaPlayerComponent, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            try {
                if (!mediaPlayer.media().play(JVlcPlayer.this.originalUrl)) {
                    throw new IllegalArgumentException("Media failed to play, no reason specified.");
                }
            } catch (Exception exception) {
                new IllegalStateException("VLC failed to play video from url: " + JVlcPlayer.this.originalUrl, exception).printStackTrace();
            }
        });
    }

}
