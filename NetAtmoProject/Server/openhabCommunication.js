// openhabCommunicataion.js
// ========
module.exports = {
  getOpenhabData: function (ip_adress_and_port,callback) {
  	var http = require("http")
     var urlOfOpenHab = "http://"+ip_adress_and_port+"/rest/items?type=json";
     var windMessage = "";
     var flow_time_indoor = 0;
     var flow_time_outdoor = 0;

	var request = http.get(urlOfOpenHab, function (response) {	
	    // data is streamed in chunks from the server
	    // so we have to handle the "data" event    
	    var buffer = "", 
	        data,
	        route;

	    response.on("error", function (error) {
	    	console.log(error);
	    });
	    
	    response.on("data", function (chunk) {
	        buffer += chunk;
	    }); 

	    response.on("end", function (err) {			


			var json = JSON.parse(buffer);
			var dataArray = new Array();
			var returnObjectOfOpenhab = {
				"airquality": {"indoor":0,"percentage" : 0, "origin" : new Array(),"message":""},
				"air_pressure": {"indoor" : 0,"percentage" : 2.7,"unit" : "mb","message":windMessage,"flow_time_indoor" : flow_time_indoor, "flow_indoor": new Array()},
				"co2": {"indoor" : 0,"percentage" : 0,"unit" : "ppm","flow_time_indoor" : flow_time_indoor, "flow_indoor": new Array()},
				"food": [],
				"humidity": {"indoor" : 0, "unit" : "%","flow_time_indoor" : flow_time_indoor,"flow_indoor": new Array(), "outdoor" : 0,"flow_time_outdoor" : flow_time_outdoor, "flow_outdoor": new Array()},
			    "noise": {"indoor" : 0,"percentage" : 0,"origin" : new Array(),"unit" : "dB","origin": new Array(),"flow_time_indoor" : flow_time_indoor,"flow_indoor": new Array()},
			    "pollen": new Array(),
			    "device" : {"battery": 0,"wifiState" : 0,"alt":0,"fstate": 0,"lat": 0,"lng": 0},
			    "stresslevel": {"indoor":0,"percentage" : 0,"origin" : new Array(),"message":""},
			    "temperature": {"indoor" : 0, "unit" : "°C","flow_time_indoor" : flow_time_indoor,"flow_indoor": new Array(), "outdoor" : 0,"flow_time_outdoor" : flow_time_outdoor, "flow_outdoor": new Array()},
			}
			//console.log("Here comes the json:"+json)
			for (var it = 0; it < json.item.length; it++){
					var name = json.item[it].name;

					if(name.indexOf("Netatmo_Indoor_Temperature") > -1){
						returnObjectOfOpenhab.temperature.indoor = Number(json.item[it].state);
						
					}
					if(name.indexOf("Netatmo_Outdoor_Temperature") > -1){
						returnObjectOfOpenhab.temperature.outdoor = Number(json.item[it].state);
						
					}
					if(name.indexOf("Netatmo_Indoor_Pressure") > -1){
						returnObjectOfOpenhab.air_pressure.indoor = Number(json.item[it].state);
						
					}
					if(name.indexOf("Netatmo_Indoor_Noise") > -1){
						returnObjectOfOpenhab.noise.indoor = Number(json.item[it].state);
						
					}
					if(name.indexOf("Netatmo_Indoor_CO2") > -1){
						returnObjectOfOpenhab.co2.indoor = Number(json.item[it].state);
						
					}
					if(name.indexOf("Netatmo_Indoor_Humidity") > -1){
						returnObjectOfOpenhab.humidity.indoor = Number(json.item[it].state);
					}
					if(name.indexOf("Netatmo_Outdoor_Humidity") > -1){
						returnObjectOfOpenhab.humidity.outdoor = Number(json.item[it].state);
						
					}
					if(name.indexOf("Netatmo_Indoor_latitude") > -1){
						returnObjectOfOpenhab.device.lng = Number(json.item[it].state);
					}
					if(name.indexOf("Netatmo_Indoor_longitude") > -1){
						returnObjectOfOpenhab.device.lat = Number(json.item[it].state);
					}
					if(name.indexOf("Netatmo_Indoor_altitude") > -1){
						returnObjectOfOpenhab.device.alt = Number(json.item[it].state);
					}
					if(name.indexOf("Netatmo_Indoor_wifi") > -1){
						returnObjectOfOpenhab.device.wifiState = Number(json.item[it].state);
					}
					if(name.indexOf("Netatmo_Outdoor_Batteryvp") > -1){
						returnObjectOfOpenhab.device.battery = Number(json.item[it].state);
					}
					if(name.indexOf("Netatmo_Outdoor_Rfstatus") > -1){
						returnObjectOfOpenhab.device.fstate = Number(json.item[it].state);
					}
					if(name.indexOf("iSnsLvVolume") > -1){
						var maxVolumen = 15;
						var returnObject = new Object();
						returnObject["radio"] = Math.round((Number(json.item[it].state)/maxVolumen*100)/10);
						returnObjectOfOpenhab.noise.origin.push(returnObject);
					}
			} //----END For
			//-------------------------Devices from Openhab an their noise
			console.log("Length: "+returnObjectOfOpenhab.noise.origin.length)
			if(returnObjectOfOpenhab.noise.origin.length == 0){
				console.log("inside Noise origin");
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
							var obj = new Object()
							obj["unknownSource"] = 10;
							randomArray.push(obj);
					}
				console.log("set originarray: "+ randomArray);
				returnObjectOfOpenhab.noise.origin = randomArray;
			}
			console.log("Return now:"+JSON.stringify(returnObjectOfOpenhab))
			callback(returnObjectOfOpenhab);
		});//--- response.on
	});//--- http.get
  }, //--- End function
}; // --- End Export

function randomIntInc (low, high) {
    return Math.floor(Math.random() * (high - low + 1) + low);
}