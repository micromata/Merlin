import {CONFIG_LOAD, CONFIG_RECEIVED, CONFIG_SET, CONFIG_SET_PROPERTY} from './types';

export const loadConfig = () => ({
    type: CONFIG_LOAD,
});

export const receiveConfig = data => ({
    type: CONFIG_RECEIVED,
    payload: data
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
