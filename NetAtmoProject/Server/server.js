var http = require('http')
	, domain = require('domain')
  	, serverDomain = domain.create()
  	, fs = require('fs');
var https = require('https');
var url  = require('url')
var fs = require('fs');
var obst = new Array();
var gemuese = new Array();
var salat = new Array();
var pollen = new Array();

var feelgood_temp = 20.1;
var feelgood_humidity = 50;
var feelgood_co2 = 650;
var flow_air_pressure_indoor 	= [];
var flow_air_pressure_outdoor 	= [];
var flow_co2			 	= [];
var flow_humidity_indoor	= [];
var flow_humidity_outdoor	= [];
var flow_noise				= [];
var flow_temperatur_indoor 	= [];
var flow_temperatur_outdoor	= [];
var flow_time_indoor = 0;
var flow_time_outdoor = 0;
var RequestCounter = 0;
var RequestCounterForMeasurment = 0;
var accessToken = "";
var expTimer = 10800;
var resfreshToken = "";
var dataTimestampOfRequest = "";
var windMessage = "";

retrieveAccessTokenInital();
//-------------------PARSE CSV DATA --------------
var csv = require('csv');

var handler_obst = function(error, content){
   
    if (error){
      console.log(error);
    }
    else{
      csv.parse(content,{delimiter: ';'}, function(err, output){
 		 obst = output;
		});
    } 
  }
 var handler_gemuese = function(error, content){
   
    if (error){
      console.log(error);
    }
    else{
     csv.parse(content, {delimiter: ';',comment: '#'}, function(err, output){
 		 gemuese = output;
 		 
		});
    }
  }
 var handler_salat = function(error, content){
   
    if (error){
      console.log(error);
    }
    else{
      csv.parse(content, {delimiter: ';',comment: '#'}, function(err, output){
 		 salat = output;

		});
    }
  }
var handler_pollen = function(error, content){
   
    if (error){
      console.log(error);
    }
    else{
      csv.parse(content, {delimiter: ';',comment: '#'}, function(err, output){
 		 pollen = output;

		});
    }
  }
