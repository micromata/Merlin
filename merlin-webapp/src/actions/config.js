import {CONFIG_CHANGED_PROPERTY, CONFIG_FETCH_FAILED, CONFIG_RECEIVED, CONFIG_REQUESTED} from './types';

const path = 'http://localhost:8042/rest/configuration/config-old';

const requestConfig = () => ({
    type: CONFIG_REQUESTED
});

const receivedConfig = data => ({
    type: CONFIG_RECEIVED,
    payload: data
});

const failedConfigFetch = () => ({
    type: CONFIG_FETCH_FAILED
});

const changedConfigProperty = (property, value) => ({
    type: CONFIG_CHANGED_PROPERTY,
    payload: {
        property, value
    }
});

export const fetchConfig = () => dispatch => {
    dispatch(requestConfig());

    return fetch(path, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => response.json())
        .then(json => dispatch(receivedConfig(json)))
        .catch(() => dispatch(failedConfigFetch()));
};

export const updateConfigProperty = (property, value) => (dispatch, getState) => {
    return fetch(path, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(Object.assign({}, getState().config.properties, {
            [property]: value
        }))
    })
        .then(response => {
            if (response.status >= 200 && response.status < 300) {
                dispatch(changedConfigProperty(property, value))
            }
        })
};


export const fetchConfigIfNeeded = () => (dispatch, getState) => {
    if (shouldFetchConfig(getState().config)) {
        dispatch(fetchConfig());
    }
};

const shouldFetchConfig = config => !config.failed && !config.properties && !config.isFetching;
