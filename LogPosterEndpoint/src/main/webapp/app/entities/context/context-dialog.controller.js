(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('ContextDialogController', ContextDialogController);

    ContextDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Context'];

    function ContextDialogController ($scope, $stateParams, $uibModalInstance, entity, Context) {
        var vm = this;
        vm.context = entity;
        vm.load = function(id) {
            Context.get({id : id}, function(result) {
                vm.context = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('logPosterEndpointApp:contextUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.context.id !== null) {
                Context.update(vm.context, onSaveSuccess, onSaveError);
            } else {
                Context.save(vm.context, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
