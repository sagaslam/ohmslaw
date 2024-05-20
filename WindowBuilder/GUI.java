// * This code was generated with love by Window Builder VS Code extension. * //

import javax.swing.*;
import java.awt.*;


public class GUI {
  public static void main(String[] args) {
    JFrame frame = new JFrame("Window builder!");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 400);
    
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();

    // Fill the layout with placeholders
    for (int row = 0; row < 20; row++) {
        for (int col = 0; col < 15; col++) {
            constraints.gridx = col;
            constraints.gridy = row;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.fill = GridBagConstraints.BOTH;

            Component filler = Box.createRigidArea(new Dimension(0, 0));
            panel.add(filler, constraints);
        }
    }

    constraints.weightx = 0;
    constraints.weighty = 0;
    constraints.fill = GridBagConstraints.NONE;
    constraints.anchor = GridBagConstraints.NORTHWEST;

    
    JLabel label_0 = new JLabel("Label");
    constraints.gridx = -7;
    constraints.gridy = 6;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    panel.add(label_0, constraints);
    
    JLabel label_1 = new JLabel("Label");
    constraints.gridx = -7;
    constraints.gridy = 7;
    constraints.gridwidth = 2;
    constraints.gridheight = 2;
    panel.add(label_1, constraints);
    
    JTextField text_field_0 = new JTextField(15);
    text_field_0.setPreferredSize(new Dimension(133, 23));
    constraints.gridx = -5;
    constraints.gridy = 6;
    constraints.gridwidth = 5;
    constraints.gridheight = 5;
    panel.add(text_field_0, constraints);
    
    JTextField text_field_1 = new JTextField(15);
    text_field_1.setPreferredSize(new Dimension(133, 23));
    constraints.gridx = -5;
    constraints.gridy = 6;
    constraints.gridwidth = 5;
    constraints.gridheight = 5;
    panel.add(text_field_1, constraints);
    
    JTextField text_field_2 = new JTextField(15);
    text_field_2.setPreferredSize(new Dimension(133, 23));
    constraints.gridx = -6;
    constraints.gridy = 6;
    constraints.gridwidth = 5;
    constraints.gridheight = 5;
    panel.add(text_field_2, constraints);
    
    frame.add(panel);
    frame.setVisible(true);
  }
}
    