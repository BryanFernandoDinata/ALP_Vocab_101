import javax.swing.*;

import org.json.simple.parser.ParseException;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.*;

public class Vocab101App 
{
    public static String wordsToDiplay ="";
    public static String definitionOfTheWord = "";
    static boolean isPlayingTypingGame = false;
    static int round = 1;
    static JLabel backgroundLabel;
    public static void main(String[] args) 
    {
        // Create the main frame
        MainMenuTab();
    }
    private static void MainMenuTab()
    {
        JFrame frame = new JFrame("VOCAB 101");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(1920, 1080);
        frame.setLayout(new BorderLayout());

        // Create a panel for center alignment
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Add the "VOCAB 101" label
        JLabel titleLabel = new JLabel("VOCAB 101");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add spacing between elements
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing

        // Add the "Play" button
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Serif", Font.PLAIN, 24));
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.setBorder(null); // Remove border
        playButton.setContentAreaFilled(false); // Make background transparent

        // Add mouse listener to change text color on hover
        playButton.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                playButton.setForeground(Color.red); // Change text color to blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) 
            {
                playButton.setForeground(Color.BLACK); // Reset text color when not hovering
            }
        });

        // Add action listener to the button
        playButton.addActionListener(e -> 
        {
            // Function to execute when the button is clicked
            //JOptionPane.showMessageDialog(frame, "Play button clicked!");
            isPlayingTypingGame = true;
            Logic.GetAvailableVocab();
            Logic.PrintRandomizedVocab();
            TypingGameTab();
            frame.dispose();
        });
        
        panel.add(playButton);
        panel.add(Box.createVerticalGlue());

        // Add the panel to the frame
        frame.add(panel, BorderLayout.CENTER);

        // Center the frame on screen and make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    // Function to be called when Enter is pressed
    public static void onEnterPressed() 
    {
        round++;   
        Logic.PrintRandomizedVocab();
    }
    // Method to open a new tab (new JFrame)
    private static void TypingGameTab() 
    {
        JFrame frame = new JFrame("VOCAB 101");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(1920, 1080); // Set the frame size
        frame.setLayout(new BorderLayout());
    
        // Create a JLayeredPane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1920, 1080));
        layeredPane.setLayout(new BoxLayout(layeredPane, BoxLayout.Y_AXIS)); // Use null layout for absolute positioning
    
        // Add the "VOCAB 101" title label
        layeredPane.add(Box.createRigidArea(new Dimension(0, -525)));
        JLabel titleLabel = new JLabel("Your Word : ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Add spacing between elements
        layeredPane.add(Box.createVerticalGlue());
        layeredPane.add(titleLabel);
        layeredPane.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing

        // Add the background label
        layeredPane.add(Box.createRigidArea(new Dimension(0, 0)));
        backgroundLabel = new JLabel(wordsToDiplay, SwingConstants.CENTER);
        backgroundLabel.setFont(new Font("Serif", Font.BOLD, 34));
        backgroundLabel.setForeground(new Color(200, 200, 200)); // Light gray
        backgroundLabel.setHorizontalAlignment(JTextField.LEFT); // Center the text inside the field
        backgroundLabel.setMaximumSize(new Dimension(250, 40)); // Set a preferred size
        backgroundLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the text field within the panel
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
    
        // Add the text field
        layeredPane.add(Box.createRigidArea(new Dimension(0, 500)), JLayeredPane.PALETTE_LAYER);
        JTextField textField = new JTextField("");
        textField.setFont(new Font("Serif", Font.BOLD, 34));
        textField.setHorizontalAlignment(JTextField.LEFT); // Center the text inside the field
        textField.setMaximumSize(new Dimension(250, 40)); // Set a preferred size
        textField.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the text field within the panel
        textField.setOpaque(false);
        textField.setBorder(null); // Removes the border

        textField.addKeyListener(new KeyAdapter() 
        {
            @Override
            public void keyPressed(KeyEvent e) 
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) 
                {
                    // Enter key was pressed
                    String inputText = textField.getText();

                    if(inputText.equalsIgnoreCase(backgroundLabel.getText()))
                    {
                        try {
                            Logic.PrintVocabDescription();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        System.out.println(definitionOfTheWord);
                        JTextArea textArea = new JTextArea("Definition of " + wordsToDiplay + ": " + definitionOfTheWord);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        textArea.setEditable(false);  // Prevent user edits
                        textArea.setOpaque(false);   // Make it look like plain text
                        textArea.setColumns(30);     // Set the width (in columns)
                        textArea.setRows(10);
                        //JOptionPane.showMessageDialog(null, textArea, "Limited Width Dialog", JOptionPane.INFORMATION_MESSAGE);
                        JOptionPane.showMessageDialog(frame, textArea, "Definition", JOptionPane.INFORMATION_MESSAGE);
                        
                        onEnterPressed();
                        backgroundLabel.setText(wordsToDiplay);
                        textField.setText("");
                        System.out.println(wordsToDiplay);
                    }else
                    {
                        textField.setText("");
                    }
                    //System.out.println("Enter pressed. Input text: " + inputText);
                    // You can now use the inputText for whatever functionality you need
                }
            }
        });

        // Add the text field to the panel
        layeredPane.add(textField, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(Box.createVerticalGlue());
    
        // Add the layered pane to the frame
        frame.add(layeredPane, BorderLayout.CENTER);
    
        // Center the frame on screen and make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //System.out.println(backgroundLabel.getText());        
    }   
}
