/* global kangaroute */

/**
 * all constants are declared in config-service
 **/

sysdevinterface.factory('config', function () {
  let common = {
    attribution: 'Map &copy; 2016 <a href="http://developer.here.com">HERE</a>',
    subdomains: '1234',
    app_id: 'WmIkt7vA4CQCMLSXEmOf',
    app_code: 'LBj3S0_CED-_JWWO4VvUcg',
    mapID: 'newest',
    maxZoom: 18,
    language: 'eng',
    format: 'png8',
    size: '256',
    showOnSelector: false
  }
  let map = {
    name: 'Map',
    url: 'http://{s}.{base}.maps.api.here.com/maptile/2.1/{type}/{mapID}/{scheme}/{z}/{x}/{y}/{size}/{format}?app_id={app_id}&app_code={app_code}&lg={language}',
    type: 'xyz',
    layerOptions: Object.assign({
      base: 'base',
      type: 'maptile',
      scheme: 'normal.day'
    }, common)
  }
  let satellite = {
    name: 'Satellite',
    url: 'http://{s}.{base}.maps.api.here.com/maptile/2.1/{type}/{mapID}/{scheme}/{z}/{x}/{y}/{size}/{format}?app_id={app_id}&app_code={app_code}&lg={language}',
    type: 'xyz',
    layerOptions: Object.assign({
      base: 'aerial',
      type: 'maptile',
      scheme: 'hybrid.day'
    }, common)
  }
  /* dark grey map in preperation of visited node functionality */
  let grey = {
    name: 'Map',
    url: 'http://{s}.{base}.maps.api.here.com/maptile/2.1/{type}/{mapID}/{scheme}/{z}/{x}/{y}/{size}/{format}?app_id={app_id}&app_code={app_code}&lg={language}',
    type: 'xyz',
    layerOptions: Object.assign({
      base: 'base',
      type: 'blinetile',
      scheme: 'reduced.night'
    }, common)
  }
  let poi = {
    'eat-drink': 'Eat & Drink',
    'going-out': 'Going Out',
    'sights-museums': 'Sights & Museums',
    'transport': 'Transportation',
    'shopping': 'Shopping',
    'leisure-outdoor': 'Leisure & Outdoor',
    'accommodation,administrative-areas-buildings,natural-geographical,petrol-station,atm-bank-exchange,toilet-rest-area,hospital-health-care-facility': 'Other'
  }
  return {
    model: {
      map: {
        center: {
          lat: 52.512,
          lng: 13.401,
          zoom: 12
        },
        defaults: {
          maxZoom: 18,
          minZoom: 5
        },
        layers: {
        },
        baselayers: {
          map: map,
          satellite: satellite,
          grey: grey
        },
        geojson: [],
        markers: [],
        paths: []
      },
      simulation: {},
      poi: poi,
      selected: {
        poi: []
      }
    }
  }
})
