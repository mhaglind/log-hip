(function() {
    'use strict';
    angular
        .module('logPosterEndpointApp')
        .factory('Context', Context);

    Context.$inject = ['$resource'];

    function Context ($resource) {
        var resourceUrl =  'api/contexts/:id';

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