fs.readFile('Server/obst_gemuese/obst.csv','utf-8', handler_obst);
fs.readFile('Server/obst_gemuese/gemuese.csv','utf-8', handler_gemuese);
fs.readFile('Server/obst_gemuese/salat.csv','utf-8', handler_salat);
fs.readFile('Server/pollen/pollen.csv','utf-8', handler_pollen);
//-------------------Create Server --------------
http.createServer(function (req, res) {
  
  var http = require("http");
    urlOfOpenHab = "http://192.168.33.33:8080/rest/items";

	// get is a simple wrapper for request()
	// which sets the http method to GET
	if(req.url=='/universal/index.html' || req.url=='/universal/') {
		res.setHeader("Content-Type", "text/html");
		var runningPath = 'universal/'
			    var pathname = 'index.html';
			  	//console.log(url.format(pathname));
				//css(res,runningPath+"\\"+url.format(pathname))
				var handler = function(error, content){

				    if (error){
				      console.log(error);
				    }
				    else{
				      res.write(content);
				      res.end()
				    }
				    //if(count == 1){
				    //	response.end();
				   // }
				    
				}
			  res.setHeader("Content-Type", "text/html");
			  fs.readFile(runningPath+"\\"+url.format(pathname), handler);


  	 }
	if(req.url=='/index.html' || req.url=='/') {
		
		var runningPath = 'sabine/'
			    var pathname = 'index.html';
			  	//console.log(url.format(pathname));
				//css(res,runningPath+"\\"+url.format(pathname))
				var handler = function(error, content){

				    if (error){
				      console.log(error);
				    }
				    else{
				      res.write(content);
				      res.end()
				    }
				    //if(count == 1){
				    //	response.end();
				   // }
				    
				}
			  res.setHeader("Content-Type", "text/html");
			  fs.readFile(runningPath+"\\"+url.format(pathname), handler);


  	 }else
		{

			//if(req.url=='/update' ){
			if(req.url.indexOf('/update') > -1  ){
				res.setHeader('Content-Type','application/json',"charset=utf-8");
				//res.header("Content-Type", "application/json"; );
				var http = require("http");
			     urlOfOpenHab = "http://192.168.33.33:8080/rest/items";
				var request = http.get(urlOfOpenHab, function (response) {	
				    // data is streamed in chunks from the server
				    // so we have to handle the "data" event    
				    var buffer = "", 
				        data,
				        route;

				    response.on("data", function (chunk) {
				        buffer += chunk;
				    }); 

				    response.on("end", function (err) {			
						var parseString = require('xml2js').parseString;
						var xml = buffer
						var dataArray = new Array();
						var returnObject = {
							"airquality": {"indoor":0,"percentage" : 0, "origin" : new Array(),"message":"Temp Message"},
							"air_pressure": {"indoor" : 0,"percentage" : 0,"unit" : "mb","message":windMessage,"flow_time_indoor" : flow_time_indoor, "flow_indoor": new Array()},
							"co2": {"indoor" : 0,"percentage" : 0,"unit" : "ppm","flow_time_indoor" : flow_time_indoor, "flow_indoor": new Array()},
							"food": [],
							"humidity": {"indoor" : 0, "unit" : "%","flow_time_indoor" : flow_time_indoor,"flow_indoor": new Array(), "outdoor" : 0,"flow_time_outdoor" : flow_time_outdoor, "flow_outdoor": new Array()},
						    "noise": {"indoor" : 0,"percentage" : 0,"origin" : new Array(),"unit" : "dB","origin": new Array(),"flow_time_indoor" : flow_time_indoor,"flow_indoor": new Array()},
						    "pollen": new Array(),
						    "device" : {"battery": 0,"wifiState" : 0,"alt":0,"fstate": 0,"lat": 0,"lng": 0},
						    "stresslevel": {"indoor":0,"percentage" : 0,"origin" : new Array(),"message":"Temp Message"},
						    "temperature": {"indoor" : 0, "unit" : "°C","flow_time_indoor" : flow_time_indoor,"flow_indoor": new Array(), "outdoor" : 0,"flow_time_outdoor" : flow_time_outdoor, "flow_outdoor": new Array()},
						}
						parseString(xml, function (err, result) {
							
							if(req.url == '/update/test/stress'){
								result = JSON.parse(fs.readFileSync('smartHomeServer/test/stress.json','utf8'));
							}
							if(req.url == '/update/test/airquality-co2'){
								result = JSON.parse(fs.readFileSync('smartHomeServer/test/airquality-co2.json','utf8'));
							}
							if(req.url == '/update/test/extrem-carbon'){
								result = JSON.parse(fs.readFileSync('smartHomeServer/test/extrem-carbon.json','utf8'));
							}
							if(req.url == '/update/test/extrem-humidity'){
								result = JSON.parse(fs.readFileSync('smartHomeServer/test/extrem-humidity.json','utf8'));
							}
							if(req.url == '/update/test/extrem-noise'){
								result = JSON.parse(fs.readFileSync('smartHomeServer/test/extrem-noise.json','utf8'));
							}
							if(req.url == '/update/test/extrem-temp'){
								result = JSON.parse(fs.readFileSync('smartHomeServer/test/extrem-temp.json','utf8'));
							}
							
							for (var it = 0; it < result.items.item.length; it++){
								var name = result.items.item[it].name[0];
								if(name.indexOf("Netatmo_Indoor_Temperature") > -1){
									returnObject.temperature.indoor = Number(result.items.item[it].state[0]);
									returnObject.temperature.flow_indoor = flow_temperatur_indoor;
								}
								if(name.indexOf("Netatmo_Outdoor_Temperature") > -1){
									returnObject.temperature.outdoor = Number(result.items.item[it].state[0]);
									returnObject.temperature.flow_outdoor = flow_temperatur_outdoor;
								}
								if(name.indexOf("Netatmo_Indoor_Pressure") > -1){
									returnObject.air_pressure.indoor = Number(result.items.item[it].state[0]);
									returnObject.air_pressure.flow_indoor = flow_air_pressure_indoor;
								}
								if(name.indexOf("Netatmo_Indoor_Noise") > -1){
									returnObject.noise.indoor = Number(result.items.item[it].state[0]);
									returnObject.noise.flow_indoor = flow_noise;
								}
								if(name.indexOf("Netatmo_Indoor_CO2") > -1){
									returnObject.co2.indoor = Number(result.items.item[it].state[0]);
									returnObject.co2.flow_indoor = flow_co2;
								}
								if(name.indexOf("Netatmo_Indoor_Humidity") > -1){
									returnObject.humidity.indoor = Number(result.items.item[it].state[0]);
									returnObject.humidity.flow_indoor = flow_humidity_indoor;
								}
								if(name.indexOf("Netatmo_Outdoor_Humidity") > -1){
									returnObject.humidity.outdoor = Number(result.items.item[it].state[0]);
									returnObject.humidity.flow_outdoor = flow_humidity_outdoor;
								}
								if(name.indexOf("Netatmo_Indoor_latitude") > -1){
									returnObject.device.lng = Number(result.items.item[it].state[0]);
								}
								if(name.indexOf("Netatmo_Indoor_longitude") > -1){
									returnObject.device.lat = Number(result.items.item[it].state[0]);
								}
								if(name.indexOf("Netatmo_Indoor_altitude") > -1){
									returnObject.device.alt = Number(result.items.item[it].state[0]);
								}
								if(name.indexOf("Netatmo_Indoor_wifi") > -1){
									returnObject.device.wifiState = Number(result.items.item[it].state[0]);
								}
								if(name.indexOf("Netatmo_Outdoor_Batteryvp") > -1){
									returnObject.device.battery = Number(result.items.item[it].state[0]);
								}
								if(name.indexOf("Netatmo_Outdoor_Rfstatus") > -1){
									returnObject.device.fstate = Number(result.items.item[it].state[0]);
								}

							}
							//---------------------FOOD----------------------
								

							var d = new Date();
							var n = d.getMonth();
							n = n+1;
							console.log("month: "+n);
							for (var obstIndex = 1; obstIndex < obst.length-1; obstIndex++){
								//console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
								if(obst[obstIndex][n] == 'ja' ||obst[obstIndex][n] == '(ja)' ){
									var currentObst = obst[obstIndex][0];
									var pushObject = new Object();

									pushObject[currentObst] = "http://www.chefkoch.de/rs/s0/"+obst[obstIndex][0]+"/Rezepte.html";
									returnObject.food.push(pushObject);
								}
							}
							for (var gemueseIndex = 1; gemueseIndex < gemuese.length-1; gemueseIndex++){
								//console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
								if(gemuese[gemueseIndex][n] == 'ja' ||gemuese[gemueseIndex][n] == '(ja)' ){
									var currentGemuese = gemuese[gemueseIndex][0];
									var pushObject = new Object();
									pushObject[currentGemuese] = "http://www.chefkoch.de/rs/s0/"+gemuese[gemueseIndex][0]+"/Rezepte.html" ;
									returnObject.food.push(pushObject);
								}
							}
							for (var salatIndex = 1; salatIndex < salat.length-1; salatIndex++){
								//console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
								if(salat[salatIndex][n] == 'ja' ||salat[salatIndex][n] == '(ja)' ){
									var currentSalat = salat[salatIndex][0];
									var pushObject = new Object()
									pushObject[currentSalat] =  "http://www.chefkoch.de/rs/s0/"+salat[salatIndex][0]+"/Rezepte.html";
									returnObject.food.push(pushObject);
								}
							}
						
							//---------------------airquality_indoor---------
							var airquality = 0;
							var maxValue_airquality = 16;
							var temparatur_indoor = Number(returnObject.temperature.indoor);
							var humidity_indoor = Number(returnObject.humidity.indoor);
							var co2 = Number(returnObject.co2.indoor);
							var humidity_diff = Math.abs(feelgood_humidity - humidity_indoor);
							var temp_diff = Math.abs(temparatur_indoor - feelgood_temp);

							returnObject.air_pressure.percentage = 0.27;
							//----normierung humidity
							if(temp_diff>=15){
								airquality = maxValue_airquality;
								returnObject.airquality.percentage = 1;
								var pushObject = new Object();
								pushObject["temperature"] = 10;
								returnObject.airquality.origin.push(pushObject);
								if(temparatur_indoor >= feelgood_temp){
									returnObject.airquality.message = "Misserable Qualität, es ist viel zu heiß hier drin";
								}else{
									returnObject.airquality.message = "Misserable Qualität, es ist viel zu kalt hier drin. Heizung anschalten!";
								}
							}
							else{
								if(temp_diff>=10){
									var pushObject = new Object();
									pushObject["temperature"] = 6;
									returnObject.airquality.origin.push(pushObject);
									airquality+=4;
								}
								else{
									if(temp_diff>=5){
										var pushObject = new Object();
										pushObject["temperature"] = 5;
										returnObject.airquality.origin.push(pushObject);
										airquality+=3;
									}else{
										if(temp_diff>=2){
											var pushObject = new Object();
											pushObject["temperature"] = 3;
											returnObject.airquality.origin.push(pushObject)
											airquality+=2;
										}else{
											var pushObject = new Object();
											pushObject["temperature"] = 1;
											returnObject.airquality.origin.push(pushObject)
											airquality+=1;
										}
									}
								}
							}
							console.log("after temp:"+airquality)
							//----normierung humidity
							if(humidity_diff>=30){
								airquality = maxValue_airquality;
								returnObject.airquality.percentage = 1;
								
								var pushObject = new Object();
								pushObject["humidity"] = 10;
								returnObject.airquality.origin.push(pushObject)

								returnObject.airquality.message = "Misserable Qualität, zu hohe Luftfeuchtigkeit im Raum, unbedingt Lüften";
							}
							else{
								if(humidity_diff>=10){
									var pushObject = new Object();
									pushObject["humidity"] = 7;
									returnObject.airquality.origin.push(pushObject)
									airquality+=4;
								}
								else{
									if(humidity_diff>=7){
										var pushObject = new Object();
										pushObject["humidity"] = 5;
										returnObject.airquality.origin.push(pushObject)
										airquality+=3;
									}else{
										if(humidity_diff>=3){
											var pushObject = new Object();
											pushObject["humidity"] = 3;
											returnObject.airquality.origin.push(pushObject)
											airquality+=2;
										}else{
											var pushObject = new Object();
											pushObject["humidity"] = 1;
											returnObject.airquality.origin.push(pushObject)
											airquality+=1;
										}
									}
								}
							}
							console.log("after humidity:"+airquality)
							console.log("co2:"+co2)
							//----normierung Co2
							if(co2>=2000){
								airquality = maxValue_airquality;
								returnObject.airquality.percentage = 1;
								returnObject.co2.percentage = 1;
								var pushObject = new Object();
								pushObject["co2"] = 10;
								returnObject.airquality.origin.push(pushObject)

								returnObject.airquality.message = "Misserable Qualität, zu viel CO2 im Raum, unbedingt Lüften";
							}
							else{
								if(co2>=1500){
									var pushObject = new Object();
									pushObject["co2"] = 8;
									returnObject.airquality.origin.push(pushObject)

									airquality+=8;
									returnObject.co2.percentage = 0.75;
								}
								else{
									if(co2>=1000){
										var pushObject = new Object();
										pushObject["co2"] = 6;
										returnObject.airquality.origin.push(pushObject)

										returnObject.co2.percentage = 0.6;
										airquality+=6;
									}else{
										if(co2>=800){
											var pushObject = new Object();
											pushObject["co2"] = 5;
											returnObject.airquality.origin.push(pushObject)
											returnObject.co2.percentage = 0.5;
											airquality+=4;
										}else{
											var pushObject = new Object();
											pushObject["co2"] = 2;
											returnObject.airquality.origin.push(pushObject)
											returnObject.co2.percentage = 0.2;
											airquality+=1;
										}
									}
								}
							}
							console.log("after co2:"+airquality)
							var proz = 100/maxValue_airquality*airquality;
							console.log("Proz Wert: "+proz);
							if(proz<= 30){
								returnObject.airquality.percentage = 0.3;
								returnObject.airquality.indoor = Number(1);
								if(returnObject.airquality.message.length == 0 ){
									returnObject.airquality.message = "Ausgezeichnete Qualität, fast wie im tiefsten Urwald";
								}
							}else{
								if(proz> 30 && proz<= 60){
									returnObject.airquality.percentage = 0.6;
									returnObject.airquality.indoor = Number(2);
									if(returnObject.airquality.message.length == 0 ){
										returnObject.airquality.message = "Mittelmaessige Qualität, denke daran in nächster Zeit zu lüften";
									}
								}else{
									returnObject.airquality.percentage = 0.9;
									returnObject.airquality.indoor = Number(3);
									if(returnObject.airquality.message.length == 0 ){
										returnObject.airquality.message = "Misserable Qualität, es ist unbedingt Zeit zum Lüften";
									}	
								}
							}
								//---------------------Stresslevel---------------
							//Werte die Einfließen sollen:
							//		- Temperatur check
							//		- Luftfeuchtigkeitcheck
							//		- Luftdruck 
							//		- Co2 check
							//		- Sonometer check
							//	47 db
							//	1130 ppm
							//	1027 mb
							//	46%
							//	21 C
							// -- genau in der Mitte Orange
							var stresslevel = airquality;
							var maxValue_stresslevel = maxValue_airquality+12;
							var noise = returnObject.noise.indoor;
							returnObject.stresslevel.origin = returnObject.airquality.origin;
							if(noise >=70){
								returnObject.noise.percentage = 1;
								stresslevel = maxValue_stresslevel;
								
								var pushObject = new Object();
								pushObject["noise"] = 10;
								returnObject.stresslevel.origin.push(pushObject)
								
								returnObject.stresslevel.message = "Die Umgebung ist unglaublich unruhig. Jetzt ist eine Ruhepause angesagt";
							}else{
								if(noise >=60){
									returnObject.noise.percentage = 0.65;
									stresslevel += 12;
									var pushObject = new Object();
									pushObject["noise"] = 8;
									returnObject.stresslevel.origin.push(pushObject)
								}else{
									if(noise >=50){
										returnObject.noise.percentage = 0.6;
										stresslevel += 10;
										var pushObject = new Object();
										pushObject["noise"] = 7;
										returnObject.stresslevel.origin.push(pushObject)
									}else{
										if(noise >=45){
											returnObject.noise.percentage = 0.5;
											stresslevel += 8;
											var pushObject = new Object();
											pushObject["noise"] = 5;
											returnObject.stresslevel.origin.push(pushObject)
										}else{
											if(noise >=40){
												returnObject.noise.percentage = 0.4;
												stresslevel += 6;
												var pushObject = new Object();
												pushObject["noise"] = 4;
												returnObject.stresslevel.origin.push(pushObject)
											}else{
												if(noise >=35){
													returnObject.noise.percentage = 0.3;
													stresslevel += 4;
													var pushObject = new Object();
													pushObject["noise"] = 3;
													returnObject.stresslevel.origin.push(pushObject)
												}else{
													if(noise >=30){
														returnObject.noise.percentage = 0.2;
														stresslevel += 2;
														var pushObject = new Object();
														pushObject["noise"] = 2;
														returnObject.stresslevel.origin.push(pushObject)
													}else{
														returnObject.noise.percentage = 0.0;
														stresslevel += 1;
														var pushObject = new Object();
														pushObject["noise"] = 1;
														returnObject.stresslevel.origin.push(pushObject)
													}
												}	
											}
										}
									}
								}
							}
							proz = 100/maxValue_stresslevel*stresslevel;
							if(proz<= 30){
								returnObject.stresslevel.percentage = 0.3;
								returnObject.stresslevel.indoor = Number(1);
								returnObject.stresslevel.message = "Du lässt dich von nichts aus der Ruhe bringen, du kannst ganz entspannt sein.";
							}else{
								if(proz> 30 && proz<= 60){
									returnObject.stresslevel.percentage = 0.6;
									returnObject.stresslevel.indoor = Number(2);
									returnObject.stresslevel.message = "Die Umgebung ist unruhig. Entspanne dich und genieße einen Tee";
								}else{
									returnObject.stresslevel.percentage = 0.9;
									returnObject.stresslevel.indoor = Number(3);
									returnObject.stresslevel.message = "Die Umgebung ist sehr hektisch. Überprüfe deine Geräte und Luftqualität.";
								}
							}
							


							//---------------------pollen--------------------
							//console.log(pollen)
							for (var pollenIndex = 1; pollenIndex < pollen.length-1; pollenIndex++){
								//console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
								if(pollen[pollenIndex][n] == '1' || pollen[pollenIndex][n] == '2' ||pollen[pollenIndex][n] == '3' ){
									var setObject = new Object();
									if(pollen[pollenIndex][n] == '1'){
										setObject[pollen[pollenIndex][0]] = Number(0.2);
									}
									if(pollen[pollenIndex][n] == '2'){
										setObject[pollen[pollenIndex][0]] = Number(0.6);
									}
									if(pollen[pollenIndex][n] == '3'){
										setObject[pollen[pollenIndex][0]] = Number(0.9);
									}

									
									returnObject.pollen.push(setObject);
									//returnObject.pollen[pollen[pollenIndex][0]] = Number(pollen[pollenIndex][n]);
								}
							}
							//-------------------------Devices from Openhab an their noise
							var randomValue = randomIntInc(1,6);
							var randomArray = new Array();

							switch(randomValue) {
   								case 1:
   									var obj = new Object()
   									obj["tv"] = 10;
   									randomArray.push(obj);
   								break
   								case 2:
   									var obj = new Object()
   									obj["box"] = 10;
   									randomArray.push(obj);
   								break
   								case 3:
   									var obj = new Object()
   									obj["radio"] = 10;
   									randomArray.push(obj);
   								break
   								case 4:
   									var obj = new Object()
   									obj["tv"] = 7;
   									randomArray.push(obj);
   									obj = new Object()
   									obj["radio"] = 5;
   									randomArray.push(obj);
   								break
   								case 5:
   									var obj = new Object()
   									obj["radio"] = 7;
   									randomArray.push(obj);
   									obj = new Object()
   									obj["box"] = 5;
   									randomArray.push(obj);
   								break
   								case 6:
   									var obj = new Object()
   									obj["box"] = 10;
   									randomArray.push(obj);
   									obj = new Object()
   									obj["tv"] = 1;
   									randomArray.push(obj);
   									
   								break
   								default:
   									randomObject["unknownSource"] = 10;
   							}
							returnObject.noise.origin = randomArray;
							//------start request for weather forcast
							var urlOfweatherAPI = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+returnObject.device.lat+"&lon="+returnObject.device.lng+"&APPID=a1226a9bd9130f264545304f7738c10c&units=metric&lang=de&cnt=3";
							
							var request = http.get(urlOfweatherAPI, function (response) {	
							    // data is streamed in chunks from the server
							    // so we have to handle the "data" event    
							    var buffer2 = ""

							    response.on("data", function (chunk) {
							        buffer2 += chunk;
							    }); 

							    response.on("end", function (err) {
							    	console.log(buffer2);
							    	var weatherForcast = JSON.parse(buffer2);
							    	var returnForcast = new Array(3);
							    	var today = weatherForcast.list[0];
							    	var tomorrow = weatherForcast.list[1];
							    	var theDayAfterTomorrow = weatherForcast.list[1];
							    	//-----TODAY------------------
							    	returnForcast[0] = new Object();
							    	returnForcast[0]["dt"] = today["dt"];
							    	returnForcast[0]["temp"] = new Object();
							    	returnForcast[0]["temp"]["min"] = today["temp"].min;
							    	returnForcast[0]["temp"]["max"] = today["temp"].max;
							    	returnForcast[0]["weather"] = today["weather"][0];
							    	if(today.hasOwnProperty("rain")){
							    		returnForcast[0]["rain"] = today["rain"];
							    	}else
							    	{
							    		returnForcast[0]["rain"] = 0	
							    	}
							    	//-----Tomorrow------------------
							    	returnForcast[1] = new Object();
							    	returnForcast[1]["dt"] = tomorrow["dt"];
							    	returnForcast[1]["temp"] = new Object();
							    	returnForcast[1]["temp"]["min"] = tomorrow["temp"].min;
							    	returnForcast[1]["temp"]["max"] = tomorrow["temp"].max;
							    	returnForcast[1]["weather"] = tomorrow["weather"][0];
							    	if(tomorrow.hasOwnProperty("rain")){
							    		returnForcast[1]["rain"] = tomorrow["rain"];
							    	}else
							    	{
							    		returnForcast[1]["rain"] = 0	
							    	}
							    	//-----the day after tomorrow------------------
							    	returnForcast[2] = new Object();
							    	returnForcast[2]["dt"] = theDayAfterTomorrow["dt"];
							    	returnForcast[2]["temp"] = new Object();
							    	returnForcast[2]["temp"]["min"] = theDayAfterTomorrow["temp"].min;
							    	returnForcast[2]["temp"]["max"] = theDayAfterTomorrow["temp"].max;
							    	returnForcast[2]["weather"] = theDayAfterTomorrow["weather"][0];
							    	if(theDayAfterTomorrow.hasOwnProperty("rain")){
							    		returnForcast[2]["rain"] = theDayAfterTomorrow["rain"];
							    	}else
							    	{
							    		returnForcast[2]["rain"] = 0	
							    	}
							    	returnObject["forecast"] = returnForcast;
							    	//returnObject["forcast"] = JSON.parse(buffer2);

							    	//res.setEncoding('utf-8');
							    	//console.log(JSON.stringify(returnObject))
									//res.setHeader('Content-Type', 'application/json');
									fs.writeFile("./data.json", JSON.stringify(returnObject, null, 4), function(err) {
									    if(err) {
									        return console.log(err);
									    }

									    console.log("The file was saved!");
									}); 
								    res.write(JSON.stringify(returnObject),"utf8");
									res.end()
							    });
							 });	
							





							
						});
				    });
			 
				}); 
			}
			else
			{
				var runningPath = 'sabine/'
				var urlString = req.url;
				if(req.url.indexOf('universal/') >= 1) {
					runningPath = "./"
				}


				//var runningPath = 'sabine/'
			    var pathname = req.url;
				res.setHeader("Content-Type", "text/html");
				var extension = pathname.split(".")[1];
			    if(extension == "css"){
			    	res.setHeader("Content-Type", "text/css");
			    }
			    if(extension == "svg"){
			    	res.setHeader("Content-Type", "image/svg+xml");
			    }
			    if(extension == "jpg"){
			    	res.setHeader("Content-Type", "image/jpg");
			    }
			    if(extension == "js"){
			    	res.setHeader("Content-Type", "application/javascript");
			    }
			    
			  	//console.log(url.format(pathname));
				//css(res,runningPath+"\\"+url.format(pathname))
				var handler = function(error, content){

				    if (error){
				      console.log(error);
				    }
				    else{
				      res.write(content);
				      res.end()
				    }
				    //if(count == 1){
				    //	response.end();
				   // }
				    
				}
				console.log(url.format(runningPath+url.format(pathname)));
			  	fs.readFile(runningPath+url.format(pathname), handler);
				
			}
		} 
  
}).listen(1337, 'localhost');

