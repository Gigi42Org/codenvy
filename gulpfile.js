var gulp = require('gulp'),
    minifyCSS = require('gulp-minify-css'), // CSS minifying
    //imagemin = require('gulp-imagemin'), // Img minifying
    uglify = require('gulp-uglify'),    // JS minifying
    concat = require('gulp-concat'),    // Files merging
    rev = require('gulp-rev'),          // Files versioning
    compass = require('gulp-compass'),  // Compass to work with CSS
    rjs = require("gulp-requirejs"),    //A gulp interface to r.js
    connect = require('gulp-connect'),  // Webserver
    rename = require("gulp-rename"),    //Rename files
    useref = require('gulp-useref'),    //Removes <!-- build:js ... blocks from html
    replaceRevRef = require('gulp-rev-manifest-replace'), //Plugin to replace assets urls based on generated manifest file
    reverse = require('reversible'),
    rimraf = require('gulp-rimraf');                      // Remove files and folders
    //wait = require('gulp-wait'),
    //lr = require('tiny-lr'), // Минивебсервер для livereload
    //server = lr(),
    //livereload = require('gulp-livereload'), // Livereload for Gulp

var buildConfig = {
        jekyllStageConfig : "_config.stage.yml",
        jekyllProdConfig : "_config.prod.yml",
        jekyllGHConfig : "_config.gh.yml",
        jekyllEEConfig : "_config.enterprise.yml",
    };

var paths = {
        src: 'app/',
        prod: 'target/prod/',
        stage: 'target/stage/',
        temp: 'target/temp/',
        dist: 'target/dist/',
        config: 'build/',
        site: 'app/_site/'
};

// prod building
gulp.task('jek', function () {
     require('child_process')
        .spawn('jekyll', ['build'], {stdio: 'inherit', cwd: paths.temp, exclude: [paths.temp +'site/_scss']}); 
});

gulp.task('rjs1',function(){
      /*return 
      gulp.src(paths.temp + 'site/_rjs/*.js')
        .pipe(rjs({
            mainConfigFile: paths.temp +'site/_rjs/main.js',
            //optimize: 'none', //hardcoded in requirejs plugin
            baseUrl: paths.temp + 'site/_rjs',
            wrap: true,
            name: 'main',
            mainFile: paths.temp+'site/index.html',
            out: 'main.js'
        }))*/
        rjs({
            mainConfigFile: paths.temp +'site/_rjs/main.js',
            //optimize: 'none', //hardcoded in requirejs plugin
            baseUrl: paths.temp + 'site/_rjs',
            wrap: true,
            name: 'main',
            mainFile: paths.temp+'site/index.html',
            out: 'main.js'
        })
        .pipe(gulp.dest(paths.temp + 'site/scripts'))

        ;
 });

// This task creates local server
gulp.task('connect', function() {
  connect.server({
    root: paths.prod,
  });
});

// --------------------------- Building Prod -----------------------------
//----------------
//----------
gulp.task('prod',['copy_src','prod_cfg','css','rjs','jekyll','myrev','replace','rmbuild','copy_prod'], function(){

})
// Copies src to temp folder
gulp.task('copy_src', function(){
  return gulp.src(paths.src + '**/*.*')
  .pipe(gulp.dest(paths.temp))
})

gulp.task('prod_cfg', function(){
  return gulp.src(paths.config + buildConfig.jekyllProdConfig)
  .pipe(rename('_config.yml'))
  .pipe(gulp.dest(paths.temp))
})

gulp.task('css', ['copy_src'], function() {
  return gulp.src(paths.temp+'site/styles/*.scss')
  .pipe(compass({
    //config_file: './compass-config.rb',
    css: paths.temp +'site/styles',
    sass: paths.temp +'site/styles'
  }))
  .pipe(minifyCSS())
  .pipe(gulp.dest(paths.prod + 'site/styles/'));
});

// Builds projects using require.js's optimizer + Minify files with UglifyJS
gulp.task('rjs',['copy_src'], function(){
      return  rjs({
            mainConfigFile: paths.temp +'site/scripts/main.js',
            //optimize: 'none', //hardcoded in requirejs plugin
            baseUrl: paths.temp + 'site/scripts',
            wrap: true,
            name: 'main',
            mainFile: paths.temp+'site/index.html',
            out: 'amd-main.js'
        })
      .pipe(gulp.src(paths.temp +'site/scripts/vendor/require.js'))
      .pipe(reverse({objectMode: true})) // requirejs should be at the begining
      .pipe(concat('amd-app.js'))
      .pipe(uglify())
      .pipe(gulp.dest(paths.prod + 'site/scripts'));
 });

gulp.task('jekyll',['copy_src','prod_cfg']/*,['copy_src','css','rjs']*/, function () {
         console.log('Jekyll ......... ');
     return require('child_process')
        .spawn('jekyll', ['build'], {stdio: 'inherit', cwd: paths.temp});

});

// Replaces references with reved names
  gulp.task ('myrev', ['copy_src','prod_cfg','css','rjs','jekyll'], function(){
    return gulp.src([paths.prod + 'site/scripts/amd-app.js', paths.prod + 'site/styles/*.css'],{base:paths.prod})
    //.pipe(wait(1500))
    .pipe(rev())
    .pipe(gulp.dest(paths.prod))
    .pipe(rev.manifest())
    .pipe(gulp.dest(paths.prod));
  });

// Start replacing rev references
gulp.task('replace',['copy_src','prod_cfg','css','rjs','jekyll','myrev'], function(){

  var manifest = require(paths.prod+'rev-manifest.json')
  return gulp.src([paths.prod+'**/*.html'])
  .pipe(replaceRevRef({
    base: process.cwd()+'/target/prod',
    manifest: manifest,
    path: ''/*,
    cdnPrefix: 'http://absolute.path/cdn/'*/
  }))
  .pipe(gulp.dest(paths.prod));

});

// Removes <!-- build:js ... blocks from html for prod configeration
gulp.task('rmbuild', ['copy_src','prod_cfg','css','rjs','jekyll','myrev','replace'], function(){
  return gulp.src(paths.prod+'**/*.html')
  .pipe(useref())
  .pipe(gulp.dest(paths.prod));

});

gulp.task('copy_prod',['copy_src','prod_cfg','css','rjs','jekyll','myrev','replace','rmbuild'], function(){
  gulp.src([paths.prod+'/**/*.html', // all HTML
    paths.prod+'**/amd-app-*.js', // minified JS
    paths.prod+'**/*-*.css', // minified CSS
    paths.prod+'**/*.jpg',
    paths.prod+'**/*.png',
    paths.prod+'**/*.svg',
    paths.prod+'**/*.txt',
    paths.prod+'**/modernizr.custom.*.js'] // robots.txt
    )
  .pipe(gulp.dest(paths.dist+'prod'));
});

// Cleans gulp's folders
gulp.task('clean',function(){
  return gulp.src([paths.temp,paths.prod,paths.stage,paths.dist],{ read: false }) // much faster
    .pipe(rimraf());
})
