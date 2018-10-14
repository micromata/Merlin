import React from 'react';
import PropTypes from 'prop-types';
import {Table} from 'reactstrap';
import LogEntry from './LogEntry';

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

function LogTable({locationFormat, entries, search}) {
    const lowercaseSearch = search.toLowerCase();

    return (
        <Table striped bordered hover size={'sm'} responsive>
            <thead>
            <tr>
                <th>Timestamp</th>
                <th>Level</th>
                {locationFormat !== 'none' ? <th>Location</th> : null}
                <th>Message</th>
            </tr>
            </thead>
            <tbody>
            {entries
                .filter(entry => [entry.message, getLocationString(locationFormat, entry), entry.level, entry.logDate]
                    .join('|#|').toLowerCase()
                    .indexOf(lowercaseSearch) !== -1)
                .map((entry, index) => <LogEntry
                    entry={entry}
                    search={lowercaseSearch}
                    locationString={getLocationString(locationFormat, entry)}
                    key={index}
                />)}
            </tbody>
        </Table>
    );
}

LogTable.propTypes = {
    locationFormat: PropTypes.oneOf(['none', 'short', 'normal']),
    entries: PropTypes.array,
    search: PropTypes.string
};

LogTable.defaultProps = {
    locationFormat: 'none',
    entries: [],
    search: ''
};

export default LogTable;