function randomIntInc (low, high) {
    return Math.floor(Math.random() * (high - low + 1) + low);
}

function css(response,path) {
  response.writeHead(200, {"Content-Type": "text/css"});

  var count = 0;
  var handler = function(error, content){
    count++;
    if (error){
      console.log(error);
    }
    else{
      response.write(content);
    }
    //if(count == 1){
    //	response.end();
   // }
    
  }

  fs.readFile(path, handler);
 
}

function retrieveAccessTokenInital(){
	//return ;
	console.log("retrieveAccessTokenInital called")
	//var url = "https://api.netatmo.net/oauth2/token/"
	//var grant = "grant_type=password&clienat_id=552d1ba21e7759ef905d9b6a&client_secret=ghffFwPKUKLGhlGX8mY5dwwX4UGFa9mk&username=kk114%40hdm-stuttgart.de&password=HdMurc2015zPq7!&scope=read_station";
	//var urlOfNetAtmoAPiForMeasurements = url + grant;
	var post_data = "grant_type=password&client_id=552d1ba21e7759ef905d9b6a&client_secret=ghffFwPKUKLGhlGX8mY5dwwX4UGFa9mk&username=kk114%40hdm-stuttgart.de&password=HdMurc2015zPq7!&scope=read_station"
	var post_options = {
      host: 'api.netatmo.net',
      port: '443',
      accept: '*/*',
      path: '/oauth2/token',
      method: 'POST',
      headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Content-Length': Buffer.byteLength(post_data)
      }
  };
  	console.log("retrieveAccessTokenInital start post request")
	var post_req = https.request(post_options, function (response) {	
	    // data is streamed in chunks from the server
	    // so we have to handle the "data" event    
	   
  		console.log('Start Request for access_token ['+RequestCounter+']');
  		RequestCounter+=1;
  		var getAccessToken = ""
	    response.on('error', function(e) {
		  console.log("Got error: " + e.message);
		});
	    response.on("data", function (chunk) {
	        getAccessToken += chunk;
	    }); 

	    response.on("end", function (err) {
	    	console.log("get AccessCode Response");
	    	var parsedJSON = JSON.parse(getAccessToken);
	    	 accessToken= parsedJSON.access_token;
	    	 expTimer = parsedJSON.expires_in;
	    	 resfreshToken = parsedJSON.refresh_token;
	    	console.log("Set Accesstoken: "+accessToken);
	    	console.log("Set expTimer: "+expTimer);
	    	console.log("Set resfreshToken: "+resfreshToken);	
	 	});
	});
	post_req.write(post_data);
  	post_req.end();
}

