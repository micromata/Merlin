import {getRestServiceUrl} from '../utilities/global';
import {LOG_VIEW_CHANGE_FILTER, LOG_VIEW_RELOADED, LOG_VIEW_REQUEST_RELOAD} from './types';

const requestedLogReload = () => ({
    type: LOG_VIEW_REQUEST_RELOAD
});

const reloadedLog = (data) => ({
    type: LOG_VIEW_RELOADED,
    payload: data
});

const changedFilter = (name, value) => ({
    type: LOG_VIEW_CHANGE_FILTER,
    payload: {name, value}
});

export const changeFilter = (name, value) => (dispatch) => dispatch(changedFilter(name, value));

const loadLog = (dispatch, getState) => {
    dispatch(requestedLogReload());

    const {filters} = getState().log;

    fetch(getRestServiceUrl('logging/query', {
        search: filters.search,
        treshold: filters.threshold,
        maxSize: filters.maxSize,
        showStackTrace: filters.showStackTrace,
        ascendingOrder: filters.ascendingOrder,
        // TODO ADD lastReceivedOrderNumber
    }), {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(json => dispatch(reloadedLog(json.map(entry => ({
            ...entry,
            level: entry.level.toLowerCase()
        })))));
};

export const requestLogReload = () => (dispatch, getState) => {
    if (!getState().log.loading) {
        loadLog(dispatch, getState);
    }
};