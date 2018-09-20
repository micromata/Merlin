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
                // MAPS THE ARRAY OF TEMPLATES TO A 'HashMap' (id => template)
               list: action.payload.reduce((accumulator, current) => ({
                   ...accumulator,
                   [current["id"]]: current
               }), {})
            });

        // TODO HANDLE CASES
        case TEMPLATE_REQUESTED:
        case TEMPLATE_REQUEST_FAILED:
            return state;

        case TEMPLATE_RECEIVED:
            return {
                ...state,
                list: Object.assign({}, state.list, {
                    [action.payload.id]: action.payload.template
                })
            };

        default:
            return state;
    }
};

export default reducer;
