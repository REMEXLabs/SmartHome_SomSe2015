// Include gulp
var gulp = require('gulp');

// Include gulp plugins
var plumber   = require('gulp-plumber'),
    sass      = require('gulp-sass'),
    notify    = require('gulp-notify');

// damit imagemin funktioniert, folgende Zeile in der Datei ~/.profile ergänzen:
//   ulimit -S -n 4096
// dadurch wird das Limit der parallel geöffneten Dateien auf 4096 erhöht (Standard: 256)



function sassError(error){
  var data = error.message.split(':');
  var file = data[0].split('/').pop();
  error.lineNumber = data[1];
  var msg = "\n"+'Sass Error in File "'+file+'" on Line '+error.lineNumber+"\n";
  for(var i=3, len=data.length; i<len; i++){
    msg += data[i]+"\n";
  }
  return msg;
}
function endStream(){
  this.emit('end');
}

function compileError(error){
  console.log(error);
  return false;
}


// Compile Scss
gulp.task('scss', function(){
  return gulp.src('../scss/styles.scss')
    .pipe(plumber({errorHandler: notify.onError(sassError)}))
    .pipe(sass({
      sourceComments: 'none'
    }).on('error', endStream))
    .pipe(gulp.dest('../css'));
});
gulp.task('scss-build', function(){
  return gulp.src('../scss/styles.scss')
    .pipe(plumber({errorHandler: notify.onError(sassError)}))
    .pipe(sass({
      sourceComments: false
    }).on('error', endStream))
    .pipe(rename('styles.min.css'))
    .pipe(minifycss())
    .pipe(gulp.dest('../css'));
});


// Watch files for changes
gulp.task('watch', function(){
  // Sass
  gulp.watch('../scss/**/*.scss', ['scss']);
});


// Default task - run all the other tasks
gulp.task('default', ['scss','watch']);
