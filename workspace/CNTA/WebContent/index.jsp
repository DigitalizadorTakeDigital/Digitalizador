<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="js/jquery-3.3.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="css/estilo.css">
<!--  
<meta http-equiv="Refresh" content="1;URL=arquivosCaptura/<%=request.getParameter("nmArq")%>">
-->
<title>Digitalizador</title>
</head>

<body>
	<%
		
	%>
	<div>

		<div id="conteudo-esquerda">
			<img id="doc" src="images/Bradesco1.jpg" class="imagemBrad" />
		</div>

		<div id="conteudo-direita">

			<div class="conjuntoTitulos">
				<h1>Digitalizador</h1>
				
				<h2>Componente de Digitaliza��o da Bradesco Seguros</h2>

        </div>
        <div>     			
				
					<h3>Instru��es para Digitaliza��o:</h3>
				
			
					Ap�s o download autom�tico, <strong>Salvar</strong> e/ou <strong>Abrir</strong> o arquivo. 
				
				
				<p style="color: red;">
					Se o download autom�tico n�o for efetuado, 
						<INPUT TYPE="button" class="btnAtualizar" style="text-decoration: none;"
				VALUE="clique aqui" onClick='parent.location="javascript:location.reload()"'>
				</p>
				
			</div>

			<div class="italico">
			 
			 <strong>Aten��o :</strong>
			 <p>				
				� necess�rio que o componente <strong>BGCC-ClientCaptura</strong> esteja
					instalado.Caso <strong>n�o</strong> esteja instalado, siga as instru��es
					abaixo:
				</p>
			</div>
			
			<h3>Instru��es para Instala��o do Digitalizador:</h3>

			<p>
				1 - Clique no bot�o <strong>"Baixar Instalador"</strong>. (Ser� feito
				o download do instalador).
			</p>
			<p>
				2 - Selecione o diret�rio onde foi feito o download e clique em <strong>"BGCC-ClientCaptura"</strong>.
			</p>
			<p>
				3 - Coloque o <strong>Usu�rio e Senha</strong> de Administrador deste computador
				para prosseguir com a instala��o.
			</p>
			<p>
				4 - Clique em <strong>"Instalar"</strong> para iniciar a instala��o.
			</p>
			<p>
				5 - Clique em <strong>Concluir</strong> para finalizar a instala��o.
			</p>
			<p>
				6 - Execute o BGCC-ClientCaptura atrav�s do <strong>.bgcc</strong>
				baixado.
			</p>


			<a id="btn_baixar" class="centerBotao" style="text-decoration: none;"
				class="tambotao" href="instalador/BGCC-ClientCaptura.exe"
				target="_blank">Baixar Instalador</a>

			
		</div>
	</div>

	<a style="display: none;" id="linkDonwload"
		href="arquivosCaptura/<%=request.getParameter("nmArq")%>"
		download="<%=request.getParameter("nmArq")%>" target="_blank">
		Donwload do Arquivo</a>

	<script type="text/javascript">
		function executarDonwload() {

			if (window.navigator.msSaveBlob) { // IE
				//alert("IE");
				
				var reader = new FileReader();
							
				
				var queryString = window.location.search;
				
				var aFileParts = [queryString];
				
				var oMyBlob = new Blob(aFileParts, {type : 'text/html'}); // the blob
				
		        navigator.msSaveBlob(oMyBlob, "<%=request.getParameter("nmArq")%>");

			} else {
				document.getElementById("linkDonwload").click();
			}

		}

		executarDonwload();
	</script>
</body>
</html>
