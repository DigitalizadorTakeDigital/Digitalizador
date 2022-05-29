package br.com.webscanner.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TXTReader {
	
	public static List<String> leitor(String path) throws IOException {
		BufferedReader buffRead = new BufferedReader(new FileReader(path));
		String linha = "";
		List <String> textoCount = new ArrayList<String>();
		boolean primeiro = true;
		while (true) {
			if (linha != null ) {
				if(!primeiro) {
				textoCount.add(linha);
				System.out.println(linha);
				}
			} else
				break;
			linha = buffRead.readLine();
			primeiro = false;
		}
		return textoCount;
		
		
	}

	
}
