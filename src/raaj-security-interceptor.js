/*
 * Copyright (C) 2013 Iorga Group
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

    angular.module('raaj-security-interceptor', ['raaj-authentication-service'])
        .provider('irajSecurityInterceptor', function() {
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
        .config(function ($httpProvider) {
            $httpProvider.interceptors.push(function($q, $rootScope, irajSecurityInterceptor, irajAuthenticationService) {
                return {
                    'request': function(config) {
                        if (irajSecurityInterceptor.shouldInterceptRequest(config)) {
                            if (!irajAuthenticationService.authenticated && !config.authenticating) {
                                var deferred = $q.defer();
                                irajAuthenticationService.appendQuery(config, deferred);
                                $rootScope.$broadcast('raaj:auth-loginRequired');
                                return deferred.promise;
                            } else {
                                // this is an api request, let's add the Authorization header
                                irajAuthenticationService.addAuthorizationHeader(config);
                            }
                        }
                        return config;
                    }
                }
            });
        })
    ;
})();