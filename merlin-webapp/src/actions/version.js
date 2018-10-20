import {VERSION_RELOADED, VERSION_REQUEST_RELOAD} from './types';
import {getRestServiceUrl} from '../utilities/global';

const requestedVersionReload = () => ({
    type: VERSION_REQUEST_RELOAD
});

const reloadedVersion = (json) => ({
    type: VERSION_RELOADED,
    payload: {
        version: json.version,
        buildDate: json.buildDate
    }
});

export const loadVersion = () => (dispatch, getState) => {
    if (getState().version.loading) {
        return;
    }

    dispatch(requestedVersionReload());

    fetch(getRestServiceUrl('version'), {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(json => dispatch(reloadedVersion(json)))
        .catch(alert);
};