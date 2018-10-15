import React from 'react';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {PageHeader} from '../../general/BootstrapComponents';
import LogFilters from './LogFilters';
import {changeFilter, requestLogReload} from '../../../actions';
import LogTable from './LogTable';
import './LogViewer.css';

class LogView extends React.Component {

    componentDidMount = () => this.props.loadLog();

    render = () => (
            <React.Fragment>
                <PageHeader>Log viewer</PageHeader>
                <LogFilters
                    filters={this.props.filters}
                    changeFilter={this.props.changeFilter}
                    loadLog={(event) => {
                        event.preventDefault();
                        this.props.loadLog();
                    }}
                />
                <LogTable
                    search={this.props.filters.search}
                    locationFormat={this.props.filters.locationFormat}
                    entries={this.props.entries}
                />
            </React.Fragment>
        );
}

LogView.propTypes = {
    changeFilter: PropTypes.func.isRequired,
    filters: PropTypes.shape({
        threshold: PropTypes.oneOf(['error', 'warn', 'info', 'debug', 'trace']),
        search: PropTypes.string,
        locationFormat: PropTypes.oneOf(['none', 'short', 'normal']),
        maxSize: PropTypes.oneOf(['50', '100', '500', '1000', '10000']),
        ascendingOrder: PropTypes.oneOf(['true', 'false'])
    }).isRequired,
    loadLog: PropTypes.func.isRequired
};

const mapStateToProps = state => state.log;

const actions = {
    changeFilter,
    loadLog: requestLogReload
};

export default connect(mapStateToProps, actions)(LogView);