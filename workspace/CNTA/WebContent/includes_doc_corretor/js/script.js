function listaSucursal(e) {

	document.querySelector("#sucursal").innerHTML = '';
	var sucursal_select = document.querySelector("#sucursal");

	var lista_sucursais = lerSucursal();
	var num_sucursal = lista_sucursais.regioes.length;
	var suc_index = -1;

	for (var x = 0; x < num_sucursal; x++) {
		if (lista_sucursais.regioes[x].sigla == e) {
			suc_index = x;
		}
	}
	var sucursal_select = document.getElementById("sucursal");
	if (suc_index != -1) {
		lista_sucursais.regioes[suc_index].sucursais
				.forEach(function(sucursal) {
					var suc_opts = document.createElement('option');
					var codSucursal = (sucursal.split("-"))[0];
					suc_opts.setAttribute('value', codSucursal);
					suc_opts.innerHTML = sucursal;
					sucursal_select.appendChild(suc_opts);
				});
		sucursal_select.disabled = false;
	} else {
		document.querySelector("#sucursal").innerHTML = '';
		sucursal_select.disabled = true;
	}
}

function listaPapel() {
	
	var comboPapeis = document.getElementById("papel");
	var comboSegmento = document.getElementById("segmento").value;

	var zero = document.createElement("option");
	zero.value = ""
	zero.text = "Selecione"
	
	var um = document.createElement("option");
	um.value = "1"
	um.text = "Angariador"

	var dois = document.createElement("option");
	dois.value = "2"
	dois.text = "Angariador não receb."

	var tres = document.createElement("option");
	tres.value = "3"
	tres.text = "Assessoria"

	var quatro = document.createElement("option");
	quatro.value = "4"
	quatro.text = "Corretor"

	var cinco = document.createElement("option");
	cinco.value = "5"
	cinco.text = "Funcionário"

	var seis = document.createElement("option");
	seis.value = "6"
	seis.text = "Imobiliária"

	var sete = document.createElement("option");
	sete.value = "7"
	sete.text = "Master"

	var oito = document.createElement("option");
	oito.value = "8"
	oito.text = "Pro Labore (Convênio)"

	var nove = document.createElement("option");
	nove.value = "9"
	nove.text = "Repres. Segs"

	while (comboPapeis.length){
		comboPapeis.remove(0);
	}


	if (comboSegmento == 1){
		comboPapeis.disabled = false;
		comboPapeis.add(zero, comboPapeis.options[""]);
		comboPapeis.add(quatro, comboPapeis.options[4]); //CORRETOR
		comboPapeis.add(tres, comboPapeis.options[3]); //ASSESSORIA
		comboPapeis.add(oito, comboPapeis.options[8]); //PRO LABORE
		comboPapeis.add(seis, comboPapeis.options[6]); //IMOBILIARIA
	}else if (comboSegmento == 2){
		comboPapeis.disabled = false;
		comboPapeis.add(zero, comboPapeis.options[""]);
		comboPapeis.add(quatro, comboPapeis.options[4]);
		comboPapeis.add(sete, comboPapeis.options[7]);
		comboPapeis.add(cinco, comboPapeis.options[5]);
	} else if (comboSegmento == 3){
		comboPapeis.disabled = false;
		comboPapeis.add(zero, comboPapeis.options[""]);
		comboPapeis.add(quatro, comboPapeis.options[4]);
		comboPapeis.add(sete, comboPapeis.options[7]);
		comboPapeis.add(oito, comboPapeis.options[8]);
		comboPapeis.add(tres, comboPapeis.options[3]);
	}

}


function listaSegmentos(e) {

	document.querySelector("#segmento").innerHTML = '';
	var segmento_select = document.querySelector("#segmento");

	var servico_select = document.getElementById("tipoServico");

	var lista_segmentos = tipoSegmento();
	var num_seg = lista_segmentos.todosSegmentos.length;
	var seg_index = -1;

	for (var x = 0; x < num_seg; x++) {
		if (lista_segmentos.todosSegmentos[x].segmento == e) {
			seg_index = x;
		}
	}
	var segmentos_select = document.getElementById("segmento");
	if (seg_index != -1) {
		lista_segmentos.todosSegmentos[seg_index].segmentos.forEach(function(
				todosSegmentos) {

			var seeg = document.createElement('option');
			var CodSegmentos = (todosSegmentos.split("-"))[0];
			seeg.setAttribute('value', CodSegmentos)
			seeg.innerHTML = todosSegmentos;
			segmentos_select.appendChild(seeg);
		});
		segmentos_select.disabled = true;
		servico_select.disabled = false;
		document.getElementById("tipoServico").selectedIndex = 0;

	} else {
		document.querySelector("#segmento").innerHTML = '';
		segmentos_select.disabled = true;

	}
}

// FUNCAO PARA FORMATAR CAMPO CPF/CNPJ
function formatarCampo(valor) {
	if (valor.length <= 11) {
		valor = mascaraCpf(valor);
	} else {
		valor = mascaraCnpj(valor);
	}
	return valor;
}

function retirarArrobaEmail() {

	var recebeEmail = document.getElementById("email");
	var emailnovo = recebeEmail.value.replace("@", "%40");

	// document.getElementById("email").value = emailnovo;
	return emailnovo;
}

function retirarMaskCnpjCpf() {
	var cnpjCpf = document.getElementById("cpfCnpj");
	cnpjCpf.value = cnpjCpf.value.replace(/[\sa-zA-Z\.\-\+\*\/\,]/g, "");

}

