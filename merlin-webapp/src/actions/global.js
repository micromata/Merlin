// Later Webpack, Context etc. should be used instead.

global.server = 'http://localhost:8042';
global.restBaseUrl = global.server + '/rest';

export function getRestServiceUrl(restService) {
    return global.restBaseUrl + '/' + restService;
}