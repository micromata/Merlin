import {
    LOG_VIEW_CHANGE_FILTER,
    LOG_VIEW_FAILED_RELOAD,
    LOG_VIEW_RELOADED,
    LOG_VIEW_REQUEST_RELOAD
} from '../actions/types';

const initialState = {
    filters: {
        threshold: 'info',
        search: '',
        locationFormat: 'none',
        showStackTrace: 'false',
        maxSize: '100',
        ascendingOrder: 'false'
    },
    loading: false,
    failed: false,
    entries: []
};

const reducer = (state = initialState, action) => {
    switch (action.type) {
        case LOG_VIEW_REQUEST_RELOAD:
            return Object.assign({}, state, {
                loading: true,
                failed: false
            });
        case LOG_VIEW_RELOADED:
            return Object.assign({}, state, {
                loading: false,
                entries: action.payload
            });
        case LOG_VIEW_CHANGE_FILTER:
            return Object.assign({}, state, {
                filters: {
                    ...state.filters,
                    [action.payload.name]: action.payload.value
                }
            });
        case LOG_VIEW_FAILED_RELOAD: {
            return Object.assign({}, state, {
                loading: false,
                failed: true
            });
        }
        default:
            return state;
    }
};

export default reducer;
