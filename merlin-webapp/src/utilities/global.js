// Later Webpack, Context etc. should be used instead.

export const isDevelopmentMode = () => {
    return process.env.NODE_ENV === 'development';
}

global.testserver = 'http://localhost:8042';
global.restBaseUrl = (isDevelopmentMode() ? global.testserver : '') + '/rest';

// Remove registered server workers from Merlin version <= V0.5 and get rid of caching hell:
if (window.navigator && navigator.serviceWorker) {
    navigator.serviceWorker.getRegistrations()
        .then(function (registrations) {
            for (let registration of registrations) {
                console.log('Found serviceWorker registration.');
                registration.unregister();
            }
        });
}


const createQueryParams = params =>
    Object.keys(params)
        .map(k => `${k}=${encodeURI(params[k])}`)
        .join('&');

export const getRestServiceUrl = (restService, params) => {
    if (params) return `${global.restBaseUrl}/${restService}?${createQueryParams(params)}`;
    return `${global.restBaseUrl}/${restService}`;
}

export const getResponseHeaderFilename = contentDisposition => {
    const matches = /filename[^;=\n]*=(UTF-8(['"]*))?(.*)/.exec(contentDisposition);
    return matches && matches.length >= 3 && matches[3] ? decodeURI(matches[3].replace(/['"]/g, '')) : 'download';
};

export const formatDateTime = (millis) => {
    var options = {year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'};
    const date = new Date(millis);
    return date.toLocaleDateString(options) + ' ' + date.toLocaleTimeString(options);
    //return date.toLocaleDateString("de-DE", options);
}

/* Checks if a given array is definied and is not empty. */
export const arrayNotEmpty = (array) => {
    return array && array.length;
}
