import {VERSION_RELOADED, VERSION_REQUEST_RELOAD} from '../actions/types';

const initialState = {
    version: '0.0.0',
    buildDate: 'never',
    loading: false
};

const reducer = (state = initialState, action) => {
    switch (action.type) {
        case VERSION_REQUEST_RELOAD:
            return Object.assign({}, state, {
                loading: true
            });
        case VERSION_RELOADED:
            return Object.assign({}, state, {
                loading: false,
                version: action.payload.version,
                buildDate: action.payload.buildDate
            });
        default:
            return state;
    }
};

export default reducer;