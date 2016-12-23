module.exports = {
getOpenWeatherMapData: function (device_lat, device_lng,app_id, callback) {
	var http = require("http")
	var returnObject = new Object();
	
	var urlOfweatherAPI = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+device_lat+"&lon="+device_lng+"&APPID="+app_id+"&units=metric&lang=de&cnt=3";
							
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
		    	//returnObject["forecast"] = returnForcast;
		    	callback(returnForcast);
		    	//returnObject["forcast"] = JSON.parse(buffer2);

		    	//res.setEncoding('utf-8');
		    	//console.log(JSON.stringify(returnObject))
				//res.setHeader('Content-Type', 'application/json');
				/*fs.writeFile("./data.json", JSON.stringify(returnObject, null, 4), function(err) {
				    if(err) {
				        return console.log(err);
				    }

				    console.log("The file was saved!");
				});
			    res.write(JSON.stringify(returnObject),"utf8");
				res.end()*/
		    });
		 });
	
	}
};