module.exports = function(grunt) {
    // Project configuration.
    var config = grunt.initConfig({
      karma: {
        unit: {
          configFile: 'test/karma.conf.js'
        }
      }
    });

    grunt.loadNpmTasks('grunt-karma');
    grunt.registerTask('test', 'Run all tests', ['test:unit']);

    grunt.registerTask('test:unit', 'Runs all unit tests with PhantomJS', function () {
      grunt.task.run('karma:unit');
    });
    
    grunt.registerTask('travis',['test:unit']);
};
