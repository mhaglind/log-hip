(function() {
    'use strict';
    angular
        .module('logPosterEndpointApp')
        .factory('Log', Log);

    Log.$inject = ['$resource', 'DateUtils'];

    function Log ($resource, DateUtils) {
        var resourceUrl =  'api/logs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.createdTime = DateUtils.convertDateTimeFromServer(data.createdTime);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
