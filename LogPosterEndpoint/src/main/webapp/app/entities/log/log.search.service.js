(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .factory('LogSearch', LogSearch);

    LogSearch.$inject = ['$resource'];

    function LogSearch($resource) {
        var resourceUrl =  'api/_search/logs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