function mascaraCpf(valor) {
	return valor.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/g, "\$1.\$2.\$3\-\$4");
}
function mascaraCnpj(valor) {
	return valor.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/g,
			"\$1.\$2.\$3\/\$4\-\$5");
}

function iniciar() {

	// formatarCampo(this)
	getURLCpfCnpj('cpfCnpj');
	getURLToken('token');
}

// FUNCAO PARA PEGAR DADOS DA URL
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

		document.getElementById('cpfCnpj').value = formatarCampo(decodeURI(results[1]));
	}
}

function getURLToken(param, url) {
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
		document.getElementById('token').value = formatarCampo(decodeURI(results[1]));

	}
}

function msgManutencao() {
	var msg = document.getElementById('mManutencao');
	var servico = document.getElementById('tipoServico');
	if (servico.value == 0) {
		msg.innerText = "";
	} else if (servico.value == 3 ) {

		msg.innerText = "Caro Corretor, ao realizar a manutenção do seu endereço comercial na sucursal selecionada,"
				+ " as demais sucursais onde você possui cadastro NÃO sofrerão alteração."
				+ " É necessário selecionar separadamente cada sucursal que você deseja realizar essa alteração";
	} else if (servico.value == 4) {

		msg.innerText = "Caro Corretor, ao realizar a manutenção dos seus dados bancários na sucursal selecionada,"
				+ " as demais sucursais onde você possui cadastro NÃO sofrerão alteração."
				+ " É necessário selecionar separadamente cada sucursal que você deseja realizar essa alteração";
	}else {
		msg.innerText = "Caro Corretor, ao realizar esse tipo de manutenção na sucursal selecionada,"
				+ " as informações das demais sucursais onde você possui cadastro também sofrerão alteração.";
	}
}

function montaURL() {

	var valorToken = document.getElementById("token").value;
	var valorCpfCnpj = document.getElementById("cpfCnpj").value;
	var valorEmail = retirarArrobaEmail();
	var valorTel = document.getElementById("tel").value;
	var valorCel = document.getElementById("cel").value;
	var valorRegional = document.getElementById("regional").value;
	var valorSucursal = document.getElementById("sucursal").value;
	var valorTipoServico = document.getElementById("tipoServico").value;
	var valorSegmento = document.getElementById("segmento").value;
	var tipoPessoa = "";
	
	var papel = document.getElementById("papel").value;
	
	var docsPessoa = document.getElementsByName('tipoPessoa');
	for (var i=0;i<docsPessoa.length;i++){
	  if ( docsPessoa[i].checked ) {
		  tipoPessoa = docsPessoa[i].value;
	  }
	}
	

	var url = '?product=CORR01' + '&serv=' + valorTipoServico + '&cpfCnpj='
			+ valorCpfCnpj + '&email=' + valorEmail + '&tel=' + valorTel + '&cel=' + valorCel
			+ '&rgn=' + valorRegional + '&suc=' + valorSucursal + '&seg=' + valorSegmento
			+ '&tipoPessoa=' + tipoPessoa + '&papel=' + papel + '&token=' + valorToken;

	var url_atual = window.location.href;
	var nomePagina = "envioDocCorretor.html";
	var origin = url_atual.split(nomePagina)[0].replace(nomePagina, "");
	var path = 'index.html';
	window.open(origin + path + url);
	location.reload();	return false;
}

function validacaoEmail(field) {
	var vEmail = document.getElementById('validaEmail');

	usuario = field.value.substring(0, field.value.indexOf("@"));
	dominio = field.value.substring(field.value.indexOf("@") + 1,
			field.value.length);

	if ((field.length == 0)
			|| ((usuario.length >= 1) && (dominio.length >= 3)
					&& (usuario.search("@") == -1)
					&& (dominio.search("@") == -1)
					&& (usuario.search(" ") == -1)
					&& (dominio.search(" ") == -1)
					&& (dominio.search(".") != -1)
					&& (dominio.indexOf(".") >= 1) && (dominio.lastIndexOf(".") < dominio.length - 1))) {
		vEmail.style.display = "none";
	} else {
		/* alert("E-mail invalido"); */

		vEmail.style.display = "inline";
		field.value = '';
	}

}

function limpaSegmento(){
 document.querySelector("#segmento").innerHTML = "";
}

function limpaSelecoes(){
	limpaTipoPessoa();
	var servico = document.getElementById("tipoServico");
	tipoServico.selectedIndex = 0;
	
}

function limpaDocs(){
	document.querySelector("#documentosNecessarios").innerHTML = "";
}

function limpaTipoPessoa(){
	var pessoa = document.getElementsByName('tipoPessoa');
	for(var i=0; i<pessoa.length;i++){
	pessoa[i].checked = false;
	}
	
	var pessoa = document.getElementById("papel");
	pessoa.selectedIndex = 0;
	pessoa.disabled = true;
	limpaDocs();
}

function validaCampos(){
	var form = document.forms.dados;
	var confere = 0;
	
	for(i=0; i<form.elements.length;i++){
			if(form.elements[i].required == true){
			
				if(form.elements[i].value == ''){
					dados.elements[i].style.border = "1px solid #ff0000";
					confere++;
				}
				else{
				dados.elements[i].style.border = "thin solid #CCC";
				}
	       }
	}
	
	if (confere == 0){
		montaURL();
	}
	else{
		return false;
	}
	

}
