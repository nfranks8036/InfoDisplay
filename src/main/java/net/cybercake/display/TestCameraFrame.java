package net.cybercake.display;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class TestCameraFrame extends JFrame {

    private static final int IMG_WIDTH = 1920;
    private static final int IMG_HEIGHT = 1080;

    public TestCameraFrame() {
        setTitle("TestApp");
        setSize(IMG_WIDTH, IMG_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        new Thread(new TestCamera(this)).start();
    }

    public class TestCamera implements Runnable {

        private JFrame jFrame;
        private VideoCapture camera;
        private Mat frame;
        private BufferedImage image;
        private Graphics2D graphics;

        public TestCamera(JFrame jFrame) {
            this.jFrame = jFrame;

            camera = new VideoCapture(1);
            if (!camera.isOpened()) {
                System.out.println("Camera is not available");
                return;
            }

            frame = new Mat(1920, 1080, 1);
        }


        @Override
        public void run() {
            while (true) {
                if (!camera.read(frame)) continue;

                image =matToBufferedImage(frame);

                graphics = (Graphics2D) jFrame.getGraphics();

                if (graphics != null) {
                    graphics.drawImage(image, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
                }

                try {
                    Thread.sleep(10);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    break;
                }
            }
        }

        // Convert Mat to BufferedImage
        private BufferedImage matToBufferedImage(Mat mat) {
            int width = mat.width();
            int height = mat.height();
            int channels = mat.channels();
            byte[] sourcePixels = new byte[width * height * channels];
            mat.get(0, 0, sourcePixels);

            // Create a BufferedImage and set the pixel data
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

            return image;
        }
    }
}
