import React from 'react';
import {
    faCheck,
    faExclamationTriangle,
    faInfoCircle,
    faPlus,
    faSortDown,
    faSortUp,
    faSync,
    faTrash,
    faTimes,
    faSkullCrossbones,
    faUpload
} from '@fortawesome/free-solid-svg-icons'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

function IconAdd() {
    return (
        <FontAwesomeIcon icon={faPlus}/>
    );
}

function IconCancel() {
    return (
        <FontAwesomeIcon icon={faTimes}/>
    );
}

function IconCheck() {
    return (
        <FontAwesomeIcon icon={faCheck}/>
    );
}

function IconDanger() {
    return (
        <FontAwesomeIcon icon={faSkullCrossbones}/>
    );
}

function IconInfo() {
    return (
        <FontAwesomeIcon icon={faInfoCircle}/>
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

function IconSortDown() {
    return (
        <FontAwesomeIcon icon={faSortDown}/>
    );
}

function IconSortUp() {
    return (
        <FontAwesomeIcon icon={faSortUp}/>
    );
}

function IconUpload() {
    return (
        <FontAwesomeIcon icon={faUpload}/>
    );
}

function IconWarning() {
    return (
        <FontAwesomeIcon icon={faExclamationTriangle}/>
    );
}

export {
    IconAdd, IconCancel, IconCheck, IconDanger, IconInfo, IconRefresh, IconRemove, IconSortDown, IconSortUp, IconUpload, IconWarning
};
