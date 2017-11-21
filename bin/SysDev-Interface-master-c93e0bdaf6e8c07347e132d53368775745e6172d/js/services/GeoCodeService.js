/* global sysdevinterface */

sysdevinterface.service('reverseGeocode', [ '$http', function ($http) {
  const hereUrl = 'https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?'
  const appId = 'WmIkt7vA4CQCMLSXEmOf'
  const appCode = 'LBj3S0_CED-_JWWO4VvUcg'
  const auth = '&app_id=' + appId + '&app_code=' + appCode
  const features = '&mode=retrieveAddresses&gen=9&language=en&maxresults=5'

  /* handles the reverse geocoding of markers and saves the physical address to the markers directory */
  this.reverseMarker = function (model, index) {
    let prox = 'prox=' + model.map.markers[index].lat + ',' + model.map.markers[index].lng + ',1000'

    $http.get(hereUrl + prox + auth + features)
      .then(response => {
        let i = 0
        if (response.data.Response.View[0].Result[0]['MatchLevel'] === 'houseNumber') i = 0
        else if (response.data.Response.View[0].Result.length >= 2 && response.data.Response.View[0].Result[1].Location.Address.Street) i = 1
        model.map.markers[index].formattedAddress = response.data.Response.View[0].Result[i].Location.Address.Label.split(', ')
        model.map.markers[index].formattedAddress.pop()
        model.infoDrop = true
      })
  }

  /* handles the revers geocoding of route instructions */
  this.reverseInstructions = function (model) {
    for (let feature of model.map.geojson.data.features) {
      for (let index in feature.properties.instructions) {
        if (feature.properties.instructions[index] instanceof Array || feature.properties.instructions[index] instanceof Object) {
          let prox = 'prox=' + feature.properties.instructions[index]['0'] + ',' + feature.properties.instructions[index]['1'] + ',1000'
          $http.get(hereUrl + prox + auth + features)
            .then(response => {
              let i = 0
              if (response.data.Response.View[0].Result[0]['MatchLevel'] === 'houseNumber') i = 0
              else if (response.data.Response.View[0].Result.length >= 2) i = 1
              feature.properties.instructions[index] = response.data.Response.View[0].Result[i].Location.Address.Label.split(', ')
              feature.properties.instructions[index] = feature.properties.instructions[index][0]
            })
        }
      }
    }
  }
}])
