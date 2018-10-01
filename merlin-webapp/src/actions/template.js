import {
    TEMPLATE_LIST_RECEIVED,
    TEMPLATE_LIST_REQUEST_FAILED,
    TEMPLATE_LIST_REQUESTED,
    TEMPLATE_RECEIVED,
    TEMPLATE_REQUEST_FAILED,
    TEMPLATE_REQUESTED,
    TEMPLATE_RUN_FAILED,
    TEMPLATE_RUN_REQUESTED,
    TEMPLATE_RUN_SUCCESS
} from './types';

const path = 'http://localhost:8042/rest/templates/';

const requestTemplateList = () => ({
    type: TEMPLATE_LIST_REQUESTED
});

const receivedTemplateList = data => ({
    type: TEMPLATE_LIST_RECEIVED,
    payload: data
});

const failedTemplateListRequest = error => ({
    type: TEMPLATE_LIST_REQUEST_FAILED,
    payload: error
});

const requestTemplate = id => ({
    type: TEMPLATE_REQUESTED,
    payload: {id}
});

const receivedTemplate = (id, data) => ({
    type: TEMPLATE_RECEIVED,
    payload: {
        id,
        template: data
    }
});

const failedTemplateRequest = (id, error) => ({
    type: TEMPLATE_REQUEST_FAILED,
    payload: {
        id, error
    }
});

const requestTemplateRun = (data) => ({
    type: TEMPLATE_RUN_REQUESTED,
    payload: {...data}
});

const succeedTemplateRun = (data) => ({
    type: TEMPLATE_RUN_SUCCESS,
    payload: {...data}
});

const failedTemplateRun = (data, error) => ({
    type: TEMPLATE_RUN_FAILED,
    payload: {...data, error}
});

const getHeader = () => ({
    method: 'GET',
    headers: {
        'Accept': 'application/json'
    }
});

export const runTemplate = (data) => dispatch => {
    dispatch(requestTemplateRun(data));

    return fetch(`${path}run`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            return response.blob();
        })
        .then(blob => {
            dispatch(succeedTemplateRun(data));

            const a = document.createElement('a');
            a.style = {
                display: 'none'
            };
            a.href = URL.createObjectURL(blob);
            a.download = `${data.templateId}.xlsx`;

            document.body.appendChild(a);
            a.click();
            URL.revokeObjectURL(a.href);
            a.remove();
        })
        .catch(error => dispatch(failedTemplateRun(data, error)));
};

export const listTemplates = () => dispatch => {
    dispatch(requestTemplateList());

    return fetch(`${path}definition-list`, getHeader())
        .then(response => response.json())
        .then(json => dispatch(receivedTemplateList(json)))
        .catch(error => dispatch(failedTemplateListRequest(error)));
};

export const getTemplate = id => dispatch => {
    dispatch(requestTemplate(id));

    return fetch(`${path}${id}`, getHeader())
        .then(response => response.json())
        .then(json => dispatch(receivedTemplate(id, json)))
        .catch(error => dispatch(failedTemplateRequest(id, error)));
};

const shouldLoadTemplates = templates => !templates.failed && !templates.list && !templates.isFetching;

export const listTemplatesIfNeeded = () => (dispatch, getState) => {
    if (shouldLoadTemplates(getState().templates)) {
        dispatch(listTemplates());
    }
};
