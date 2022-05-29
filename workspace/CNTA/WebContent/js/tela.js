//variaveis
var fCnpj = document.getElementById('fCnpj');
var fplaca = document.getElementById('fplaca');
var fsinistro = document.getElementById('fsinistro');

//mascara da placa
function maskPlaca2() {
    $("#fplaca").mask("SSS-9999");

}

function validarPlaca(valorPlaca){
	
	if(valorPlaca == ""){
		
		alert('Placa está vazia');
		
		return false;
		
	}
	
}

//mascara da placa
function maskCnpj2() {
    $('#fCnpj').mask('00.000.000/0000-00', {
        reverse: true
    });
}


//mascaras
function maskCnpj() {
    var fCnpj = document.getElementById('fCnpj');
    var fCnpj2 = 0;
    if (fCnpj.value.length <= 14) {
        fCnpj2 = fCnpj.value.toString();
        fCnpj.value = fCnpj2.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/g, "\$1.\$2.\$3/\$4-\$5");
    } else {
        //fCnpj.value = fCnpj.value.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/g, "\$1.\$2.\$3/\$4-\$5");
        //alert("ok2");
    }
}

//Sinistro	
function maskSinistro(e) {
    var tecla = (window.event) ? event.keyCode : e.which;
    if ((tecla > 47 && tecla < 58)) return true;
    else {
        if (tecla == 8 || tecla == 0) return true;
        else return false;
    }
}

function iniciaGccd() {

    var fCnpj = document.getElementById('fCnpj');
    var fplaca = document.getElementById('fplaca');

    var valorPlaca = fplaca.value.replace("-", "");


    var fsinistro = document.getElementById('fsinistro');
    var ftipo = document.getElementById('ftipo');
    var fCnpj3 = fCnpj.value.replace(/[^\d]+/g, '');

    if (fCnpj.value == "" || valorPlaca == "" || fsinistro.value == "" || ftipo.value == "null") {

        alert("Todos os campos devem ser preenchidos.");
        return false;
    }

    if (valida_cnpj(document.getElementById('fCnpj').value) == false) {

        alert('CNPJ invÃ¡lido');
        return false;
    }

    if (validarPlaca(valorPlaca)== false) {
        alert("Placa Invalída");
        return false;
    }

    //http://10.243.145.43/GCCD_SEGUROS/index.html?product=REEM01&cpfCnpj=12345678901&tipo=NF&placa=ekj2002&sinistro=122212312

    //alterar aqui
    var origin = 'http://10.243.145.43';
    var pathGccd = '/GCCD_SEGUROS/index.html';
    var parameters = '?product=REEM01&cpfCnpj=' + fCnpj3 + '&tipo=' + ftipo.value + '&placa=' + valorPlaca + '&sinistro=' + fsinistro.value;

    window.open(origin + pathGccd + parameters, 'REEM01', 'width=800,height=600,top=0,left=0,screenX=0,screenY=0,status=no,scrollbars=no,toolbar=yes,resizable=yes,menubar=no,location=no').resizeTo(800, 600);


}