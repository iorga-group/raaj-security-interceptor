module.exports = function(config) {
	config.set({
		// base path, that will be used to resolve files and exclude
		basePath : '../',

		// list of files / patterns to load in the browser
		files : [
			'bower_components/angular/angular.js',
			'bower_components/angular-mocks/angular-mocks.js',
			'bower_components/raaj-*/src/*.js',
			'src/raaj-security-interceptor.js',
			'test/unit/**/*.js' ],

		// list of files to exclude
		exclude : [],

		frameworks : [ 'jasmine' ],

		// test results reporter to use
		// possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
		reporters : [ 'progress' ],

		// enable / disable colors in the output (reporters and logs)
		colors : true,

		// enable / disable watching file and executing tests whenever any file
		// changes
		autoWatch : false,

		browsers : [ 'PhantomJS' ],

		singleRun : true
	});
};