import {TEMPLATE_RUN_SAVE_CONFIGURATION} from './types';

const savedTemplateRunConfiguration = (template, configuration) => ({
    type: TEMPLATE_RUN_SAVE_CONFIGURATION,
    payload: {template, configuration}
});

export const saveTemplateRunConfiguration = (template, configuration) => (dispatch) => {
    dispatch(template, configuration);
};