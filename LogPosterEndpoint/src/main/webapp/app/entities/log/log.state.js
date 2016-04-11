(function() {
    'use strict';

    angular
        .module('logPosterEndpointApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('log', {
            parent: 'entity',
            url: '/log?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'logPosterEndpointApp.log.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/log/logs.html',
                    controller: 'LogController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('log');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('log-detail', {
            parent: 'entity',
            url: '/log/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'logPosterEndpointApp.log.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/log/log-detail.html',
                    controller: 'LogDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('log');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Log', function($stateParams, Log) {
                    return Log.get({id : $stateParams.id});
                }]
            }
        })
        .state('log.new', {
            parent: 'log',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/log/log-dialog.html',
                    controller: 'LogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                createdTime: null,
                                text: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('log', null, { reload: true });
                }, function() {
                    $state.go('log');
                });
            }]
        })
        .state('log.edit', {
            parent: 'log',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/log/log-dialog.html',
                    controller: 'LogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Log', function(Log) {
                            return Log.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('log', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('log.delete', {
            parent: 'log',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/log/log-delete-dialog.html',
                    controller: 'LogDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Log', function(Log) {
                            return Log.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('log', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
