module.exports = {

  getPollenData: function(callback){
  	var fs = require('fs')
  	,   csv = require('csv');
  	
  	var handler_pollen = function(error, content){
   
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
  	fs.readFile('Server/pollen/pollen.csv','utf-8', handler_pollen);
  }
};
function transformFoodData(csvData){
  var returnObject = new Array();
  var d = new Date();
  var n = d.getMonth();
  
  for (var pollenIndex = 1; pollenIndex < csvData.length-1; pollenIndex++){
    //console.log(obst[obstIndex]+"-->"+obst[obstIndex][n])
    if(csvData[pollenIndex][n] == '1' || csvData[pollenIndex][n] == '2' ||csvData[pollenIndex][n] == '3' ){
      var setObject = new Object();
      if(csvData[pollenIndex][n] == '1'){
        setObject[csvData[pollenIndex][0]] = Number(0.2);
      }
      if(csvData[pollenIndex][n] == '2'){
        setObject[csvData[pollenIndex][0]] = Number(0.6);
      }
      if(csvData[pollenIndex][n] == '3'){
        setObject[csvData[pollenIndex][0]] = Number(0.9);
      }

      
      returnObject.push(setObject);
    }
  }
  return returnObject;
}