(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('LogDeleteController',LogDeleteController);

    LogDeleteController.$inject = ['$uibModalInstance', 'entity', 'Log'];

    function LogDeleteController($uibModalInstance, entity, Log) {
        var vm = this;
        vm.log = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Log.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
