(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('FlowDialogController', FlowDialogController);

    FlowDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Flow'];

    function FlowDialogController ($scope, $stateParams, $uibModalInstance, entity, Flow) {
        var vm = this;
        vm.flow = entity;
        vm.load = function(id) {
            Flow.get({id : id}, function(result) {
                vm.flow = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('logPosterEndpointApp:flowUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.flow.id !== null) {
                Flow.update(vm.flow, onSaveSuccess, onSaveError);
            } else {
                Flow.save(vm.flow, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
