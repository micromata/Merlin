import {CONFIG_FETCH_FAILED, CONFIG_RECEIVED, CONFIG_REQUESTED, CONFIG_SET, CONFIG_SET_PROPERTY} from './types';

const basePath = 'http://localhost:8042';

const requestConfig = () => ({
    type: CONFIG_REQUESTED,
});

export const receivedConfig = data => ({
    type: CONFIG_RECEIVED,
    payload: data
});

export const failedConfigFetch = () => ({
    type: CONFIG_FETCH_FAILED
});

export const setConfig = config => ({
    type: CONFIG_SET,
    payload: config
});

export const setConfigProperty = (property, value) => ({
    type: CONFIG_SET_PROPERTY,
    payload: {
        property, value
    }
});

export const fetchConfig = () => dispatch => {
    dispatch(requestConfig());

    return fetch(`${basePath}/rest/configuration/config`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => response.json())
        .then(json => dispatch(receivedConfig(json.configuration)))
        .catch(() => dispatch(failedConfigFetch()));
};

const shouldFetchConfig = config => !config.failed && !config.properties && !config.isFetching;

export const fetchConfigIfNeeded = () => (dispatch, getState) => {
    if (shouldFetchConfig(getState().config)) {
        dispatch(fetchConfig());
    }
};
