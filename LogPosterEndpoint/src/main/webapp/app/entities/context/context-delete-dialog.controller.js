(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('ContextDeleteController',ContextDeleteController);

    ContextDeleteController.$inject = ['$uibModalInstance', 'entity', 'Context'];

    function ContextDeleteController($uibModalInstance, entity, Context) {
        var vm = this;
        vm.context = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Context.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
