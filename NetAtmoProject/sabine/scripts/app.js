// Execute when DOM fully loaded and parsed
document.addEventListener("DOMContentLoaded", function(event) {

// DomElements and Variables
var gridEl			= document.getElementById('grid'),
		buttonEl		= document.getElementById('clsBtn'),
		detailEl 		= document.getElementById('detail');

var data = {},
		settings = {};
		// Default Settings
		settings.temperature = 20;

var idleTime = 0;
		idelInterval = setInterval(timerIncrement, 60 * 1000);

// Initialize Interface and load Data
getData();
setTimeout(reload, 5 * 60 * 1000);

function reload(){
	location.reload();
}

// Eventlisteners
gridEl.addEventListener('click', activate, false);
buttonEl.addEventListener('click', deactivate, false);
detailEl.addEventListener('click', slide, false);

// Functions
// Active Tile  For Detail View
function activate(evt) {
	idleTime = 0;

	if(evt.target !== evt.currentTarget){
		el = evt.target;

		// Bubble To Clicked Tile Element
		while( !el.classList.contains('tile') ){
			el = el.parentElement;
		}

		// Remove Active Class From All Other Tiles
		els = document.getElementsByClassName('tile');
		for (var i = 0; i < els.length; i++) {
		  if(els[i].classList.contains('active')){
		  	els[i].classList.remove('active');
		  }
		}

		// Add Detail Class to Grid
		el.classList.toggle('active');
		if(!gridEl.classList.contains('detail')){
			gridEl.classList.add('detail');
		}

		// Display DetailView
		if(detailEl.classList.contains('hide')){
			detailEl.classList.remove('hide');
		}

		els = detailEl.children;
		for (var i = 0; i < els.length; i++) {
			if(!els[i].classList.contains('hide') && els[i].nodeName == 'DIV'){
				els[i].classList.add('hide');
			}
			if(els[i].classList.contains(el.id)){
				els[i].classList.remove('hide');
			}
		};
	}

	evt.stopPropagation();
}

// Close Detail View
function deactivate (evt) {

	// Remove Active Class From All Tile Elements
	els = document.getElementsByClassName('tile');
	for (var i = 0; i < els.length; i++) {
	  if(els[i].classList.contains('active')){
	  	els[i].classList.remove('active');
	  }
	}

	// Remove Detail Class From Grid
	if(gridEl.classList.contains('detail')){
		gridEl.classList.remove('detail');
	}

	// Hide DetailView
	if(!detailEl.classList.contains('hide')){
		detailEl.classList.add('hide');
	}

	els = detailEl.children;
	for (var i = 0; i < els.length; i++) {
		if(!els[i].classList.contains('hide') && els[i].classList.contains('tile')){
			els[i].classList.add('hide');
		}
	};
}

// Get NetAtmo Data
function getData() {

	// AJAX Request
  var	request = new XMLHttpRequest();

  // Server URL
  //request.open('GET', 'update');

  // Local Example Data
	request.open('GET', 'data.json');

  // ErrorHandling
  request.onerror = function() {
    console.log('Fehler beim Senden der Daten');
  };

  // Status Handling
  request.onload = function() {
    if (this.status >= 200 && this.status < 400){
      data = JSON.parse(this.response);

      initializeData(data);

    } else {
      console.log('Fehler: ' + this);
    }
  }

  request.send();
}

// Insert NetAtmo Data Into Elements
function initializeData(data){
	setTemperature(data.temperature);
	setFood(data.food);
	setWeather(data.forecast);
	setStresslevel(data.stresslevel);
	setNoise(data.noise);
	setAirquality(data.airquality);
}

// Temperature Element
function setTemperature(temperature){

 	// Tile
	el = document.getElementById('temperature');

	el.querySelector('data').innerHTML = Math.floor(temperature.indoor) + '°C';
	el.classList.remove('temp-low', 'temp-good', 'temp-warm', 'temp-hot');

	// Temperature less than 15
	if(temperature.indoor < settings.temperature - 5){
		el.classList.add('temp-low');
	} else if(temperature.indoor > settings.temperature - 5 && temperature.indoor < settings.temperature +5){
		el.classList.add('temp-good');
	} else if(temperature.indoor > settings.temperature + 5 && temperature.indoor < settings.temperature + 10){
		el.classList.add('temp-warm');
	} else if(temperature.indoor > settings.temperature + 10){
		el.classList.add('temp-hot');
	}

	// DetailView
	detailEl.querySelector('.temperature .indoor').innerHTML = Math.floor(temperature.indoor) + '°C';
	detailEl.querySelector('.temperature .outdoor').innerHTML = Math.floor(temperature.outdoor) + '°C';
}

// Food Element
function setFood(food){

	//DetailView
	list = detailEl.querySelector('.food ul');

	// Detecting Portriat/Landscape
	if(window.innerWidth > window.innerHeight){
		itemWidth = window.innerWidth * 0.60 - 80;
	} else {
		itemWidth = window.innerWidth - 80;
	}

	list.style.width = itemWidth * food.length + 'px';
	for (var i = 0; food.length > i; i++) {
		list.appendChild(foodListItem(Object.keys(food[i])[0], itemWidth));
	};
}

// Weather Element
function setWeather(forecast){
	el = document.querySelector('#weather figcaption');

	//DetailView
	list = detailEl.querySelector('.weather ul');

	// Detecting Portriat/Landscape
	if(window.innerWidth > window.innerHeight){
		itemWidth = window.innerWidth * 0.60 - 80;
	} else {
		itemWidth = window.innerWidth - 80;
	}

	list.style.width = itemWidth * forecast.length + 'px';
	day = ['heute', 'morgen', 'ubermorgen'];
	for (var i = 0; forecast.length > i; i++) {
		list.appendChild(weatherListItem(forecast[i], itemWidth, i,  day[i]));
	};
}

// Stresslevel Element
function setStresslevel(stresslevel){

	// Tile
	el = document.getElementById('stresslevel');
	el.classList.remove('stresslevel-1', 'stresslevel-2', 'stresslevel-3');
	el.classList.add('stresslevel-' + stresslevel.indoor);

	// DetailView
	dEl = detailEl.querySelector('.stresslevel');
	dEl.querySelector('h2').innerHTML = stresslevel.message;
	dEl.querySelector('.ampel').classList.remove('ampel-1', 'ampel-2', 'ampel-3');
	dEl.querySelector('.ampel').classList.add('ampel-'+ stresslevel.indoor);
}

// noise Element
function setNoise(noise){

	// Tile
	el = document.getElementById('noise');
	el.querySelector('data').innerHTML = noise.indoor + 'db';
	el.classList.remove('noise-low', 'noise-medium', 'noise-height');

	if(noise.indoor <= 63){
		domClass = 1;
	} else if(noise.indoor > 63 && noise.indoor < 81){
		domClass = 2;
	} else if(noise.indoor >= 81){
		domClass = 3;
	}

	el.classList.add('noise-' + domClass);

	// DetailView
	dEl = detailEl.querySelector('.noise');
	dEl.querySelector('data').innerHTML = noise.indoor + 'db';
	dEl.querySelector('.ampel').classList.remove('ampel-1', 'ampel-2', 'ampel-3');
	dEl.querySelector('.ampel').classList.add('ampel-'+ domClass);

	list = dEl.querySelector('.devices');
	items = list.querySelectorAll('img');

	for (var i = 0; i < noise.origin.length; i++) {
		for(device in noise.origin[i]){
			for (var i = 0; i < items.length; i++) {
				if(items[i].getAttribute('alt').toLowerCase() === device){
					items[i].classList.add('active');
				}
			};
		}
	}
}

// Airquality Element
function setAirquality(airquality){

	// Tile
	el = document.getElementById('airquality');
	el.classList.remove('airquality-1', 'airquality-2', 'airquality-3');
	el.classList.add('airquality-' + airquality.indoor);

	// DetailView
	dEl = detailEl.querySelector('.airquality')
	dEl.querySelector('h2').innerHTML = airquality.message;
	dEl.querySelector('.ampel').classList.remove('ampel-1', 'ampel-2', 'ampel-3');
	dEl.querySelector('.ampel').classList.add('ampel-'+ airquality.indoor);
}

function foodListItem(data, itemWidth){
	item = document.createElement('li');
	item.style.width = itemWidth + 'px';
	figure = document.createElement('figure');
	figure.classList.add('slide');
	img = document.createElement('img');
	name = data.replace(' ', '-').replace('ä', 'ae').replace('ö', 'oe').replace('ü', 'ue');
	img.src = 'images/food/' + name + '.jpg';
	figcaption = document.createElement('figcaption');
	h2 = document.createElement('h2');
	h2.innerHTML = data;

	figcaption.appendChild(h2);
	figure.appendChild(img);
	figure.appendChild(figcaption);
	item.appendChild(figure);

	return item
}

function weatherListItem(data, itemWidth, position, id){
	item = document.createElement('li');
	item.style.width = itemWidth + 'px';
	item.id = id;
	item.dataset.position = itemWidth * position;
	figure = document.createElement('figure');
	figure.classList.add('slide');

	imgWeather = document.createElement('img');
	imgWeather.src = 'images/weather/' + data.weather.icon + '.svg';
	figcaption = document.createElement('figcaption');

	h3Temp = document.createElement('h3');
	h3Temp.innerHTML = Math.floor(data.temp.max) + '°C / ' + Math.floor(data.temp.min) + '°C';
	h3Descr = document.createElement('h3');
	h3Descr.innerHTML = data.weather.description;
	h3Rain = document.createElement('h3');
	h3Rain.innerHTML = Math.floor(data.rain) + '%';
	h3Rain.classList.add('rain');

	figcaption.appendChild(h3Temp);
	figcaption.appendChild(h3Descr);
	figcaption.appendChild(h3Rain);
	figure.appendChild(imgWeather);
	figure.appendChild(figcaption);
	item.appendChild(figure);

	return item
}

// IdleTimer
function timerIncrement(){
	idleTime ++;
	if(idleTime == 15){
		deactivate();
	}
}

// Slider
function slide(evt){
	el = evt.target;
	if(el.nodeName === 'BUTTON'){
		slider = el.parentElement;
		if(el.classList.contains('left')){
			if(parseInt(slider.dataset.position) > 0){
				slider.dataset.position = parseInt(slider.dataset.position) - 1;
			}
		} else if(el.classList.contains('right')){
			if(parseInt(slider.dataset.position) < (slider.querySelectorAll('li').length - 1)){
				slider.dataset.position = parseInt(slider.dataset.position) + 1;
			}
		}
		if(parseInt(slider.dataset.position) == 0){
			slider.classList.add('limit-left');
		} else if(parseInt(slider.dataset.position) == (slider.querySelectorAll('li').length - 1)){
			slider.classList.add('limit-right');
		} else {
			slider.classList.remove('limit-left', 'limit-right');
		}
		slider.querySelector('ul').style.right = slider.getBoundingClientRect().width * slider.dataset.position + 'px';
	} else if(el.nodeName === 'A'){
		evt.preventDefault();

		for (var i = 0; i < el.parentElement.children.length; i++) {
			el.parentElement.children[i].classList.remove('active')
		};
		el.classList.add('active');

		var slider = el.parentElement.parentElement.querySelector('ul');
		slider.style.right = document.getElementById(el.getAttribute('href')).dataset.position + 'px';
	}

}

});