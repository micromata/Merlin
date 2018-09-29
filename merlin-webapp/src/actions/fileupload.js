import {FILE_UPLOAD_FAILED, FILE_UPLOAD_FINISHED, FILE_UPLOAD_STARTED} from './types';

const path = 'http://localhost:8042/rest/files/upload';

const startedFileUpload = (file) => ({
    type: FILE_UPLOAD_STARTED,
    payload: {
        file
    }
});

const finishedFileUpload = (file) => ({
    type: FILE_UPLOAD_FINISHED,
    payload: {
        file
    }
});

const failedFileUpload = (file) => ({
    type: FILE_UPLOAD_FAILED,
    payload: {
        file
    }
});

export const uploadFile = file => dispatch => {
    dispatch(startedFileUpload(file));

    const formData = new FormData();
    formData.append('file', file);

    return fetch(path, {
        method: 'POST',
        body: formData
    })
        .then(() => dispatch(finishedFileUpload(file)))
        .catch(() => dispatch(failedFileUpload(file)));
};
