// CalcData.js
// ========
module.exports = {
  getData: function (returnObject) {
//	var humidity_diff = Math.abs(feelgood_humidity - humidity_indoor);
//	var temp_diff = Math.abs(temparatur_indoor - feelgood_temp);
	var feelgood_temp = 20.1;
	var feelgood_humidity = 50;
	var feelgood_co2 = 650;
//	var ReturnAirquality = {"airquality": {"indoor":0,"percentage" : 0, "origin" : new Array(),"message":"Temp Message"}
//	var ReturnCO2 = {"co2": {"indoor" : 0,"percentage" : 0,"unit" : "ppm","flow_time_indoor" : flow_time_indoor, "flow_indoor": new Array()}}
//	var ReturnHumidity = {"humidity": {"indoor" : 0, "unit" : "%","flow_time_indoor" : flow_time_indoor,"flow_indoor": new Array(), "outdoor" : 0,"flow_time_outdoor" : flow_time_outdoor, "flow_outdoor": new Array()}}
//	var Returntemperature = {"temperature": {"indoor" : 0, "unit" : "°C","flow_time_indoor" : flow_time_indoor,"flow_indoor": new Array(), "outdoor" : 0,"flow_time_outdoor" : flow_time_outdoor, "flow_outdoor": new Array()}}
//	var airquality = 0;
//	var maxValue_airquality = 16;
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
									returnObject.airquality.message = "Ausgezeichnete Luft Qualität";
								}
							}else{
								if(proz> 30 && proz<= 60){
									returnObject.airquality.percentage = 0.6;
									returnObject.airquality.indoor = Number(2);
									if(returnObject.airquality.message.length == 0 ){
										returnObject.airquality.message = "Mittelmässige Qualität, denke daran in nächster Zeit zu lüften";
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
return returnObject;

/*
  	//----normierung temperature
		if(temp_diff>=15){
			airquality = maxValue_airquality;
			ReturnAirquality.airquality.percentage = 1;
			var pushObject = new Object();
			pushObject["temperature"] = 10;
			Returntemperature.temperature.origin.push(pushObject);
			if(temparatur_indoor >= feelgood_temp){
				Returntemperature.temperature.message = "Misserable Qualität, es ist viel zu heiß hier drin";
			}else{
				Returntemperature.temperature.message = "Misserable Qualität, es ist viel zu kalt hier drin. Heizung anschalten!";
			}
		}
		else{
			if(temp_diff>=10){
				var pushObject = new Object();
				pushObject["temperature"] = 6;
				Returntemperature.temperature.origin.push(pushObject);
				airquality+=4;
			}
			else{
				if(temp_diff>=5){
					var pushObject = new Object();
					pushObject["temperature"] = 5;
					Returntemperature.temperature.origin.push(pushObject);
					airquality+=3;
				}else{
					if(temp_diff>=2){
						var pushObject = new Object();
						pushObject["temperature"] = 3;
						Returntemperature.temperature.origin.push(pushObject)
						airquality+=2;
					}else{
						var pushObject = new Object();
						pushObject["temperature"] = 1;
						Returntemperature.temperature.origin.push(pushObject)
						airquality+=1;
					}
				}
			}
		}
		console.log("after temp:"+airquality)
		//----normierung humidity
		if(humidity_diff>=30){
			airquality = maxValue_airquality;
			ReturnHumidity.humidity.percentage = 1;
			
			var pushObject = new Object();
			pushObject["humidity"] = 10;
			ReturnHumidity.humidity.origin.push(pushObject)

			ReturnHumidity.humidity.message = "Misserable Qualität, zu hohe Luftfeuchtigkeit im Raum, unbedingt Lüften";
		}
		else{
			if(humidity_diff>=10){
				var pushObject = new Object();
				pushObject["humidity"] = 7;
				ReturnHumidity.humidity.origin.push(pushObject)
				airquality+=4;
			}
			else{
				if(humidity_diff>=7){
					var pushObject = new Object();
					pushObject["humidity"] = 5;
					ReturnHumidity.humidity.origin.push(pushObject)
					airquality+=3;
				}else{
					if(humidity_diff>=3){
						var pushObject = new Object();
						pushObject["humidity"] = 3;
						ReturnHumidity.humidity.origin.push(pushObject)
						airquality+=2;
					}else{
						var pushObject = new Object();
						pushObject["humidity"] = 1;
						ReturnHumidity.humidity.origin.push(pushObject)
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
			ReturnCO2.co2.percentage = 1;
			co2.percentage = 1;
			var pushObject = new Object();
			pushObject["co2"] = 10;
			ReturnCO2.co2.origin.push(pushObject)

			ReturnCO2.co2.message = "Misserable Qualität, zu viel CO2 im Raum, unbedingt Lüften";
		}
		else{
			if(co2>=1500){
				var pushObject = new Object();
				pushObject["co2"] = 8;
				ReturnCO2.co2.origin.push(pushObject)

				airquality+=8;
				ReturnCO2.co2.percentage = 0.75;
			}
			else{
				if(co2>=1000){
					var pushObject = new Object();
					pushObject["co2"] = 6;
					ReturnCO2.co2.origin.push(pushObject)

					returnObject.co2.percentage = 0.6;
					airquality+=6;
				}else{
					if(co2>=800){
						var pushObject = new Object();
						pushObject["co2"] = 5;
						ReturnCO2.co2.origin.push(pushObject)
						ReturnCO2.co2.percentage = 0.5;
						airquality+=4;
					}else{
						var pushObject = new Object();
						pushObject["co2"] = 2;
						ReturnCO2.co2.origin.push(pushObject)
						ReturnCO2.co2.percentage = 0.2;
						airquality+=1;
					}
				}
			}
		}
		console.log("after co2:"+airquality)
		var proz = 100/maxValue_airquality*airquality;
		console.log("Proz Wert: "+proz);
		if(proz<= 30){
			ReturnAirquality.airquality.percentage = 0.3;
			ReturnAirquality.airquality.indoor = Number(1);
			if(ReturnAirquality.airquality.message.length == 0 ){
				ReturnAirquality.airquality.message = "Ausgezeichnete Qualität, fast wie im tiefsten Urwald";
			}
		}else{
			if(proz> 30 && proz<= 60){
				ReturnAirquality.airquality.percentage = 0.6;
				ReturnAirquality.airquality.indoor = Number(2);
				if(ReturnAirquality.airquality.message.length == 0 ){
					ReturnAirquality.airquality.message = "Mittelmaessige Qualität, denke daran in nächster Zeit zu lüften";
				}
			}else{
				ReturnAirquality.airquality.percentage = 0.9;
				ReturnAirquality.airquality.indoor = Number(3);
				if(ReturnAirquality.airquality.message.length == 0 ){
					ReturnAirquality.airquality.message = "Misserable Qualität, es ist unbedingt Zeit zum Lüften";
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
			} */

  }
};