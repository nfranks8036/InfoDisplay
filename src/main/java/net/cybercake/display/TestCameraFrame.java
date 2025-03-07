package net.cybercake.display;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

public class TestCameraFrame extends JFrame {

    private static final int IMG_WIDTH = 1920;
    private static final int IMG_HEIGHT = 1080;

    private CameraPanel cameraPanel;

    public TestCameraFrame() {
        setTitle("TestApp");
        setSize(IMG_WIDTH, IMG_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.cameraPanel = new CameraPanel();
        add(cameraPanel);
        setVisible(true);

        new Thread(new TestCamera(this.cameraPanel)).start();
    }

    public class CameraPanel extends JPanel {
        private Image image;
        private int width, height;

        public void setResolution(int width, int height) {
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(width, height));
            revalidate();
        }

        public void updateImage(byte[] pixels, int width, int height) {
            MemoryImageSource mis = new MemoryImageSource(width, height, createPixelArray(pixels), 0, width);
            image = Toolkit.getDefaultToolkit().createImage(mis);
            repaint();
        }

        private int[] createPixelArray(byte[] pixels) {
            int[] intPixels = new int[pixels.length / 3]; // Assuming BGR 3 bytes per pixel
            for (int i = 0, j = 0; i < pixels.length; i += 3, j++) {
                int b = (pixels[i] & 0xFF);
                int g = (pixels[i + 1] & 0xFF) << 8;
                int r = (pixels[i + 2] & 0xFF) << 16;
                intPixels[j] = 0xFF000000 | r | g | b; // Add full alpha
            }
            return intPixels;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, width, height, this);
            }
        }
    }

    public class TestCamera implements Runnable {

        private CameraPanel panel;
        private VideoCapture camera;
        private Mat frame;
        private int width, height;

        public TestCamera(CameraPanel panel) {
            this.panel = panel;
            camera = new VideoCapture(1);
            if (!camera.isOpened()) {
                System.out.println("Camera is not available");
                return;
            }

            setHighestResolution(camera);

            panel.setResolution(width, height);

            frame = new Mat();
        }


        @Override
        public void run() {
            while (true) {
                if (!camera.read(frame) || frame.empty()) {
                    System.out.println("EMPTY");
                    camera.release();
                    continue;
                }

                byte[] data = matToBufferedImage(frame);
                panel.updateImage(data, frame.width(), frame.height());

                try {
                    Thread.sleep(5);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    break;
                }
            }
        }

        // Convert Mat to BufferedImage
        private byte[] matToBufferedImage(Mat mat) {
            byte[] sourcePixels = new byte[(int) (mat.total() * mat.channels())];
            mat.get(0, 0, sourcePixels);

            // Create a BufferedImage and set the pixel data
//            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//            byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//            System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

            return sourcePixels;
        }

        private void setHighestResolution(VideoCapture camera) {
            // Try setting high resolutions and fall back if not supported
            int[][] resolutions = {
                    {3840, 2160}, // 4K
                    {2560, 1440}, // 2K
                    {1920, 1080}, // 1080p
                    {1280, 720},  // 720p
                    {640, 480}    // Default low-res fallback
            };

            for (int[] res : resolutions) {
                camera.set(Videoio.CAP_PROP_FRAME_WIDTH, res[0]);
                camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, res[1]);

                // Read back actual resolution
                double actualWidth = camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
                double actualHeight = camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);
                System.out.println("Found: "+ actualWidth + "x" + actualHeight + " from " + res[0] + "x" + res[1]);

                if ((int) actualWidth == res[0] && (int) actualHeight == res[1]) {
                    width = res[0];
                    height = res[1];
                    System.out.println("Using resolution: " + width + "x" + height);
                    return;
                }
            }

            // Fallback to default
            width = IMG_WIDTH;
            height = IMG_HEIGHT;
            System.out.println("Using fallback resolution: " + width + "x" + height);
        }
    }
}
