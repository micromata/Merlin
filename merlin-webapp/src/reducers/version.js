import {VERSION_RELOADED, VERSION_REQUEST_RELOAD} from '../actions/types';
import {fetchNewDictionary} from '../utilities/i18n';

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

            if (state.version !== action.payload.version) {
                fetchNewDictionary(action.payload.version);
            }

            return Object.assign({}, state, {
                loading: false,
                version: action.payload.version,
                buildDate: action.payload.buildDate,
                updateVersion: action.payload.updateVersion
            });
        default:
            return state;
    }
};

export default reducer;
