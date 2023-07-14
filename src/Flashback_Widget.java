
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;


public class Flashback_Widget extends JFrame {
    private final Timer timer;
    private JPanel titlePanel;
    private final JPanel imagePanel;
    private File[] imageFiles;
    private int currentIndex;

    private int initialX;
    private int initialY;
    private final ImageIcon trayIconImage = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icono.png")));
    private JPopupMenu colormenu;
    private ColorEditor colorEditor;

    public Flashback_Widget() {
        // Configuración de la ventana principal
        super("Marissa Memories");
        initialX = 0;
        initialY = 0;

        setSize(200, 230);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, this.getBounds().width, this.getBounds().height, 30, 19));
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(trayIconImage.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel de título
        titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(200, 30));
        titlePanel.setBackground(new Color(187, 109, 210));
        JLabel titleLabel = new JLabel("Flashback Memories");
        titleLabel.setFont(new Font("Papyrus", Font.PLAIN, 15));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);


        // Panel de imágenes
        imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(200, 200));
        imagePanel.setBackground(new Color(208, 164, 222));

        // Botón Cerrar
        JLabel closeButton = new JLabel("[X]");
        closeButton.setForeground(Color.white);
        closeButton.setPreferredSize(new Dimension(20, 30));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        // Botón Añadir
        JLabel addButton = new JLabel("  [+]");
        addButton.setForeground(Color.white);
        addButton.setPreferredSize(new Dimension(30, 30));
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    File file = new File("C:\\Imagenes");
                    desktop.browse(file.toURI());
                    // Volver a cargar las imágenes del directorio
                    loadImagesFromFolder("C:\\Imagenes");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        titlePanel.add(addButton);
        titlePanel.add(closeButton);

        // Manejador de eventos del ratón para el arrastre de la ventana
        MouseAdapter mouseDragAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialX = e.getX();
                initialY = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    setLocation(screenSize.width - getWidth(), 0);
                }
            }

            public void mouseDragged(MouseEvent e) {
                int currentX = getLocation().x + e.getX() - initialX;
                int currentY = getLocation().y + e.getY() - initialY;
                setLocation(currentX, currentY);
            }
        };
        titlePanel.addMouseListener(mouseDragAdapter);
        titlePanel.addMouseMotionListener(mouseDragAdapter);

        //Crear menu contextual para cambiar color
        colormenu = new JPopupMenu();
        JMenuItem customizeColorsItem = new JMenuItem("Personalizar colores");
        customizeColorsItem.addActionListener(e -> showColorEditor());
        colormenu.add(customizeColorsItem);

        //Añadir MouseListener al titulo para poder mostrar el menu contextual
        titlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    colormenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        // Configuración del contenedor principal
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(titlePanel, BorderLayout.NORTH);
        contentPane.add(imagePanel, BorderLayout.CENTER);
        //contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Manejador de eventos del ratón
        MouseAdapter mouseAdapter = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    showNextImage();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    showPreviousImage();
                }
            }
        };
        imagePanel.addMouseListener(mouseAdapter);

        // Carga de las imágenes desde la carpeta
        loadInitialImages();
        loadImagesFromFolder("C:\\Imagenes");

        // Mostrar la primera imagen
        if (imageFiles.length > 0) {
            currentIndex = 0;
            displayImage(imageFiles[currentIndex]);
        }

        // Mostrar la ventana en la esquina superior derecha de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width - getWidth(), 0);


        timer = new Timer(3000, e -> showNextImage());
        timer.start();

    }

    private void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("La bandeja de notificaciones no es compatible");
            JOptionPane.showMessageDialog(null, "La bandeja de notificaciones no es compatible");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();

        // Acción para mostrar/ocultar la ventana principal al hacer doble clic en el icono de la bandeja de notificaciones
        ActionListener showHideAction = e -> {
            if (isVisible()) {
                setVisible(false);
            } else {
                setVisible(true);
                setExtendedState(JFrame.NORMAL); // Mostrar la ventana si estaba minimizada
            }
        };

        // Acción para salir del programa desde el menú contextual del icono
        ActionListener exitAction = e -> System.exit(0);

        // Crear el menú contextual del icono
        PopupMenu popupMenu = new PopupMenu();
        MenuItem showHideItem = new MenuItem("Mostrar/ocultar");
        MenuItem exitItem = new MenuItem("Salir");
        showHideItem.addActionListener(showHideAction);
        exitItem.addActionListener(exitAction);
        popupMenu.add(showHideItem);
        popupMenu.add(exitItem);

        // Crear el icono de la bandeja de notificaciones
        TrayIcon trayIcon = new TrayIcon(trayIconImage.getImage(), "Flashback Memories", popupMenu);
        trayIcon.setImageAutoSize(true);

        // Añadir el icono a la bandeja de notificaciones
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Error al agregar el icono a la bandeja de notificaciones");
            JOptionPane.showMessageDialog(null, "Error al agregar el icono a la bandeja de notificaciones");
            e.printStackTrace();
        }

        // Ocultar la ventana principal al iniciar el programa
        setVisible(true);
    }


    private void loadInitialImages() {
        File folder = new File("C:\\Imagenes");
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("La carpeta de imágenes no existe");
            JOptionPane.showMessageDialog(null, "La carpeta 'Imagenes' no se encontro en el disco local C: , se creara la carpeta por favor reinicia el programa");
            return;
        }
        File[] imageFiles = folder.listFiles(file -> {
            String name = file.getName().toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
        });
        if (imageFiles.length == 0) {
            System.out.println("No se encontraron imágenes en la carpeta");
            JOptionPane.showMessageDialog(null, "No se encontraron imagenes en la carpeta del programa\t por favor añade imagenes y reincia el programa");
            return;
        }
        // Actualizar la lista de imágenes y mostrar la primera
        this.imageFiles = imageFiles;
        currentIndex = 0;
        displayImage(imageFiles[currentIndex]);
    }


    private void loadImagesFromFolder(String folderPath) {
        File folder = new File(folderPath);

        // Verificar si la carpeta existe, de lo contrario, crearla
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                JOptionPane.showMessageDialog(null, "No se pudo crear la carpeta");
                System.out.println("No se pudo crear la carpeta");
                JOptionPane.showMessageDialog(null, "No se pudo crear la carpeta, trata de ejecutar con permisos de administrador");
                return;
            }
        }

        imageFiles = folder.listFiles();
    }

    private void displayImage(File file) {
        ImageIcon imageIcon = new ImageIcon(file.getPath());
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        imagePanel.removeAll();
        imagePanel.add(new JLabel(imageIcon));
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void showNextImage() {
        System.out.println("ImageFiles size: " + (imageFiles.length - 1));
        if (currentIndex < imageFiles.length - 1) {
            currentIndex++;
            displayImage(imageFiles[currentIndex]);
            System.out.printf("Current Index: %d\n", currentIndex);
        }
        if (currentIndex == (imageFiles.length - 1)) {
            currentIndex = 0;
        }

    }


    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            displayImage(imageFiles[currentIndex]);
        }
    }

    private void stopTimer() {
        timer.stop();
    }

    private void showColorEditor() {
        if (colorEditor == null) colorEditor = new ColorEditor(this);

        colorEditor.setVisible(true);
        Color selectedColor = colorEditor.getSelectedColor();

        if (selectedColor != null) {
            titlePanel.setBackground(selectedColor);
            imagePanel.setBackground(selectedColor);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--developer")) {
            System.out.println("\nPrograma desarrollado por: OrionBlack");
            System.out.println("Github: https://github.com/Orion-Black");
            System.out.println("By OrionBlack @2023\n");
        } else if (args.length > 0 && args[0].equals("--version")) {
            System.out.println("\nFlashback Widget v1.0\n");
        } else {
            SwingUtilities.invokeLater(() -> {
                Flashback_Widget frame = new Flashback_Widget();
                frame.setVisible(true);
                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        frame.stopTimer();
                        System.exit(0);
                    }
                });

                // Configurar el icono del sistema
                frame.setupSystemTray();
            });
        }
    }

}

