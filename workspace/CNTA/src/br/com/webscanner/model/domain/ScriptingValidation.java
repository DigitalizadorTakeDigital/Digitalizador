package br.com.webscanner.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe responsável por interpretar um código javascript.
 * @author Jonathan Camara
 */
public class ScriptingValidation {
	private static Logger logger = LogManager.getLogger(ScriptingValidation.class.getName());
	private static StringBuilder functions;
	
	static{
		functions = new StringBuilder();
		functions.append(getPadLeftFunction())
		.append(getCpfValidateFunction())
		.append(getCpnjValidateFunction())
		.append(getCpfCnpjValidateFunction())
		.append(getModulo11Function())
		.append(getAccountDigitValidateFunction())
		.append(getModulo10Function())
		.append(getCmc7ValidateFunction())
		.append(isEmpty())
		.append(notEmpty())
		.append(getIsCMC7Part1Valid())
		.append(getIsCMC7Part2Valid())
		.append(getIsCMC7Part3Valid());
	}
	
	public static boolean validate(Document document, String javascriptCode){
		logger.debug("Iniciando validação do documento. Codigo javascript {}", javascriptCode);
		ScriptEngineManager factory = new ScriptEngineManager();
	    ScriptEngine engine = factory.getEngineByName("JavaScript");

	    for(Field field : document.getFields()){
	    	engine.put(field.getFieldModel().getName(), field.getValue().toString() != null ? field.getValue().toString().trim() : "");
	    }
	    
	    StringBuilder builder = new StringBuilder();
	    builder.append("function documentValidation(){").append(javascriptCode).append("}");
	    
	    try {
	    	engine.eval(builder.toString());
	    	loadBaseFunctions(engine);
			Invocable invocable = (Invocable) engine;
			Object obj = invocable.invokeFunction("documentValidation");
			return (Boolean) obj;
		} catch (ScriptException e) {
			logger.error("Erro na função escrita. {}", e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.error("Não foi encontrada a função. {}", e.getMessage());	
		}
		
		return false;
	}
	
	public static boolean validate(String function, Document document){
		logger.debug("Iniciando validação do documento. Função javascript {}", function);
		ScriptEngineManager factory = new ScriptEngineManager();
	    ScriptEngine engine = factory.getEngineByName("JavaScript");

	    String functionName = function.substring(0, function.indexOf("("));

	    Pattern p = Pattern.compile("(?<=\\().+(?=\\))");
	    Matcher matcher = p.matcher(function);
	    
	    List<String> values = new ArrayList<String>();
	    if(matcher.find()){
	    	String parameters = matcher.group();
	    	for(String parameter : parameters.split(",")){
	    		if (parameter.contains("+")) {
	    			String[] split = parameter.split("\\+");
	    			
	    			String join = "";
	    			for (String s : split) {
	    				join += document.getFieldByName(s.trim()).getValue().toString();
	    			}
	    			
	    			values.add(join);
	    		} else {
	    			values.add(document.getFieldByName(parameter.trim()).getValue().toString());
	    		}
	    	}
	    }
	    
	    try {
	    	loadBaseFunctions(engine);
			Invocable invocable = (Invocable) engine;
			Object obj = invocable.invokeFunction(functionName, values.toArray());
			return (Boolean) obj;
		} catch (ScriptException e) {
			logger.error("Erro na função escrita. {}", e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.error("Não foi encontrada a função. {}", e.getMessage());	
		}
		return false;
	}
	
	private static String getCmc7ValidateFunction(){
		StringBuilder function = new StringBuilder("function isCMC7Valid(part1, part2, part3){");
		function.append("var cmc7 = part1 + \"\" + part2 + \"\" + part3;")
		.append("var regexValida = \"\\\\d{8}\\\\d{10}\\\\d{12}\";")
		.append("var regexInvalida = \"[0]{8}[0]{10}[0]{12}\";")
		.append("if (cmc7.match(new RegExp(regexValida)) == null){ return false; }")
		.append("if (cmc7.match(new RegExp(regexInvalida)) != null){ return false; }")
		.append("var grupo1 = cmc7.substring(0,7);")
		.append("var grupo2 = cmc7.substring(8,18);")
		.append("var grupo3 = cmc7.substring(19,29);")
		.append("var ret1 = false;")
		.append("var ret2 = false;")
		.append("var ret3 = false;")
		.append("if (modulo10(grupo1) == cmc7.substring(18,19)){ ret1 = true; }")
		.append("if (modulo10(grupo2) == cmc7.substring(7,8)){ ret2 = true; }")
		.append("if (modulo10(grupo3) == cmc7.substring(29,30)){ ret3  = true; }")
		.append("return ret1 && ret2 && ret3;}");
		
		return function.toString();
	}
	
	private static String getModulo10Function(){
		StringBuilder function = new StringBuilder("function modulo10(dado){");
		function.append("var i;")
		.append("var mult = 2;")
		.append("var soma = 0;")
		.append("var s = \"\";")
		.append("for (i=dado.length-1; i>=0; i--){")
		.append("s = (mult * parseInt(dado.charAt(i))) + s;")
		.append("if (--mult<1){ mult = 2; }}")
		.append("for (i=0; i<s.length; i++){")
		.append("soma = soma + parseInt(s.charAt(i));}")
		.append("soma = soma % 10;")
		.append("if (soma != 0){ soma = 10 - soma; }")
		.append("return soma.toString();}");
		return function.toString();
	}
	
	private static String getAccountDigitValidateFunction(){
		StringBuilder function = new StringBuilder("function isAccountDigitValid(account, digit){");
		function.append("if(modulo11(account, 1) == digit){return true;}")
		.append("else{ return false;}}");
		return function.toString();
	}
	
	private static String getModulo11Function(){
		StringBuilder function = new StringBuilder("function modulo11(dado, dig){");
		function.append("var mult, soma, i, n;")
		.append("var posicao  = dado.length -1;")
		.append("for(n=1; n<=dig; n++){")
		.append("soma = 0;")
		.append("mult = 2;")
		.append("for(i=dado.length-1; i>=0; i--){")
		.append("soma += (mult * parseInt(dado.charAt(i)));")
		.append("if(++mult == 8){ mult = 2;}}")
		.append("dado += ((soma * 10) % 11) % 10;}")
		.append("return dado.substr(dado.length-dig, dig);}");
		return function.toString();
	}
	
	private static String getCpfCnpjValidateFunction(){
		StringBuilder function = new StringBuilder("function isCpfCnpjValid(cpfCnpj){");
		function.append("if(cpfCnpj.length > 11){ return isCnpjValid(cpfCnpj); }")
		.append("else{ return isCpfValid(cpfCnpj); }}");
		return function.toString();
	}
	
	private static String getCpnjValidateFunction(){
		StringBuilder function = new StringBuilder("function isCnpjValid(cnpj){");
		function.append("var numeros, digitos, soma, i, resultado, pos, tamanho, digitos_iguais, cnpj = c.value.replace(/\\D+/g, '');")
		.append("digitos_iguais = 1;")
		.append("if (cnpj.length != 14){ return false; }")
		.append("for (i = 0; i < cnpj.length - 1; i++){")
		.append("if (cnpj.charAt(i) != cnpj.charAt(i + 1)){")
		.append("digitos_iguais = 0;")
		.append("break;}}")
		.append("if (!digitos_iguais){")
		.append("tamanho = cnpj.length - 2;")
		.append("numeros = cnpj.substring(0,tamanho);")
		.append("digitos = cnpj.substring(tamanho);")
		.append("soma = 0;")
		.append("pos = tamanho - 7;")
		.append("for (i = tamanho; i >= 1; i--){")
		.append("soma += numeros.charAt(tamanho - i) * pos--;")
		.append("if (pos < 2){ pos = 9; }}")
		.append("resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;")
		.append("if (resultado != digitos.charAt(0)){ return false; }")
		.append("tamanho = tamanho + 1;")
		.append("numeros = cnpj.substring(0,tamanho);")
		.append("soma = 0;")
		.append("pos = tamanho - 7;")
		.append("for (i = tamanho; i >= 1; i--){")
		.append("soma += numeros.charAt(tamanho - i) * pos--;")
		.append("if (pos < 2) { pos = 9; }}")
		.append("resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;")
		.append("if (resultado != digitos.charAt(1)){ return false; }")
		.append("else{ return true;}")
		.append("}else{return false; }}");
		return function.toString();
	}
	
	private static String getCpfValidateFunction(){
		StringBuilder function = new StringBuilder("function isCpfValid(cpf){");
		function.append("cpf = padLeft('0', 11, cpf);")
		.append("var numeros, digitos, soma, i, resultado, digitos_iguais;")
		.append("digitos_iguais = 1;")
		.append("if (cpf.length < 11){ return false; }")
		.append("for (i = 0; i < cpf.length - 1; i++){")
		.append("if (cpf.charAt(i) != cpf.charAt(i + 1)){")
		.append("digitos_iguais = 0;")
		.append("break; }}")
		.append("if (!digitos_iguais){")
		.append("numeros = cpf.substring(0,9);")
		.append("digitos = cpf.substring(9);")
		.append("soma = 0;")
		.append("for (i = 10; i > 1; i--){")
		.append("soma += numeros.charAt(10 - i) * i;}")
		.append("resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;")
		.append("if (resultado != digitos.charAt(0)){ return false; }")
		.append("numeros = cpf.substring(0,10);")
		.append("soma = 0;")
		.append("for (i = 11; i > 1; i--){")
		.append("soma += numeros.charAt(11 - i) * i;}")
		.append("resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;")
		.append("if (resultado != digitos.charAt(1)){ return false;}")
		.append("return true;")
		.append("}else{ return false;}}");

		return function.toString();
	}
	
	private static void loadBaseFunctions(ScriptEngine engine) throws ScriptException{
		engine.eval(functions.toString());
	}
	
	private static String isEmpty() {
		return "function isEmpty(inputStr) { return ( null == inputStr || \"\" == inputStr )}";
	}

	private static String notEmpty() {
		return "function notEmpty(inputStr) { return ( null != inputStr && \"\" != inputStr )}";
	}
	
	private static String getPadLeftFunction() {
		StringBuilder function = new StringBuilder();
		function.append("function padLeft(paddingChar, length, text) {")
		.append("return Array(length-String(text).length+1).join(paddingChar||'0')+text}");
		
		return function.toString();
	}
	
	public static String getIsCMC7Part1Valid() {
		StringBuilder function = new StringBuilder("function isCMC7Part1Valid(part, cmc7){");
		function.append("var regexValida = \"\\\\d{8}\";")
		.append("var regexInvalida = \"[0]{8}\";")
		.append("if (part.match(new RegExp(regexInvalida)) != null){ return false; }")
		.append("if (part.match(new RegExp(regexValida))){")
		.append("var grupo1 = cmc7.substring(0,7);")
		.append("return (modulo10(grupo1) == cmc7.substring(18,19)); }")
		.append("return false; }");
		
		return function.toString();
	}
	
	public static String getIsCMC7Part2Valid() {
		StringBuilder function = new StringBuilder("function isCMC7Part2Valid(part, cmc7){");
		function.append("var regexValida = \"\\\\d{10}\";")
		.append("var regexInvalida = \"[0]{10}\";")
		.append("if (part.match(new RegExp(regexInvalida)) != null){ return false; }")
		.append("if (part.match(new RegExp(regexValida))){")
		.append("return (modulo10(part) == cmc7.substring(7,8)); }")
		.append("return false; }");
		
		return function.toString();
	}
	
	public static String getIsCMC7Part3Valid() {
		StringBuilder function = new StringBuilder("function isCMC7Part3Valid(part){");
		function.append("var regexValida = \"\\\\d{12}\";")
		.append("var regexInvalida = \"[0]{12}\";")
		.append("if (part.match(new RegExp(regexInvalida)) != null){ return false; }")
		.append("if (part.match(new RegExp(regexValida))){")
		.append("var grupo1 = part.substring(1,11);")
		.append("return (modulo10(grupo1) == part.substring(11,12)); }")
		.append("return false; }");
		
		return function.toString();
	}
}