'use strict';

describe('Controller Tests', function() {

    describe('Flow Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockFlow;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockFlow = jasmine.createSpy('MockFlow');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Flow': MockFlow
            };
            createController = function() {
                $injector.get('$controller')("FlowDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'logPosterEndpointApp:flowUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
