package main.java;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Program {
    public static void main(String[] args) {
        String fileName = args[0];
        int size = Integer.parseInt(args[1]);
        String mode = args[2];
        SwingUtilities.invokeLater(() -> processImage(fileName, size, mode));
    }

    public static void processImage(String fileName, int size, String mode) {
        // Frame and label to show the image
        JFrame frame = new JFrame("Processing...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel();
        JScrollPane scrollPane = new JScrollPane(label);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        SwingWorker<Void, BufferedImage> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    File file = new File(String.format("src/main/images/%s.jpg", fileName));
                    // Read the image
                    BufferedImage originalImage = ImageIO.read(file);

                    // Get the screen dimensions
                    int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
                    int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

                    // Set the size of the frame
                    frame.setSize(screenWidth, screenHeight);
                    frame.setVisible(true);

                    int numCores = Runtime.getRuntime().availableProcessors();
                    if (mode.equalsIgnoreCase("s")) numCores = 1;

                    // Create an ExecutorService with a number of threads equal to the number of cores
                    ExecutorService executorService = Executors.newFixedThreadPool(numCores);

                    // Calculate the height of each part
                    int partHeight = originalImage.getHeight() / numCores;

                    // Loop through each processor core
                    for (int i = 0; i < numCores; i++) {
                        final int startRow = i * partHeight;
                        final int endRow = (i == numCores - 1) ? originalImage.getHeight() : (i + 1) * partHeight;

                        executorService.submit(() -> {
                            // Loop through each pixel and retrieve its RGB values in the assigned part
                            for (int y = startRow; y < endRow; y += size) {
                                for (int x = 0; x < originalImage.getWidth(); x += size) {
                                    // Calculate the bounds of the current square
                                    int endX = Math.min(x + size, originalImage.getWidth());
                                    int endY = Math.min(y + size, originalImage.getHeight());

                                    // Calculate the average color of the current square
                                    int totalRed = 0, totalGreen = 0, totalBlue = 0, count = 0;

                                    for (int squareY = y; squareY < endY; squareY++) {
                                        for (int squareX = x; squareX < endX; squareX++) {
                                            int pixel = originalImage.getRGB(squareX, squareY);
                                            // Extract the RGB values
                                            int red = (pixel >> 16) & 0xFF;
                                            int green = (pixel >> 8) & 0xFF;
                                            int blue = pixel & 0xFF;

                                            // Accumulate the color values
                                            totalRed += red;
                                            totalGreen += green;
                                            totalBlue += blue;
                                            count++;
                                        }
                                    }

                                    // Calculate the average color
                                    int avgRed = totalRed / count;
                                    int avgGreen = totalGreen / count;
                                    int avgBlue = totalBlue / count;

                                    // Replace all pixels in the square with the average color
                                    for (int squareY = y; squareY < endY; squareY++) {
                                        for (int squareX = x; squareX < endX; squareX++) {
                                            int newPixel = (avgRed << 16) | (avgGreen << 8) | avgBlue;
                                            originalImage.setRGB(squareX, squareY, newPixel);
                                        }
                                    }

                                    // send image to the event dispatch thread for processing
                                    publish(deepCopy(originalImage)); // deepCopy to avoid references
                                }
                            }
                        });
                    }

                    // Shut down the executor service
                    executorService.shutdown();

                    try {
                        // Wait for all threads to finish processing
                        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // save the result image
                    File outputFile = new File(String.format("src/main/images/%s_result.jpg", fileName));
                    ImageIO.write(originalImage, "jpg", outputFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(java.util.List<BufferedImage> chunks) {
                // Update the UI with the latest processed image
                label.setIcon(new ImageIcon(chunks.get(chunks.size() - 1)));
            }

            private BufferedImage deepCopy(BufferedImage original) {
                BufferedImage copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
                Graphics g = copy.getGraphics();
                g.drawImage(original, 0, 0, null);
                g.dispose();
                return copy;
            }
        };

        worker.execute();
    }

    public static void processSingle(String fileName, int size) {
        // Frame and label to show the image
        JFrame frame = new JFrame("Processing...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel();
        JScrollPane scrollPane = new JScrollPane(label);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        SwingWorker<Void, BufferedImage> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    File file = new File(String.format("src/main/images/%s.jpg", fileName));
                    // Read the image
                    BufferedImage originalImage = ImageIO.read(file);

                    // Get the screen dimensions
                    int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
                    int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

                    // Set the size of the frame
                    frame.setSize(screenWidth, screenHeight);
                    frame.setVisible(true);

                    for (int y = 0; y < originalImage.getHeight(); y += size) {
                        for (int x = 0; x < originalImage.getWidth(); x += size) {
                            // Calculate the bounds of the current square
                            int endX = Math.min(x + size, originalImage.getWidth());
                            int endY = Math.min(y + size, originalImage.getHeight());

                            // Calculate the average color of the current square
                            int totalRed = 0, totalGreen = 0, totalBlue = 0, count = 0;

                            for (int squareY = y; squareY < endY; squareY++) {
                                for (int squareX = x; squareX < endX; squareX++) {
                                    int pixel = originalImage.getRGB(squareX, squareY);

                                    // Extract the RGB values
                                    int red = (pixel >> 16) & 0xFF;
                                    int green = (pixel >> 8) & 0xFF;
                                    int blue = pixel & 0xFF;

                                    // Sum up the color values
                                    totalRed += red;
                                    totalGreen += green;
                                    totalBlue += blue;
                                    count++;
                                }
                            }

                            // Find the average color (r,g,b)
                            int avgRed = totalRed / count;
                            int avgGreen = totalGreen / count;
                            int avgBlue = totalBlue / count;

                            // Replace all pixels in the square with the average color
                            for (int squareY = y; squareY < endY; squareY++) {
                                for (int squareX = x; squareX < endX; squareX++) {
                                    int newPixel = (avgRed << 16) | (avgGreen << 8) | avgBlue;
                                    originalImage.setRGB(squareX, squareY, newPixel);
                                }
                            }

                            // send image to the event dispatch thread for processing
                            publish(deepCopy(originalImage)); // deepCopy to avoid references
                        }
                    }

                    // save the result image
                    File outputFile = new File(String.format("src/main/images/%s_result.jpg", fileName));
                    ImageIO.write(originalImage, "jpg", outputFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(java.util.List<BufferedImage> chunks) {
                // Update the UI with the latest processed image
                label.setIcon(new ImageIcon(chunks.get(chunks.size() - 1)));
            }

            private BufferedImage deepCopy(BufferedImage original) {
                BufferedImage copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
                Graphics g = copy.getGraphics();
                g.drawImage(original, 0, 0, null);
                g.dispose();
                return copy;
            }
        };

        worker.execute();
    }
}
