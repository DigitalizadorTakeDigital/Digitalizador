package br.com.webscanner.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationUtilTest {

	@Test
	public void cpfShouldBeValid() {
		String cpf = "34178440888";
		assertTrue(ValidationUtil.isCpfValid(cpf));
	}
	
	@Test
	public void cnpjShouldBeValid() {
		String cnpj = "79137960000158";
		assertTrue(ValidationUtil.isCnpjValid(cnpj));
	}
		
	@Test
	public void accountDigitShouldBeValid(){
		String account = "3392";
		String digit = "8";
		
		assertTrue(ValidationUtil.isAccountDigitValid(account, digit));
	}
	
	@Test
	public void accountDigitShouldBeValid2(){
		String account = "1";
		String digit = "9";
		
		assertTrue(ValidationUtil.isAccountDigitValid(account, digit));
	}
	
	@Test
	public void accountDigitShouldBeValid3(){
		String account = "3376";
		String digit = "6";
		
		assertTrue(ValidationUtil.isAccountDigitValid(account, digit));
	}
	
	@Test
	public void accountDigitShouldBeValid4(){
		String account = "4499";
		String digit = "7";
		
		assertTrue(ValidationUtil.isAccountDigitValid(account, digit));
	}
	
	@Test
	public void accountDigitShouldBeValid5(){
		String account = "0496";
		String digit = "0";
		
		assertTrue(ValidationUtil.isAccountDigitValid(account, digit));
	}
	
	@Test
	public void accountDigitShouldBeValid6(){
		String account = "4001";
		String digit = "0";
		
		assertTrue(ValidationUtil.isAccountDigitValid(account, digit));
	}
	
	@Test
	public void cmc7ShouldBeValid(){
		String part1 = "";
		String part2 = "";
		String part3 = "";
		
		assertTrue(ValidationUtil.isCMC7Valid(part1, part2, part3));
	}
	
	
}