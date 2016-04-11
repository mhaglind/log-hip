(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('ContextDetailController', ContextDetailController);

    ContextDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Context'];

    function ContextDetailController($scope, $rootScope, $stateParams, entity, Context) {
        var vm = this;
        vm.context = entity;
        vm.load = function (id) {
            Context.get({id: id}, function(result) {
                vm.context = result;
            });
        };
        var unsubscribe = $rootScope.$on('logPosterEndpointApp:contextUpdate', function(event, result) {
            vm.context = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
