import {CONFIG_RECEIVED, CONFIG_REQUESTED, CONFIG_SET, CONFIG_SET_PROPERTY} from '../actions/types';

const basePath = 'http://localhost:8042';

// TODO CHANGE INITIAL STATE
const initialState = {
    isFetching: false,
    loaded: false
};

const postConfig = config => {
    // TODO CHANGE BASE PATH
    fetch(basePath + '/rest/configuration/config', {
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

        case CONFIG_REQUESTED:
            console.log('Config will load', action);
            return Object.assign({}, state, {
                isFetching: true,
                loaded: false
            });

        case CONFIG_RECEIVED:
            console.log('Config received');
            return Object.assign({}, state, {
                isFetching: false,
                properties: {...action.payload}
            });

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
