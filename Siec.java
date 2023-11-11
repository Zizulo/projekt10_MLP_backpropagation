import java.util.List;
import java.util.ArrayList;

public class Siec{
	Warstwa [] warstwy;
	int liczba_warstw;
	
	public Siec(){
		warstwy=null;
		this.liczba_warstw=0;
	}
	public Siec(int liczba_wejsc,int liczba_warstw,int [] lnww){
		this.liczba_warstw=liczba_warstw;
		warstwy=new Warstwa[liczba_warstw];
		for(int i=0;i<liczba_warstw;i++)
			warstwy[i]=new Warstwa((i==0)?liczba_wejsc:lnww[i-1],lnww[i]);
	}
	double [] oblicz_wyjscie(double [] wejscia){
		double [] wyjscie=null;
		for(int i=0;i<liczba_warstw;i++)
			wejscia = wyjscie = warstwy[i].oblicz_wyjscie(wejscia);
		return wyjscie;
	}

	public void ucz(double[] daneWejsciowe, double[] oneHot, double wspolczynnikUczenia, int liczbaEpok) {
        for (int epoka = 0; epoka < liczbaEpok; epoka++) {
            for (int i = 0; i < daneWejsciowe.length; i++) {
                double[] wejscia = daneWejsciowe;
                double[] oczekiwane = oneHot;

                // Propagacja wsteczna
                propagacjaWsteczna(wejscia, oczekiwane, wspolczynnikUczenia);
            }
        }
    }

    private void propagacjaWsteczna(double[] wejscia, double[] oneHot, double wspolczynnikUczenia) {
        // Obliczenie wyjścia sieci
        double[] wyjscie = oblicz_wyjscie(wejscia);
    
        // Obliczenie błędów dla ostatniej warstwy
        double[] bledy = new double[wyjscie.length];
        for (int i = 0; i < wyjscie.length; i++) {
            bledy[i] = oneHot[i] - wyjscie[i];
        }
    
        // Aktualizacja wag i biasów wstecz
        for (int i = liczba_warstw - 1; i >= 0; i--) {
            Warstwa warstwa = warstwy[i];
    
            // Obliczenie gradientu
            double[] gradient = new double[warstwa.liczba_neuronow];
            for (int j = 0; j < warstwa.liczba_neuronow; j++) {
                gradient[j] = bledy[j] * wyjscie[j] * (1 - wyjscie[j]);
            }
    
            // Aktualizacja wag i biasów
            for (int j = 0; j < warstwa.liczba_neuronow; j++) {
                Neuron neuron = warstwa.neurony[j];
                neuron.algorytmDelta(wejscia, gradient);
            }
    
            // Przygotowanie błędów dla kolejnej warstwy wstecz
            double[] noweBledy = new double[warstwa.neurony[0].wagi.length];
            for (int j = 0; j < warstwa.neurony[0].wagi.length; j++) {
                double suma = 0;
                for (int k = 0; k < warstwa.liczba_neuronow; k++) {
                    suma += warstwa.neurony[k].wagi[j] * gradient[k];
                }
                noweBledy[j] = suma;
            }
    
            bledy = noweBledy;
        }
    }
}

