import {
    TEMPLATE_LIST_RECEIVED,
    TEMPLATE_LIST_REQUEST_FAILED,
    TEMPLATE_LIST_REQUESTED,
    TEMPLATE_RECEIVED,
    TEMPLATE_REQUEST_FAILED,
    TEMPLATE_REQUESTED
} from '../actions/types';

const initialState = {
    isFetching: false,
    failed: false,
    loaded: false
};

const reducer = (state = initialState, action) => {
    switch (action.type) {

        case TEMPLATE_LIST_REQUESTED:
            return Object.assign({}, state, {
                isFetching: true,
                loaded: false
            });

        case TEMPLATE_LIST_REQUEST_FAILED:
            return Object.assign({}, state, {
                isFetching: false,
                failed: true,
                loaded: false
            });

        case TEMPLATE_LIST_RECEIVED:
            console.log(action);

            return Object.assign({}, state, {
                isFetching: false,
                failed: false,
                loaded: true,
                list: {...action.payload}
            });

        // TODO HANDLE CASES
        case TEMPLATE_REQUESTED:
        case TEMPLATE_REQUEST_FAILED:
        case TEMPLATE_RECEIVED:
            console.log(action);
            return state;

        default:
            return state;
    }
};

export default reducer;
