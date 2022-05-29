window.onload = function iniciar() {

	var nomeProduto = getURLProduto('product');

	if (nomeProduto == "CORR01") {

		montarUrlCorretor();
	} else {

		identificarAmbiente();
	}

}

function montarUrlCorretor() {

	var codSucursal = null;
	var cpfCnpj = 0;
	var nomeProduto = null;

	codSucursal = getURLSucursal('suc');

	if (codSucursal == null || codSucursal == "") {

		cpfCnpj = getURLCpfCnpj('cpfCnpj');

		encaminharURL(cpfCnpj);
	}else{
		identificarAmbiente();
	}
}

function getURLCpfCnpj(param, url) {
	param = param.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + param + "=([^&#]*)";
	var regex = new RegExp(regexS);

	if (typeof url == "undefined")
		var results = regex.exec(window.location.href);
	else
		var results = regex.exec(url);

	if (results == null) {
		return "";
	} else {
		var cpfCnpj = decodeURI(results[1]);
		return cpfCnpj;
	}
}

function getURLProduto(param, url) {
	param = param.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + param + "=([^&#]*)";
	var regex = new RegExp(regexS);

	if (typeof url == "undefined")
		var results = regex.exec(window.location.href);
	else
		var results = regex.exec(url);

	if (results == null) {
		return "";
	} else {
		var produto = decodeURI(results[1]);
		return produto;
	}
}
function getURLSucursal(param, url) {
	param = param.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + param + "=([^&#]*)";
	var regex = new RegExp(regexS);

	if (typeof url == "undefined")
		var results = regex.exec(window.location.href);
	else
		var results = regex.exec(url);

	if (results == null) {
		return "";
	} else {
		var sucursal = decodeURI(results[1]);
		return sucursal;
	}
}
function encaminharURL(cpfCnpj) {

	var url = '?product=CORR01&cpfCnpj=' + cpfCnpj;

	var url_atual = window.location.href;
	var nomePagina = "index.html";
	var origin = url_atual.split(nomePagina)[0].replace(nomePagina, "");
	var path = 'envioDocCorretor.html';
	window.location.href = origin + path + url;

}

function returnCall(status, product) {
	alert(status);
	var produto = jQuery.parseJSON(product);
	alert(produto.name);
}

function returnGccdFiav(produto, valor) {
	window.parent.postMessage(valor, '*');
}

function returnGccdFirp04(produto, valor) {
	window.parent.postMessage(valor, '*');
}

function returnGccdPccf(produto, valor) {
	var prod = JSON.stringify(produto);
	window.parent.postMessage(prod, '*');
}
function identificarAmbiente() {

	var url = window.location.href;

	// Mock de teste
	/*
	 * url =
	 * "http://wsphttp.hml.bradseg.com.br/BGCC-ClientCaptura/index.html?product=VIDA01&codigo&usuarioCaptura=m172284&numeroProposta=1234&cpf=&cnpj=&codigoCEI=1234&codigoSucursal=1234&codigoCorretor=1234&dataAssinatura=10032018&codigoProduto=1&numeroTermo=1321321321";
	 */

	// Pega o dominio da URL
	var site = url.substring(0, url.indexOf("/index.html"));

	// Limpa o Parametro ambiente da url
	var parametro = url.substring(url.indexOf("?product"), url.length).replace(
			"&ambiente=DESE", "").replace("&ambiente=INTE", "").replace(
			"&ambiente=HOMO", "").replace("&ambiente=PROD", "").replace(
			"&ambiente=", "");

	// Verifica se existe dsv no site
	if (site.indexOf("dsv") != -1) {

		parametro += "&ambiente=DESE";
	}
	// Verifica se existe hml no site
	else if (site.indexOf("hml") != -1) {

		parametro += "&ambiente=HOMO";
	} else {

		parametro += "&ambiente=PROD";
	}
	
	parametro = site + "/scanner.html" + parametro;

	// Teste com a url
	// alert(parametro);

	window.location = parametro;
}