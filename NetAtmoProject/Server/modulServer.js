var http = require('http')
,  fs = require('fs')
,  url  = require('url')
,  q = require('q')
,  openhabCom = require("./openhabCommunication.js")
,  calcData = require("./CalcData.js")
,  foodData = require("./food.js")
,  pollenData = require("./pollen.js")
,  weatherData = require("./openWeatherMap.js")
,  netatmo = require("./netAtmoHistory.js")
,  openhabData = new Object()
,  food = new Array()
,  pollen = new Array()
,  forecast = new Object()
,  openhabCommunicationTimer_in_secounds = 20*1000			// all 20 secounds
,  foodUpdateTimer_in_secounds = 30*60*1000 				// all 20 secounds
,  openWeatherMapUpdateTimer_in_secounds = 30*60*1000 		// all 30 minutes
,  netatmoHistoryUpdateTimer_in_secounds = 5*60*1000 		// all 5 minutes
,  accessToken = ""
,  expTimer = 10800*1000
,  resfreshToken = ""
,  historyIndoor = new Object()
,  historyOutdoor = new Object()
,  firstRun = true
,  configSettings = JSON.parse(fs.readFileSync("config.json", "utf8"));

getFoodData();
getPollenData();
getNewNetAtmoAccessToken();


http.createServer(function (req, res) {
	
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
	else
	{
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
			}
			else
			{
				if(req.url.indexOf('/update') > -1  ){
					res.setHeader('Content-Type','application/json',"charset=utf-8");
					var returnObject = openhabData;

					fs.writeFile("./data.json", JSON.stringify(returnObject, null, 4), function(err) {
									    if(err) {
									        return console.log(err);
									    }

									    console.log("The file was saved!");
									}); 
					res.write(JSON.stringify(returnObject),"utf8");
					res.end()
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
	}

}).listen(configSettings.server_port, 'localhost');

console.log('Server running at http://localhost:1337/');


//--------------------------------------------------Intervall Functions
//-----------------------retrieve data from opnehab----------------------
setInterval(function(){
	console.log("communicate with Openhab")
	openhabCom.getOpenhabData(configSettings.openhab_ip_and_port,function(result) {
	    openhabData = result;
	    openhabData = calcData.getData(openhabData)
	    openhabData.food = food;
	    openhabData.pollen = pollen;
	    if(firstRun){
	    	retrieveOpenWeatherMapData();
	    	openhabData["forecast"] = new Array();
	    	firstRun = false;
	    }else
	    {
	    	openhabData["forecast"] = forecast;
	    }
	    
	    if ( typeof historyIndoor.flow_co2 !== 'undefined' && historyIndoor.flow_co2){
	    	//--CO2-Indoor---------------------------------------------
	    	openhabData.co2.flow_time_indoor 		= historyIndoor.flow_time_indoor;
	    	openhabData.co2.flow_indoor 			= historyIndoor.flow_co2;
	    	//--humidity-Indoor
	    	openhabData.humidity.flow_indoor 		= historyIndoor.flow_humidity_indoor;
	    	openhabData.humidity.flow_time_indoor 	= historyIndoor.flow_time_indoor;
	    	//--noise-Indoor--------------------------------------------
	    	openhabData.noise.flow_indoor 			= historyIndoor.flow_noise;
	    	openhabData.noise.flow_time_indoor 		= historyIndoor.flow_time_indoor;
	    	//--temperature-Indoor--------------------------------------
	    	openhabData.temperature.flow_indoor 	 = historyIndoor.flow_temperatur_indoor;
	    	openhabData.temperature.flow_time_indoor = historyIndoor.flow_time_indoor;
	    	//--WindMessage---------------------------------------------
	    	openhabData.air_pressure.flow_indoor 	= historyIndoor.flow_air_pressure_indoor
	    	openhabData.air_pressure.flow_time_indoor 	= historyIndoor.flow_time_indoor;
	    	openhabData.air_pressure.message 		= historyIndoor.windMessage;
	    }
	    if(typeof historyOutdoor.flow_time_outdoor !== 'undefined' && historyOutdoor.flow_time_outdoor){
	    	//--temperature-Outdoor--------------------------------------
	    	openhabData.temperature.flow_outdoor 	= historyOutdoor.flow_temperatur_outdoor;
	    	openhabData.temperature.flow_time_outdoor = historyOutdoor.flow_time_outdoor;
	    	//----humidity-Outdooor-------------------------------------
	    	openhabData.humidity.flow_outdoor 	= historyOutdoor.flow_humidity_outdoor;
	    	openhabData.humidity.flow_time_outdoor = historyOutdoor.flow_time_outdoor;
	    }
	    
 });
}, openhabCommunicationTimer_in_secounds); 


//-----------------------Food parsing----------------------
setInterval(function(){
	console.log("read Again FoodFile")
	getFoodData();
}, foodUpdateTimer_in_secounds); 


//-----------------------OpenWeatherMap----------------------
setInterval(function(){
	retrieveOpenWeatherMapData();
	
},openWeatherMapUpdateTimer_in_secounds); 

//-----------------History Data----------------------------------

		//-------Refresh AccessToken---------
setInterval(function(){
	getNewNetAtmoAccessToken();
},expTimer );
	
		//--------retrieveHistoryData--------
setInterval(function(){
	retrieveHistroyData();
}, netatmoHistoryUpdateTimer_in_secounds);

//--------------------------------------------------Helper Functions
function retrieveOpenWeatherMapData(){
	console.log("communicate with OpenweatherMap")
	if ( typeof openhabData.device !== 'undefined' && openhabData.device)
	{
		weatherData.getOpenWeatherMapData(openhabData.device.lat,openhabData.device.lng,configSettings.openWeatherMap_app_id, function(result) {
		   forecast = result;
	 	});
	}
}


function retrieveHistroyData(){
	//var device_id = "70:ee:50:12:be:10";
	//var module_id = "02:00:00:12:d6:4c";
	if(accessToken.length != 0){
		netatmo.getHistoryDataForIndoor(accessToken,
			configSettings.device_id,
			configSettings.netAtmo_history_scale,
			configSettings.netAtmo_limit_of_entry,
			function(result){
				historyIndoor = result;
		});
		netatmo.getHistoryDataForOutdoor(accessToken,
			configSettings.device_id,
			configSettings.module_id,
			configSettings.netAtmo_history_scale,
			configSettings.netAtmo_limit_of_entry,
			function(result){
				historyOutdoor = result;
		});
	}
	
}

function getNewNetAtmoAccessToken(){
	if(resfreshToken.length == 0){
		netatmo.retrieveAccessTokenInital(configSettings.netAtmo_client_id,
			configSettings.netAtmo_client_secret,
			configSettings.netAtmo_username,
			configSettings.netAtmo_password,
			function(result){
				setNetAtmoData(result);
		});
	}
	else{
		netatmo.refreshAccessToken(resfreshToken,
		 configSettings.netAtmo_client_id,
		 configSettings.netAtmo_client_secret,
		 function(result){
			setNetAtmoData(result);
		});
	}
}

function setNetAtmoData(result){
	accessToken 	= result.accessToken,
	expTimer		= ((result.expTimer-3600)*1000)
	resfreshToken 	= result.resfreshToken;
}

function getFoodData(){
	food = new Array();
	foodData.getObstData(function(result){
		food = food.concat(result);
	});
	foodData.getGemueseData(function(result){
		food = food.concat(result);
	});
	foodData.getSalatData(function(result){
		food = food.concat(result);
	});
	
}
function getPollenData(){
	pollen = new Array();
	pollenData.getPollenData(function(result){
		pollen = pollen.concat(result);
	});
}