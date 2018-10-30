import {VERSION_RELOAD_FAILED, VERSION_RELOADED, VERSION_REQUEST_RELOAD} from '../actions/types';
import {fetchNewDictionary} from '../utilities/i18n';

const initialState = {
    version: '0.0.0',
    buildDate: 'never',
    language: null,
    loading: false
};

const reducer = (state = initialState, action) => {
    switch (action.type) {
        case VERSION_REQUEST_RELOAD:
            return Object.assign({}, state, {
                loading: true,
                failed: false
            });
        case VERSION_RELOADED:

            if (state.version !== action.payload.version ||
                state.language !== action.payload.language) {
                //console.log("reducers/version.js: state.version=" + state.version + ", payload.version=" + action.payload.version + ", state.lang=" + state.language + ", payload.lang=" + action.payload.language);
                fetchNewDictionary(action.payload.version, action.payload.language);
            }

            return Object.assign({}, state, {
                loading: false,
                failed: false,
                version: action.payload.version,
                language: action.payload.language,
                buildDate: action.payload.buildDate,
                updateVersion: action.payload.updateVersion
            });
        case VERSION_RELOAD_FAILED:
            return Object.assign({}, state, {
                loading: false,
                failed: true
            });
        default:
            return state;
    }
};

export default reducer;
