import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;


public class Neuron{
	double [] wagi;
	int liczba_wejsc;
	double bias;

	public Neuron(){
		liczba_wejsc=0;
		wagi=null;
	}
	public Neuron(int liczba_wejsc){
		this.liczba_wejsc=liczba_wejsc;
		wagi=new double[liczba_wejsc+1];
		this.bias = new Random().nextDouble();
		generuj();
	}
	private void generuj() {
		Random r=new Random();
		for(int i=0;i<=liczba_wejsc;i++)
			//wagi[i]=(r.nextDouble()-0.5)*2.0*10;//do ogladania
			wagi[i]=(r.nextDouble()-0.5)*2.0*0.01;//do projektu w[i] 
	}

	public void algorytmDelta(double[] wejscia, double[] gradient) {
		double[] errors = new double[wagi.length];
	
		for (int i = 0; i < wagi.length; i++) {
			errors[i] = gradient[i]; // Błąd to pochodna funkcji kosztu
			double delta = errors[i] * wejscia[i] * 0.01;
			wagi[i] += delta;
		}
	
		bias += errors[0] * 0.01;
	}
	
	
	public double oblicz_wyjscie(double [] wejscia){
		double fi=bias;
		//double fi=0.0;  
		for(int i=1;i<=liczba_wejsc;i++)
			fi+=wagi[i]*wejscia[i-1];
		double wynik=1.0/(1.0+Math.exp(-fi));// funkcja aktywacji sigma -unip
		//double wynik=(fi>0.0)?1.0:0.0;//skok jednostkowy
		//double wynik=fi; //f.a. liniowa 
		return wynik;
	}
}
