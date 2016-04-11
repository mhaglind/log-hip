(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('FlowDeleteController',FlowDeleteController);

    FlowDeleteController.$inject = ['$uibModalInstance', 'entity', 'Flow'];

    function FlowDeleteController($uibModalInstance, entity, Flow) {
        var vm = this;
        vm.flow = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Flow.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
