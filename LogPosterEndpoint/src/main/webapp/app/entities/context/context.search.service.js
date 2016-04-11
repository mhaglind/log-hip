(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .factory('ContextSearch', ContextSearch);

    ContextSearch.$inject = ['$resource'];

    function ContextSearch($resource) {
        var resourceUrl =  'api/_search/contexts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
