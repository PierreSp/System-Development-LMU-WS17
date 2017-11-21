/* global sysdevinterface L */

sysdevinterface.service('modifyMap', ['$timeout', 'leafletData', 'reverseGeocode', function ($timeout, leafletData, reverseGeocode) {
  let that = this

  /* handels the changing of the map imagery */
  this.changeBaselayer = function (model, layer) {
    model.map.layers.baselayers = {}
    model.map.layers.baselayers[layer] = model.map.baselayers[layer]
  }

  /* adds marker to the map and safes it to the marker directory, reverse geocoding is triggered */
  this.addMarker = function (model, event, args, update) {
    let markers = model.map.markers

    if (update) {
      markers[args.modelName].lat = args.model.lat
      markers[args.modelName].lng = args.model.lng
      reverseGeocode.reverseMarker(model, args.modelName)
      model.map.geojson = []
    } else if (markers.length < 2) {
      markers.push({
        lat: args.leafletEvent.latlng.lat,
        lng: args.leafletEvent.latlng.lng,
        draggable: true,
        focus: true
      })
      reverseGeocode.reverseMarker(model, markers.length - 1)
    }
  }

  /* remove markers from the map */
  this.removeMarker = function (model) {
    model.map.markers = []
  }

  /* binds a pupup to a route segment, handles route highlighting when user hovers over geoJson */
  this.handleMousOverGeoJson = function (model, event, args) {
    let index = args.leafletEvent.target.feature.properties.index
    if (model.algorithmKind === 'single') {
      let instructions = model.map.geojson.data.features[index].properties.instructions

      let popupContent = ''
      if (instructions[2] !== 'NULL' && instructions[2]) {
        popupContent = 'Take <b>' + instructions[2] + '</b> from<br/><b>' + instructions[0] + '</b> to<br/><b>' + instructions[1] + '</b>'
      } else popupContent = 'Drive or walk from<br/><b>' + instructions[0] + '</b> to<br/><b>' + instructions[1] + '</b>'
      args.leafletEvent.target.bindPopup(popupContent)
    }

    args.leafletEvent.target.setStyle({color: '#68c631'}).bringToFront()
    model.selected.hover = index
  }

  /* handles the mouseout event on the geoJSON-Layer */
  this.handleMousOutGeoJson = function (model, event, args) {
    that.colorifyRoute()
    model.selected.hover = -1
  }

  /* handles the route highlighting when user hovers over route instructions */
  this.highlightSegment = function (model, index, inFlag) {
    leafletData.getMap()
      .then(map => {
        let i = 0
        for (let j in map._layers) {
          if (i === 4) {
            for (let k in map._layers[j]._layers) {
              if (map._layers[j]._layers[k].feature.properties.index === index) {
                inFlag
                ? map._layers[j]._layers[k].setStyle({color: '#68c631'}).bringToFront()
                : that.colorifyRoute()
              }
            }
          }
          i++
        }
      })
  }

  /* coloring of the route on the map depending on the mode of transportation */
  this.colorifyRoute = function () {
    leafletData.getMap()
      .then(map => {
        let i = 0
        for (let j in map._layers) {
          if (i === 4) {
            for (let k in map._layers[j]._layers) {
              map._layers[j]._layers[k].setStyle(map._layers[j]._layers[k].feature.properties.mode === 'PUBLIC' ? {color: '#c63168'} : {color: '#0033ff'})
            }
          }
          i++
        }
      })
  }

  /* centers the map on the elements on the map */
  this.centerOnRoute = function (model, bBox) {
    let latlngs = []
    if (!bBox) {
      for (let marker of model.map.markers) {
        let coord = [marker.lng, marker.lat]
        latlngs.push(L.GeoJSON.coordsToLatLng(coord))
      }
      for (let j in model.map.paths) {
        let coord = [model.map.paths[j].latlngs.lng, model.map.paths[j].latlngs.lat]
        latlngs.push(L.GeoJSON.coordsToLatLng(coord))
      }
      if (model.map.geojson.data) {
        for (let feature of model.map.geojson.data.features) {
          for (let coordinate of feature.geometry.coordinates) {
            let coord = [coordinate[0], coordinate[1]]
            latlngs.push(L.GeoJSON.coordsToLatLng(coord))
          }
        }
      }
    } else {
      for (let coord of bBox) {
        latlngs.push(L.GeoJSON.coordsToLatLng(coord))
      }
    }
    leafletData.getMap()
      .then(map => {
        map.fitBounds(latlngs, {paddingTopLeft: [20, 20], paddingBottomRight: [250, 20]})
      })
    $timeout(() => { that.colorifyRoute() }, 200)
  }
}])
