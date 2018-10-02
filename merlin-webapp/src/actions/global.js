// Later Webpack, Context etc. should be used instead.

global.server = 'http://localhost:8042';
global.restBaseUrl = global.server + '/rest';

export function getRestServiceUrl(restService) {
    return global.restBaseUrl + '/' + restService;
}

function format2Digits(number) {
    return (number < 10) ? "0" + number : number;
}

export function getISOTimestamp(date) {
    return date.getFullYear() + '-' + format2Digits(date.getMonth() + 1) + '-' + format2Digits(date.getDate()) + '_'
        + format2Digits(date.getHours()) + '.' + format2Digits(date.getMinutes());
}

export function getResponseHeaderFilename(contentDisposition) {
    var regex = /filename[^;=\n]*=(UTF-8(['"]*))?(.*)/;
    var matches = regex.exec(contentDisposition);
    var filename;
    if (matches != null && matches[3]) {
        filename = matches[3].replace(/['"]/g, '');
    }
    return filename ? decodeURI(filename) : "download";
}