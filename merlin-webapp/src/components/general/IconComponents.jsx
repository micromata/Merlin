import React from 'react';
import {library} from '@fortawesome/fontawesome-svg-core';
import {faCheckSquare, faCoffee} from '@fortawesome/free-solid-svg-icons';
import {
    faCheck,
    faPlus,
    faSync,
    faUpload,
    faTrash
} from '@fortawesome/free-solid-svg-icons'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

function IconAdd() {
    return (
        <FontAwesomeIcon icon={faPlus}/>
    );
}

function IconCheck() {
    return (
        <FontAwesomeIcon icon={faCheck}/>
    );
}

function IconRefresh() {
    return (
        <FontAwesomeIcon icon={faSync}/>
    );
}

function IconRemove() {
    return (
        <FontAwesomeIcon icon={faTrash}/>
    );
}

function IconUpload() {
    return (
        <FontAwesomeIcon icon={faUpload}/>
    );
}

export {
    IconAdd, IconCheck, IconRefresh, IconRemove, IconUpload
};
