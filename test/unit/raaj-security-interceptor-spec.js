'use strict';

describe('raajSecurityInterceptor', function() {

	it('should be given a specific function to control weither a request should be intercepted or not', function() {
		var shouldInterceptCalled = false,
			shouldInterceptResult = undefined;
		
		angular.module('myApp', ['raajSecurityInterceptor']).config(function(raajSecurityInterceptorProvider) {
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
		module('raajSecurityInterceptor', 'myApp');

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
	
	it('should be able to bypass the Authorization header computation with raajBypassSecurity', function() {
		module('raajSecurityInterceptor', 'raajSecurityUtils');

		inject(function($http, $rootScope, $httpBackend, RaajAesUtil) {
			// mocking login
			$httpBackend.expect("GET", "api/security/getTime")
				.respond(200, new Date().getTime());
			// logging in
			$rootScope.$broadcast('raaj:auth-tryLogin' , 'user', 'password');
			$httpBackend.flush();
			
			// mocking bypass
			var token = 'testtoken',
				iv = '123456789abcdef0123456789abcdef0',
				salt = '123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0',
				keySize = 128,
				iterationCount = 50,
				aesUtil = new RaajAesUtil(keySize, iterationCount),
				encryptedToken = aesUtil.encrypt(salt, iv, 'password', token);
			
			$httpBackend.expect("GET", "api/security/createBypassSecurityToken").respond(200, {
				salt: salt,
				iv: iv,
				keySize: keySize,
				iterationCount: iterationCount,
				encryptedToken: encryptedToken
			});
			
			// mocking simple api
			$httpBackend.expect("GET", "api/test", undefined, function(headers) {
				return headers['X-IRAJ-BypassSecurityToken']; // assert that header exists
			}).respond(200, "OK");
			
			var response = null
			// calling bypass
			$http.get('api/test', {raajBypassSecurity: true}).success(function(_response_) {
				response = _response_;
			});
			$httpBackend.flush();
			expect(response).toBe("OK");
		});
	});
});
