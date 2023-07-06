import javax.swing.*;
import java.awt.*;

public class ColorEditor extends JDialog {
    private JColorChooser colorChooser;
    private JButton okButton;
    private JButton cancelButton;
    private Color selectedColor;


    public ColorEditor(JFrame parentFrame) {
        super(parentFrame, "Editar colores", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(parentFrame);

        colorChooser = new JColorChooser();
        add(colorChooser, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancelar");

        //accion del boton OK
        okButton.addActionListener(e -> {
            selectedColor = colorChooser.getColor();
            dispose();
        });

        //accion del boton cancelar
        cancelButton.addActionListener(e -> dispose());

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    public Color getSelectedColor(){
        return selectedColor;
    }
}
