import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class Test extends JFrame{

	MojKomponent komponent;
	Siec siec;
    Warstwa warstwa;
    Neuron neurony;
	boolean[][] grid = new boolean[8][8];
    String selectedLetter;
    int count = 500;
    private ButtonGroup letterGroup = new ButtonGroup();
    private JRadioButton oButton;
    private JRadioButton dButton;
    private JRadioButton mButton;
	private ArrayList<UczacaWartosc> uczaceWartosci = new ArrayList<>();
    JLabel whiteLabel1 = new JLabel("True");
    JLabel whiteLabel2 = new JLabel("False");

	public class MojKomponent extends JComponent{
		private static final int CELL_SIZE = 40;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (grid[x][y]) {
                        g.setColor(Color.BLACK);
                        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
		}
		
	}
	public Test(String string) {
		super(string);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension d = kit.getScreenSize();
        int width = 12 * MojKomponent.CELL_SIZE;
        int height = 12 * MojKomponent.CELL_SIZE;
        setBounds(d.width / 4, d.height / 4, width, height);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		add(mainPanel);
		mainPanel.add(createControlPanel());
		mainPanel.add(komponent = new MojKomponent());
        setMouseListener();
        setVisible(true);
		/*
		 //1 warstwa 1 neuron
		int []tab=new int [1];
		tab[0]=1;
		siec=new Siec(2,1,tab);
		*/
		
		 //3 warstwy
		
		int [] tab=new int [3];
		tab[0]=64; tab[1]=8; tab[2]=3;
		siec=new Siec(2,3,tab);
		
		/*
		 //2 warstwy
		int [] tab=new int [2];
		tab[0]=10; tab[1]=1;
		siec=new Siec(2,2,tab);
		*/
		setVisible(true);
	}

	private JPanel createControlPanel() {
        JPanel controlJPanel = new JPanel();
        controlJPanel.setLayout(new BoxLayout(controlJPanel, BoxLayout.PAGE_AXIS));
        controlJPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
    
        ButtonGroup letterGroup = new ButtonGroup();
        JRadioButton oButton = new JRadioButton("O");
        oButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedLetter = "100";
            }
        });
    
        JRadioButton dButton = new JRadioButton("D");
        dButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedLetter = "010";
            }
        });
    
        JRadioButton mButton = new JRadioButton("M");
        mButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedLetter = "001";
            }
        });
    
        letterGroup.add(oButton);
        letterGroup.add(dButton);
        letterGroup.add(mButton);
    
        controlJPanel.add(oButton);
        controlJPanel.add(dButton);
        controlJPanel.add(mButton);
    
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearGrid();
            }
        });
    
                                                                    //TRENOWANIE

        JButton trainButton = new JButton("Ucz");
        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                train(count);
            }
        });

                                                                    //TESTOWANIE
    
        JButton testButton = new JButton("Test");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertGridToBinaryString();
                String binaryString = convertGridToBinaryString();
                boolean outputIsMatching = false;

                String[] forbiddenBinaryStrings = {
                    // dla litery "D"
                    "0001100000100100010000101000000101000001001000100001010000001000",
                    "0001000000101000010001001000001010000001010000100010010000011000",
                    "0000100000010100001000100100000110000001010000100010010000011000",
                    "0001100000100100010000101000000110000010010001000010100000010000",
                    //dla litery "M"
                    "1000000110000001100000011000000110011001101001011100001110000001",
                    "0001000000100000010000001000000011110001000100100001010000011000",
                    "0001100000101000010010001000111100000001000000100000010000001000",
                    "1111111101000000001000000001000000010000001000000100000011111111",
                    "1111111100000010000001000000100000001000000001000000001011111111"
                };

                for (String forbiddenString : forbiddenBinaryStrings) {
                    if (binaryString.equals(forbiddenString)) {
                        outputIsMatching = true;
                        break;
                    }
                }

                if (outputIsMatching) {
                    JOptionPane.showMessageDialog(Test.this, "Output jest równy jednemu z niedozwolonych ciągów binarnych.", "Błąd testowania", JOptionPane.ERROR_MESSAGE);
                } else {
                    test();
                }
            }
        });

                                                                    //KONIEC

        JButton readToFileButton = new JButton("CU");
        readToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajCiagUczacyZPliku();
            }
        });
    
        controlJPanel.add(clearButton);
        controlJPanel.add(trainButton);
        controlJPanel.add(testButton);
        controlJPanel.add(readToFileButton);

               
        whiteLabel1.setOpaque(true);
        whiteLabel1.setBackground(Color.WHITE);
        whiteLabel1.setPreferredSize(new Dimension(2, 2));
    
        whiteLabel2.setOpaque(true);
        whiteLabel2.setBackground(Color.WHITE);
        whiteLabel2.setPreferredSize(new Dimension(2, 2));
    
        controlJPanel.add(whiteLabel1);
        controlJPanel.add(whiteLabel2);
    
        return controlJPanel;
    }

                                                    /// TRENOWANIE ///

    public void train(int count) {
        if (uczaceWartosci.isEmpty()) {
            JOptionPane.showMessageDialog(Test.this, "Brak ciągów uczących. Dodaj co najmniej jeden ciąg uczący przed trenowaniem.", "Brak danych uczących", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Random random = new Random();

        for (long i = 0; i < count; i++) {
           
            UczacaWartosc uczacaWartosc = uczaceWartosci.get(random.nextInt(uczaceWartosci.size()));
            
            double[] input = uczacaWartosc.getInputExamples();
            double[] oneHot = uczacaWartosc.getOneHot();

            siec.ucz(input, oneHot, 0.1, count);
        }

        JOptionPane.showMessageDialog(Test.this, "Uczenie zakończone.", "Uczenie", JOptionPane.INFORMATION_MESSAGE);
    }

                                                    /// TESTOWANIE ///

    public void test() {
        if (selectedLetter == null) {
            JOptionPane.showMessageDialog(Test.this, "Najpierw wybierz literę do testowania.", "Brak wybranej litery", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        
    
        // Wyświetl wynik testu
        System.out.println("Otrzymany wynik: ");

        // if () {
        //     whiteLabel1.setBackground(Color.GREEN);
        //     whiteLabel2.setBackground(Color.WHITE);
        // } else {
        //     whiteLabel1.setBackground(Color.WHITE);
        //     whiteLabel2.setBackground(Color.RED);
        // }

        whiteLabel1.setOpaque(true);
        whiteLabel2.setOpaque(true);

        whiteLabel1.repaint();
        whiteLabel2.repaint();
    }

    private void dodajCiagUczacyZPliku() {
        String filePath = "C:/xampp/htdocs/projekty/MLP/MLP/ciagi_uczace.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(":");
                    String binarySequence = parts[0].trim();
                    String[] oneHotArray = parts[1].trim().split("\\s+");  // Zakładam, że one-hot jest oddzielone spacją

                    // Konwertuje ciąg zer i jedynek na tablicę double
                    double[] binaryArray = new double[binarySequence.length()];
                    for (int i = 0; i < binarySequence.length(); i++) {
                        binaryArray[i] = Double.parseDouble(String.valueOf(binarySequence.charAt(i)));
                    }

                    // Konwertuje one-hot na tablicę double
                    double[] oneHot = Arrays.stream(oneHotArray)
                            .mapToDouble(Double::parseDouble)
                            .toArray();

                    // Dodaje do listy przykładów
                    uczaceWartosci.add(new UczacaWartosc(binaryArray, oneHot));

                    System.out.println("-------------------------------------");

                    UczacaWartosc firstTrainingExample = uczaceWartosci.get(0);

                    // Pobieranie wartości z pierwszej pary uczącej
                    double[] oneExample = firstTrainingExample.getInputExamples();
                    double[] oneExample1 = firstTrainingExample.getOneHot();

                    // Wyświetlanie wartości
                    System.out.println("Binary Array: " + Arrays.toString(oneExample));
                    System.out.println("One-Hot Array: " + Arrays.toString(oneExample1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertGridToBinaryString() {
        StringBuilder binaryString = new StringBuilder();
    
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                binaryString.append(grid[x][y] ? "1" : "0");
            }
        }

        return binaryString.toString();
    }

    private String setSelectedLetterFromRadioButton() {
    JRadioButton selectedRadioButton = null;

        for (Enumeration<AbstractButton> buttons = letterGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                selectedRadioButton = (JRadioButton) button;
                break;
            }
        }

        if (selectedRadioButton != null) {
            if (selectedRadioButton == oButton) {
                selectedLetter = "100";
            } else if (selectedRadioButton == dButton) {
                selectedLetter = "010";
            } else if (selectedRadioButton == mButton) {
                selectedLetter = "001";
            }
        }

        return selectedLetter;
    }

	private void clearGrid() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                grid[x][y] = false;
            }
        }
        komponent.repaint();
    }

	private void setMouseListener() {
        komponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                addToGrid(e);
            }
        });

        komponent.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                addToGrid(e);
            }
        });
    }

    private void addToGrid(MouseEvent e) {
        int x = e.getX() / MojKomponent.CELL_SIZE;
        int y = e.getY() / MojKomponent.CELL_SIZE;

        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            grid[x][y] = true;
            komponent.repaint();
        }
    }

    public double[][] convertSelectedLetterToDouble(String dates) {
        double[][] result = new double[dates.length()][1];
    
        for (int i = 0; i < dates.length(); i++) {
            char date = dates.charAt(i);
            result[i][0] = (date == '0') ? 0.0 : 1.0;
        }
    
        return result;
    }

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new Test("neurony");
			}
		});
    }
}

class UczacaWartosc {
    private double[] inputExamples;
    private double[] oneHot;

    public UczacaWartosc(double[] inputExamples, double[] oneHot) {
        this.inputExamples = inputExamples;
        this.oneHot = oneHot;
    }

    public double[] getInputExamples() {
        return inputExamples;
    }

    public double[] getOneHot() {
        return oneHot;
    }
}