import {TEMPLATE_RUN_SAVE_CONFIGURATION} from '../actions/types';

const initialState = {
    runConfigurations: {}
};

const reducer = (state = initialState, action) => {
    switch (action.type) {
        case TEMPLATE_RUN_SAVE_CONFIGURATION:
            return Object.assign({}, state, {
                runConfigurations: {
                    ...state.runConfigurations,
                    [action.payload.template]: action.payload.configuration
                }
            });
        default:
            return state;
    }
};

export default reducer;