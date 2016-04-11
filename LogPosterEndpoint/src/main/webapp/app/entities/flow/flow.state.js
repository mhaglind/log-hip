(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('flow', {
            parent: 'entity',
            url: '/flow',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'logPosterEndpointApp.flow.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/flow/flows.html',
                    controller: 'FlowController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('flow');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('flow-detail', {
            parent: 'entity',
            url: '/flow/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'logPosterEndpointApp.flow.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/flow/flow-detail.html',
                    controller: 'FlowDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('flow');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Flow', function($stateParams, Flow) {
                    return Flow.get({id : $stateParams.id});
                }]
            }
        })
        .state('flow.new', {
            parent: 'flow',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/flow/flow-dialog.html',
                    controller: 'FlowDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                text: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('flow', null, { reload: true });
                }, function() {
                    $state.go('flow');
                });
            }]
        })
        .state('flow.edit', {
            parent: 'flow',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/flow/flow-dialog.html',
                    controller: 'FlowDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Flow', function(Flow) {
                            return Flow.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('flow', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('flow.delete', {
            parent: 'flow',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/flow/flow-delete-dialog.html',
                    controller: 'FlowDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Flow', function(Flow) {
                            return Flow.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('flow', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
