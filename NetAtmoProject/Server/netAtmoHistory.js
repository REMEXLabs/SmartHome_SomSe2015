module.exports = {
 retrieveAccessTokenInital: function(client_id,client_secret,username,pw,callback){
 		var https = require('https')
		console.log("retrieveAccessTokenInital called")
		//var url = "https://api.netatmo.net/oauth2/token/"
		//var grant = "grant_type=password&clienat_id=552d1ba21e7759ef905d9b6a&client_secret=ghffFwPKUKLGhlGX8mY5dwwX4UGFa9mk&username=kk114%40hdm-stuttgart.de&password=HdMurc2015zPq7!&scope=read_station";
		//var urlOfNetAtmoAPiForMeasurements = url + grant;
		var post_data = "grant_type=password&client_id="+client_id+"&client_secret="+client_secret+"&username="+username+"&password="+pw+"&scope=read_station"
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
		    	var returnObject = {"accessToken": "a","expTimer" : "b", "resfreshToken" : "c"}
		    	returnObject.accessToken= parsedJSON.access_token;
		    	returnObject.expTimer = parsedJSON.expires_in;
		    	returnObject.resfreshToken = parsedJSON.refresh_token;
		    	callback(returnObject)
		    	//console.log("Set Accesstoken: "+accessToken);
		    	//console.log("Set expTimer: "+expTimer);
		    	//console.log("Set resfreshToken: "+resfreshToken);	
		 	});
		});
		post_req.write(post_data);
	  	post_req.end();
 },

 refreshAccessToken: function(resfreshToken,client_id,client_secret,callback){
 	var https = require('https')
	console.log("inside refresh Token function!!")
		var post_data_refresh = "grant_type=refresh_token&refresh_token="+resfreshToken+"&client_id="+client_id+"&client_secret="+client_secret
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
	    	var returnObject = {"accessToken": "a","expTimer" : "b", "resfreshToken" : "c"}
	    	returnObject.accessToken= parsedJSON.access_token;
	    	returnObject.expTimer = parsedJSON.expires_in;
	    	returnObject.resfreshToken = parsedJSON.refresh_token;
	    	callback(returnObject)
	    });
	  
	});
	post_req.write(post_data_refresh);
	post_req.end(); 
 },

 getHistoryDataForIndoor: function(accessToken, device_id,scale,limit, callback){
	var https = require('https')
	//Request for indoor
	var urlOfNetAtmoAPiForMeasurements = "https://api.netatmo.net/api/getmeasure?access_token="+accessToken+"&device_id="+device_id+"&type=Temperature,Humidity,Co2,Pressure,Noise&scale="+scale+"&limit="+limit;
	console.log("do request to get history: "+urlOfNetAtmoAPiForMeasurements)
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
	    		//if(dataTimestampOfRequest.length == 0){
	    		//	console.log("Can't retrieve data because of error, next try in 5 minutes last successfull request was never");
	    		//}else{
	    			console.log("Can't retrieve data because of error, next try in 5 minutes last")// successfull request was:"+dataTimestampOfRequest);
	    		//}
	    	}
	    	else{
	    		var returnObject = {"flow_air_pressure_indoor" : new Array(), 
			    		"flow_co2": new Array(),
			    		"flow_humidity_indoor": new Array(),
			    		"flow_noise": new Array(),
			    		"flow_temperatur_indoor": new Array(),
			    		"flow_time_indoor" : 0,
			    		"windMessage" : ""
	    		 }
				/*flow_air_pressure_indoor 	= [];
			
			 	flow_co2			 	= [];
				flow_humidity_indoor	= [];
				
				flow_noise				= [];
				flow_temperatur_indoor 	= [];
				flow_time_indoor = 0;*/
	    		
		    	var parsedJSONFromMeasurement = JSON.parse(measurment);
		    	returnObject.flow_time_indoor = parsedJSONFromMeasurement.body[0].beg_time;
		    	for(var counter = 0; counter < parsedJSONFromMeasurement.body[0].value.length;counter++){
		    		var step =  parsedJSONFromMeasurement.body[0].value[counter];
		    		console.log("step"+step)
		    		returnObject.flow_temperatur_indoor.push(step[0]);
		    		returnObject.flow_humidity_indoor.push(step[1]);
		    		returnObject.flow_co2.push(step[2]);
		    		returnObject.flow_air_pressure_indoor.push(step[3]);
		    		returnObject.flow_noise.push(step[4]);
		    	}
		    	if(returnObject.flow_air_pressure_indoor.length > 3)
		    	{
			    	var pressureDiff = returnObject.flow_air_pressure_indoor[returnObject.flow_air_pressure_indoor.length-1] - returnObject.flow_air_pressure_indoor[returnObject.flow_air_pressure_indoor.length-2]
			    	if(pressureDiff < -6){
			    		returnObject.windMessage = "Achtung Orkan! beachte die genaue Wettervorhersage und achte auf Meldungen"
			    	}else
			    	{
			    		if(pressureDiff >= -3 && pressureDiff < -6){
							returnObject.windMessage = "starker Wind mit heftigen Böhen"
			    		}else{
			    			if(pressureDiff >= 4 && pressureDiff < 6){
								returnObject.windMessage = "starker Wind  mit heftigen Böhen"
			    			}else{
			    				if(pressureDiff >= 6 && pressureDiff <= 9){
									returnObject.windMessage = "sehr starker Wind  mit heftigen Böhen, es wird nicht empfohlen das Haus zu verlassen"
			    				}else
			    					if(pressureDiff >= 10){
			    						returnObject.windMessage = "Achtung Orkan! beachte die genaue Wettervorhersage und achte auf Meldungen"
			    					}else
			    					{
			    						returnObject.windMessage = "leichter Wind"
			    					}
			    			}
			    		}

			    	}
		    	}
		    	var currentdate = new Date(); 
				//dataTimestampOfRequest = currentdate.getDate() + "/"
                + (currentdate.getMonth()+1)  + "/" 
                + currentdate.getFullYear() + " @ "  
                + currentdate.getHours() + ":"  
                + currentdate.getMinutes() + ":" 
                + currentdate.getSeconds();
                callback(returnObject);
			 }
	    });
	 });
 },
 getHistoryDataForOutdoor: function(accessToken, device_id, module_id,scale,limit, callback){
	var https = require('https')
//Request for outdoor
	var urlOfNetAtmoAPiForMeasurements = "https://api.netatmo.net/api/getmeasure?access_token="+accessToken+"&device_id="+device_id+"&module_id="+module_id+"&type=Temperature,Humidity&&scale="+scale+"&limit="+limit;
	console.log("do request to get history: "+urlOfNetAtmoAPiForMeasurements)
			
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
	    		//if(dataTimestampOfRequest.length == 0){
	    		//	console.log("Can't retrieve data because of error, next try in 5 minutes last successfull request was never");
	    		//}else{
	    			console.log("Can't retrieve data because of error, next try in 5 minutes last")// successfull request was:"+dataTimestampOfRequest);
	    		//}
	    		
	    	}
	    	else{
	    		var returnObject = {"flow_humidity_outdoor" : new Array(), 
			    		"flow_temperatur_outdoor": new Array(),
			    		"flow_time_outdoor" : 0
	    		 }


				//flow_humidity_outdoor	= [];
				//flow_temperatur_outdoor	= [];
	    		
		    	var parsedJSONFromMeasurement = JSON.parse(measurment);
		    	returnObject.flow_time_outdoor = parsedJSONFromMeasurement.body[0].beg_time;
		    	for(var counter = 0; counter < parsedJSONFromMeasurement.body[0].value.length;counter++){
		    		var step =  parsedJSONFromMeasurement.body[0].value[counter];
		    		console.log("step"+step)
		    		returnObject.flow_humidity_outdoor.push(step[1]);
		    		returnObject.flow_temperatur_outdoor.push(step[0]);
		    	}
		    	callback(returnObject);
		    }
	    });
	 });
 },
};
