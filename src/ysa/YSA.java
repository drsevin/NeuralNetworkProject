package ysa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;



public class YSA {
	
	
	
	private static final File egitimDosya = new File(YSA.class.getResource("Egitim.txt").getPath());
	private static final File testDosya = new File(YSA.class.getResource("Test.txt").getPath());
	

	
	private double[] minimumlar;
	private double[] maksimumlar;
	
	
	private int araKatmanNoronSayisi;
	private MomentumBackpropagation mbp;
	
	//BackPropagation bp;		
	private DataSet egitimVeriSeti;
	private DataSet testVeriSeti; 

	
	public YSA(double araKatmanSayisi,double momentum ,double ogrenmeKatsayisi, double maxHata, int epoch) throws FileNotFoundException {
		
		
		minimumlar = new double[3];
		maksimumlar = new double[3];
		//this.araKatmanNoronSayisi = araKatmanNoronSayisi;
		
		//double en büyük değeri mine ata, double en küçük değeri maxa ata ve karşılaştır sayıları
		
		for(int i = 0; i<1; i++) {
			minimumlar[i] = Double.MAX_VALUE; //Double sınıfını kullanıyoruz
			maksimumlar[i] = Double.MIN_VALUE;
		}
		MinimumveMaksimumlar(egitimDosya);
		MinimumveMaksimumlar(testDosya);
		egitimVeriSeti = veriSetiOku(egitimDosya);
		testVeriSeti = veriSetiOku(testDosya);
		
		
	
		
		mbp = new MomentumBackpropagation();
		mbp.setMomentum(momentum);
		mbp.setLearningRate(ogrenmeKatsayisi);
		mbp.setMaxError(maxHata);
		mbp.setMaxIterations(epoch);
		
		/*bp = new BackPropagation();	
		bp.setLearningRate(ogrenmeKatsayisi);
		bp.setMaxError(maxHata);
		bp.setMaxIterations(epoch);*/
	}
	public void egit() {
		MultiLayerPerceptron sinirselAg = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,2,araKatmanNoronSayisi,1);
		//NeuralNetwork<BackPropagation> sinirselAg = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,2,5,1);
		sinirselAg.setLearningRule(mbp);
		sinirselAg.learn(egitimVeriSeti);
		sinirselAg.save("ysa.nnet");
		System.out.println("Egitim tamamlandı. ");
	}
	public double test() {
		NeuralNetwork sinirselAg = NeuralNetwork.createFromFile("ysa.nnet");
		//NeuralNetwork<BackPropagation> sinirselAg = NeuralNetwork.createFromFile("ysa.nnet");
		double toplamHata = 0;
		var satirlar = testVeriSeti.getRows();
		for(DataSetRow satir : satirlar)
		{
			sinirselAg.setInput(satir.getInput());
			sinirselAg.calculate();//ileri besleme
			toplamHata += mse(satir.getDesiredOutput(),sinirselAg.getOutput());
			
		}
		return toplamHata/testVeriSeti.size();
	}
	public double egitimHata() {
		return mbp.getTotalNetworkError();
		
	}
	private double mse(double[] beklenen, double[] uretilen) {
		double birVeridekiHata = 0;
		for(int i = 0; i<beklenen.length; i++)
		{
			birVeridekiHata += Math.pow(beklenen[i] - uretilen[i],2);
		}
		return birVeridekiHata/beklenen.length;
	}
	private void MinimumveMaksimumlar(File dosya) throws FileNotFoundException {
		Scanner in = new Scanner(dosya);
		while (in.hasNextLine()) {

			String[] values = in.nextLine().split("\\s+"); // Satirdaki değerleri boşluklara göre ayır
			// Her bir giriş değeri için minimum ve maksimumu güncelle
			for (int i = 0; i < 3; i++) {
				double d = Double.parseDouble(values[i]);
				if (d < minimumlar[i])
					minimumlar[i] = d;
				if (d > maksimumlar[i])
					maksimumlar[i] = d;
			}

		}
		
		in.close();
		
	}
	private DataSet veriSetiOku(File dosya) throws FileNotFoundException {
		Scanner in = new Scanner(dosya);
		DataSet ds = new DataSet(2, 1); //8 input 3 output
		while (in.hasNextLine()) {

			String[] values = in.nextLine().split("\\s+"); // Satirdaki değerleri boşluklara göre ayır
			double[] input = new double[2];// Giriş değerlerini oku ve normalleştir
			double[] output = new double[1];
			for (int i = 0; i < 3; i++) {
				double d = Double.parseDouble(values[i]);
				// Giriş değerlerini normalleştir
				if (i == 2) {
					output[0] = minmaxDeger(d, minimumlar[i], maksimumlar[i]);

				} else {
					input[i] = minmaxDeger(d, minimumlar[i], maksimumlar[i]);
				}
			}

			// Çıkış değerlerini oku

			// Satırı DataSet'e ekle
			DataSetRow satir = new DataSetRow(input, output);
			ds.add(satir);
		}
		in.close();
		return ds;
	}
	private double minmaxDeger(double d, double min, double max) {
		return(d-min)/(max-min);
	}
	

}
