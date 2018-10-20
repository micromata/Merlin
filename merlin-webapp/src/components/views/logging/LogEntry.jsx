import React from 'react';
import PropTypes from 'prop-types';
import Highlight from 'react-highlighter';

function LogEntry({entry, search, locationString, showStackTrace}) {
    let message = (showStackTrace === 'true' && entry.stackTrace) ? entry.message + <br/> + entry.stackTrace : entry.message;
    return (
        <tr>
            <td>{entry.logDate}</td>
            <td className={`log-${entry.level}`}><Highlight search={search}>{entry.level}</Highlight></td>
            {locationString ? <td><Highlight search={search}>{locationString}</Highlight></td> : undefined}
            <td className={'tt'}><Highlight search={search}>{message}</Highlight></td>
        </tr>
    );
}

LogEntry.propTypes = {
    entry: PropTypes.shape({}).isRequired,
    search: PropTypes.string,
    locationString: PropTypes.string,
    showStackTrace: PropTypes.oneOf(['true', 'false'])
};

export default LogEntry;