import React from 'react';
import PropTypes from 'prop-types';
import {Table} from 'reactstrap';
import LogEntry from './LogEntry';
import {IconSortDown, IconSortUp} from '../../general/IconComponents';
import I18n from '../../general/translation/I18n';

const getLocationString = (locationFormat, entry) => {
    switch (locationFormat) {
        case 'short':
            return `${entry.javaClassSimpleName}:${entry.methodName}:${entry.lineNumber}`;
        case 'normal':
            return `${entry.javaClass}:${entry.methodName}:${entry.lineNumber}`;
        default:
            return null;
    }
};

function LogTable({locationFormat, showStackTrace, entries, search, ascendingOrder, toggleOrder}) {
    const lowercaseSearch = search.toLowerCase();
    let sort = ascendingOrder === 'true' ? <IconSortUp/> : <IconSortDown/>;
    return (
        <Table striped bordered hover size={'sm'} responsive>
            <thead>
            <tr>
                <th style={{whiteSpace: 'nowrap'}}>
                    <I18n name={'logviewer.timestamp'}>Timestamp</I18n>{' '}
                    <button
                        onClick={toggleOrder}
                        type="button"
                        className="btn btn-link btn-outline-primary btn-sm"
                    >
                        {sort}
                    </button>
                </th>
                <th><I18n name={'logviewer.level'}>Level</I18n></th>
                <th><I18n name={'logviewer.message'}>Message</I18n></th>
                {locationFormat !== 'none' ? <th><I18n name={'logviewer.location'}>Location</I18n></th> : null}
            </tr>
            </thead>
            <tbody>
            {entries
                .filter(entry => [entry.message, (showStackTrace === 'true') ? entry.stackTrace : '', getLocationString(locationFormat, entry), entry.level, entry.logDate]
                    .join('|#|').toLowerCase()
                    .indexOf(lowercaseSearch) !== -1)
                .map((entry, index) => <LogEntry
                    entry={entry}
                    search={lowercaseSearch}
                    locationString={getLocationString(locationFormat, entry)}
                    showStackTrace={showStackTrace}
                    key={index}
                />)}
            </tbody>
        </Table>
    );
}

LogTable.propTypes = {
    locationFormat: PropTypes.oneOf(['none', 'short', 'normal']),
    entries: PropTypes.array,
    ascendingOrder: PropTypes.oneOf(['true', 'false']),
    search: PropTypes.string
};

LogTable.defaultProps = {
    locationFormat: 'none',
    ascendingOrder: 'false',
    entries: [],
    search: ''
};

export default LogTable;
