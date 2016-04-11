(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('FlowDetailController', FlowDetailController);

    FlowDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Flow'];

    function FlowDetailController($scope, $rootScope, $stateParams, entity, Flow) {
        var vm = this;
        vm.flow = entity;
        vm.load = function (id) {
            Flow.get({id: id}, function(result) {
                vm.flow = result;
            });
        };
        var unsubscribe = $rootScope.$on('logPosterEndpointApp:flowUpdate', function(event, result) {
            vm.flow = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
