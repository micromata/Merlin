import {
    TEMPLATE_LIST_RECEIVED,
    TEMPLATE_LIST_REQUEST_FAILED,
    TEMPLATE_LIST_REQUESTED,
    TEMPLATE_RECEIVED,
    TEMPLATE_REQUEST_FAILED,
    TEMPLATE_REQUESTED
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

const getHeader = () => ({
    method: 'GET',
    headers: {
        'Accept': 'application/json'
    }
});

export const listTemplates = () => dispatch => {
    dispatch(requestTemplateList());

    return fetch(`${path}list`, getHeader())
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
