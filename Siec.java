import java.util.ArrayList;

public class Siec {
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
			wejscia=wyjscie=warstwy[i].oblicz_wyjscie(wejscia);
		return wyjscie;
	}
	public double[] trenuj(ArrayList<Double> dane_wejsciowe, int output)
	{
		double [] wejscie;
    	double [] wynik;
    	double [] errorNetwork = new double [3];
    	wejscie = new double[dane_wejsciowe.size()];
    	for(int i=0; i<dane_wejsciowe.size(); i++)
    	{
    		wejscie[i]=dane_wejsciowe.get(i);
    	}
		
    	wynik = oblicz_wyjscie(wejscie);
    	
    	double eNO =  ((output == 1? 1:0)-wynik[0]);
    	double eND =  ((output == 2? 1:0)-wynik[1]);
    	double eNM =  ((output == 3? 1:0)-wynik[2]);
   
    	for(int i=warstwy.length-1; i>=0; i--)
    	{
    	
    		if(i==warstwy.length-1)
    		{
    			warstwy[i].neurony[0].blad=eNO;
    			warstwy[i].neurony[1].blad=eND;
    			warstwy[i].neurony[2].blad=eNM;
    		}
    		else
    		{
    			Neuron lNOutputO = warstwy[liczba_warstw-1].neurony[0];
    			Neuron lNOutputD = warstwy[liczba_warstw-1].neurony[1];
    			Neuron lNOutputM = warstwy[liczba_warstw-1].neurony[2];
    			for(int j=0; j<warstwy[i].liczba_neuronow; j++)
    			{
    				Neuron neuron = warstwy[i].neurony[j];
    				neuron.blad=(eNO*lNOutputO.wagi[j])+(eND*lNOutputD.wagi[j])+(eNM*lNOutputM.wagi[j]);
    			}
    		}
    	}
 	
    	for(int i=0; i<liczba_warstw; i++)
    	{
    		for(int j=0; j<warstwy[i].liczba_neuronow; j++)
    		{
    			for(int k=0; k<=warstwy[i].neurony[j].liczba_wejsc; k++)
    			{
    				double bladNeuronu = warstwy[i].neurony[j].blad;
    				double wynikNeuronu = warstwy[i].neurony[j].wynik;
    				double sygnalWejsciowy=0;
    				if(i==0)
    				{
    					if(k<wejscie.length)
    					sygnalWejsciowy=wejscie[k];
    				}
    				else
    				{
    					if(k<warstwy[i-1].neurony.length)
    					sygnalWejsciowy=warstwy[i-1].neurony[k].wynik;
    				}
    				
    				warstwy[i].neurony[j].wagi[k] += wynikNeuronu*(1-wynikNeuronu)*bladNeuronu*sygnalWejsciowy;
    			}
    		}
    	}
    	errorNetwork[0]=Math.abs(eNO);
    	errorNetwork[1]=Math.abs(eND);
    	errorNetwork[2]=Math.abs(eNM);
    	
    	return errorNetwork;
	}
}
