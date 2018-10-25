import React from 'react';
import {getRestServiceUrl} from '../../../utilities/global';
import LogTable from './LogTable';
import './LogViewer.css';
import PropTypes from 'prop-types';

class LogEmbeddedPanel extends React.Component {

    componentDidMount = () => {
        this.reload();
    };

    handleToggleSortOrder = () => {
        this.setState({
            ascendingOrder: (this.state.ascendingOrder === 'true') ? 'false' : 'true'
        }, () => {
            this.reload()
        });
    };

    handleInputChange = (event) => {
        this.props.changeFilter(event.target.name, event.target.value);
    };

    reload = () => {
        this.setState({
            isFetching: true,
            failed: false,
            logEntries: undefined
        });
        fetch(getRestServiceUrl("logging/query", {
            search: this.state.search,
            treshold: this.props.treshold,
            maxSize: this.props.maxSize,
            ascendingOrder: this.state.ascendingOrder,
            mdcTemplatePk: this.props.mdcTemplatePk,
            mdcTemplateDefinitionPk: this.props.mdcTemplateDefinitionPk
        }), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => {
                const logEntries = json.map(logEntry => {
                    return {
                        level: logEntry.level.toLowerCase(),
                        message: logEntry.message,
                        logDate: logEntry.logDate,
                        javaClass: logEntry.javaClass,
                        javaClassSimpleName: logEntry.javaClassSimpleName,
                        lineNumber: logEntry.lineNumber,
                        methodName: logEntry.methodName
                    };
                });
                this.setState({
                    isFetching: false,
                    logEntries
                })
            })
            .catch(() => this.setState({isFetching: false, failed: true}));
    };


    render = () => {
        return <LogTable
            search={''}
            locationFormat={this.props.locationFormat}
            entries={this.state.logEntries}
            ascendingOrder={this.state.ascendingOrder}
            toggleOrder={this.handleToggleSortOrder}
            showStackTrace={this.props.showStackTrace}
        />
    };

    constructor(props) {
        super(props);
        this.state = {
            search: '',
            ascendingOrder: 'false'
        }
    }
}

LogEmbeddedPanel.propTypes = {
    locationFormat: PropTypes.oneOf(['none', 'short', 'normal']),
    showStackTrace: PropTypes.oneOf(['true', 'false']),
    threshold: PropTypes.oneOf(['error', 'warn', 'info', 'debug', 'trace']),
    maxSize: PropTypes.oneOf(['50', '100', '500', '1000', '10000']),
    mdcTemplatePk: PropTypes.string,
    mdcTemplateDefinitionPk: PropTypes.string,
    search: PropTypes.string
};

LogEmbeddedPanel.defaultProps = {
    locationFormat: 'none',
    showStackTrace: 'false',
    treshold: 'info',
    maxSize: '50',
    mdcTemplatePk: null,
    mdcTemplateDefinitionPk: null,
    search: ''
};

export default LogEmbeddedPanel;
