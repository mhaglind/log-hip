'use strict';

describe('Controller Tests', function() {

    describe('Log Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockLog, MockContext, MockFlow;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockLog = jasmine.createSpy('MockLog');
            MockContext = jasmine.createSpy('MockContext');
            MockFlow = jasmine.createSpy('MockFlow');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Log': MockLog,
                'Context': MockContext,
                'Flow': MockFlow
            };
            createController = function() {
                $injector.get('$controller')("LogDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'logPosterEndpointApp:logUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
