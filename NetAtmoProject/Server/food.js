module.exports = {

  getObstData: function(callback){
  	var fs = require('fs')
  	,   csv = require('csv');
  	
  	var handler_food = function(error, content){
   
    if (error){
      console.log(error);
    }
	else{
	      csv.parse(content,{delimiter: ';'}, function(err, output){
	 		var transformedFoodData = transformFoodData(output);
	 		callback(transformedFoodData);
			});
	    } 
  	}
  	fs.readFile('Server/obst_gemuese/obst.csv','utf-8', handler_food);
  	
  },

  getSalatData: function(callback){
  	var fs = require('fs')
  	,   csv = require('csv');
  	
  	var handler_food = function(error, content){
   
    if (error){
      console.log(error);
    }
	else{
	      csv.parse(content,{delimiter: ';'}, function(err, output){
	 		var transformedFoodData = transformFoodData(output)
	 		callback(transformedFoodData);
			});
	    } 
  	}
  	fs.readFile('Server/obst_gemuese/salat.csv','utf-8', handler_food);
  	
  },
  getGemueseData: function(callback){
  	var fs = require('fs')
  	,   csv = require('csv');
  	
  	var handler_food = function(error, content){
   
    if (error){
      console.log(error);
    }
	else{
	      csv.parse(content,{delimiter: ';'}, function(err, output){
	 		var transformedFoodData = transformFoodData(output)
	 		callback(transformedFoodData);
			});
	    } 
  	}
  	fs.readFile('Server/obst_gemuese/gemuese.csv','utf-8', handler_food);
  	
  },

};

function transformFoodData(csvData){
	var returnObject = new Array();
	var d = new Date();
	var n = d.getMonth();
	n = n+1;
	for (var foodIndex = 1; foodIndex < csvData.length-1; foodIndex++){
		//console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
		if(csvData[foodIndex][n] == 'ja' ||csvData[foodIndex][n] == '(ja)' ){
			var currentFood = csvData[foodIndex][0];
			var pushObject = new Object()
			pushObject[currentFood] =  "http://www.chefkoch.de/rs/s0/"+csvData[foodIndex][0]+"/Rezepte.html";
			returnObject.push(pushObject);
		}
	}
	return returnObject;
}