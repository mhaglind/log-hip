(function() {
    'use strict';
    angular
        .module('logPosterEndpointApp')
        .factory('Flow', Flow);

    Flow.$inject = ['$resource'];

    function Flow ($resource) {
        var resourceUrl =  'api/flows/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