setInterval(function(){
	    		console.log("inside refresh Token function!!")
	    		var post_data_refresh = "grant_type=refresh_token&refresh_token="+resfreshToken+"&client_id=552d1ba21e7759ef905d9b6a&client_secret=ghffFwPKUKLGhlGX8mY5dwwX4UGFa9mk"
				var post_options_refresh = {
			      host: 'api.netatmo.net',
			      port: '443',
			      accept: '*/*',						//<-- hier den einen Slash raus!!!!
			      path: '/oauth2/token',
			      method: 'POST',
			      headers: {
			          'Content-Type': 'application/x-www-form-urlencoded',
			          'Content-Length': Buffer.byteLength(post_data_refresh)
			      }
			  };
			  console.log('Start Request for access_token ['+RequestCounter+']');
  			  RequestCounter+=1;
  			  var getAccessToken = "";
			  var post_req = https.request(post_options_refresh, function (response) {	
			    // data is streamed in chunks from the server
			    // so we have to handle the "data" event    
			   response.on('error', function(e) {
				  console.log("Got error: " + e.message);
				});
		  		//console.log('Start Request for access_token ['+RequestCounter+']');
		  		//RequestCounter+=1;
			    response.on("data", function (chunk) {
			        getAccessToken += chunk;
			    }); 

			    response.on("end", function (err) {
			    	console.log(getAccessToken);
			    	var parsedJSON = JSON.parse(getAccessToken);
			    	accessToken= parsedJSON.access_token;
			    	expTimer = parsedJSON.expires_in;
			    	resfreshToken = parsedJSON.refresh_token;
			    	console.log("Set Accesstoken: "+accessToken);
			    	console.log("Set expTimer: "+expTimer);
			    	console.log("Set resfreshToken: "+resfreshToken);
			    });
			  
	    	});
	    	post_req.write(post_data_refresh);
  			post_req.end(); 
	    	
}, (expTimer-3600)*1000); // eine stunde vor ablauf wird der access token erneuert

