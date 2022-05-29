/**
 * 
 */
package br.com.webscanner.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Classe utilitária que contém validações diversas.
 * @author Jonathan Camara
 *
 */
/**
 * @author fernando.germano
 *
 */
public class ValidationUtil {
	/**
	 * Verifica se o cpf/cnpj é válido.
	 * @param cpf - Cpf/cnpj que deseja validar;
	 * @return
	 */
	public static boolean isCpfCnpjValid(String cpfCnpj){
		if (cpfCnpj.length() > 11) 
			return isCnpjValid(cpfCnpj);
		else
			return isCpfValid(cpfCnpj);
	}
	
	/**
	 * Verifica se o cpf é válido.
	 * @param cpf - Cpf que deseja validar;
	 * @return
	 */
	public static boolean isCpfValid(String cpf){
	    try{  
	        int     d1, d2;  
	        int     digito1, digito2, resto;  
	        int     digitoCPF;  
	        String  nDigResult;  
	        cpf=cpf.replace('.',' ');  
	        cpf=cpf.replace('-',' ');  
	        cpf=cpf.replaceAll(" ","");  
	        d1 = d2 = 0;  
	        digito1 = digito2 = resto = 0;  
	          
	        for (int nCount = 1; nCount < cpf.length() -1; nCount++) {  
	            digitoCPF = Integer.valueOf(cpf.substring(nCount -1, nCount)).intValue();  
	            
	            d1 = d1 + ( 11 - nCount ) * digitoCPF;  
	            
	            d2 = d2 + ( 12 - nCount ) * digitoCPF;  
	        };  
	          
	        resto = (d1 % 11);  
	          
	        if (resto < 2)  
	            digito1 = 0;  
	        else  
	            digito1 = 11 - resto;  
	          
	        d2 += 2 * digito1;  
	          
	        resto = (d2 % 11);  
	          
	        if (resto < 2)  
	            digito2 = 0;  
	        else  
	            digito2 = 11 - resto;  
	          
	        String nDigVerific = cpf.substring(cpf.length()-2, cpf.length());  
	          
	        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);  
	          
	        return nDigVerific.equals(nDigResult);  
	    }catch (Exception e){  
	        return false;  
	    }    
	}  
	
	/**
	 * Verifica se o CNPJ é válido.
	 * @param cpf - CNPJ que deseja validar;
	 * @return
	 */
	public static boolean isCnpjValid(String cnpj){
	    try{  
	        cnpj=cnpj.replace('.',' ');  
	        cnpj=cnpj.replace('/',' ');  
	        cnpj=cnpj.replace('-',' ');  
	        cnpj=cnpj.replaceAll(" ","");  
	        int soma = 0, dig;  
	        String cnpj_calc = cnpj.substring(0,12);  
	          
	        if ( cnpj.length() != 14 )  
	            return false;  
	        char[] chr_cnpj = cnpj.toCharArray();  
	      
	        for( int i = 0; i < 4; i++ )  
	            if ( chr_cnpj[i]-48 >=0 && chr_cnpj[i]-48 <=9 )  
	                soma += (chr_cnpj[i] - 48 ) * (6 - (i + 1)) ;  
	        for( int i = 0; i < 8; i++ )  
	            if ( chr_cnpj[i+4]-48 >=0 && chr_cnpj[i+4]-48 <=9 )  
	                soma += (chr_cnpj[i+4] - 48 ) * (10 - (i + 1)) ;  
	        dig = 11 - (soma % 11);  
	        cnpj_calc += ( dig == 10 || dig == 11 ) ?  
	            "0" : Integer.toString(dig);  
	      
	        soma = 0;  
	        for ( int i = 0; i < 5; i++ )  
	            if ( chr_cnpj[i]-48 >=0 && chr_cnpj[i]-48 <=9 )  
	                soma += (chr_cnpj[i] - 48 ) * (7 - (i + 1)) ;  
	        for ( int i = 0; i < 8; i++ )  
	            if ( chr_cnpj[i+5]-48 >=0 && chr_cnpj[i+5]-48 <=9 )  
	                soma += (chr_cnpj[i+5] - 48 ) * (10 - (i + 1)) ;  
	        dig = 11 - (soma % 11);  
	        cnpj_calc += ( dig == 10 || dig == 11 ) ?  
	            "0" : Integer.toString(dig);  
	        return cnpj.equals(cnpj_calc);  
	    }catch (Exception e){  
	        return false;  
	    }  
	}
	
	/**
	 * Verifica se a agência e conta são válidos
	 * @param account - Conta que deseja verificar
	 * @param digit - Dígito verificador da conta.
	 * @return
	 */
	public static boolean isAccountDigitValid(String account, String digit){
		int digitCalculed = modulo11(account);
		
		if (digitCalculed == Integer.parseInt(digit)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Verifica se a agência e conta são válidos
	 * @param account - Conta que deseja verificar
	 * @param digit - Dígito verificador da conta.
	 * @return
	 */
	public static boolean isAccountDigitValidOrExtractedField(String account, String digit, boolean extracted){
		if (extracted && digit.isEmpty()){
			return true;
		}else{
			return isAccountDigitValid(account, digit);
		}
	}
	
	/**
	 * Verifica se o CMC7 é válido
	 * @param part1 - Primeira parte do cmc7
	 * @param part2 - Segunda parte do cmc7
	 * @param part3 - Terceira parte do cmc7
	 * @return
	 */
	public static boolean isCMC7Valid(String part1, String part2, String part3){
		StringBuilder cmc7 = new StringBuilder();
		cmc7.append(part1).append(part2).append(part3);

		String regexValida = "\\d{8}\\d{10}\\d{12}";  
		String regexInvalida = "[0]{8}[0]{10}[0]{12}";  

		Pattern invalido = Pattern.compile(regexInvalida);  
		Pattern valido = Pattern.compile(regexValida);  

		Matcher mInvalido = invalido.matcher(cmc7);  
		Matcher mValido = valido.matcher(cmc7);  

		if ( (mInvalido.matches()) || (! mValido.matches()) ) {  
			return false;  
		}  

		String grupo1 = cmc7.substring(0,7);  
		String grupo2 = cmc7.substring(8,18);  
		String grupo3 = cmc7.substring(19,29);  

		boolean ret1 = (modulo10(grupo1).equals(cmc7.substring(18,19)));  
		boolean ret2 = (modulo10(grupo2).equals(cmc7.substring(7,8)));  
		boolean ret3 = (modulo10(grupo3).equals(cmc7.substring(29,30)));  

		return( ret1 && ret2 && ret3);  
	}

	private static String modulo10(String numero) {  

		int multi, posicao1, posicao2, acumula, resultado, dac;  

		dac = 0;  
		posicao1 = numero.length()-1;  
		multi   = 2;  
		acumula = 0;  

		while (posicao1 >= 0) {  

			resultado = Integer.parseInt(numero.substring(posicao1, posicao1 + 1)) * multi;  
			posicao2  = Integer.toString(resultado).length()-1;  

			while (posicao2 >= 0) {  
				acumula += Integer.parseInt(Integer.toString(resultado).substring(posicao2,posicao2 + 1));  
				posicao2--;  
			}  

			multi = (multi == 2) ? 1 : 2;  
			posicao1--;  
		}  


		dac = acumula % 10;  
		dac = 10 - dac;  

		if (dac == 10) {  
			dac = 0;  
		}  

		return String.valueOf(dac);  
	}  

	
	/** Calcula o Modulo 11 da posição 2 a 7.
	 * @param account
	 * @return digito
	 */
	private static int modulo11(String account){
		int result = 0;
		int posicao1 = account.length() -1;
		int multi = 2;
		int rest =0;
		int digit = 0;
		
		while (posicao1 >= 0) {  
			result += Integer.parseInt(account.substring(posicao1, posicao1 + 1)) * multi;  
			
			multi++;
			if (multi == 8) multi =2; 
			   
	        posicao1--;  
        }
		
		rest = result % 11;
		
		if (rest >0){
			digit = 11-rest;
		}
			
		if (digit == 10){
			digit = 0;
		}
		
		return digit;
	}
}