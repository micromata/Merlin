import {CONFIG_LOAD, CONFIG_RECEIVED, CONFIG_SET, CONFIG_SET_PROPERTY} from '../actions/types';

// TODO CHANGE INITIAL STATE
const initialState = {
    port: 8042,
    language: 'en'
};
//const initialState = {};

const postConfig = config => {
    console.log('Config', JSON.stringify(config));
    fetch('/rest/configuration/config', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(config)
    })
        .then(response => {
            // TODO SHOW ERRORS
        });
};

const reducer = (state = initialState, action) => {
    switch (action.type) {

        case CONFIG_LOAD:
            // TODO LOAD CONFIG
            return state;

        case CONFIG_RECEIVED:
            return action.payload;

        case CONFIG_SET:
            postConfig(state);
            return state;

        case CONFIG_SET_PROPERTY:
            state = Object.assign({}, state, {
                [action.payload.property.toLowerCase()]: action.payload.value
            });
            postConfig(state);
            return state;

        default:
            return state;
    }
};

export default reducer;
