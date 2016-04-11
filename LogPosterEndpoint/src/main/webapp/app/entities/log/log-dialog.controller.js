(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .controller('LogDialogController', LogDialogController);

    LogDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Log', 'Context', 'Flow'];

    function LogDialogController ($scope, $stateParams, $uibModalInstance, $q, entity, Log, Context, Flow) {
        var vm = this;
        vm.log = entity;
        vm.predecessors = Log.query({filter: 'log-is-null'});
        $q.all([vm.log.$promise, vm.predecessors.$promise]).then(function() {
            if (!vm.log.predecessorId) {
                return $q.reject();
            }
            return Log.get({id : vm.log.predecessorId}).$promise;
        }).then(function(predecessor) {
            vm.predecessors.push(predecessor);
        });
        vm.successors = Log.query({filter: 'log-is-null'});
        $q.all([vm.log.$promise, vm.successors.$promise]).then(function() {
            if (!vm.log.successorId) {
                return $q.reject();
            }
            return Log.get({id : vm.log.successorId}).$promise;
        }).then(function(successor) {
            vm.successors.push(successor);
        });
        vm.contexts = Context.query();
        vm.flows = Flow.query();
        vm.load = function(id) {
            Log.get({id : id}, function(result) {
                vm.log = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('logPosterEndpointApp:logUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.log.id !== null) {
                Log.update(vm.log, onSaveSuccess, onSaveError);
            } else {
                Log.save(vm.log, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.createdTime = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
