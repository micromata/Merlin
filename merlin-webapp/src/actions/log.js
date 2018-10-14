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

export const changeFilter = (event) => dispatch => dispatch(changedFilter(event.target.name, event.target.value));