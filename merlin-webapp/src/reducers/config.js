import {
    CONFIG_FETCH_FAILED,
    CONFIG_RECEIVED,
    CONFIG_REQUESTED,
    CONFIG_SET,
    CONFIG_SET_PROPERTY
} from '../actions/types';

const basePath = 'http://localhost:8042';

const initialState = {
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
            return Object.assign({}, state, {
                isFetching: true,
                loaded: false
            });

        case CONFIG_FETCH_FAILED:
            return Object.assign({}, state, {
                isFetching: false,
                failed: true,
                loaded: true
            });

        case CONFIG_RECEIVED:
            return Object.assign({}, state, {
                isFetching: false,
                failed: false,
                loaded: true,
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
