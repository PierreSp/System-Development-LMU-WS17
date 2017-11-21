/* global angular */

let sysdevinterface = angular.module('app', ['leaflet-directive', 'angular-loading-bar'])

/* module.config is needed to specify parameters for the loading bar and disable debug logging */
sysdevinterface.config(['cfpLoadingBarProvider', '$logProvider', function (cfpLoadingBarProvider, $logProvider) {
  cfpLoadingBarProvider.includeSpinner = false
  $logProvider.debugEnabled(false)
}])

sysdevinterface.controller('Controller',
  ['$scope', 'modifyMap', 'pathQueryService', 'config',
  function ($scope, modifyMap, pathQueryService, config) {
    /* function initialize gets the config and safes it to the scope
     * some additional data is safed to the scope as well */
    let initialize = function () {
      Object.assign($scope, config)
      $scope.model.map.layers['baselayers'] = $scope.model.map.baselayers
      pathQueryService.getInitialInformation($scope.model)
    }
    initialize()

    /* clicklistener on the layer switches */
    $scope.changeBaselayer = function (layer) {
      modifyMap.changeBaselayer($scope.model, layer)
    }

    /* clicklistener on the direct-route-Go-Button */
    $scope.calculate = function () {
      if ($scope.model.map.markers.length === 2) pathQueryService.fetchDirect($scope.model)
    }

    /* selectAlgo handles the selection of the algorithm */
    $scope.selectAlgo = function (algorithm) {
      pathQueryService.selectAlgo($scope.model, algorithm)
    }

    /* selectCost handles the selection of the algorithm costs */
    $scope.selectCost = function (cost) {
      pathQueryService.selectCost($scope.model, cost)
    }
  /* clicklistener on the remove-elements-Button to delete all elements on the map */
    $scope.removeElements = function () {
      //modifyMap.removeMarker($scope.model)
      pathQueryService.removeRoute($scope.model)
      $scope.model.infoDrop = false
    }

    /* hoverSegment handles the route highlighting */
    $scope.hoverSegment = function (index, flag) {
      $scope.model.selected['hover'] = flag ? index : -1
      modifyMap.highlightSegment($scope.model, index, flag)
    }

    /* clicklistener on the map */
    $scope.$on('leafletDirectiveMap.click', function (event, args) {
      modifyMap.addMarker($scope.model, event, args, false)
    })

    /* draglistener on markers */
    $scope.$on('leafletDirectiveMarker.dragend', function (event, args) {
      modifyMap.addMarker($scope.model, event, args, true)
    })

    /* mouseover on the geoJSON-Layer */
    $scope.$on('leafletDirectiveGeoJson.mouseover', function (event, args) {
      if ($scope.model.usedAlgorithm !== undefined) modifyMap.handleMousOverGeoJson($scope.model, event, args)
    })

    /* mouseout on the geoJSON-Layer */
    $scope.$on('leafletDirectiveGeoJson.mouseout', function (event, args) {
      if (args.target) modifyMap.handleMousOutGeoJson($scope.model, event, args)
    })

  }]
)
