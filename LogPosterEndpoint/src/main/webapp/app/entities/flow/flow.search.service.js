(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .factory('FlowSearch', FlowSearch);

    FlowSearch.$inject = ['$resource'];

    function FlowSearch($resource) {
        var resourceUrl =  'api/_search/flows/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
