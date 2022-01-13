angular.module('AnonTestApp', [])
   .controller('MainController', function($scope, $timeout, $http) {

       $scope.title = "Anon Traffic Simulation";
       $scope.value = 0;
       $scope.polling = false;
       $scope.error = false;
       $scope.querying = false;

       var url = "/Anon/client/list/traffic";

       $scope.start = function() {
         if (!$scope.polling) {
           poll();
           $scope.polling = true;
         }
       };

       $scope.stop = function() {
         if ($scope.polling) {
           $scope.polling = false;
         }
       };

       var getData = function() {
        return $http({
            method: 'GET',
            url: url,
         });
       };

       var poll = function() {
         $timeout(function() {
            $scope.value++;
            $scope.querying = true;
            getData().then(function success(response) {
              $scope.flows = response.data.flows;
              $scope.time = response.data.time;
              $scope.querying = false;
            }, function error(response) {
              $scope.error = true;
            });
            if ($scope.polling) {
              poll();
            }
         }, 2000);
        };
});
