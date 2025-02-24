package net.cybercake.display.vlc;

import net.cybercake.display.utils.YtDlpReceiver;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;

public class JVlcPlayer extends JPanel {

    private static final String[] vlcArgs = new String[]{
            "--no-ts-trust-pcr",
            "--ts-seek-percent",
            "--codec=h264"
    };

    private final VlcManager vlcManager;

    private boolean ytConvert;
    private String originalUrl;

    protected JVlcPlayer(VlcManager manager, String url) {
        super(new BorderLayout());

        System.setProperty("LIBGL_ALWAYS_SOFTWARE", "1");

        this.originalUrl = url;
        this.ytConvert = false;
        if (this.originalUrl.contains("yt:")) {
            this.originalUrl = this.originalUrl.substring("yt:".length());
            this.ytConvert = true;
        }

        this.vlcManager = manager;

        SwingUtilities.invokeLater(() -> {
            if (JVlcPlayer.this.ytConvert) {
                this.originalUrl = YtDlpReceiver.getRawLinkFor(this.originalUrl);
            }

            EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent(
                    new MediaPlayerFactory(
                            null,
                            vlcArgs
                    ),
                    null, null, null, null
            );
            this.add(mediaPlayerComponent, BorderLayout.CENTER);

            EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();

            mediaPlayer.media().play(JVlcPlayer.this.originalUrl);

        });
    }

}