setInterval(function(){
	console.log("Interval for history data called")
	//var url = "https://api.netatmo.net/oauth2/token/"
	//var grant = "grant_type=password&clienat_id=552d1ba21e7759ef905d9b6a&client_secret=ghffFwPKUKLGhlGX8mY5dwwX4UGFa9mk&username=kk114%40hdm-stuttgart.de&password=HdMurc2015zPq7!&scope=read_station";
	
	//var urlOfNetAtmoAPiForMeasurements = url + grant;
	//var post_data = "grant_type=password&client_id=552d1ba21e7759ef905d9b6a&client_secret=ghffFwPKUKLGhlGX8mY5dwwX4UGFa9mk&username=kk114%40hdm-stuttgart.de&password=HdMurc2015zPq7!&scope=read_station"
	//var post_options = {
    //// host: 'api.netatmo.net',
    //  port: '443',
    //  accept: '*/',
    //  path: '/oauth2/token',
    //  method: 'POST',
    //  headers: {
    //       'Content-Type': 'application/x-www-form-urlencoded',
    //      'Content-Length': post_data.length
    //  }
 // };

/*	var post_req = https.request(post_options, function (response) {	
	    // data is streamed in chunks from the server
	    // so we have to handle the "data" event    
	    var getAccessToken = ""
  		console.log('Start Request for access_token ['+RequestCounter+']');
  		RequestCounter+=1;
	    response.on("data", function (chunk) {
	        getAccessToken += chunk;
	    }); 

	    response.on("end", function (err) {
	    	console.log(getAccessToken);
	    	var parsedJSON = JSON.parse(getAccessToken);
	    	var temp_accessToken= parsedJSON.access_token;
	    	console.log("access_token: "+temp_accessToken)*/
	    	var device_id = "70:ee:50:12:be:10";
	    	var module_id = "02:00:00:12:d6:4c";
	    	

	    	//Request for indoor
	    	var urlOfNetAtmoAPiForMeasurements = "https://api.netatmo.net/api/getmeasure?access_token="+accessToken+"&device_id="+device_id+"&type=Temperature,Humidity,Co2,Pressure,Noise&scale=30min&limit=48";
			console.log("do request to get history: "+urlOfNetAtmoAPiForMeasurements)
			console.log("Measurement Request Counter : ["+RequestCounterForMeasurment+"]")
			RequestCounterForMeasurment += 1;				
			var request = https.get(urlOfNetAtmoAPiForMeasurements, function (response) {	
			    // data is streamed in chunks from the server
			    // so we have to handle the "data" event    
			    var measurment = ""
			    //console.log('Start Request for measurment');
			    response.on("data", function (chunk) {
			        measurment += chunk;
			    }); 

			    response.on("end", function (err) {
			    	response.setEncoding("utf8"); 
			    	console.log("Get Result from history API "+measurment);
			    	if(measurment.indexOf('{"error":') === 0){
			    		if(dataTimestampOfRequest.length == 0){
			    			console.log("Can't retrieve data because of error, next try in 5 minutes last successfull request was never");
			    		}else{
			    			console.log("Can't retrieve data because of error, next try in 5 minutes last successfull request was:"+dataTimestampOfRequest);
			    		}
			    	}
			    	else{
						flow_air_pressure_indoor 	= [];
					
					 	flow_co2			 	= [];
						flow_humidity_indoor	= [];
						
						flow_noise				= [];
						flow_temperatur_indoor 	= [];
						flow_time_indoor = 0;
			    		
				    	var parsedJSONFromMeasurement = JSON.parse(measurment);
				    	flow_time_indoor = parsedJSONFromMeasurement.body[0].beg_time;
				    	for(var counter = 0; counter < parsedJSONFromMeasurement.body[0].value.length;counter++){
				    		var step =  parsedJSONFromMeasurement.body[0].value[counter];
				    		console.log("step"+step)
				    		flow_temperatur_indoor.push(step[0]);
				    		flow_humidity_indoor.push(step[1]);
				    		flow_co2.push(step[2]);
				    		flow_air_pressure_indoor.push(step[3]);
				    		flow_noise.push(step[4]);
				    	}
				    	if(flow_air_pressure_indoor.length > 3)
				    	{
					    	var pressureDiff = flow_air_pressure_indoor[flow_air_pressure_indoor.length-1] - flow_air_pressure_indoor[flow_air_pressure_indoor.length-2]
					    	if(pressureDiff < -6){
					    		windMessage = "Achtung Orkan! beachte die genaue Wettervorhersage und achte auf Meldungen"
					    	}else
					    	{
					    		if(pressureDiff >= -3 && pressureDiff < -6){
									windMessage = "starker Wind mit heftigen Böhen"
					    		}else{
					    			if(pressureDiff >= 4 && pressureDiff < 6){
										windMessage = "starker Wind  mit heftigen Böhen"
					    			}else{
					    				if(pressureDiff >= 6 && pressureDiff <= 9){
											windMessage = "sehr starker Wind  mit heftigen Böhen, es wird nicht empfohlen das Haus zu verlassen"
					    				}else
					    					if(pressureDiff >= 10){
					    						windMessage = "Achtung Orkan! beachte die genaue Wettervorhersage und achte auf Meldungen"
					    					}else
					    					{
					    						windMessage = "leichter Wind"
					    					}
					    			}
					    		}

					    	}
				    	}
				    	var currentdate = new Date(); 
						dataTimestampOfRequest = currentdate.getDate() + "/"
		                + (currentdate.getMonth()+1)  + "/" 
		                + currentdate.getFullYear() + " @ "  
		                + currentdate.getHours() + ":"  
		                + currentdate.getMinutes() + ":" 
		                + currentdate.getSeconds();
					    	}
			    });
			 });


			//Request for outdoor
	    	var urlOfNetAtmoAPiForMeasurements = "https://api.netatmo.net/api/getmeasure?access_token="+accessToken+"&device_id="+device_id+"&module_id="+module_id+"&type=Temperature,Humidity&&scale=30min&limit=48";
			console.log("do request to get history: "+urlOfNetAtmoAPiForMeasurements)
			console.log("Measurement Request Counter : ["+RequestCounterForMeasurment+"]")
			RequestCounterForMeasurment += 1;				
			var request = https.get(urlOfNetAtmoAPiForMeasurements, function (response) {	
			    // data is streamed in chunks from the server
			    // so we have to handle the "data" event 
			    response.setEncoding("binary");   
			    var measurment = ""
			    //console.log('Start Request for measurment');
			    response.on("data", function (chunk) {
			        measurment += chunk;
			    }); 

			    response.on("end", function (err) {
			    	console.log("Get Result from history API "+measurment);
			    	if(measurment.indexOf('{"error":') === 0){
			    		if(dataTimestampOfRequest.length == 0){
			    			console.log("Can't retrieve data because of error, next try in 5 minutes last successfull request was never");
			    		}else{
			    			console.log("Can't retrieve data because of error, next try in 5 minutes last successfull request was:"+dataTimestampOfRequest);
			    		}
			    	}
			    	else{
						flow_humidity_outdoor	= [];
						flow_temperatur_outdoor	= [];
			    		
				    	var parsedJSONFromMeasurement = JSON.parse(measurment);
				    	flow_time_outdoor = parsedJSONFromMeasurement.body[0].beg_time;
				    	for(var counter = 0; counter < parsedJSONFromMeasurement.body[0].value.length;counter++){
				    		var step =  parsedJSONFromMeasurement.body[0].value[counter];
				    		console.log("step"+step)
				    		flow_humidity_outdoor.push(step[1]);
				    		flow_temperatur_outdoor.push(step[0]);
				    	}
				    }
			    });
			 });
//	    });
//	 });
//	post_req.write(post_data);
//  	post_req.end();
}, 5*60*1000); //alle 5 mimnuten werden die abgeholt
console.log('Server running at http://localhost:1337/');