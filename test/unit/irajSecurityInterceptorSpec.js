'use strict';

describe('raajSecurityInterceptor', function() {

	it('should be given a specific function to control weither a request should be intercepted or not', function() {
		var shouldInterceptCalled = false,
			shouldInterceptResult = undefined;
		
		angular.module('myApp', ['raaj-security-interceptor']).config(function(raajSecurityInterceptorProvider) {
			raajSecurityInterceptorProvider.setShouldInterceptRequestFn(function(config) {
				shouldInterceptCalled = true;
				if (config.url.indexOf('/test') === 0) {
					shouldInterceptResult = true;
					return true;
				} else {
					shouldInterceptResult = false;
					return false;
				}
			});
		});
		module('raaj-security-interceptor', 'myApp');

		inject(function($http, $rootScope, $httpBackend) {
			var loginRequiredCalled = false;
			$rootScope.$on('raaj:auth-loginRequired', function() {
				loginRequiredCalled = true;
			});
			
			$httpBackend.when("GET", "/foo/test")
				.respond(200, 'foo');
			$http.get('/foo/test').success(function(response) {
				expect(response).toBe("foo");
			})
			$httpBackend.flush();
			
			expect(shouldInterceptCalled).toBe(true);
			expect(shouldInterceptResult).toEqual(false);
			expect(loginRequiredCalled).toEqual(false);
			shouldInterceptCalled = false;
			shouldInterceptResult = undefined;
			
			$httpBackend.when("GET", "/test/toto")
				.respond(200, {value:"OK"});
			$http.get('/test/toto');
			expect($httpBackend.flush).toThrow();
			
			expect(shouldInterceptCalled).toBe(true);
			expect(shouldInterceptResult).toBe(true);
			expect(loginRequiredCalled).toBe(true);
		});
	});
});
