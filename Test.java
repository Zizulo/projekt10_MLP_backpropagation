import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;
import java.io.FileReader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Test extends JFrame{
	MojKomponent komponent;
	Siec siec;
    Neuron neurony;
	boolean[][] grid = new boolean[8][8];
    String selectedLetter;
    int count = 200;
    private ButtonGroup letterGroup = new ButtonGroup();
    private JRadioButton oButton;
    private JRadioButton dButton;
    private JRadioButton mButton;
	private ArrayList<UczacaWartosc> uczaceWartosci = new ArrayList<>();
    private ArrayList<TestowaWartosc> testoweWartosci = new ArrayList<>();
    private ArrayList<DoZapisuUczacaWartosc> dodajCiagDoZapisu = new ArrayList<>();
    JLabel whiteLabel1 = new JLabel("True");
    JLabel whiteLabel2 = new JLabel("False");
    private String currentLetter;

	public class MojKomponent extends JComponent{
		private static final int CELL_SIZE = 40;
    
        public MojKomponent() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    addToGrid(e);
                }
            });
    
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    addToGrid(e);
                }
            });
        }
    
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
    
        private void addToGrid(MouseEvent e) {
            int x = e.getX() / CELL_SIZE;
            int y = e.getY() / CELL_SIZE;

            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                grid[x][y] = true;
                System.out.println("Added to grid at (" + x + ", " + y + ")");
                printGridValues();
                repaint();
            }

        }

        private void printGridValues() {
            System.out.println("Grid Values:");
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    System.out.print(grid[j][i] ? "1" : "0");
                }
                System.out.println();
            }
        }
    
        public void clearGrid() {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    grid[x][y] = false;
                }
            }
            repaint();
        }
    
        public boolean[][] getGrid() {
            return grid;
        }
	}
	public Test(String string) {
		super(string);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension d = kit.getScreenSize();
        int width = 14 * MojKomponent.CELL_SIZE;
        int height = 12 * MojKomponent.CELL_SIZE;
        setBounds(d.width / 4, d.height / 4, width, height);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		add(mainPanel);
		mainPanel.add(createControlPanel());
		mainPanel.add(komponent = new MojKomponent());
		
        setVisible(true);

		int [] tab=new int [2];
		tab[0]=25; tab[1]=3;
		siec=new Siec(64,2,tab);
		
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
                selectedLetter = "1";
            }
        });
    
        JRadioButton dButton = new JRadioButton("D");
        dButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedLetter = "2";
            }
        });
    
        JRadioButton mButton = new JRadioButton("M");
        mButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedLetter = "3";
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
                komponent.clearGrid();
            }
        });

        JButton addCiagButton = new JButton("Dodaj Ciag do Pliku");
        addCiagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                dodajCiag();
            }
        });

        JButton saveToFileButton = new JButton("Zapisz Plik z Ciągami");
        saveToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                zapiszDoPliku("uczaceWartosci.txt");
            }
        });
    
                                                            /// TRENOWANIE ///

        JButton trainButton = new JButton("Ucz");
        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                train(count);
            }
        });

                                                            /// TESTOWANIE ///
    
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

        JButton readToFileCUButton = new JButton("Wczytaj CU do Programu");
        readToFileCUButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajCiagUczacyZPliku();
            }
        });

        JButton readToFileCTButton = new JButton("Wczytaj CT do Programu");
        readToFileCTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajCiagTestowyZPliku();
            }
        });
    
        controlJPanel.add(clearButton);
        controlJPanel.add(trainButton);
        controlJPanel.add(testButton);
        controlJPanel.add(addCiagButton);
        controlJPanel.add(saveToFileButton);
        controlJPanel.add(readToFileCUButton);
        controlJPanel.add(readToFileCTButton);
               
        whiteLabel1.setOpaque(true);
        whiteLabel1.setBackground(Color.WHITE);
        whiteLabel1.setPreferredSize(new Dimension(2, 2));
    
        whiteLabel2.setOpaque(true);
        whiteLabel2.setBackground(Color.WHITE);
        whiteLabel2.setPreferredSize(new Dimension(2, 2));
    
        controlJPanel.add(whiteLabel1);
        controlJPanel.add(whiteLabel2);

                                                    /// ROZPOZNAWANIE ///

        JButton recognizeButton = new JButton("Rozpoznaj");
        recognizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recognize();
            }
        });

        
        controlJPanel.add(recognizeButton);

    
        return controlJPanel;
    }

                                                    /// TRENOWANIE ///

    public void train(int count) {
		double oczekiwanaSkutecznosc=90;
		double sumaBledow=100;
		double najmniejszyBlad = sumaBledow;
		double najwiekszSkutecznosc=0;
		int j = 0;
		double[] wyniki = new double[3];

        if (uczaceWartosci.isEmpty()) {
            JOptionPane.showMessageDialog(Test.this, "Brak ciągów uczących. Dodaj co najmniej jeden ciąg uczący przed trenowaniem.", "Brak danych uczących", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Collections.shuffle(uczaceWartosci);
        
        while(najwiekszSkutecznosc < oczekiwanaSkutecznosc){

            for(int i=0; i<count; i++)
            {
                sumaBledow=0;
                for(UczacaWartosc uczacaWartosc : uczaceWartosci)
                {
                    ArrayList<Double> input = uczacaWartosc.getInputExamples();
                    int oneHot = uczacaWartosc.getOneHot();
                    double [] wynik=siec.trenuj(input, oneHot);
                    sumaBledow+=wynik[0];
					sumaBledow+=wynik[1];
					sumaBledow+=wynik[2];
                    wyniki = wynik;
                } 
				najmniejszyBlad = (sumaBledow<najmniejszyBlad)? sumaBledow:najmniejszyBlad;
				najwiekszSkutecznosc=(100-(najmniejszyBlad/uczaceWartosci.size())*100);
					if(najwiekszSkutecznosc>oczekiwanaSkutecznosc)
						break;     
            }
            j++;
    
            System.out.println("P�tla: " + j + "   " + sumaBledow + "  (" + String.valueOf(najmniejszyBlad)+")" + " Najwieksza skutecznosc: " + najwiekszSkutecznosc);

            if(najwiekszSkutecznosc>oczekiwanaSkutecznosc){
                System.out.println(j + " : " + wyniki[0] + ":" + wyniki[1] + ":" + wyniki[2]);
                break;
            }
			int [] tab=new int [2];
			tab[0]=25; tab[1]=3;
			siec=new Siec(64,2,tab);
        }

    }

                                                    /// TESTOWANIE ///

    public void test() {

        for(TestowaWartosc testowaWartosc : testoweWartosci){
            ArrayList<Double> input = testowaWartosc.getInputExamples();
            int oneHote = testowaWartosc.getOneHot();
            
            double [] wejscie;
            double [] wynik;
            
            wejscie = new double[input.size()];
            for(int i=0; i<input.size(); i++)
            {
                wejscie[i]=input.get(i);
            }

            wynik = siec.oblicz_wyjscie(wejscie);

            if(wynik[0] > 0.75 && oneHote == 1){System.out.println(oneHote + "LITERA O");} 
            else if(wynik[1] > 0.75 && oneHote == 2){System.out.println(oneHote + "LITERA D");}
            else if(wynik[2] > 0.75 && oneHote == 3){System.out.println(oneHote + "LITERA M");}
            else{System.out.println(oneHote + "ZADNA Z LITER");}
        }
    }

                                                /// ROZPOZNAWANIE ///

    public void recognize(){
        double[] wejscie;
        double[] wynik;

        String binaryString = convertGridToBinaryString();
        System.out.println(binaryString);
        ArrayList<Double> recognizeInput = convertBinaryStringToDoubleArray(binaryString);
        System.out.println(recognizeInput);
        
        wejscie = new double[recognizeInput.size()];
        for(int i=0; i<recognizeInput.size(); i++)
    	{
    		wejscie[i] = recognizeInput.get(i);
    	}

    	wynik = siec.oblicz_wyjscie(wejscie);

        if (wynik[0] > 0.75 && wynik[0] > wynik[1] && wynik[0] > wynik[2]) {
            currentLetter = "O";
            whiteLabel1.setBackground(Color.GREEN);
            whiteLabel1.setText(currentLetter);
            whiteLabel2.setBackground(Color.WHITE);
        } else if (wynik[1] > 0.75 && wynik[1] > wynik[0] && wynik[1] > wynik[2]) {
            currentLetter = "D";
            whiteLabel1.setBackground(Color.GREEN);
            whiteLabel1.setText(currentLetter);
            whiteLabel2.setBackground(Color.WHITE);
        } else if (wynik[2] > 0.75 && wynik[2] > wynik[0] && wynik[2] > wynik[1]) {
            currentLetter = "M";
            whiteLabel1.setBackground(Color.GREEN);
            whiteLabel1.setText(currentLetter);
            whiteLabel2.setBackground(Color.WHITE);
        } else {
            currentLetter = "IDK";
            whiteLabel1.setBackground(Color.WHITE);
            whiteLabel2.setText(currentLetter);
            whiteLabel2.setBackground(Color.RED);
        }

        whiteLabel1.setOpaque(true);
        whiteLabel2.setOpaque(true);

        whiteLabel1.repaint();
        whiteLabel2.repaint();

    	String st = ((wynik[0]>0.75 && wynik[0] > wynik[1] && wynik[0] > wynik[2])? "TO ZNAK O":"TO NIE ZNAK O") + "\nWynik: " + String.valueOf(wynik[0]) +
    			"\n" + ((wynik[1]>0.75 && wynik[1] > wynik[0] && wynik[1] > wynik[2])? "TO ZNAK D":"TO NIE ZNAK D") + "\nWynik: " + String.valueOf(wynik[1]) +
    			"\n" + ((wynik[2]>0.75 && wynik[2] > wynik[0] && wynik[2] > wynik[1])? "TO ZNAK M":"TO NIE ZNAK M") + "\nWynik: " + String.valueOf(wynik[2]);
    	JOptionPane.showMessageDialog(null, st);

        resetRecognitionState();
    }
   
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~ METODY ~~~~~~~~~~~~~~~~~~~~~~~~~~//


    private void dodajCiag() {
        String wybranaLitera = setSelectedLetterFromRadioButton();
        double[] output = convertSelectedLetterToDoubleArray(wybranaLitera);

        double[] input = new double[64];
    
        String binaryString = convertGridToBinaryString();
        input = convertBinaryStringToDouble(binaryString);
        System.out.println(input[0]);
    
        String inputBinaryString = convertDoubleArrayToBinaryString(input);
        String outputBinaryString = convertDoubleArrayToBinaryString(output);
    
        DoZapisuUczacaWartosc doZapisuUczacaWartosc = new DoZapisuUczacaWartosc(inputBinaryString, outputBinaryString);
        dodajCiagDoZapisu.add(doZapisuUczacaWartosc);
    }
    

    private void zapiszDoPliku(String nazwaPliku) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nazwaPliku))) {
            for (DoZapisuUczacaWartosc dozapisuuczacaWartosc : dodajCiagDoZapisu) {
                writer.write(dozapisuuczacaWartosc.getInput() + ":" + dozapisuuczacaWartosc.getOutput());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Dane uczące zostały zapisane do pliku " + nazwaPliku, "Zapisano do pliku", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd zapisu danych uczących do pliku.", "Błąd zapisu", JOptionPane.ERROR_MESSAGE);
        }
    } 

    private void dodajCiagUczacyZPliku() {
        String currentDirectory = System.getProperty("user.dir");
    
        JFileChooser fileChooser = new JFileChooser(currentDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);

        System.out.println("Wybierz Plik CU");

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

        int nr = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(":");
                    String binarySequence = parts[0].trim();
                    String oneHotArray = parts[1].trim();  
    
                    ArrayList<Double> binaryArrayList = new ArrayList<>();
                    for (int i = 0; i < binarySequence.length(); i++) {
                        binaryArrayList.add(Double.parseDouble(String.valueOf(binarySequence.charAt(i))));
                    }
                    
                    int oneHotArrayList = Integer.parseInt(String.valueOf(oneHotArray));
  
                    uczaceWartosci.add(new UczacaWartosc(binaryArrayList, oneHotArrayList));
    
                    System.out.println("Pobrano CU nr: " + nr++);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    } 

    private void dodajCiagTestowyZPliku() {
        String currentDirectory = System.getProperty("user.dir");
    
        JFileChooser fileChooser = new JFileChooser(currentDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);

        System.out.println("Wybierz Plik CT");

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        
        int nr = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(":");
                    String binarySequence = parts[0].trim();
                    String oneHotArray = parts[1].trim();  
    
                    ArrayList<Double> binaryArrayList = new ArrayList<>();
                    for (int i = 0; i < binarySequence.length(); i++) {
                        binaryArrayList.add(Double.parseDouble(String.valueOf(binarySequence.charAt(i))));
                    }
                    
                    int oneHotArrayList = Integer.parseInt(String.valueOf(oneHotArray));
  
                    testoweWartosci.add(new TestowaWartosc(binaryArrayList, oneHotArrayList));
    
                    System.out.println("Pobrano CT nr: " + nr++);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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
                    selectedLetter = "1";
                } else if (selectedRadioButton == dButton) {
                    selectedLetter = "2";
                } else if (selectedRadioButton == mButton) {
                    selectedLetter = "3";
                }
            }
    
            return selectedLetter;
        }

    private String convertDoubleArrayToBinaryString(double[] array) {
        StringBuilder binaryString = new StringBuilder();
        for (double value : array) {
            binaryString.append((int) value);
        }
        return binaryString.toString();
    }

    public double[] convertSelectedLetterToDoubleArray(String selectedLetter) {
        double[] result = new double[selectedLetter.length()];
    
        for (int i = 0; i < selectedLetter.length(); i++) {
            char c = selectedLetter.charAt(i);
            if(c == '1'){
                result[i] = 1.0;
            } else if (c == '2'){
                result[i] = 2.0;
            } else if (c == '3'){
                result[i] = 3.0;
            } else{
                result[i] = 0.0;
            }
        }
    
        return result;
    }

    public ArrayList<Double> convertBinaryStringToDoubleArray(String binaryStringRecognize) {
        ArrayList<Double> binaryStringDoubleArray = new ArrayList<>();
        for (int i = 0; i < binaryStringRecognize.length(); i++) {
            char c = binaryStringRecognize.charAt(i);
            double parsedDouble = Double.parseDouble(String.valueOf(c));
            binaryStringDoubleArray.add(parsedDouble);
        }
        return binaryStringDoubleArray;
    }    

    private double[] convertBinaryStringToDouble(String binaryStringRecognize) {
        double[] binaryStringDoubleArray = new double[binaryStringRecognize.length()];
        for (int i = 0; i < binaryStringRecognize.length(); i++) {
            char c = binaryStringRecognize.charAt(i);
            binaryStringDoubleArray[i] = Character.getNumericValue(c);
        }
        return binaryStringDoubleArray;
    }    

    private String convertGridToBinaryString() {
        StringBuilder binaryStrings = new StringBuilder();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                binaryStrings.append(grid[x][y] ? "1" : "0");
                System.out.print(grid[x][y] ? "1" : "0"); 
            }
            System.out.println();
        }
    
        return binaryStrings.toString();
    }       

    private void resetRecognitionState() {
        whiteLabel1.setBackground(Color.WHITE);
        whiteLabel1.setText("True");
        whiteLabel2.setBackground(Color.WHITE);
        whiteLabel2.setText("False");
        
        currentLetter = null;
        komponent.clearGrid();
        grid = komponent.getGrid();
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

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~ KLASY ~~~~~~~~~~~~~~~~~~~~~~~~~~//

class UczacaWartosc {
    private ArrayList<Double> inputExamples;
    private int oneHot;

    public UczacaWartosc(ArrayList<Double> inputExamples, int oneHot) {
        this.inputExamples = inputExamples;
        this.oneHot = oneHot;
    }

    public ArrayList<Double> getInputExamples() {
        return inputExamples;
    }

    public int getOneHot() {
        return oneHot;
    }
}

class DoZapisuUczacaWartosc {
        private String input;
        private String output;

        public DoZapisuUczacaWartosc(String input, String output) {
            this.input = input;
            this.output = output;
        }

        public String getInput() {
            return input;
        }

        public String getOutput() {
            return output;
        }

        @Override
        public String toString() {
            return "Input (binary): " + input + " Output (binary): " + output;
        }
    }

class TestowaWartosc {
    private ArrayList<Double> inputExamples;
    private int oneHot;

    public TestowaWartosc(ArrayList<Double> inputExamples, int oneHot) {
        this.inputExamples = inputExamples;
        this.oneHot = oneHot;
    }

    public ArrayList<Double> getInputExamples() {
        return inputExamples;
    }

    public int getOneHot() {
        return oneHot;
    }
}