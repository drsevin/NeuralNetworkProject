package ysa;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;



public class Program {

	public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
		Scanner in = new Scanner(System.in);
		int araKatmanNoronSayisi;
		double momentum, ogrenmeKatsayisi, maxError;
		int epoch,secenek;
		YSA ysa8 = null;

		
		do {
			System.out.println("1.Eğitim ve Test");
			System.out.println("2.Cikis");
			System.out.println("=>");
			secenek = in.nextInt();
			switch(secenek) {
			case 1:
				
				ysa8 = new YSA(70,0.008,0.9,0.0008,5000);
				ysa8.egit();
				System.out.println("Egitim Hata: " + ysa8.egitimHata());
				System.out.println("Test Hata: " + ysa8.test());
				break;
				
			}
		}while(secenek != 2);
	}

}
