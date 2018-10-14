import {LOG_VIEW_CHANGE_FILTER, LOG_VIEW_RELOADED, LOG_VIEW_REQUEST_RELOAD} from '../actions/types';

const initialState = {
    filters: {
        threshold: 'info',
        search: '',
        locationFormat: 'none',
        maxSize: '100',
        ascendingOrder: 'false'
    },
    loading: false
};

const reducer = (state = initialState, action) => {
    switch (action.type) {
        case LOG_VIEW_REQUEST_RELOAD:
            return Object.assign({}, state, {
                loading: true
            });
        case LOG_VIEW_RELOADED:
            return Object.assign({}, state, {
                // TODO ADD DATA FROM RESPONSE
                loading: false
            });
        case LOG_VIEW_CHANGE_FILTER:
            return Object.assign({}, state, {
                filters: {
                    ...state.filters,
                    [action.payload.name]: action.payload.value
                }
            });
        default:
            return state;
    }
};

export default reducer;