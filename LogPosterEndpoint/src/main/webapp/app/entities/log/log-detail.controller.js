(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('LogDetailController', LogDetailController);

    LogDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Log', 'Context', 'Flow'];

    function LogDetailController($scope, $rootScope, $stateParams, entity, Log, Context, Flow) {
        var vm = this;
        vm.log = entity;
        vm.load = function (id) {
            Log.get({id: id}, function(result) {
                vm.log = result;
            });
        };
        var unsubscribe = $rootScope.$on('logPosterEndpointApp:logUpdate', function(event, result) {
            vm.log = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
