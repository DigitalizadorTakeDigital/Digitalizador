//FUNÇÃO PREENCHE A COMBOBOX DE SEGMENTO DE ACORDO COM O DADO SELECIONADO NO TIPO DE SERVIÇO
function tipoSegmento() {
	var lista_segmentos = {
			"todosSegmentos" : [ {
				
						segmento:"854",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"840",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"849",
						segmentos : [ "2-REDE"]},
						{
						segmento:"838",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"847",
						segmentos : [ "2-REDE"]},
						{
						segmento:"841",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"850",
						segmentos : [ "2-REDE"]},
						{
						segmento:"853",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"836",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"845",
						segmentos : [ "2-REDE"]},
						{
						segmento:"835",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"844",
						segmentos : [ "2-REDE"]},
						{
						segmento:"842",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"851",
						segmentos : [ "2-REDE"]},
						{
						segmento:"839",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"848",
						segmentos : [ "2-REDE"]},
						{
						segmento:"837",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"846",
						segmentos : [ "2-REDE"]},
						{
						segmento:"843",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"852",
						segmentos : [ "2-REDE"]},
						{
						segmento:"982",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"983",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"608",
						segmentos : [ "2-REDE"]},
						{
						segmento:"607",
						segmentos : [ "2-REDE"]},
						{
						segmento:"624",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"311",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"625",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"629",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"609",
						segmentos : [ "2-REDE"]},
						{
						segmento:"630",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"626",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"612",
						segmentos : [ "2-REDE"]},
						{
						segmento:"631",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"610",
						segmentos : [ "2-REDE"]},
						{
						segmento:"627",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"628",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"611",
						segmentos : [ "2-REDE"]},
						{
						segmento:"866",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"856",
						segmentos : [ "2-REDE"]},
						{
						segmento:"855",
						segmentos : [ "2-REDE"]},
						{
						segmento:"877",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"873",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"870",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"863",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"860",
						segmentos : [ "2-REDE"]},
						{
						segmento:"865",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"861",
						segmentos : [ "2-REDE"]},
						{
						segmento:"872",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"862",
						segmentos : [ "2-REDE"]},
						{
						segmento:"874",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"876",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"869",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"817",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"868",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"859",
						segmentos : [ "2-REDE"]},
						{
						segmento:"871",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"857",
						segmentos : [ "2-REDE"]},
						{
						segmento:"864",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"867",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"858",
						segmentos : [ "2-REDE"]},
						{
						segmento:"875",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"634",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"639",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"657",
						segmentos : [ "2-REDE"]},
						{
						segmento:"658",
						segmentos : [ "2-REDE"]},
						{
						segmento:"791",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"650",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"641",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"642",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"659",
						segmentos : [ "2-REDE"]},
						{
						segmento:"644",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"660",
						segmentos : [ "2-REDE"]},
						{
						segmento:"633",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"648",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"663",
						segmentos : [ "2-REDE"]},
						{
						segmento:"357",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"646",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"645",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"653",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"661",
						segmentos : [ "2-REDE"]},
						{
						segmento:"652",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"643",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"662",
						segmentos : [ "2-REDE"]},
						{
						segmento:"666",
						segmentos : [ "2-REDE"]},
						{
						segmento:"600",
						segmentos : [ "2-REDE"]},
						{
						segmento:"615",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"602",
						segmentos : [ "2-REDE"]},
						{
						segmento:"613",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"616",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"601",
						segmentos : [ "2-REDE"]},
						{
						segmento:"605",
						segmentos : [ "2-REDE"]},
						{
						segmento:"604",
						segmentos : [ "2-REDE"]},
						{
						segmento:"984",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"370",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"244",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"603",
						segmentos : [ "2-REDE"]},
						{
						segmento:"621",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"622",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"623",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"617",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"606",
						segmentos : [ "2-REDE"]},
						{
						segmento:"647",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"636",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"655",
						segmentos : [ "2-REDE"]},
						{
						segmento:"640",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"637",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"656",
						segmentos : [ "2-REDE"]},
						{
						segmento:"651",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"632",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"654",
						segmentos : [ "2-REDE"]},
						{
						segmento:"635",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"649",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"638",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"793",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"955",
						segmentos : [ "2-REDE"]},
						{
						segmento:"973",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"972",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"974",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"970",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"951",
						segmentos : [ "2-REDE"]},
						{
						segmento:"965",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"962",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"952",
						segmentos : [ "2-REDE"]},
						{
						segmento:"956",
						segmentos : [ "2-REDE"]},
						{
						segmento:"979",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"768",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"768",
						segmentos : [ "5-SAÚDE - REEMBOLSO"]},
						{
						segmento:"968",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"959",
						segmentos : [ "2-REDE"]},
						{
						segmento:"969",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"958",
						segmentos : [ "2-REDE"]},
						{
						segmento:"967",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"978",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"957",
						segmentos : [ "2-REDE"]},
						{
						segmento:"966",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"977",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"971",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"964",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"995",
						segmentos : [ "2-REDE"]},
						{
						segmento:"981",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"980",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"961",
						segmentos : [ "2-REDE"]},
						{
						segmento:"953",
						segmentos : [ "2-REDE"]},
						{
						segmento:"947",
						segmentos : [ "2-REDE"]},
						{
						segmento:"954",
						segmentos : [ "2-REDE"]},
						{
						segmento:"949",
						segmentos : [ "2-REDE"]},
						{
						segmento:"963",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"950",
						segmentos : [ "2-REDE"]},
						{
						segmento:"948",
						segmentos : [ "2-REDE"]},
						{
						segmento:"960",
						segmentos : [ "2-REDE"]},
						{
						segmento:"975",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"912",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"911",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"915",
						segmentos : [ "2-REDE"]},
						{
						segmento:"910",
						segmentos : [ "2-REDE"]},
						{
						segmento:"913",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"928",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"927",
						segmentos : [ "2-REDE"]},
						{
						segmento:"930",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"916",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"929",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"945",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"943",
						segmentos : [ "2-REDE"]},
						{
						segmento:"944",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"946",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"920",
						segmentos : [ "3-CORPORATE"]},
						{
						segmento:"919",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"918",
						segmentos : [ "2-REDE"]},
						{
						segmento:"921",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"922",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"986",
						segmentos : [ "2-REDE"]},
						{
						segmento:"923",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"924",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"925",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"940",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"939",
						segmentos : [ "2-REDE"]},
						{
						segmento:"932",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"931",
						segmentos : [ "2-REDE"]},
						{
						segmento:"936",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"935",
						segmentos : [ "2-REDE"]},
						{
						segmento:"933",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"934",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"937",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"941",
						segmentos : [ "1-MERCADO"]},
						{
						segmento:"907",
						segmentos : [ "2-REDE"]}
						]

	};
	return lista_segmentos;
}




