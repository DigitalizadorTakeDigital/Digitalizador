package br.com.webscanner.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe responsável pela Compactação dos arquivos.
 * 
 * @author Fernando Germano
 */
public class ZipUtil {
	private static Logger logger = LogManager.getLogger(ZipUtil.class.getName());

	/**
	 * Método para compactação de arquivos que compõem um lote.
	 * 
	 * @param zipFileName - nome do arquivo compactado sem a extensão .zip
	 * @param files       - array contendo os arquivos a serem compactadas
	 * @param path        - caminho onde o arquivo compactado será gerado
	 * @throws ZipException
	 * @throws FileNotFoundException
	 */
	public static File compact(String zipFileName, ArrayList<File> files, String path)
			throws ZipException, FileNotFoundException {
		File file = new File(path, zipFileName + ".zip");
		logger.info("Iniciando compactação de arquivos para o arquivo {}", file.getAbsolutePath());

		if (!file.getParentFile().exists()) {
			throw new FileNotFoundException(
					"O caminho " + file.getParentFile().getAbsolutePath() + " não existe ou não está acessível");
		}

		ZipFile zipFile = null;
		zipFile = new ZipFile(file);
		zipFile.setRunInThread(true);

		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

		zipFile.addFiles(files, parameters);

		ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
		while (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {

			logger.info("Aguardando compactação do arquivo zip");
			logger.info("Percent Done: " + progressMonitor.getPercentDone());

			logger.info("File: " + progressMonitor.getFileName());

			switch (progressMonitor.getCurrentOperation()) {
			case ProgressMonitor.OPERATION_NONE:
				logger.info("no operation being performed");
				break;
			case ProgressMonitor.OPERATION_ADD:
				logger.info("Add operation");
				break;
			case ProgressMonitor.OPERATION_EXTRACT:
				logger.info("Extract operation");
				break;
			case ProgressMonitor.OPERATION_REMOVE:
				logger.info("Remove operation");
				break;
			case ProgressMonitor.OPERATION_CALC_CRC:
				logger.info("Calcualting CRC");
				break;
			case ProgressMonitor.OPERATION_MERGE:
				logger.info("Merge operation");
				break;
			default:
				logger.info("invalid operation");
				break;
			}

		}
		logger.info("Result: " + progressMonitor.getResult());

		if (progressMonitor.getResult() == ProgressMonitor.RESULT_ERROR) {
			// Any exception can be retrieved as below:
			if (progressMonitor.getException() != null) {
				progressMonitor.getException().printStackTrace();
			} else {
				logger.error("An error occurred without any exception");
				throw new ZipException("Erro ao compactar o arquivo.");
			}
		}

		logger.info("Arquivos compactados com sucesso.");
		return file;
	}

	public static File compactTamanhoMaximoPorZip(String zipFileName, ArrayList<File> files, String path)
			throws ZipException, FileNotFoundException {
		File file = new File(path, zipFileName + ".zip");
		logger.info("Iniciando compactação de arquivos para o arquivo {}", file.getAbsolutePath());

		if (!file.getParentFile().exists()) {
			logger.error("O caminho " + file.getParentFile().getAbsolutePath() + " não existe ou não está acessível");
			throw new FileNotFoundException(
					"O caminho " + file.getParentFile().getAbsolutePath() + " não existe ou não está acessível");
		}

		ZipFile zipFile = null;
		zipFile = new ZipFile(file);
		zipFile.setRunInThread(true);

		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

		zipFile.createZipFile(files, parameters, true, 20971520);

		ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
		while (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {

			logger.info("Aguardando compactação do arquivo zip");
			logger.info("Percent Done: " + progressMonitor.getPercentDone());

			logger.info("File: " + progressMonitor.getFileName());

			switch (progressMonitor.getCurrentOperation()) {
			case ProgressMonitor.OPERATION_NONE:
				logger.info("no operation being performed");
				break;
			case ProgressMonitor.OPERATION_ADD:
				logger.info("Add operation");
				break;
			case ProgressMonitor.OPERATION_EXTRACT:
				logger.info("Extract operation");
				break;
			case ProgressMonitor.OPERATION_REMOVE:
				logger.info("Remove operation");
				break;
			case ProgressMonitor.OPERATION_CALC_CRC:
				logger.info("Calcualting CRC");
				break;
			case ProgressMonitor.OPERATION_MERGE:
				logger.info("Merge operation");
				break;
			default:
				logger.info("invalid operation");
				break;
			}

		}
		logger.info("Result: " + progressMonitor.getResult());

		if (progressMonitor.getResult() == ProgressMonitor.RESULT_ERROR) {
			// Any exception can be retrieved as below:
			if (progressMonitor.getException() != null) {
				progressMonitor.getException().printStackTrace();
			} else {
				System.err.println("An error occurred without any exception");
				throw new ZipException("Erro ao compactar o arquivo.");
			}
		}

		logger.info("Arquivos compactados com sucesso.");
		return file;
	}

	public static File compactFolder(String zipFileName, String folder, File subDiretorioExport) throws ZipException {
		logger.info("Iniciando compactação do diretório {}.", folder);

		File file = new File(subDiretorioExport, zipFileName + ".zip");
		ZipFile zipFile = new ZipFile(file);
		zipFile.setRunInThread(true);

		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
		
		zipFile.addFolder(folder, parameters);

		ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
		while (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {

			logger.info("Aguardando compactação do arquivo zip");
			logger.info("Percent Done: " + progressMonitor.getPercentDone());

			logger.info("File: " + progressMonitor.getFileName());

			switch (progressMonitor.getCurrentOperation()) {
			case ProgressMonitor.OPERATION_NONE:
				logger.info("no operation being performed");
				break;
			case ProgressMonitor.OPERATION_ADD:
				logger.info("Add operation");
				break;
			case ProgressMonitor.OPERATION_EXTRACT:
				logger.info("Extract operation");
				break;
			case ProgressMonitor.OPERATION_REMOVE:
				logger.info("Remove operation");
				break;
			case ProgressMonitor.OPERATION_CALC_CRC:
				logger.info("Calcualting CRC");
				break;
			case ProgressMonitor.OPERATION_MERGE:
				logger.info("Merge operation");
				break;
			default:
				logger.info("invalid operation");
				break;
			}

		}
		logger.info("Result: " + progressMonitor.getResult());

		if (progressMonitor.getResult() == ProgressMonitor.RESULT_ERROR) {
			// Any exception can be retrieved as below:
			if (progressMonitor.getException() != null) {
				progressMonitor.getException().printStackTrace();
			} else {
				System.err.println("An error occurred without any exception");
				throw new ZipException("Erro ao compactar o arquivo.");
			}
		}

		logger.info("Diretório compactado com sucesso.");
		return file;
	}

	public static File compactFolderTamanhoMaximoPorZip(String zipFileName, String folder, File subDiretorioExport)
			throws ZipException {
		logger.info("Iniciando compactação do diretório {}.", folder);

		File file = new File(subDiretorioExport, zipFileName + ".zip");
		ZipFile zipFile = new ZipFile(file);
		zipFile.setRunInThread(true);

		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

		zipFile.createZipFileFromFolder(folder, parameters, true, 20971520);
		
		ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
		while (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {
			
			logger.info("Aguardando compactação do arquivo zip");
			logger.info("Percent Done: " + progressMonitor.getPercentDone());
			
			logger.info("File: " + progressMonitor.getFileName());
			
			switch (progressMonitor.getCurrentOperation()) {
            case ProgressMonitor.OPERATION_NONE:
                logger.info("no operation being performed");
                break;
            case ProgressMonitor.OPERATION_ADD:
                logger.info("Add operation");
                break;
            case ProgressMonitor.OPERATION_EXTRACT:
                logger.info("Extract operation");
                break;
            case ProgressMonitor.OPERATION_REMOVE:
                logger.info("Remove operation");
                break;
            case ProgressMonitor.OPERATION_CALC_CRC:
                logger.info("Calcualting CRC");
                break;
            case ProgressMonitor.OPERATION_MERGE:
                logger.info("Merge operation");
                break;
            default:
                logger.info("invalid operation");
                break;
            }
			
		}
		logger.info("Result: " + progressMonitor.getResult());

        if (progressMonitor.getResult() == ProgressMonitor.RESULT_ERROR) {
            // Any exception can be retrieved as below:
            if (progressMonitor.getException() != null) {
                progressMonitor.getException().printStackTrace();
            } else {
                System.err.println("An error occurred without any exception");
                throw new ZipException("Erro ao compactar o arquivo.");
            }
        }

		logger.info("Diretório compactado com sucesso.");
		return file;
	}

	public static List<String> listSplitFile(File file) throws ZipException {
		ZipFile zipFile = new ZipFile(file);
		return zipFile.getSplitZipFiles();
	}
}
