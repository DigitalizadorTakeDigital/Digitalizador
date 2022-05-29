package br.com.webscanner.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandExecution {
	private static Logger logger = LogManager.getLogger(CommandExecution.class
			.getName());

	/**
	 * Executa um comando utilizando a Runtime
	 * 
	 * @param command
	 *            - Comando que será executado
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void execute(String command) throws IOException,
			InterruptedException {
		logger.info("Iniciando execução do comando: {}", command);
		Process process = Runtime.getRuntime().exec(command);
		process.waitFor();

		InputStream errorStream = process.getErrorStream();
		InputStream infoStream = process.getInputStream();

		if (errorStream.available() != 0) {
			logger.warn(getLog(errorStream));
		} else {
			logger.info(getLog(infoStream));
		}
	}

	/**
	 * Executa um comando utilizando a Runtime
	 * 
	 * @param command
	 *            - Comando que será executado
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void execute(String... commands) throws IOException,
			InterruptedException {
		logger.info("Iniciando execução do comando: {}",
				Arrays.toString(commands));
		Process process = Runtime.getRuntime().exec(commands);
		process.waitFor();

		InputStream errorStream = process.getErrorStream();
		InputStream infoStream = process.getInputStream();

		if (errorStream.available() != 0) {
			logger.warn(getLog(errorStream));
		} else {
			logger.info(getLog(infoStream));
		}
	}

	/**
	 * Executa um comando utilizando a Runtime
	 * 
	 * @param wait
	 *            - Deve aguardar o processo terminar
	 * @param command
	 *            - Comando que será executado
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void execute(boolean wait, String commands)
			throws IOException, InterruptedException {
		logger.info("Iniciando execução do comando: {}", commands);
		Process process = Runtime.getRuntime().exec(commands);

		if (wait) {
			process.waitFor();
			InputStream errorStream = process.getErrorStream();
			InputStream infoStream = process.getInputStream();

			if (errorStream.available() != 0) {
				logger.warn(getLog(errorStream));
			} else {
				logger.info(getLog(infoStream));
			}
		}
	}
	
	/**
	 * Executa um comando utilizando a Runtime
	 * 
	 * @param wait
	 *            - Deve aguardar o processo terminar
	 * @param command
	 *            - Comando que será executado
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Process executeAssicrono(String commands)
			throws IOException, InterruptedException {
		logger.info("Iniciando execução do comando: {}", commands);
		Process process = Runtime.getRuntime().exec(commands);

		return process;
	}
	

	/**
	 * Recupera o output de um Stream
	 * 
	 * @param is
	 *            Stream de execução
	 * @return String contento o valor do output
	 * @throws IOException
	 */
	public static String getLog(InputStream is) throws IOException {
		StringWriter sw = null;
		try {
			sw = new StringWriter();
			int i = -1;
			while ((i = is.read()) != -1) {
				sw.write(i);
			}
			return sw.toString().trim();
		} finally {
			sw.close();
			is.close();
		}
	}

	public static String executarCmdRetorno(String comando) {

		BufferedReader input;

		String result = "";

		StringBuffer cmdOut = new StringBuffer();
		String lineOut = null;
		int numberOfOutline = 0;

		try {

			Process p = Runtime.getRuntime().exec(comando);

			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			while ((lineOut = input.readLine()) != null) {
				if (numberOfOutline > 0) {
					cmdOut.append("\n");
				}
				cmdOut.append(lineOut);
				numberOfOutline++;
			}
			result = cmdOut.toString();

			if (result.isEmpty()) {

				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(p.getErrorStream()));

				String s = null;
				while ((s = stdError.readLine()) != null) {
					logger.error(s);
				}

				return "Erro na execucao do comando";
			}

			input.close();
		} catch (IOException e) {

			return "Erro: " + e.getMessage();
		}
		return result;
	}
}
