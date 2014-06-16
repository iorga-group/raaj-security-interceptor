/*
 * Copyright (C) 2014 Iorga Group
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
 */
(function () {
    'use strict';
    // based on https://github.com/witoldsz/angular-http-auth/blob/master/src/http-auth-interceptor.js

    angular.module('raajSecurityInterceptor', ['raajAuthenticationService', 'raajSecurityUtils'])
        .provider('raajSecurityInterceptor', function() {
            var apiPrefix = 'api/',
            	shouldInterceptRequestFn = function(config) {
	            	return config.url.indexOf(apiPrefix) == 0;
	            };

            this.setApiPrefix = function(apiPrefixParam) {
                apiPrefix = apiPrefixParam;
            }
            
            this.setShouldInterceptRequestFn = function(fn) {
            	shouldInterceptRequestFn = fn;
            }

            this.$get = function() {
                return {
                    apiPrefix: apiPrefix,
                    shouldInterceptRequest: shouldInterceptRequestFn
                };
            }
        })
        .factory('raajSecurityRequestInterceptor', function($q, $rootScope, raajSecurityInterceptor, raajAuthenticationService, RaajAesUtil, $injector) {
        	var $http;
            return {
                'request': function(config) {
                    if (raajSecurityInterceptor.shouldInterceptRequest(config)) {
                        if (!raajAuthenticationService.authenticated && !config.authenticating) {
                            var deferred = $q.defer();
                            raajAuthenticationService.appendQuery(config, deferred);
                            $rootScope.$broadcast('raaj:auth-loginRequired');
                            return deferred.promise;
                        } else {
                            // this is an api request, let's add the Authorization header
                        	if (config.raajBypassSecurity) {
                        		// request which require to bypass the security header
                        		// we well first ask a bypassSecurityToken
                        		$http = $http || $injector.get('$http'); // Lazy inject to avoid angular circular dependency
                        		var deferred = $q.defer();
                        		$http.get(raajSecurityInterceptor.apiPrefix+'security/createBypassSecurityToken').success(function(data) {
									var aesUtil = new RaajAesUtil(data.keySize, data.iterationCount);
									var bypassSecurityToken = aesUtil.decrypt(data.salt, data.iv, raajAuthenticationService.digestedPassword, data.encryptedToken);
									
									config.headers['X-IRAJ-BypassSecurityToken'] = bypassSecurityToken;
									
									raajAuthenticationService.addAuthorizationHeader(config);
									
									// and continue the original request
									deferred.resolve(config);
                        		});
                        		return deferred.promise;
                        	} else {
                        		// classical request
                        		raajAuthenticationService.addAuthorizationHeader(config);
                        	}
                        }
                    }
                    return config;
                }
            };
        })
        .config(function ($httpProvider) {
            $httpProvider.interceptors.push('raajSecurityRequestInterceptor');
        })
    ;
})();