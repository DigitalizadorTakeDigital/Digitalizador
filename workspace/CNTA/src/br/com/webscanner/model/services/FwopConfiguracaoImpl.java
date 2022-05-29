package br.com.webscanner.model.services;

import br.com.bradesco.fwop.cliente.api.v1.FwopConfiguracao;

public class FwopConfiguracaoImpl implements FwopConfiguracao {
	private String serviceBaseUrl;

	public FwopConfiguracaoImpl(String serviceBaseUrl) {
		this.serviceBaseUrl = serviceBaseUrl;
	}

	@Override
	public String getFwopJndiInitialContextFactoryClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFwopJndiQueueConnectionFactoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFwopJndiUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFwopRequestJndiQueueName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFwopResponseJndiQueueName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFwopWebServiceBaseUrl() {
		return serviceBaseUrl;
	}

	@Override
	public String getFwopWebServiceNamespaceUri() {
		// TODO Auto-generated method stub
		return null;
	}

}
