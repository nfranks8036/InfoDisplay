package net.cybercake.display.vlc;

import net.cybercake.display.utils.Log;
import net.cybercake.display.utils.YtDlpReceiver;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;

public class JVlcPlayer extends JPanel {

    private final VlcManager vlcManager;

    private boolean ytConvert;
    private String originalUrl;

    protected JVlcPlayer(VlcManager manager, String url) {
        super(new BorderLayout());

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

            EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
            this.add(mediaPlayerComponent, BorderLayout.CENTER);

            EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();

            mediaPlayer.media().play(JVlcPlayer.this.originalUrl);

        });
    }

}
