(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('context', {
            parent: 'entity',
            url: '/context',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'logPosterEndpointApp.context.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/context/contexts.html',
                    controller: 'ContextController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('context');
                    $translatePartialLoader.addPart('contextType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('context-detail', {
            parent: 'entity',
            url: '/context/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'logPosterEndpointApp.context.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/context/context-detail.html',
                    controller: 'ContextDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('context');
                    $translatePartialLoader.addPart('contextType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Context', function($stateParams, Context) {
                    return Context.get({id : $stateParams.id});
                }]
            }
        })
        .state('context.new', {
            parent: 'context',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/context/context-dialog.html',
                    controller: 'ContextDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                contextType: null,
                                text: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('context', null, { reload: true });
                }, function() {
                    $state.go('context');
                });
            }]
        })
        .state('context.edit', {
            parent: 'context',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/context/context-dialog.html',
                    controller: 'ContextDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Context', function(Context) {
                            return Context.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('context', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('context.delete', {
            parent: 'context',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/context/context-delete-dialog.html',
                    controller: 'ContextDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Context', function(Context) {
                            return Context.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('context', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
