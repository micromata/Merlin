import {CONFIG_CHANGED_PROPERTY, CONFIG_FETCH_FAILED, CONFIG_RECEIVED, CONFIG_REQUESTED} from '../actions/types';

const reducer = (state = {}, action) => {
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

        case CONFIG_CHANGED_PROPERTY:
            return Object.assign({}, state, {
                properties: {
                    ...state.properties,
                    [action.payload.property]: action.payload.value
                }
            });

        default:
            return state;
    }
};

export default reducer;
